package graphql.kickstart.spring.rsocket;

import graphql.kickstart.execution.GraphQLRequest;
import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.execution.input.GraphQLSingleInvocationInput;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGraphQlSpringRSocketInvocationInputFactory
    implements GraphQlSpringRSocketInvocationInputFactory {

  private final Supplier<GraphQLSchemaProvider> schemaProviderSupplier;
  private Supplier<GraphQlSpringRSocketContextBuilder> contextBuilderSupplier;
  private Supplier<GraphQLSpringRSocketRootObjectBuilder> rootObjectBuilderSupplier;

  public DefaultGraphQlSpringRSocketInvocationInputFactory(
      GraphQLSchemaProvider schemaProvider,
      GraphQlSpringRSocketContextBuilder contextBuilder,
      GraphQLSpringRSocketRootObjectBuilder rootObjectBuilder) {

    Objects.requireNonNull(schemaProvider, "GraphQLSchemaProvider is required");
    this.schemaProviderSupplier = () -> schemaProvider;
    if (contextBuilder != null) {
      contextBuilderSupplier = () -> contextBuilder;
    }
    if (rootObjectBuilder != null) {
      rootObjectBuilderSupplier = () -> rootObjectBuilder;
    }
  }

  @Override
  public GraphQLSingleInvocationInput create(GraphQLRequest request, Map<String, Object> headers) {
    return new GraphQLSingleInvocationInput(
        request,
        schemaProviderSupplier.get().getSchema(),
        contextBuilderSupplier.get().build(headers),
        rootObjectBuilderSupplier.get().build(headers));
  }
}
