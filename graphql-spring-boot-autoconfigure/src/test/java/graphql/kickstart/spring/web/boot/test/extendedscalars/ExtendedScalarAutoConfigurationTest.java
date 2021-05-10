package graphql.kickstart.spring.web.boot.test.extendedscalars;

import static org.assertj.core.api.Assertions.assertThat;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import java.util.AbstractMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ExtendedScalarAutoConfigurationTest.ExtendedScalarsTestApplication.class)
@TestPropertySource(properties = "graphql.extended-scalars=BigDecimal")
@DisplayName("Testing extended scalars auto configuration")
public class ExtendedScalarAutoConfigurationTest {

  @Autowired private ApplicationContext applicationContext;

  @Test
  @DisplayName(
      "The extended scalars initializer should be properly picked up by Spring auto configuration.")
  void testAutoConfiguration() {
    assertThat(applicationContext.getBeansOfType(GraphQLScalarType.class))
        .containsOnly(
            new AbstractMap.SimpleEntry<>("BigDecimal", ExtendedScalars.GraphQLBigDecimal));
  }

  @SpringBootApplication
  public static class ExtendedScalarsTestApplication {}
}
