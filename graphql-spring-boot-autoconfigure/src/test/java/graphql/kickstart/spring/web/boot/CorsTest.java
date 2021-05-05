package graphql.kickstart.spring.web.boot;

import static graphql.Scalars.GraphQLString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ImportAutoConfiguration({JacksonAutoConfiguration.class, GraphQLWebAutoConfiguration.class})
@SpringBootTest(
    properties = {
      "debug=true",
      "graphql.servlet.mapping=/graphql",
      "graphql.servlet.cors.allowed-origins=https://trusted.com"
    })
class CorsTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void evilDomain_shouldNotBeAllowed() throws Exception {
    ResultActions resultActions = performCorsPreflight("https://evil.com");
    resultActions
        .andExpect(status().isForbidden())
        .andExpect(content().string("Invalid CORS request"));
  }

  private ResultActions performCorsPreflight(String origin) throws Exception {
    return mockMvc.perform(
        options("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Access-Control-Request-Method", "POST")
            .header("Origin", origin));
  }

  @Test
  void trustedDomain_shouldBeAllowed() throws Exception {
    ResultActions resultActions = performCorsPreflight("https://trusted.com");
    resultActions
        .andExpect(status().isOk())
        .andExpect(header().string("Access-Control-Allow-Origin", "https://trusted.com"))
        .andExpect(header().string("Access-Control-Allow-Methods", "GET,HEAD,POST"));
  }

  @TestConfiguration
  static class MyTestConfiguration {

    @Bean
    public GraphQLSchema graphQLSchema() {
      return GraphQLSchema.newSchema()
          .query(
              GraphQLObjectType.newObject()
                  .name("Query")
                  .field(
                      GraphQLFieldDefinition.newFieldDefinition()
                          .name("echo")
                          .type(GraphQLString)
                          .build())
                  .build())
          .build();
    }
  }
}
