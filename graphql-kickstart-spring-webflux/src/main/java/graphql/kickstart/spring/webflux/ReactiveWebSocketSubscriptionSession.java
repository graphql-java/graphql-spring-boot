package graphql.kickstart.spring.webflux;

import graphql.kickstart.execution.subscriptions.DefaultSubscriptionSession;
import graphql.kickstart.execution.subscriptions.GraphQLSubscriptionMapper;
import java.util.Map;
import org.springframework.web.reactive.socket.WebSocketSession;

public class ReactiveWebSocketSubscriptionSession extends DefaultSubscriptionSession {

  private final WebSocketSession webSocketSession;

  public ReactiveWebSocketSubscriptionSession(
      GraphQLSubscriptionMapper mapper, WebSocketSession webSocketSession) {
    super(mapper);
    this.webSocketSession = webSocketSession;
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public Map<String, Object> getUserProperties() {
    return webSocketSession.getAttributes();
  }

  @Override
  public String getId() {
    return webSocketSession.getId();
  }

  @Override
  public WebSocketSession unwrap() {
    return webSocketSession;
  }
}
