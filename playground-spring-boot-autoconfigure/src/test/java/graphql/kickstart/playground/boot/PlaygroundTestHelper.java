package graphql.kickstart.playground.boot;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class PlaygroundTestHelper {

  public static final String CUSTOM_LOCAL_CSS_URL = "/custom-static-path/static/css/index.css";
  public static final String CUSTOM_LOCAL_SCRIPT_URL =
      "/custom-static-path/static/js/middleware.js";
  public static final String CUSTOM_LOCAL_FAVICON_URL = "/custom-static-path/favicon.png";
  public static final String CUSTOM_LOCAL_LOGO_URL = "/custom-static-path/logo.png";

  public static final String DEFAULT_CSS_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@latest/build/static/css/index.css";
  public static final String DEFAULT_SCRIPT_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@latest/build/static/js/middleware.js";
  public static final String DEFAULT_FAVICON_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@latest/build/favicon.png";
  public static final String DEFAULT_LOGO_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@latest/build/logo.png";

  public static final String CUSTOM_VERSION_CSS_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@1.7.10/build/static/css/index.css";
  public static final String CUSTOM_VERSION_SCRIPT_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@1.7.10/build/static/js/middleware.js";
  public static final String CUSTOM_VERSION_FAVICON_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@1.7.10/build/favicon.png";
  public static final String CUSTOM_VERSION_LOGO_CDN_PATH =
      "https://cdn.jsdelivr.net/npm/graphql-playground-react@1.7.10/build/logo.png";

  public static final String CUSTOM_MAPPING = "/test-mapping";
  public static final String CUSTOM_TITLE = "My CustomTest Title";
  public static final String DEFAULT_PLAYGROUND_ENDPOINT = "/playground";
  public static final String CSS_URL_FIELD_NAME = "cssUrl";
  public static final String SCRIPT_URL_FIELD_NAME = "scriptUrl";
  public static final String FAVICON_URL_FIELD_NAME = "faviconUrl";
  public static final String LOGO_URL_FIELD_NAME = "logoUrl";
  public static final String PAGE_TITLE_FIELD_NAME = "pageTitle";

  public static void assertTitle(final Document document, final String title) {
    assertThat(document.select("head title")).extracting(Element::text).containsExactly(title);
  }

  private static void assertStylesheet(final Document document, final String cssUrl) {
    assertThat(document.select(String.format("head link[rel=stylesheet][href=%s]", cssUrl)).size())
        .isOne();
  }

  private static void assertScript(final Document document, final String scriptUrl) {
    assertThat(document.select(String.format("script[src=%s]", scriptUrl)).size()).isOne();
  }

  private static void assertFavicon(final Document document, final String faviconUrl) {
    assertThat(
            document
                .select(String.format("head link[rel=shortcut icon][href=%s]", faviconUrl))
                .size())
        .isOne();
  }

  private static void assertLoadingLogo(final Document document, final String logoUrl) {
    assertThat(document.select(String.format("#root img[src=%s]", logoUrl)).size()).isOne();
  }

  public static void assertStaticResources(
      final Document document,
      final String cssUrl,
      final String scriptUrl,
      final String faviconUrl,
      final String logoUrl) {
    assertStylesheet(document, cssUrl);
    assertScript(document, scriptUrl);
    assertFavicon(document, faviconUrl);
    assertLoadingLogo(document, logoUrl);
  }
}
