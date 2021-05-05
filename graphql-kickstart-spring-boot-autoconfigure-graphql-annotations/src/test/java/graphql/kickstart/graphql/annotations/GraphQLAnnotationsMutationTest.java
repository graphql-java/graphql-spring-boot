package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Testing mutation resolver registration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "mutation-test"})
class GraphQLAnnotationsMutationTest {

  @Autowired private GraphQLTestTemplate graphQLTestTemplate;

  @Autowired private GraphQLSchema graphQLSchema;

  @Test
  @DisplayName("Assert that mutation resolver is properly registered.")
  void testMutationResolver() throws IOException {
    // WHEN
    final GraphQLResponse actual =
        graphQLTestTemplate.postForResource("mutations/test-mutation.graphql");
    // THEN
    assertThat(actual.get("$.data.performSomeOperation.testField")).isEqualTo("Test value");
  }

  @Test
  @DisplayName("Assert that library's default input prefix and suffix are used.")
  void testInputPrefix() {
    // THEN
    assertThat(graphQLSchema.getType("InputTestModel")).isNotNull();
  }
}
