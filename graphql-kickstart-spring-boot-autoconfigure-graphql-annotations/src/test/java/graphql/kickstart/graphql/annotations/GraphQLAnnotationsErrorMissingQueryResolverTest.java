package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.graphql.annotations.exceptions.MissingQueryResolverException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextException;

@DisplayName("Test exception if no query resolver defined.")
class GraphQLAnnotationsErrorMissingQueryResolverTest {

  @Test
  @DisplayName(
      "Assert that MissingQueryResolverException is throw if no GraphQLQueryResolver classes are found.")
  void testMissingQueryResolverExceptionIfNoQueryResolverProvided() {
    // GIVEN
    final SpringApplication app = new SpringApplication(TestApplication.class);
    app.setAdditionalProfiles("test", "test-missing-query-resolver-exception");
    // WHEN - THEN
    assertThatExceptionOfType(ApplicationContextException.class)
        .isThrownBy(app::run)
        .withRootCauseExactlyInstanceOf(MissingQueryResolverException.class);
  }
}
