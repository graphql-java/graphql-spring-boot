package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestSubscription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Testing subscription resolver registration.")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "subscription-test"})
class GraphQLAnnotationsSubscriptionTest {

  @Autowired private GraphQLTestSubscription graphQLTestSubscription;

  @Test
  @DisplayName("Assert that subscription resolver is properly registered.")
  void testSubscription() {
    // GIVEN
    final GraphQLResponse graphQLResponse =
        graphQLTestSubscription
            .init()
            .start("subscriptions/test-subscription.graphql")
            .awaitAndGetNextResponse(10000);
    // THEN
    assertThat(graphQLResponse.get("$.data.testSubscription")).isEqualTo("some value");
  }
}
