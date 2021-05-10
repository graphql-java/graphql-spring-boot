package graphql.kickstart.tools.boot;

import static org.assertj.core.api.Assertions.assertThat;

import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.SchemaParserDictionary;
import graphql.schema.GraphQLSchema;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @author <a href="mailto:java.lang.RuntimeException@gmail.com">oEmbedler Inc.</a> */
class GraphQLJavaToolsAutoConfigurationTest extends AbstractAutoConfigurationTest {

  public GraphQLJavaToolsAutoConfigurationTest() {
    super(GraphQLJavaToolsAutoConfiguration.class);
  }

  @Test
  void appContextLoads() {
    load(BaseConfiguration.class);

    assertThat(this.getContext().getBean(GraphQLSchema.class)).isNotNull();
  }

  @Test
  void schemaWithInterfaceLoads() {
    load(InterfaceConfiguration.class);

    assertThat(this.getContext().getBean(GraphQLSchema.class)).isNotNull();
  }

  @Configuration
  static class BaseConfiguration {

    @Bean
    public Query query() {
      return new Query();
    }
  }

  @Configuration
  static class InterfaceConfiguration {

    @Bean
    public SchemaStringProvider schemaStringProvider() {
      ListSchemaStringProvider schemaStringProvider = new ListSchemaStringProvider();
      schemaStringProvider.add(
          "type Query {"
              + "  theInterface:Interface!"
              + "} "
              + "interface Interface {"
              + "  method:String!"
              + "}"
              + "type Implementation implements Interface {"
              + "  method:String!"
              + "}");
      return schemaStringProvider;
    }

    @Bean
    public Query query() {
      return new Query();
    }

    @Bean
    public SchemaParserDictionary schemaParserDictionary() {
      return new SchemaParserDictionary().add(Implementation.class);
    }

    interface Interface {

      String method();
    }

    class Query implements GraphQLQueryResolver {

      Interface theInterface() {
        return new Implementation();
      }

      Implementation theImplementation() {
        return new Implementation();
      }
    }

    class Implementation implements Interface {

      public String method() {
        return "method";
      }
    }
  }
}
