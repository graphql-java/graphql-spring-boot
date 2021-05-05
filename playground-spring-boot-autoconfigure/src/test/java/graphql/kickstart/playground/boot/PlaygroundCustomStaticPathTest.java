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
@TestPropertySource("classpath:application-playground-custom-static-path.properties")
public class PlaygroundCustomStaticPathTest extends PlaygroundResourcesTestBase {

  @Test
  public void shouldLoadStaticResourcesFromCustomPath() throws Exception {
    testPlaygroundResources(
        PlaygroundTestHelper.CUSTOM_LOCAL_CSS_URL,
        PlaygroundTestHelper.CUSTOM_LOCAL_SCRIPT_URL,
        PlaygroundTestHelper.CUSTOM_LOCAL_FAVICON_URL,
        PlaygroundTestHelper.CUSTOM_LOCAL_LOGO_URL);
  }
}
