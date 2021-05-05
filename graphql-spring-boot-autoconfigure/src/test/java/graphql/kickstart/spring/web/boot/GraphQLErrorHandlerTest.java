package graphql.kickstart.spring.web.boot;

import com.graphql.spring.boot.test.TestUtils;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import graphql.kickstart.spring.web.boot.test.AbstractAutoConfigurationTest;
import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

class GraphQLErrorHandlerTest extends AbstractAutoConfigurationTest {

  private GraphQL gql;
  private GraphQLObjectMapper objectMapper;

  public GraphQLErrorHandlerTest() {
    super(AnnotationConfigWebApplicationContext.class, GraphQLWebAutoConfiguration.class);
  }

  @BeforeEach
  public void setUp() {
    System.setProperty("graphql.tools.schemaLocationPattern", "graphql/error-handler-test.graphql");
    load(BaseConfiguration.class);

    GraphQLSchema schema = getContext().getBean(GraphQLSchema.class);
    gql = GraphQL.newGraphQL(schema).build();

    GraphQLErrorHandler errorHandler = getContext().getBean(GraphQLErrorHandler.class);
    objectMapper = GraphQLObjectMapper.newBuilder().withGraphQLErrorHandler(errorHandler).build();
  }

  @Test
  void illegalArgumentExceptionShouldBeHandledConcretely() {
    TestUtils.assertGraphQLError(
        gql,
        "query { illegalArgumentException }",
        new ThrowableGraphQLError(
            new IllegalArgumentException("Some argument"), "Custom illegal argument"),
        objectMapper);
  }

  @Test
  void illegalStateExceptionShouldBeHandledByCatchAll() {
    TestUtils.assertGraphQLError(
        gql,
        "query { illegalStateException }",
        new ThrowableGraphQLError(new IllegalStateException("Illegal state"), "Catch all handler"),
        objectMapper);
  }

  @Configuration
  static class BaseConfiguration {

    public static class Query implements GraphQLQueryResolver {

      boolean illegalArgumentException() {
        throw new IllegalArgumentException("Illegal argument");
      }

      boolean illegalStateException() {
        throw new IllegalStateException("Illegal state");
      }

      @ExceptionHandler(IllegalArgumentException.class)
      public ThrowableGraphQLError handle(IllegalArgumentException e) {
        return new ThrowableGraphQLError(e, "Custom illegal argument");
      }

      @ExceptionHandler(Throwable.class)
      public GraphQLError handle(Throwable e) {
        return new ThrowableGraphQLError(e, "Catch all handler");
      }

      @Bean
      Query queryResolver() {
        return new Query();
      }

      @Bean
      GraphQLSchema schema() {
        SchemaParser schemaParser =
            SchemaParser.newParser()
                .file("graphql/error-handler-test.graphql")
                .resolvers(queryResolver())
                .build();
        return schemaParser.makeExecutableSchema();
      }
    }
  }
}
