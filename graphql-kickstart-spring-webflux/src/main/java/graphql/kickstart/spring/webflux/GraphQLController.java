package graphql.kickstart.spring.webflux;

import graphql.ExecutionResult;
import graphql.kickstart.execution.GraphQLInvoker;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import graphql.kickstart.spring.AbstractGraphQLController;
import graphql.kickstart.spring.GraphQLSpringInvocationInputFactory;
import java.util.Map;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class GraphQLController extends AbstractGraphQLController {

  private final GraphQLObjectMapper objectMapper;
  private final GraphQLInvoker graphQLInvoker;
  private final GraphQLSpringInvocationInputFactory invocationInputFactory;

  public GraphQLController(
      GraphQLObjectMapper objectMapper,
      GraphQLInvoker graphQLInvoker,
      GraphQLSpringInvocationInputFactory invocationInputFactory) {
    super(objectMapper);
    this.objectMapper = objectMapper;
    this.graphQLInvoker = graphQLInvoker;
    this.invocationInputFactory = invocationInputFactory;
  }

  protected Object executeRequest(
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
