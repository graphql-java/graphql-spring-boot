package graphql.kickstart.playground.boot.webflux;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import graphql.kickstart.playground.boot.PlaygroundController;
import graphql.kickstart.playground.boot.PlaygroundTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
    classes = PlaygroundWebFluxTestConfig.class,
    properties = "graphql.playground.enabled=false")
@AutoConfigureWebTestClient
public class PlaygroundWebFluxDisabledTest {

  @Autowired private ApplicationContext applicationContext;

  @Autowired private WebTestClient webTestClient;

  @Test
  public void playgroundShouldNotLoadIfDisabled() {
    assertThatExceptionOfType(NoSuchBeanDefinitionException.class)
        .isThrownBy(() -> applicationContext.getBean(PlaygroundController.class));
  }

  @Test
  public void playgroundEndpointShouldNotExist() {
    webTestClient
        .get()
        .uri(PlaygroundTestHelper.DEFAULT_PLAYGROUND_ENDPOINT)
        .exchange()
        .expectStatus()
        .isNotFound();
  }
}
