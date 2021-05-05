package graphql.kickstart.playground.boot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

class PlaygroundResourcesTestBase {

  @Autowired private MockMvc mockMvc;

  void testPlaygroundResources(
      final String cssUrl, final String scriptUrl, final String faviconUrl, final String logoUrl)
      throws Exception {
    final MvcResult mvcResult =
        mockMvc
            .perform(get(PlaygroundTestHelper.DEFAULT_PLAYGROUND_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(model().attribute(PlaygroundTestHelper.CSS_URL_FIELD_NAME, cssUrl))
            .andExpect(model().attribute(PlaygroundTestHelper.SCRIPT_URL_FIELD_NAME, scriptUrl))
            .andExpect(model().attribute(PlaygroundTestHelper.FAVICON_URL_FIELD_NAME, faviconUrl))
            .andExpect(model().attribute(PlaygroundTestHelper.LOGO_URL_FIELD_NAME, logoUrl))
            .andReturn();

    final Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
    PlaygroundTestHelper.assertStaticResources(document, cssUrl, scriptUrl, faviconUrl, logoUrl);
  }
}
