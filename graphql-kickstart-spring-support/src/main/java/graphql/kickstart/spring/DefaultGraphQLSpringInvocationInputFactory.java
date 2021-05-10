package graphql.kickstart.spring;

import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.execution.input.GraphQLBatchedInvocationInput;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.web.server.ServerWebExchange;

@Getter(AccessLevel.PROTECTED)
public class DefaultGraphQLSpringInvocationInputFactory
    implements GraphQLSpringInvocationInputFactory {

  private final Supplier<GraphQLSchemaProvider> schemaProviderSupplier;
  private Supplier<GraphQLSpringContextBuilder> contextBuilderSupplier =
      () -> (GraphQLSpringServerWebExchangeContext::new);
  private Supplier<GraphQLSpringRootObjectBuilder> rootObjectBuilderSupplier =
      () -> (serverWebExchange -> new Object());

  public DefaultGraphQLSpringInvocationInputFactory(
      GraphQLSchemaProvider schemaProvider,
      GraphQLSpringContextBuilder contextBuilder,
      GraphQLSpringRootObjectBuilder rootObjectBuilder) {
    Objects.requireNonNull(schemaProvider, "GraphQLSchemaProvider is required");
    this.schemaProviderSupplier = () -> schemaProvider;
    if (contextBuilder != null) {
      contextBuilderSupplier = () -> contextBuilder;
    }
    if (rootObjectBuilder != null) {
      rootObjectBuilderSupplier = () -> rootObjectBuilder;
    }
  }

  public DefaultGraphQLSpringInvocationInputFactory(
      Supplier<GraphQLSchemaProvider> schemaProviderSupplier,
      Supplier<GraphQLSpringContextBuilder> contextBuilderSupplier,
      Supplier<GraphQLSpringRootObjectBuilder> rootObjectBuilderSupplier) {
    this.schemaProviderSupplier =
        Objects.requireNonNull(schemaProviderSupplier, "GraphQLSchemaProvider is required");
    if (contextBuilderSupplier != null) {
      this.contextBuilderSupplier = contextBuilderSupplier;
    }
    if (rootObjectBuilderSupplier != null) {
      this.rootObjectBuilderSupplier = rootObjectBuilderSupplier;
    }
  }

  @Override
  public GraphQLSingleInvocationInput create(
      GraphQLRequest graphQLRequest, ServerWebExchange serverWebExchange) {
    return new GraphQLSingleInvocationInput(
        graphQLRequest,
        schemaProviderSupplier.get().getSchema(),
        contextBuilderSupplier.get().build(serverWebExchange),
        rootObjectBuilderSupplier.get().build(serverWebExchange));
  }

  @Override
  public GraphQLBatchedInvocationInput create(
      Collection<GraphQLRequest> graphQLRequests, ServerWebExchange serverWebExchange) {
    throw new UnsupportedOperationException("Batch queries not suppoprted yet");
  }
}
