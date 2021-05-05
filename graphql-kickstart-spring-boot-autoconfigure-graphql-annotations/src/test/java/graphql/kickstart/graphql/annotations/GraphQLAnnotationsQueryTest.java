package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Testing query resolver registration.")
@ActiveProfiles({"test", "query-test"})
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = TestApplication.class)
class GraphQLAnnotationsQueryTest {

  @Autowired private GraphQLTestTemplate graphQLTestTemplate;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("Assert that query resolver is properly detected.")
  void testHelloQuery() throws IOException {
    // GIVEN
    final ObjectNode params = objectMapper.createObjectNode();
    params.put("who", "John");
    // WHEN
    final GraphQLResponse graphQLResponse =
        graphQLTestTemplate.perform("queries/hello.graphql", params);
    // THEN
    assertThat(graphQLResponse.get("$.data.hello")).isEqualTo("Hello, John!");
    assertThat(graphQLResponse.get("$.data.helloWorld")).isEqualTo("Hello, World!");
  }
}
