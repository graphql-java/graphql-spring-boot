package graphql.kickstart.playground.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PlaygroundTestConfig.class)
@AutoConfigureMockMvc
class PlaygroundCSRFTest {

  private static final String CSRF_ATTRIBUTE_NAME = "_csrf";

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldLoadCSRFData() throws Exception {
    final MvcResult mvcResult =
        mockMvc
            .perform(get(PlaygroundTestHelper.DEFAULT_PLAYGROUND_ENDPOINT))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists(CSRF_ATTRIBUTE_NAME))
            .andReturn();

    assertThat(mvcResult.getModelAndView()).isNotNull();
    assertThat(mvcResult.getModelAndView().getModel()).isNotNull();
    assertThat(mvcResult.getModelAndView().getModel().get(CSRF_ATTRIBUTE_NAME))
        .isInstanceOf(CsrfToken.class);
  }
}
