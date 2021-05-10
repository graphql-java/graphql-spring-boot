package graphql.kickstart.spring.webflux.boot;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RequiredArgsConstructor
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MonoAutoConfigurationTest {

  @Autowired private WebTestClient webTestClient;

  @Test
  void monoWrapper() throws JSONException {
    val result =
        webTestClient
            .post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{ \"query\": \"query { hello } \"}")
            .exchange()
            .returnResult(String.class);
    val response = result.getResponseBody().blockFirst();
    val json = new JSONObject(response);
    assertThat(json.getJSONObject("data").get("hello")).isEqualTo("Hello world");
  }
}
