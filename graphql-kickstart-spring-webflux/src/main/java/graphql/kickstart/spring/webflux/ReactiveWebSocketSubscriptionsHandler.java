package graphql.kickstart.spring.webflux;

import static java.util.Collections.singletonList;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveWebSocketSubscriptionsHandler implements WebSocketHandler {

  private final ReactiveSubscriptionsProtocolFactory subscriptionProtocolFactory;

  @Override
  public List<String> getSubProtocols() {
    return singletonList("graphql-ws");
  }

  @Override
  public Mono<Void> handle(WebSocketSession webSocketSession) {
    SubscriptionSession subscriptionSession =
        subscriptionProtocolFactory.createSession(webSocketSession);
    Consumer<String> consumer = subscriptionProtocolFactory.createConsumer(subscriptionSession);

    Mono<Void> input =
        webSocketSession
            .receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(consumer)
            .doFinally(type -> subscriptionSession.close(null))
            .then();

    Mono<Void> sender =
        webSocketSession.send(
            Flux.from(subscriptionSession.getPublisher()).map(webSocketSession::textMessage));

    return Mono.zip(input, sender).then();
  }
}
