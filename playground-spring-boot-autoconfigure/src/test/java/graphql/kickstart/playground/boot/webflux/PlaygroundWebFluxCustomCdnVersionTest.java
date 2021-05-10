package graphql.kickstart.playground.boot.webflux;

import graphql.kickstart.playground.boot.PlaygroundTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = PlaygroundWebFluxTestConfig.class)
@AutoConfigureWebTestClient
@TestPropertySource("classpath:application-playground-cdn-custom-version-test.properties")
public class PlaygroundWebFluxCustomCdnVersionTest extends PlaygroundWebFluxResourcesTestBase {

  @Test
  public void shouldLoadSpecifiedVersionFromCdn() {
    testPlaygroundResources(
        PlaygroundTestHelper.CUSTOM_VERSION_CSS_CDN_PATH,
        PlaygroundTestHelper.CUSTOM_VERSION_SCRIPT_CDN_PATH,
        PlaygroundTestHelper.CUSTOM_VERSION_FAVICON_CDN_PATH,
        PlaygroundTestHelper.CUSTOM_VERSION_LOGO_CDN_PATH);
  }
}
