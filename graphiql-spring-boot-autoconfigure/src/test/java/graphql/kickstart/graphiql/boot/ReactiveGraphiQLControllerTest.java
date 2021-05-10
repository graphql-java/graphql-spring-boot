package graphql.kickstart.graphiql.boot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest
class ReactiveGraphiQLControllerTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void shouldBeAbleToAccessGraphiQL() {
    webTestClient
        .get()
        .uri("/graphiql")
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectHeader()
        .contentType(MediaType.TEXT_HTML);
  }

  @SpringBootConfiguration
  @TestPropertySource(properties = "graphiql.enabled=true")
  @Import(GraphiQLAutoConfiguration.class)
  public static class ReactiveTestApplication {}
}
