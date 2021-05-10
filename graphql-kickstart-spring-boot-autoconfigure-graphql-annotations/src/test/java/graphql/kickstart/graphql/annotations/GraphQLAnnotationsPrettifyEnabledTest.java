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

@DisplayName("Testing prettify settings (enabled)")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "prettify-enabled-test"})
class GraphQLAnnotationsPrettifyEnabledTest {

  @Autowired private GraphQLTestTemplate graphQLTestTemplate;

  @Test
  @DisplayName("If enabled, it should always prettify fields.")
  void testDefaultPrettifySettings() throws IOException {
    // WHEN
    final GraphQLResponse graphQLResponse =
        graphQLTestTemplate.postForResource("queries/test-prettified-query.graphql");
    // THEN
    assertThat(graphQLResponse.get("$.data.someValue")).isEqualTo("some value");
  }
}
