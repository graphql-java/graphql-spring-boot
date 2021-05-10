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
@TestPropertySource("classpath:application-playground-cdn-test.properties")
public class PlaygroundCdnTest extends PlaygroundResourcesTestBase {

  @Test
  public void shouldLoadLatestVersionFromCdn() throws Exception {
    testPlaygroundResources(
        PlaygroundTestHelper.DEFAULT_CSS_CDN_PATH,
        PlaygroundTestHelper.DEFAULT_SCRIPT_CDN_PATH,
        PlaygroundTestHelper.DEFAULT_FAVICON_CDN_PATH,
        PlaygroundTestHelper.DEFAULT_LOGO_CDN_PATH);
  }
}
