package graphql.kickstart.spring;

import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.GraphQLRequest;
import lombok.RequiredArgsConstructor;
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

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractGraphQLController {

  private final GraphQLObjectMapper objectMapper;

  @PostMapping(value = "${graphql.url:graphql}",
          consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Object graphqlPOST(
      @RequestHeader(HttpHeaders.CONTENT_TYPE) final MediaType contentType,
      @Nullable @RequestParam(value = "query", required = false) String query,
      @Nullable @RequestParam(value = "operationName", required = false) String operationName,
      @Nullable @RequestParam(value = "variables", required = false) String variablesJson,
      @Nullable @RequestBody(required = false) String body,
      ServerWebExchange serverWebExchange) throws IOException {

    body = body != null ? body : "";

    // https://graphql.org/learn/serving-over-http/#post-request
    //
    // A standard GraphQL POST request should use the application/json content type,
    // and include a JSON-encoded body of the following form:
    //
    // {
    //   "query": "...",
    //   "operationName": "...",
    //   "variables": { "myVariable": "someValue", ... }
    // }

    if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
      GraphQLRequest request = objectMapper.readGraphQLRequest(body);
      if (request.getQuery() == null) {
        request.setQuery("");
      }
      return executeRequest(request.getQuery(), request.getOperationName(), request.getVariables(), serverWebExchange);
    }

    // In addition to the above, we recommend supporting two additional cases:
    //
    // * If the "query" query string parameter is present (as in the GET example above),
    //   it should be parsed and handled in the same way as the HTTP GET case.

    if (query != null) {
      return executeRequest(query, operationName, convertVariablesJson(variablesJson), serverWebExchange);
    }

    // * If the "application/graphql" Content-Type header is present,
    //   treat the HTTP POST body contents as the GraphQL query string.

    if ("application/graphql".equals(contentType.toString()) || "application/graphql; charset=utf-8".equals(contentType.toString())) {
      return executeRequest(body, null, Collections.emptyMap(), serverWebExchange);
    }

    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
  }

  @GetMapping(value = "${graphql.url:graphql}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Object graphqlGET(
      @Nullable @RequestParam("query") String query,
      @Nullable @RequestParam(value = "operationName", required = false) String operationName,
      @Nullable @RequestParam(value = "variables", required = false) String variablesJson,
      ServerWebExchange serverWebExchange) {

    // https://graphql.org/learn/serving-over-http/#get-request
    //
    // When receiving an HTTP GET request, the GraphQL query should be specified in the "query" query string.
    // For example, if we wanted to execute the following GraphQL query:
    //
    // {
    //   me {
    //     name
    //   }
    // }
    //
    // This request could be sent via an HTTP GET like so:
    //
    // http://myapi/graphql?query={me{name}}
    //
    // Query variables can be sent as a JSON-encoded string in an additional query parameter called "variables".
    // If the query contains several named operations,
    // an "operationName" query parameter can be used to control which one should be executed.

    return executeRequest(query, operationName, convertVariablesJson(variablesJson), serverWebExchange);
  }

  private Map<String, Object> convertVariablesJson(String jsonMap) {
    return jsonMap != null ? objectMapper.deserializeVariables(jsonMap) : Collections.emptyMap();
  }

  protected abstract Object executeRequest(
      String query,
      String operationName,
      Map<String, Object> variables,
      ServerWebExchange serverWebExchange
  );

}
