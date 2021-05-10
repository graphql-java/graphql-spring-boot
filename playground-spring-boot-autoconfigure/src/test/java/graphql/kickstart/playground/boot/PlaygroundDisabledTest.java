package graphql.kickstart.playground.boot;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = PlaygroundTestConfig.class,
    properties = "graphql.playground.enabled=false")
@AutoConfigureMockMvc
class PlaygroundDisabledTest {

  @Autowired private ApplicationContext applicationContext;

  @Autowired private MockMvc mockMvc;

  @Test
  void playgroundShouldNotLoadIfDisabled() {
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> applicationContext.getBean(PlaygroundController.class));
  }

  @Test
  void playgroundEndpointShouldNotExist() throws Exception {
    mockMvc
        .perform(get(PlaygroundTestHelper.DEFAULT_PLAYGROUND_ENDPOINT))
        .andExpect(status().isNotFound());
  }
}
