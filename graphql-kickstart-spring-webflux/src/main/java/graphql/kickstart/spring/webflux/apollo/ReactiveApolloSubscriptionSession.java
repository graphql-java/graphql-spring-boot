package graphql.kickstart.spring.webflux.apollo;

import graphql.kickstart.execution.subscriptions.GraphQLSubscriptionMapper;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionSession;
import graphql.kickstart.spring.webflux.ReactiveWebSocketSubscriptionSession;
import java.util.Map;
import org.springframework.web.reactive.socket.WebSocketSession;

public class ReactiveApolloSubscriptionSession extends ApolloSubscriptionSession {

  private final ReactiveWebSocketSubscriptionSession session;

  public ReactiveApolloSubscriptionSession(
      GraphQLSubscriptionMapper mapper, WebSocketSession webSocketSession) {
    super(mapper);
    session = new ReactiveWebSocketSubscriptionSession(mapper, webSocketSession);
  }

  @Override
  public boolean isOpen() {
    return session.isOpen();
  }

  @Override
  public Map<String, Object> getUserProperties() {
    return session.getUserProperties();
  }

  @Override
  public String getId() {
    return session.getId();
  }

  @Override
  public WebSocketSession unwrap() {
    return session.unwrap();
  }
}
