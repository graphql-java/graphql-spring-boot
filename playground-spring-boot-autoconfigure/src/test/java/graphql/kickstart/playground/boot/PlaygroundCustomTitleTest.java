package graphql.kickstart.playground.boot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PlaygroundTestConfig.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-playground-custom-title.properties")
public class PlaygroundCustomTitleTest {

  @Autowired private MockMvc mockMvc;

  @Test
  public void shouldUseTheCustomPageTitle() throws Exception {
    final MvcResult mvcResult =
        mockMvc
            .perform(get(PlaygroundTestHelper.DEFAULT_PLAYGROUND_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(
                model()
                    .attribute(
                        PlaygroundTestHelper.PAGE_TITLE_FIELD_NAME,
                        PlaygroundTestHelper.CUSTOM_TITLE))
            .andReturn();

    final Document document = Jsoup.parse(mvcResult.getResponse().getContentAsString());
    PlaygroundTestHelper.assertTitle(document, PlaygroundTestHelper.CUSTOM_TITLE);
  }
}
