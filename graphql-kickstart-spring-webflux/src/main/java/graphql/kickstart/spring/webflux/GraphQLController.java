package graphql.kickstart.spring.webflux;

import graphql.ExecutionResult;
import graphql.kickstart.execution.GraphQLInvoker;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import graphql.kickstart.spring.GraphQLSpringInvocationInputFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class GraphQLController {

  private final GraphQLObjectMapper objectMapper;
  private final GraphQLInvoker graphQLInvoker;
  private final GraphQLSpringInvocationInputFactory invocationInputFactory;

  public GraphQLController(
    GraphQLObjectMapper objectMapper,
    GraphQLInvoker graphQLInvoker,
    GraphQLSpringInvocationInputFactory invocationInputFactory) {
    this.objectMapper = objectMapper;
    this.graphQLInvoker = graphQLInvoker;
    this.invocationInputFactory = invocationInputFactory;
  }

  @PostMapping(
    value = "${graphql.url:graphql}",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Object> postByJson(
    @RequestBody Mono<GraphQLRequest> requestBodyMono, ServerWebExchange serverWebExchange) {
    return requestBodyMono.flatMap(
      request -> {
        if (request.getQuery() == null) {
          request.setQuery("");
        }
        return executeRequest(
          request.getQuery(),
          request.getOperationName(),
          request.getVariables(),
          serverWebExchange);
      });
  }

  @PostMapping(
    value = "${graphql.url:graphql}",
    consumes = {"application/graphql", "application/graphql; charset=utf-8"},
    produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Object> post(
    @RequestBody Mono<String> requestBodyMono, ServerWebExchange serverWebExchange) {
    // * If the "application/graphql" Content-Type header is present,
    //   treat the HTTP POST body contents as the GraphQL query string.
    return requestBodyMono.flatMap(
      query -> executeRequest(query, null, Collections.emptyMap(), serverWebExchange));
  }

  @PostMapping(
    value = "${graphql.url:graphql}",
    consumes = MediaType.ALL_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Object> postByGraphql(
    @Nullable @RequestParam(value = "query", required = false) String query,
    @Nullable @RequestParam(value = "operationName", required = false) String operationName,
    @Nullable @RequestParam(value = "variables", required = false) String variablesJson,
    ServerWebExchange serverWebExchange) {
    // * If the "query" query string parameter is present (as in the GET example above),
    //   it should be parsed and handled in the same way as the HTTP GET case.
    if (query != null) {
      return executeRequest(
        query, operationName, convertVariablesJson(variablesJson), serverWebExchange);
    }
    throw new ResponseStatusException(
      HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
  }

  @GetMapping(value = "${graphql.url:graphql}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Object> get(
    @Nullable @RequestParam("query") String query,
    @Nullable @RequestParam(value = "operationName", required = false) String operationName,
    @Nullable @RequestParam(value = "variables", required = false) String variablesJson,
    ServerWebExchange serverWebExchange) {
    return executeRequest(
      query, operationName, convertVariablesJson(variablesJson), serverWebExchange);
  }

  private Map<String, Object> convertVariablesJson(String jsonMap) {
    return Optional.ofNullable(jsonMap)
      .map(objectMapper::deserializeVariables)
      .orElseGet(Collections::emptyMap);
  }

  protected Mono<Object> executeRequest(
    String query,
    String operationName,
    Map<String, Object> variables,
    ServerWebExchange serverWebExchange) {
    GraphQLSingleInvocationInput invocationInput =
      invocationInputFactory.create(
        new GraphQLRequest(query, variables, operationName), serverWebExchange);
    Mono<ExecutionResult> executionResult =
      Mono.fromCompletionStage(graphQLInvoker.executeAsync(invocationInput));
    return executionResult.map(objectMapper::createResultFromExecutionResult);
  }
}
