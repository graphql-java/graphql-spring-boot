package graphql.kickstart.playground.boot.webflux;

import graphql.kickstart.playground.boot.PlaygroundTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = PlaygroundWebFluxTestConfig.class)
@AutoConfigureWebTestClient
@TestPropertySource("classpath:application-playground-custom-static-path.properties")
public class PlaygroundWebFluxCustomStaticPathTest extends PlaygroundWebFluxResourcesTestBase {

  @Test
  public void shouldLoadStaticResourcesFromCustomPath() {
    testPlaygroundResources(
        PlaygroundTestHelper.CUSTOM_LOCAL_CSS_URL,
        PlaygroundTestHelper.CUSTOM_LOCAL_SCRIPT_URL,
        PlaygroundTestHelper.CUSTOM_LOCAL_FAVICON_URL,
        PlaygroundTestHelper.CUSTOM_LOCAL_LOGO_URL);
  }
}
