package graphql.kickstart.spring.webflux;

import graphql.kickstart.spring.GraphQLSpringRootObjectBuilder;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;

public class DefaultGraphQLSpringWebfluxRootObjectBuilder
    implements GraphQLSpringRootObjectBuilder, GraphQLSpringWebfluxRootObjectBuilder {

  @Override
  public Object build(ServerWebExchange serverWebExchange) {
    return new Object();
  }

  @Override
  public Object build(WebSocketSession webSocketSession) {
    return new Object();
  }
}
