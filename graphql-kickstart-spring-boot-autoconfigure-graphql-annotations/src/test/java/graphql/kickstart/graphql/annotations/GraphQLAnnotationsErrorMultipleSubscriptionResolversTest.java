package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.graphql.annotations.exceptions.MultipleSubscriptionResolversException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextException;

@DisplayName("Test exception if multiple subscription resolvers are defined.")
class GraphQLAnnotationsErrorMultipleSubscriptionResolversTest {

  @Test
  @DisplayName(
      "Assert that MultipleSubscriptionResolversException is thrown when multiple subscription resolvers are found.")
  void testMultipleSubscriptionResolversExceptionIsThrown() {
    // GIVEN
    final SpringApplication app = new SpringApplication(TestApplication.class);
    app.setAdditionalProfiles("test", "test-multiple-subscription-resolvers-exception");
    // WHEN - THEN
    assertThatExceptionOfType(ApplicationContextException.class)
        .isThrownBy(app::run)
        .withRootCauseExactlyInstanceOf(MultipleSubscriptionResolversException.class);
  }
}
