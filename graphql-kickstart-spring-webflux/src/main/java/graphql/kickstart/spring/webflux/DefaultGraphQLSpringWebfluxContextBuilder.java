package graphql.kickstart.spring.webflux;

import graphql.kickstart.spring.GraphQLSpringContext;
import graphql.kickstart.spring.GraphQLSpringServerWebExchangeContext;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.server.ServerWebExchange;

public class DefaultGraphQLSpringWebfluxContextBuilder
    implements GraphQLSpringWebfluxContextBuilder {

  @Override
  public GraphQLSpringWebSocketSessionContext build(WebSocketSession webSocketSession) {
    return new DefaultGraphQLSpringWebSocketSessionContext(webSocketSession);
  }

  @Override
  public GraphQLSpringContext build(ServerWebExchange serverWebExchange) {
    return new GraphQLSpringServerWebExchangeContext(serverWebExchange);
  }
}
