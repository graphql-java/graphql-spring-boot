package graphql.kickstart.spring.webflux;

import graphql.kickstart.spring.GraphQLSpringRootObjectBuilder;
import org.springframework.web.reactive.socket.WebSocketSession;

public interface GraphQLSpringWebfluxRootObjectBuilder extends GraphQLSpringRootObjectBuilder {

  Object build(WebSocketSession webSocketSession);
}
