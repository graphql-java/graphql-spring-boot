package graphql.kickstart.spring.rsocket.boot;

import static graphql.kickstart.execution.GraphQLObjectMapper.newBuilder;

import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentationOptions;
import graphql.kickstart.execution.BatchedDataLoaderGraphQLBuilder;
import graphql.kickstart.execution.GraphQLInvoker;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.config.DefaultGraphQLSchemaProvider;
import graphql.kickstart.execution.config.GraphQLBuilder;
import graphql.kickstart.execution.config.GraphQLSchemaProvider;
import graphql.kickstart.execution.config.ObjectMapperProvider;
import graphql.kickstart.spring.error.ErrorHandlerSupplier;
import graphql.kickstart.spring.error.GraphQLErrorStartupListener;
import graphql.kickstart.spring.rsocket.DefaultGraphQLSpringRSocketRootObjectBuilder;
import graphql.kickstart.spring.rsocket.DefaultGraphQlSpringRSocketContextBuilder;
import graphql.kickstart.spring.rsocket.DefaultGraphQlSpringRSocketInvocationInputFactory;
import graphql.kickstart.spring.rsocket.GraphQLSpringRSocketRootObjectBuilder;
import graphql.kickstart.spring.rsocket.GraphQlMessageHandler;
import graphql.kickstart.spring.rsocket.GraphQlSpringRSocketContextBuilder;
import graphql.kickstart.spring.rsocket.GraphQlSpringRSocketInvocationInputFactory;
import graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration;
import graphql.schema.GraphQLSchema;
import java.util.function.Supplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@ConditionalOnBean(GraphQLSchema.class)
@AutoConfigureAfter(GraphQLJavaToolsAutoConfiguration.class)
@AutoConfigureBefore(RSocketServerAutoConfiguration.class)
@Import(GraphQlMessageHandler.class)
@PropertySource("classpath:graphql.properties")
public class GraphQlSpringRSocketAutoConfiguration {

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
  public GraphQlSpringRSocketContextBuilder graphQLSpringWebfluxContextBuilder() {
    return new DefaultGraphQlSpringRSocketContextBuilder();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLSpringRSocketRootObjectBuilder graphQLSpringWebfluxRootObjectBuilder() {
    return new DefaultGraphQLSpringRSocketRootObjectBuilder();
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQLSchemaProvider graphQLSchemaProvider(GraphQLSchema schema) {
    return new DefaultGraphQLSchemaProvider(schema);
  }

  @Bean
  @ConditionalOnMissingBean
  public GraphQlSpringRSocketInvocationInputFactory graphQLSpringInvocationInputFactory(
      GraphQLSchemaProvider graphQLSchemaProvider,
      @Autowired(required = false) GraphQlSpringRSocketContextBuilder contextBuilder,
      @Autowired(required = false) GraphQLSpringRSocketRootObjectBuilder rootObjectBuilder) {
    return new DefaultGraphQlSpringRSocketInvocationInputFactory(
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
}
