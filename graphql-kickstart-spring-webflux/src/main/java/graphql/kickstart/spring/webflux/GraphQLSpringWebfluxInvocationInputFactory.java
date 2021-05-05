package graphql.kickstart.spring.webflux;

import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import graphql.kickstart.execution.subscriptions.GraphQLSubscriptionInvocationInputFactory;
import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.spring.DefaultGraphQLSpringInvocationInputFactory;
import graphql.kickstart.spring.GraphQLSpringContextBuilder;
import graphql.kickstart.spring.GraphQLSpringRootObjectBuilder;
import java.util.function.Supplier;
import org.springframework.web.reactive.socket.WebSocketSession;

public class GraphQLSpringWebfluxInvocationInputFactory
    extends DefaultGraphQLSpringInvocationInputFactory
    implements GraphQLSubscriptionInvocationInputFactory {

  public GraphQLSpringWebfluxInvocationInputFactory(
      GraphQLSchemaProvider schemaProvider,
      GraphQLSpringContextBuilder contextBuilder,
      GraphQLSpringRootObjectBuilder rootObjectBuilder) {
    super(schemaProvider, contextBuilder, rootObjectBuilder);
  }

  public GraphQLSpringWebfluxInvocationInputFactory(
      Supplier<GraphQLSchemaProvider> schemaProviderSupplier,
      Supplier<GraphQLSpringContextBuilder> contextBuilderSupplier,
      Supplier<GraphQLSpringRootObjectBuilder> rootObjectBuilderSupplier) {
    super(schemaProviderSupplier, contextBuilderSupplier, rootObjectBuilderSupplier);
  }

  @Override
  public GraphQLSingleInvocationInput create(
      GraphQLRequest graphQLRequest, SubscriptionSession session) {
    return new GraphQLSingleInvocationInput(
        graphQLRequest,
        getSchemaProviderSupplier().get().getSchema(),
        ((GraphQLSpringWebfluxContextBuilder) getContextBuilderSupplier().get())
            .build((WebSocketSession) session.unwrap()),
        ((GraphQLSpringWebfluxRootObjectBuilder) getRootObjectBuilderSupplier().get())
            .build((WebSocketSession) session.unwrap()));
  }
}
