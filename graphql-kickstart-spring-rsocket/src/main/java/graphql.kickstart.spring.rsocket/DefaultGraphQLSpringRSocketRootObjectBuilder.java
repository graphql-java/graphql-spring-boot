package graphql.kickstart.spring.rsocket;

import java.util.Map;

public class DefaultGraphQLSpringRSocketRootObjectBuilder
    implements GraphQLSpringRSocketRootObjectBuilder {

  @Override
  public Object build(Map<String, Object> headers) {
    return new Object();
  }
}
