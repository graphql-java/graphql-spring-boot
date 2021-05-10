package graphql.kickstart.playground.boot;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;

@Controller
@RequiredArgsConstructor
public class PlaygroundController {

  private static final String CDN_ROOT = "https://cdn.jsdelivr.net/npm/graphql-playground-react";
  private static final String CSS_PATH = "static/css/index.css";
  private static final String FAVICON_PATH = "favicon.png";
  private static final String SCRIPT_PATH = "static/js/middleware.js";
  private static final String LOGO_PATH = "logo.png";

  private static final String CSS_URL_ATTRIBUTE_NAME = "cssUrl";
  private static final String FAVICON_URL_ATTRIBUTE_NAME = "faviconUrl";
  private static final String SCRIPT_URL_ATTRIBUTE_NAME = "scriptUrl";
  private static final String LOGO_URL_ATTRIBUTE_NAME = "logoUrl";
  private static final String _CSRF = "_csrf";

  private final PlaygroundPropertiesConfiguration propertiesConfiguration;

  private final ObjectMapper objectMapper;

  @GetMapping("${graphql.playground.mapping:/playground}")
  public String playground(
      final Model model, final @RequestAttribute(value = _CSRF, required = false) Object csrf) {
    if (propertiesConfiguration.getPlayground().getCdn().isEnabled()) {
      setCdnUrls(model);
    } else {
      setLocalAssetUrls(model);
    }
    model.addAttribute("pageTitle", propertiesConfiguration.getPlayground().getPageTitle());
    model.addAttribute(
        "properties", objectMapper.valueToTree(propertiesConfiguration.getPlayground()));
    if (nonNull(csrf)) {
      model.addAttribute(_CSRF, csrf);
    }
    return "playground";
  }

  private String getCdnUrl(final String assetPath) {
    return String.format(
        "%s@%s/build/%s",
        CDN_ROOT, propertiesConfiguration.getPlayground().getCdn().getVersion(), assetPath);
  }

  private String getLocalUrl(final String assetPath) {
    return Paths.get(propertiesConfiguration.getPlayground().getStaticPath().getBase(), assetPath)
        .toString()
        .replace('\\', '/');
  }

  private void setCdnUrls(final Model model) {
    model.addAttribute(CSS_URL_ATTRIBUTE_NAME, getCdnUrl(CSS_PATH));
    model.addAttribute(FAVICON_URL_ATTRIBUTE_NAME, getCdnUrl(FAVICON_PATH));
    model.addAttribute(SCRIPT_URL_ATTRIBUTE_NAME, getCdnUrl(SCRIPT_PATH));
    model.addAttribute(LOGO_URL_ATTRIBUTE_NAME, getCdnUrl(LOGO_PATH));
  }

  private void setLocalAssetUrls(final Model model) {
    model.addAttribute(CSS_URL_ATTRIBUTE_NAME, getLocalUrl(CSS_PATH));
    model.addAttribute(FAVICON_URL_ATTRIBUTE_NAME, getLocalUrl(FAVICON_PATH));
    model.addAttribute(SCRIPT_URL_ATTRIBUTE_NAME, getLocalUrl(SCRIPT_PATH));
    model.addAttribute(LOGO_URL_ATTRIBUTE_NAME, getLocalUrl(LOGO_PATH));
  }
}
