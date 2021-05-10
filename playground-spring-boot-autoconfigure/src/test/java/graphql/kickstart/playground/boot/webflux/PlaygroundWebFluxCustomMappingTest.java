package graphql.kickstart.playground.boot.webflux;

import graphql.kickstart.playground.boot.PlaygroundTestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = PlaygroundWebFluxTestConfig.class)
@AutoConfigureWebTestClient
@TestPropertySource("classpath:application-playground-mapping-test.properties")
public class PlaygroundWebFluxCustomMappingTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  public void shouldUseTheConfiguredRequestMapping() {
    webTestClient
        .get()
        .uri(PlaygroundTestHelper.CUSTOM_MAPPING)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith(MediaType.TEXT_HTML);
  }
}
