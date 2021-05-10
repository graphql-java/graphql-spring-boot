package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.graphql.annotations.exceptions.MultipleMutationResolversException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextException;

@DisplayName("Test exception if multiple mutation resolvers are defined.")
class GraphQLAnnotationsMultipleMutationResolversTest {

  @Test
  @DisplayName(
      "Assert that MultipleMutationResolversException is thrown when multiple mutation resolvers are found.")
  void testMultipleMutationResolversExceptionIsThrown() {
    // GIVEN
    final SpringApplication app = new SpringApplication(TestApplication.class);
    app.setAdditionalProfiles("test", "test-multiple-mutation-resolvers-exception");
    // WHEN - THEN
    assertThatExceptionOfType(ApplicationContextException.class)
        .isThrownBy(app::run)
        .withRootCauseExactlyInstanceOf(MultipleMutationResolversException.class);
  }
}
