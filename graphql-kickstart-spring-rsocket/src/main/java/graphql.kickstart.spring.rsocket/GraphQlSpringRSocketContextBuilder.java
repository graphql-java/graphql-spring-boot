package graphql.kickstart.spring.rsocket;

import graphql.kickstart.execution.context.GraphQLContext;
import java.util.Map;

public interface GraphQlSpringRSocketContextBuilder {
  GraphQLContext build(Map<String, Object> headers);
}
