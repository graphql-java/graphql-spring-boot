package graphql.kickstart.graphql.annotations;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeast;

import graphql.kickstart.graphql.annotations.test.custom.annotation.processor.CustomAnnotationProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("Testing custom annotation processor configuration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "custom-annotation-processor-test"})
class GraphQLAnnotationsCustomAnnotationProcessorTest {

  @SpyBean private CustomAnnotationProcessor customAnnotationProcessor;

  @Test
  @DisplayName("Assert that the custom annotation processor is used.")
  void testCustomAnnotationProcessorIsUsed() {
    then(customAnnotationProcessor).should(atLeast(1)).getContainer();
  }
}
