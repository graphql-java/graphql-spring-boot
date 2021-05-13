package graphql.kickstart.spring.rsocket.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RSocketGraphQLTest {

  private final RSocketRequester rSocketRequester =
      RSocketRequester.builder()
          .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
          .websocket(URI.create("ws://localhost:7000"));

  @Test
  void query() throws JSONException {
    val result =
        rSocketRequester
            .route("graphql")
            .data("{ \"query\": \"query { hello } \"}")
            .retrieveMono(String.class);

    val response = result.block();
    val json = new JSONObject(response);
    assertThat(json.getJSONObject("data").get("hello")).isEqualTo("Hello world");
  }

  @Test
  void subscription() {
    val result =
        rSocketRequester
            .route("subscriptions")
            .data("{ \"query\": \"subscription { hello } \"}")
            .retrieveFlux(String.class);

    AtomicInteger integer = new AtomicInteger(0);
    int counter = 3;

    result
        .take(counter)
        .doOnNext(
            data -> {
              try {
                System.out.println(data);
                val json = new JSONObject(data);
                assertThat(json.getJSONObject("data").get("hello"))
                    .isEqualTo(integer.getAndIncrement());
              } catch (Exception e) {
                fail("Exception in assertion", e);
              }
            })
        .blockLast();

    assertThat(integer.get()).isEqualTo(counter);
  }
}
