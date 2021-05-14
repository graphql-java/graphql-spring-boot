package graphql.kickstart.spring.rsocket;

import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import java.util.Map;

public interface GraphQlSpringRSocketInvocationInputFactory {
  GraphQLSingleInvocationInput create(GraphQLRequest request, Map<String, Object> message);
}
