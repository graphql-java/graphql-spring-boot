package graphql.kickstart.spring.web.boot.test.extendedscalars;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import graphql.kickstart.spring.web.boot.GraphQLExtendedScalarsInitializer;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import java.util.AbstractMap;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

@DisplayName("Testing extended scalars configuration")
public class ExtendedScalarsTest {

  @Test
  @DisplayName(
      "Should throw exception at context initialization when invalid extended scalar name is provided.")
  void shouldThrowErrorOnStartupIfExtendedScalarDoesNotExists() {
    // GIVEN
    final SpringApplication application = setupTestApplication("Long,Short,Datee,BadDecimal");
    // THEN
    assertThatExceptionOfType(ApplicationContextException.class)
        .isThrownBy(application::run)
        .withMessage(
            "Invalid extended scalar name(s) found: BadDecimal, Datee. Valid names are: BigDecimal, "
                + "BigInteger, Byte, Char, Date, DateTime, JSON, Locale, Long, NegativeFloat, NegativeInt, "
                + "NonNegativeFloat, NonNegativeInt, NonPositiveFloat, NonPositiveInt, Object, PositiveFloat, "
                + "PositiveInt, Short, Time, Url.");
  }

  @Test
  @DisplayName("Should not create any extended scalars by default.")
  void shouldNotDeclareAnyExtendedScalarsByDefault() {
    // GIVEN
    final SpringApplication application = setupTestApplication(null);
    // WHEN
    final ConfigurableApplicationContext context = application.run();
    // THEN
    assertThat(context.getBeansOfType(GraphQLScalarType.class)).isEmpty();
  }

  @Test
  @DisplayName("Should declare the configured extended scalars.")
  void shouldDeclareTheConfiguredScalars() {
    // GIVEN
    final SpringApplication application = setupTestApplication("Long,Short,BigDecimal,Date");
    // WHEN
    final ConfigurableApplicationContext context = application.run();
    // THEN
    assertThat(context.getBeansOfType(GraphQLScalarType.class))
        .containsOnly(
            new AbstractMap.SimpleEntry<>("Long", ExtendedScalars.GraphQLLong),
            new AbstractMap.SimpleEntry<>("Short", ExtendedScalars.GraphQLShort),
            new AbstractMap.SimpleEntry<>("BigDecimal", ExtendedScalars.GraphQLBigDecimal),
            new AbstractMap.SimpleEntry<>("Date", ExtendedScalars.Date));
  }

  private SpringApplication setupTestApplication(final String extendedScalarValue) {
    final StandardEnvironment standardEnvironment = new StandardEnvironment();
    standardEnvironment
        .getPropertySources()
        .addFirst(
            new MapPropertySource(
                "testProperties",
                Collections.singletonMap("graphql.extended-scalars", extendedScalarValue)));
    final SpringApplication application =
        new SpringApplication(GraphQLExtendedScalarsInitializer.class);
    application.setWebApplicationType(WebApplicationType.NONE);
    application.setEnvironment(standardEnvironment);
    return application;
  }
}
