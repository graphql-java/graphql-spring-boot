package graphql.kickstart.spring.webflux;

import graphql.kickstart.execution.context.DefaultGraphQLContext;
import java.util.Objects;
import org.dataloader.DataLoaderRegistry;
import org.springframework.web.reactive.socket.WebSocketSession;

public class DefaultGraphQLSpringWebSocketSessionContext extends DefaultGraphQLContext
    implements GraphQLSpringWebSocketSessionContext {

  private final WebSocketSession webSocketSession;

  public DefaultGraphQLSpringWebSocketSessionContext(WebSocketSession webSocketSession) {
    this(new DataLoaderRegistry(), webSocketSession);
  }

  public DefaultGraphQLSpringWebSocketSessionContext(
      DataLoaderRegistry dataLoaderRegistry, WebSocketSession webSocketSession) {
    super(dataLoaderRegistry, null);
    this.webSocketSession =
        Objects.requireNonNull(webSocketSession, "WebSocketSession is required");
  }

  @Override
  public WebSocketSession getWebSocketSession() {
    return webSocketSession;
  }
}
