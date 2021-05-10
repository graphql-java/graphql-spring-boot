package graphql.kickstart.spring.webflux.boot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InvalidJsonRequestTest {

  @Autowired private WebTestClient webTestClient;

  @ParameterizedTest
  @ValueSource(strings = {"\"false\":true", "not-a-json"})
  @DisplayName("Should return valid response to a request with invalid JSON or non-JSON body.")
  void testHandlingInvalidJsonRequest(String badRequestBody) throws IOException {
    // GIVEN
    final String expectedJson =
        Files.contentOf(
            new ClassPathResource("response-to-invalid-request.json").getFile(),
            StandardCharsets.UTF_8);
    // WHEN - THEN
    webTestClient
        .post()
        .uri("/graphql")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(badRequestBody)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.OK)
        .expectBody()
        .json(expectedJson);
  }
}
