package graphql.kickstart.spring.rsocket;

import static java.util.Collections.singletonList;

import graphql.ExecutionResult;
import graphql.GraphqlErrorBuilder;
import graphql.execution.NonNullableFieldWasNullException;
import graphql.kickstart.execution.GraphQLInvoker;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.error.GenericGraphQLError;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GraphQlMessageHandler {
  private final GraphQLObjectMapper objectMapper;
  private final GraphQlSpringRSocketInvocationInputFactory invocationInputFactory;
  private final GraphQLInvoker graphQLInvoker;

  @MessageMapping("graphql")
  Mono<Object> request(@Payload String payload, @Headers Map<String, Object> headers) {
    return processQuery(payload, headers).map(objectMapper::createResultFromExecutionResult);
  }

  @MessageMapping("subscriptions")
  Flux<Map<String, Object>> subscription(
      @Payload String payload, @Headers Map<String, Object> headers) {
    return processQuery(payload, headers)
        .flatMapMany(executionResult -> {
          Publisher<ExecutionResult> publisher;
          if (executionResult.getData() instanceof Publisher) {
            publisher = executionResult.getData();
          } else {
            if (executionResult.getData() == null) {
              publisher = Flux.empty();
            } else {
              publisher = Flux.just(executionResult.getData());
            }
          }
          return publisher;
        })
        .map(
            executionResult -> {
              Map<String, Object> result = new HashMap<>();
              result.put("data", executionResult.getData());
              return result;
            })
        .onErrorResume(
            t -> {
              log.error("Subscription error", t);
              Map<String, Object> output = new HashMap<>();
              if (t.getCause() instanceof NonNullableFieldWasNullException) {
                NonNullableFieldWasNullException e =
                    (NonNullableFieldWasNullException) t.getCause();
                output.put(
                    "errors",
                    singletonList(
                        GraphqlErrorBuilder.newError()
                            .message(e.getMessage())
                            .path(e.getPath())
                            .build()));
              } else {
                output.put("errors", singletonList(new GenericGraphQLError(t.getMessage())));
              }
              return Flux.just(output);
            });
  }

  private Mono<ExecutionResult> processQuery(String payload, Map<String, Object> headers) {
    String contentType = String.valueOf(headers.get(MessageHeaders.CONTENT_TYPE));

    if (MimeTypeUtils.APPLICATION_JSON_VALUE.equals(contentType)) {
      return Mono.fromCallable(() -> objectMapper.readGraphQLRequest(payload))
          .flatMap(
              request -> {
                if (request.getQuery() == null) {
                  request.setQuery("");
                }
                return executeRequest(
                    request.getQuery(),
                    request.getOperationName(),
                    request.getVariables(),
                    headers);
              });
    }

    if ("application/graphql".equals(contentType)
        || "application/graphql; charset=utf-8".equals(contentType)) {
      return executeRequest(payload, null, Collections.emptyMap(), headers);
    }

    throw new ResponseStatusException(
        HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
  }

  private Mono<ExecutionResult> executeRequest(
      String query,
      String operationName,
      Map<String, Object> variables,
      Map<String, Object> headers) {

    GraphQLSingleInvocationInput invocationInput =
        invocationInputFactory.create(new GraphQLRequest(query, variables, operationName), headers);
    return Mono.fromCompletionStage(graphQLInvoker.executeAsync(invocationInput));
  }
}
