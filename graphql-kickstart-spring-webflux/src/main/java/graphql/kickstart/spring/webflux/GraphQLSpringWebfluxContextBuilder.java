package graphql.kickstart.spring.webflux;

import graphql.kickstart.spring.GraphQLSpringContextBuilder;
import org.springframework.web.reactive.socket.WebSocketSession;

public interface GraphQLSpringWebfluxContextBuilder extends GraphQLSpringContextBuilder {

  GraphQLSpringWebSocketSessionContext build(WebSocketSession webSocketSession);
}
