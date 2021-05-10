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

@DisplayName("Test type extension registration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "extend-type-test"})
class GraphQLAnnotationsExtendTypeTest {

  @Autowired private GraphQLTestTemplate graphQLTestTemplate;

  @Test
  @DisplayName("Assert that extend types are properly registered.")
  void testExtendTypesAreProperlyRegistered() throws IOException {
    // WHEN
    final GraphQLResponse actual =
        graphQLTestTemplate.postForResource("queries/test-extend-type-query.graphql");
    // THEN
    assertThat(actual.get("$.data.someValue.baseTypeField")).isEqualTo("Test value");
    assertThat(actual.get("$.data.someValue.extendTypeField")).isEqualTo("TEST VALUE");
  }
}
