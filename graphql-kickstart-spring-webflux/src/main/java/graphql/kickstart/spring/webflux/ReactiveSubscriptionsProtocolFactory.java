package graphql.kickstart.spring.webflux;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import java.util.function.Consumer;
import org.springframework.web.reactive.socket.WebSocketSession;

public interface ReactiveSubscriptionsProtocolFactory {

  Consumer<String> createConsumer(SubscriptionSession session);

  SubscriptionSession createSession(WebSocketSession session);
}
