package graphql.kickstart.spring;

import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.input.GraphQLBatchedInvocationInput;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import java.util.Collection;
import org.springframework.web.server.ServerWebExchange;

public interface GraphQLSpringInvocationInputFactory {

  GraphQLSingleInvocationInput create(
      GraphQLRequest graphQLRequest, ServerWebExchange serverWebExchange);

  GraphQLBatchedInvocationInput create(
      Collection<GraphQLRequest> graphQLRequests, ServerWebExchange serverWebExchange);
}
