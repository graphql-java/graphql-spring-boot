package graphql.kickstart.spring.rsocket;

import graphql.kickstart.execution.context.DefaultGraphQLContextBuilder;
import graphql.kickstart.execution.context.GraphQLContext;
import java.util.Map;

public class DefaultGraphQlSpringRSocketContextBuilder
    implements GraphQlSpringRSocketContextBuilder {

  @Override
  public GraphQLContext build(Map<String, Object> headers) {
    return new DefaultGraphQLContextBuilder().build();
  }
}
