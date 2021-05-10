package graphql.kickstart.playground.boot.webflux;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.playground.boot.PlaygroundController;
import graphql.kickstart.playground.boot.PlaygroundTestHelper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@SpringBootTest(classes = PlaygroundWebFluxTestConfig.class)
@AutoConfigureWebTestClient
public class PlaygroundWebFluxEnabledTest {

  private static final int MAX_IN_MEMORY_SIZE = 3 * 1024 * 1024;
  private static final String DEFAULT_CSS_PATH = "/vendor/playground/static/css/index.css";
  private static final String DEFAULT_CSS_RESOURCE =
      "/static/vendor/playground/static/css/index.css";
  private static final String DEFAULT_SCRIPT_PATH = "/vendor/playground/static/js/middleware.js";
  private static final String DEFAULT_SCRIPT_RESOURCE =
      "/static/vendor/playground/static/js/middleware.js";
  private static final String DEFAULT_FAVICON_PATH = "/vendor/playground/favicon.png";
  private static final String DEFAULT_FAVICON_RESOURCE = "/static/vendor/playground/favicon.png";
  private static final String DEFAULT_LOGO_PATH = "/vendor/playground/logo.png";
  private static final String DEFAULT_LOGO_RESOURCE = "/static/vendor/playground/logo.png";
  private static final String DEFAULT_TITLE = "Playground";

  @Autowired private ApplicationContext applicationContext;

  @Autowired private WebTestClient webTestClient;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void playgroundLoads() {
    assertThat(applicationContext.getBean(PlaygroundController.class)).isNotNull();
  }

  @Test
  public void playgroundShouldBeAvailableAtDefaultEndpoint() {
    // WHEN
    final byte[] content =
        webTestClient
            .get()
            .uri(PlaygroundTestHelper.DEFAULT_PLAYGROUND_ENDPOINT)
            .accept(MediaType.TEXT_HTML)
            .acceptCharset(StandardCharsets.UTF_8)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.TEXT_HTML)
            .expectBody()
            .returnResult()
            .getResponseBodyContent();
    // THEN
    assertThat(content).isNotNull();
    final Document document = Jsoup.parse(new String(content, StandardCharsets.UTF_8));
    PlaygroundTestHelper.assertTitle(document, DEFAULT_TITLE);
    PlaygroundTestHelper.assertStaticResources(
        document, DEFAULT_CSS_PATH, DEFAULT_SCRIPT_PATH, DEFAULT_FAVICON_PATH, DEFAULT_LOGO_PATH);
  }

  @Test
  public void defaultCssShouldBeAvailable() throws IOException {
    testStaticResource(DEFAULT_CSS_RESOURCE, DEFAULT_CSS_PATH, "text/css");
  }

  @Test
  public void defaultScriptShouldBeAvailable() throws Exception {
    testStaticResource(DEFAULT_SCRIPT_RESOURCE, DEFAULT_SCRIPT_PATH, "application/javascript");
  }

  @Test
  public void defaultFaviconShouldBeAvailable() throws Exception {
    testStaticResource(DEFAULT_FAVICON_RESOURCE, DEFAULT_FAVICON_PATH, "image/png");
  }

  @Test
  public void defaultLogoShouldBeAvailable() throws Exception {
    testStaticResource(DEFAULT_LOGO_RESOURCE, DEFAULT_LOGO_PATH, "image/png");
  }

  private void testStaticResource(
      final String resourcePath, final String urlPath, final String contentType)
      throws IOException {
    // GIVEN
    final byte[] expected =
        StreamUtils.copyToByteArray(new ClassPathResource(resourcePath).getInputStream());
    // WHEN
    final byte[] actual =
        webTestClient
            .mutateWith(
                (builder, httpHandlerBuilder, connector) ->
                    builder.exchangeStrategies(
                        ExchangeStrategies.builder()
                            .codecs(
                                configurer ->
                                    configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                            .build()))
            .get()
            .uri(urlPath)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(contentType)
            .expectBody()
            .returnResult()
            .getResponseBody();
    // THEN
    assertThat(actual).isEqualTo(expected);
  }
}
