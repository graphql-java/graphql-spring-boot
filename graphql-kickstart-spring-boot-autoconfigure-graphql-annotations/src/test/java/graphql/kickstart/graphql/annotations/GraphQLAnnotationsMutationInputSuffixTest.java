package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import graphql.schema.GraphQLSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Testing input suffix")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"graphql.annotations.input-prefix=", "graphql.annotations.input-suffix=Suffix"})
@ActiveProfiles({"test", "mutation-test"})
class GraphQLAnnotationsMutationInputSuffixTest {

  @Autowired private GraphQLSchema graphQLSchema;

  @Test
  @DisplayName("Assert that input suffix is used.")
  void testInputSuffix() {
    // THEN
    assertThat(graphQLSchema.getType("TestModelSuffix")).isNotNull();
  }
}
