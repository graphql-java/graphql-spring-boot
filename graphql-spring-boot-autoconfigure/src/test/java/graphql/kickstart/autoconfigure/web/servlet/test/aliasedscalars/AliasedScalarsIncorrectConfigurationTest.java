package graphql.kickstart.autoconfigure.web.servlet.test.aliasedscalars;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.autoconfigure.scalars.GraphQLExtendedScalarsInitializer;
import graphql.schema.GraphQLScalarType;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

@DisplayName("Testing aliased scalars configuration")
class AliasedScalarsIncorrectConfigurationTest {

  @Test
  @DisplayName(
      "Should throw exception at context initialization when a non-built in scalar was aliased.")
  void shouldThrowErrorOnStartupIfScalarDoesNotExists() {
    // GIVEN
    final SpringApplication application = setupTestApplication(Collections.singletonMap(
        "graphql.aliased-scalars.BugDecimal",
        "Number"));
    // THEN
    assertThatExceptionOfType(ApplicationContextException.class)
        .isThrownBy(application::run)
        .withMessage(
            "Scalar(s) 'BugDecimal' cannot be aliased."
                + " Only the following scalars can be aliased by configuration: BigDecimal,"
                + " BigInteger, Boolean, Byte, Char, Date, DateTime, Float, ID, Int, JSON,"
                + " Locale, Long, NegativeFloat, NegativeInt, NonNegativeFloat, NonNegativeInt,"
                + " NonPositiveFloat, NonPositiveInt, Object, PositiveFloat, PositiveInt, Short,"
                + " String, Time, Url. Note that custom scalar beans cannot be aliased this way.");
  }

  @Test
  @DisplayName("Should not create any aliased scalars by default.")
  void shouldNotDeclareAnyAliasedScalarsByDefault() {
    // GIVEN
    final SpringApplication application = setupTestApplication(Collections.emptyMap());
    // WHEN
    final ConfigurableApplicationContext context = application.run();
    // THEN
    assertThat(context.getBeansOfType(GraphQLScalarType.class)).isEmpty();
  }

  private SpringApplication setupTestApplication(final Map<String, Object> properties) {
    final StandardEnvironment standardEnvironment = new StandardEnvironment();
    standardEnvironment.getPropertySources().addFirst(new MapPropertySource("testProperties",
        properties));
    final SpringApplication application =
        new SpringApplication(GraphQLExtendedScalarsInitializer.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.setEnvironment(standardEnvironment);
    return application;
  }
}
