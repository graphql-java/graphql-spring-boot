package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Testing custom type function registration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "custom-type-function-test"})
class GraphQLAnnotationsCustomTypeFunctionTest {

  @Autowired private GraphQLTestTemplate graphQLTestTemplate;

  @Test
  @DisplayName("Assert that custom type functions work properly.")
  void testCustomTypeFunction() throws IOException {
    // WHEN
    final GraphQLResponse actual =
        graphQLTestTemplate.postForResource("queries/test-custom-type-function.graphql");
    // THEN
    assertThat(actual.get("$.data.foo")).isEqualTo("foo");
  }
}
