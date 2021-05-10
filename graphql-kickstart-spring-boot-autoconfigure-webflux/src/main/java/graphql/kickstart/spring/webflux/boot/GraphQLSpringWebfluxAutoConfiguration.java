package graphql.kickstart.spring.webflux.boot;

import static graphql.kickstart.execution.GraphQLObjectMapper.newBuilder;
import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE;

import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import graphql.kickstart.execution.BatchedDataLoaderGraphQLBuilder;
import graphql.kickstart.execution.GraphQLInvoker;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.config.DefaultGraphQLSchemaProvider;
import graphql.kickstart.execution.config.GraphQLBuilder;
import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.execution.config.ObjectMapperProvider;
import graphql.kickstart.execution.subscriptions.GraphQLSubscriptionInvocationInputFactory;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.KeepAliveSubscriptionConnectionListener;
import graphql.kickstart.spring.error.ErrorHandlerSupplier;
import graphql.kickstart.spring.error.GraphQLErrorStartupListener;
import graphql.kickstart.spring.webflux.DefaultGraphQLSpringWebfluxContextBuilder;
import graphql.kickstart.spring.webflux.DefaultGraphQLSpringWebfluxRootObjectBuilder;
import graphql.kickstart.spring.webflux.GraphQLController;
import graphql.kickstart.spring.webflux.GraphQLSpringWebfluxContextBuilder;
import graphql.kickstart.spring.webflux.GraphQLSpringWebfluxInvocationInputFactory;
import graphql.kickstart.spring.webflux.GraphQLSpringWebfluxRootObjectBuilder;
import graphql.kickstart.spring.webflux.ReactiveSubscriptionsProtocolFactory;
import graphql.kickstart.spring.webflux.ReactiveWebSocketSubscriptionsHandler;
import graphql.kickstart.spring.webflux.apollo.ReactiveApolloSubscriptionProtocolFactory;
import graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration;
import graphql.schema.GraphQLSchema;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = REACTIVE)
@ConditionalOnBean(GraphQLSchema.class)
@AutoConfigureAfter(GraphQLJavaToolsAutoConfiguration.class)
@Import({GraphQLController.class, ReactiveWebSocketSubscriptionsHandler.class})
public class GraphQLSpringWebfluxAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ErrorHandlerSupplier errorHandlerSupplier() {
    return new ErrorHandlerSupplier(null);
  }

  @Bean
  public GraphQLErrorStartupListener graphQLErrorStartupListener(
      ErrorHandlerSupplier errorHandlerSupplier) {
    return new GraphQLErrorStartupListener(errorHandlerSupplier, true);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLObjectMapper graphQLObjectMapper(
      ObjectProvider<ObjectMapperProvider> provider, ErrorHandlerSupplier errorHandlerSupplier) {
    GraphQLObjectMapper.Builder builder = newBuilder();
    builder.withGraphQLErrorHandler(errorHandlerSupplier);
    provider.ifAvailable(builder::withObjectMapperProvider);
    return builder.build();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLSpringWebfluxContextBuilder graphQLSpringWebfluxContextBuilder() {
    return new DefaultGraphQLSpringWebfluxContextBuilder();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLSpringWebfluxRootObjectBuilder graphQLSpringWebfluxRootObjectBuilder() {
    return new DefaultGraphQLSpringWebfluxRootObjectBuilder();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLSchemaProvider graphQLSchemaProvider(GraphQLSchema schema) {
    return new DefaultGraphQLSchemaProvider(schema);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLSpringWebfluxInvocationInputFactory graphQLSpringInvocationInputFactory(
      GraphQLSchemaProvider graphQLSchemaProvider,
      @Autowired(required = false) GraphQLSpringWebfluxContextBuilder contextBuilder,
      @Autowired(required = false) GraphQLSpringWebfluxRootObjectBuilder rootObjectBuilder) {
    return new GraphQLSpringWebfluxInvocationInputFactory(
        graphQLSchemaProvider, contextBuilder, rootObjectBuilder);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLBuilder graphQLBuilder() {
    return new GraphQLBuilder();
  }

  @Bean
  @ConditionalOnMissingBean
  public BatchedDataLoaderGraphQLBuilder batchedDataLoaderGraphQLBuilder(
      @Autowired(required = false)
          Supplier<DataLoaderDispatcherInstrumentationOptions> optionsSupplier) {
    return new BatchedDataLoaderGraphQLBuilder(optionsSupplier);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLInvoker graphQLInvoker(
      GraphQLBuilder graphQLBuilder,
      BatchedDataLoaderGraphQLBuilder batchedDataLoaderGraphQLBuilder) {
    return new GraphQLInvoker(graphQLBuilder, batchedDataLoaderGraphQLBuilder);
  }

  @Bean
  @ConditionalOnMissingBean
  public ReactiveSubscriptionsProtocolFactory subscriptionProtocolFactory(
      GraphQLObjectMapper graphQLObjectMapper,
      GraphQLSubscriptionInvocationInputFactory invocationInputFactory,
      GraphQLInvoker graphQLInvoker,
      @Autowired(required = false)
          Collection<ApolloSubscriptionConnectionListener> connectionListeners) {
    Set<ApolloSubscriptionConnectionListener> listeners = new HashSet<>();
    if (connectionListeners != null) {
      listeners.addAll(connectionListeners);
    }
    if (listeners.stream().noneMatch(KeepAliveSubscriptionConnectionListener.class::isInstance)) {
      listeners.add(new KeepAliveSubscriptionConnectionListener());
    }
    return new ReactiveApolloSubscriptionProtocolFactory(
        graphQLObjectMapper, invocationInputFactory, graphQLInvoker, listeners);
  }

  @Bean
  public HandlerMapping webSocketHandlerMapping(
      @Value("${graphql.subscriptions.url:subscriptions}") String path,
      ReactiveWebSocketSubscriptionsHandler webSocketHandler) {
    Map<String, WebSocketHandler> map = new HashMap<>();
    map.put(path, webSocketHandler);

    SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
    handlerMapping.setOrder(1);
    handlerMapping.setUrlMap(map);
    return handlerMapping;
  }

  @Bean
  @ConditionalOnMissingBean
  WebSocketHandlerAdapter webSocketHandlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
