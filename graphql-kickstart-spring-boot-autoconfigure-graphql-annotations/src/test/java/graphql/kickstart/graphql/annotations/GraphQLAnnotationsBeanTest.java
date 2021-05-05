package graphql.kickstart.graphql.annotations;

import static org.assertj.core.api.Assertions.assertThatCode;

import graphql.annotations.processor.GraphQLAnnotations;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "query-test"})
class GraphQLAnnotationsBeanTest {

  @Autowired private ApplicationContext applicationContext;

  @Test
  @DisplayName("Should expose the GraphQL Annotations bean.")
  void testThatGraphQLAnnotationsBeanExists() {
    assertThatCode(() -> applicationContext.getBean(GraphQLAnnotations.class))
        .doesNotThrowAnyException();
  }
}
