package graphql.kickstart.playground.boot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PlaygroundTestConfig.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-playground-cdn-custom-version-test.properties")
public class PlaygroundCdnCustomVersionTest extends PlaygroundResourcesTestBase {

  @Test
  public void shouldLoadSpecifiedVersionFromCdn() throws Exception {
    testPlaygroundResources(
        PlaygroundTestHelper.CUSTOM_VERSION_CSS_CDN_PATH,
        PlaygroundTestHelper.CUSTOM_VERSION_SCRIPT_CDN_PATH,
        PlaygroundTestHelper.CUSTOM_VERSION_FAVICON_CDN_PATH,
        PlaygroundTestHelper.CUSTOM_VERSION_LOGO_CDN_PATH);
  }
}
