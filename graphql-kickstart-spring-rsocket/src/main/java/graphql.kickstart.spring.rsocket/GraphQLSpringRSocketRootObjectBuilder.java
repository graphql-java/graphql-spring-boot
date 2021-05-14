package graphql.kickstart.spring.rsocket;

import java.util.Map;

public interface GraphQLSpringRSocketRootObjectBuilder {
  Object build(Map<String, Object> headers);
}
