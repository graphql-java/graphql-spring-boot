package graphql.kickstart.spring;

import graphql.ExecutionResultImpl;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.error.GenericGraphQLError;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractGraphQLController {

  private static final String INVALID_REQUEST_BODY_MESSAGE = "Bad request - invalid request body.";

  private final GraphQLObjectMapper objectMapper;

  @PostMapping(
      value = "${graphql.url:graphql}",
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Object graphqlPOST(
      @RequestHeader(HttpHeaders.CONTENT_TYPE) final MediaType contentType,
      @Nullable @RequestParam(value = "query", required = false) String query,
      @Nullable @RequestParam(value = "operationName", required = false) String operationName,
      @Nullable @RequestParam(value = "variables", required = false) String variablesJson,
      @Nullable @RequestParam(value = "extensions", required = false) String extensionsJson,
      @Nullable @RequestBody(required = false) String body,
      ServerWebExchange serverWebExchange) {

    body = Optional.ofNullable(body).orElse("");

    if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
      GraphQLRequest request;
      try {
        request = objectMapper.readGraphQLRequest(body);
      } catch (IOException e) {
        return handleBodyParsingException(e);
      }
      if (request.getQuery() == null) {
        request.setQuery("");
      }
      return executeRequest(
          request.getQuery(),
          request.getOperationName(),
          request.getVariables(),
          request.getExtensions(),
          serverWebExchange);
    }

    // In addition to the above, we recommend supporting two additional cases:
    //
    // * If the "query" query string parameter is present (as in the GET example above),
    //   it should be parsed and handled in the same way as the HTTP GET case.

    if (query != null) {
      return executeRequest(
          query,
          operationName,
          convertVariablesJson(variablesJson),
          convertExtensionsJson(extensionsJson),
          serverWebExchange);
    }

    // * If the "application/graphql" Content-Type header is present,
    //   treat the HTTP POST body contents as the GraphQL query string.

    if ("application/graphql".equals(contentType.toString())
        || "application/graphql; charset=utf-8".equals(contentType.toString())) {
      return executeRequest(
          body, null, Collections.emptyMap(), Collections.emptyMap(), serverWebExchange);
    }

    throw new ResponseStatusException(
        HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
  }

  @GetMapping(value = "${graphql.url:graphql}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Object graphqlGET(
      @Nullable @RequestParam("query") String query,
      @Nullable @RequestParam(value = "operationName", required = false) String operationName,
      @Nullable @RequestParam(value = "variables", required = false) String variablesJson,
      @Nullable @RequestParam(value = "extensions", required = false) String extensionsJson,
      ServerWebExchange serverWebExchange) {
    return executeRequest(
        query == null ? "" : query,
        operationName,
        convertVariablesJson(variablesJson),
        convertExtensionsJson(extensionsJson),
        serverWebExchange);
  }

  private Map<String, Object> convertVariablesJson(String jsonMap) {
    return Optional.ofNullable(jsonMap)
        .map(objectMapper::deserializeVariables)
        .orElseGet(Collections::emptyMap);
  }

  private Map<String, Object> convertExtensionsJson(String jsonMap) {
    return Optional.ofNullable(jsonMap)
        .map(objectMapper::deserializeExtensions)
        .orElseGet(Collections::emptyMap);
  }

  protected abstract Object executeRequest(
      String query,
      String operationName,
      Map<String, Object> variables,
      Map<String, Object> extensions,
      ServerWebExchange serverWebExchange);

  protected Object handleBodyParsingException(Exception exception) {
    log.error("{} {}", INVALID_REQUEST_BODY_MESSAGE, exception.getMessage());
    return objectMapper.createResultFromExecutionResult(
        new ExecutionResultImpl(new GenericGraphQLError(INVALID_REQUEST_BODY_MESSAGE)));
  }
}
