package graphql.kickstart.spring.webflux.apollo;

import graphql.kickstart.execution.GraphQLInvoker;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.subscriptions.GraphQLSubscriptionInvocationInputFactory;
import graphql.kickstart.execution.subscriptions.GraphQLSubscriptionMapper;
import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionProtocolFactory;
import graphql.kickstart.spring.webflux.ReactiveSubscriptionsProtocolFactory;
import java.time.Duration;
import java.util.Collection;
import org.springframework.web.reactive.socket.WebSocketSession;

public class ReactiveApolloSubscriptionProtocolFactory extends ApolloSubscriptionProtocolFactory
    implements ReactiveSubscriptionsProtocolFactory {

  public ReactiveApolloSubscriptionProtocolFactory(
      GraphQLObjectMapper objectMapper,
      GraphQLSubscriptionInvocationInputFactory invocationInputFactory,
      GraphQLInvoker graphQLInvoker) {
    super(objectMapper, invocationInputFactory, graphQLInvoker);
  }

  public ReactiveApolloSubscriptionProtocolFactory(
      GraphQLObjectMapper objectMapper,
      GraphQLSubscriptionInvocationInputFactory invocationInputFactory,
      GraphQLInvoker graphQLInvoker,
      Duration keepAliveInterval) {
    super(objectMapper, invocationInputFactory, graphQLInvoker, keepAliveInterval);
  }

  public ReactiveApolloSubscriptionProtocolFactory(
      GraphQLObjectMapper objectMapper,
      GraphQLSubscriptionInvocationInputFactory invocationInputFactory,
      GraphQLInvoker graphQLInvoker,
      Collection<ApolloSubscriptionConnectionListener> connectionListeners) {
    super(objectMapper, invocationInputFactory, graphQLInvoker, connectionListeners);
  }

  public ReactiveApolloSubscriptionProtocolFactory(
      GraphQLObjectMapper objectMapper,
      GraphQLSubscriptionInvocationInputFactory invocationInputFactory,
      GraphQLInvoker graphQLInvoker,
      Collection<ApolloSubscriptionConnectionListener> connectionListeners,
      Duration keepAliveInterval) {
    super(
        objectMapper,
        invocationInputFactory,
        graphQLInvoker,
        connectionListeners,
        keepAliveInterval);
  }

  @Override
  public SubscriptionSession createSession(WebSocketSession session) {
    return new ReactiveApolloSubscriptionSession(
        new GraphQLSubscriptionMapper(getObjectMapper()), session);
  }
}
