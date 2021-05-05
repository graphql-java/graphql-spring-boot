package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.graphql.annotations.exceptions.MultipleQueryResolversException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextException;

@DisplayName("Test exception if multiple query resolvers are defined.")
class GraphQLAnnotationsErrorMultipleQueryResolversTest {

  @Test
  @DisplayName(
      "Assert that MultipleQueryResolversException is thrown when multiple query resolvers are found.")
  void testMultipleQueryResolversExceptionIsThrown() {
    // GIVEN
    final SpringApplication app = new SpringApplication(TestApplication.class);
    app.setAdditionalProfiles("test", "test-multiple-query-resolvers-exception");
    // WHEN - THEN
    assertThatExceptionOfType(ApplicationContextException.class)
        .isThrownBy(app::run)
        .withRootCauseExactlyInstanceOf(MultipleQueryResolversException.class);
  }
}
