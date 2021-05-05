package graphql.kickstart.playground.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PlaygroundTestConfig.class)
@AutoConfigureMockMvc
class PlaygroundEnabledTest {

  private static final String DEFAULT_CSS_PATH = "/vendor/playground/static/css/index.css";
  private static final String DEFAULT_SCRIPT_PATH = "/vendor/playground/static/js/middleware.js";
  private static final String DEFAULT_FAVICON_PATH = "/vendor/playground/favicon.png";
  private static final String DEFAULT_LOGO_PATH = "/vendor/playground/logo.png";
  private static final String DEFAULT_TITLE = "Playground";

  @Autowired private ApplicationContext applicationContext;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void playgroundLoads() {
    assertThat(applicationContext.getBean(PlaygroundController.class)).isNotNull();
  }

  @Test
  void playgroundShouldBeAvailableAtDefaultEndpoint() throws Exception {
    final MvcResult mvcResult =
        mockMvc
            .perform(get(PlaygroundTestHelper.DEFAULT_PLAYGROUND_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string(not(is(emptyString()))))
            .andExpect(model().attribute(PlaygroundTestHelper.PAGE_TITLE_FIELD_NAME, DEFAULT_TITLE))
            .andExpect(model().attribute(PlaygroundTestHelper.CSS_URL_FIELD_NAME, DEFAULT_CSS_PATH))
            .andExpect(
                model().attribute(PlaygroundTestHelper.SCRIPT_URL_FIELD_NAME, DEFAULT_SCRIPT_PATH))
            .andExpect(
                model()
                    .attribute(PlaygroundTestHelper.FAVICON_URL_FIELD_NAME, DEFAULT_FAVICON_PATH))
            .andExpect(
                model().attribute(PlaygroundTestHelper.LOGO_URL_FIELD_NAME, DEFAULT_LOGO_PATH))
            .andReturn();

    final Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
    PlaygroundTestHelper.assertTitle(document, DEFAULT_TITLE);
    PlaygroundTestHelper.assertStaticResources(
        document, DEFAULT_CSS_PATH, DEFAULT_SCRIPT_PATH, DEFAULT_FAVICON_PATH, DEFAULT_LOGO_PATH);
  }

  @Test
  void defaultCssShouldBeAvailable() throws Exception {
    mockMvc
        .perform(get(DEFAULT_CSS_PATH))
        .andExpect(status().isOk())
        .andExpect(content().string(not(is(emptyString()))))
        .andExpect(content().contentTypeCompatibleWith("text/css"));
  }

  @Test
  void defaultScriptShouldBeAvailable() throws Exception {
    mockMvc
        .perform(get(DEFAULT_SCRIPT_PATH))
        .andExpect(status().isOk())
        .andExpect(content().string(not(is(emptyString()))))
        .andExpect(content().contentTypeCompatibleWith("application/javascript"));
  }

  @Test
  void defaultFaviconShouldBeAvailable() throws Exception {
    mockMvc
        .perform(get(DEFAULT_FAVICON_PATH))
        .andExpect(status().isOk())
        .andExpect(content().string(not(is(emptyString()))))
        .andExpect(content().contentTypeCompatibleWith("image/png"));
  }

  @Test
  void defaultLogoShouldBeAvailable() throws Exception {
    mockMvc
        .perform(get(DEFAULT_LOGO_PATH))
        .andExpect(status().isOk())
        .andExpect(content().string(not(is(emptyString()))))
        .andExpect(content().contentTypeCompatibleWith("image/png"));
  }
}
