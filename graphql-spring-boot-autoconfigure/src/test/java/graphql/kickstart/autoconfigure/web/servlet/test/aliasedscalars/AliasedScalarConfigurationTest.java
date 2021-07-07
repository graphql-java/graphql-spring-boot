package graphql.kickstart.autoconfigure.web.servlet.test.aliasedscalars;

import static org.assertj.core.api.Assertions.assertThat;

import graphql.schema.GraphQLScalarType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = AliasedScalarConfigurationTest.AliasedScalarsTestApplication.class)
@ActiveProfiles("aliased-scalars")
@DisplayName("Testing aliased scalars auto configuration")
class AliasedScalarConfigurationTest {

  @Autowired private ApplicationContext applicationContext;

  @Test
  @DisplayName(
      "The aliased scalars initializer should be properly picked up by Spring auto configuration.")
  void testAutoConfiguration() {
    assertThat(applicationContext.getBeansOfType(GraphQLScalarType.class))
        .containsOnlyKeys("Decimal", "Number", "Text");
  }

  @SpringBootApplication
  public static class AliasedScalarsTestApplication {}
}