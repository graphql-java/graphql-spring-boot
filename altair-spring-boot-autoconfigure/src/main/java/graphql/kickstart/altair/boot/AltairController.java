package graphql.kickstart.altair.boot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.util.PropertyGroupReader;
import graphql.kickstart.util.PropsLoader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Moncef AOUDIA */
@Controller
public class AltairController {

  private static final String CDN_UNPKG = "//unpkg.com/";
  private static final String ALTAIR = "altair-static";

  @Autowired private AltairProperties altairProperties;

  @Autowired private Environment environment;

  private String template;
  private String props;
  private String headers;

  @PostConstruct
  public void onceConstructed() throws IOException {
    loadTemplate();
    loadProps();
    loadHeaders();
  }

  private void loadTemplate() throws IOException {
    try (InputStream inputStream = new ClassPathResource("altair.html").getInputStream()) {
      template = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
    }
  }

  private void loadProps() throws IOException {
    props = new PropsLoader(environment, "altair.props.resources.", "altair.props.values.").load();
  }

  private void loadHeaders() throws JsonProcessingException {
    PropertyGroupReader propertyReader = new PropertyGroupReader(environment, "altair.headers.");
    Properties headerProperties = propertyReader.load();
    this.headers = new ObjectMapper().writeValueAsString(headerProperties);
  }

  @GetMapping(value = "${altair.mapping:/altair}")
  public void altair(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable Map<String, String> params)
      throws IOException {
    response.setContentType("text/html; charset=UTF-8");

    Map<String, String> replacements =
        getReplacements(
            constructGraphQlEndpoint(request, params),
            request.getContextPath() + altairProperties.getEndpoint().getSubscriptions());

    String populatedTemplate = StringSubstitutor.replace(template, replacements);
    response.getOutputStream().write(populatedTemplate.getBytes(Charset.defaultCharset()));
  }

  private Map<String, String> getReplacements(
      String graphqlEndpoint, String subscriptionsEndpoint) {
    Map<String, String> replacements = new HashMap<>();
    replacements.put("graphqlEndpoint", graphqlEndpoint);
    replacements.put("subscriptionsEndpoint", subscriptionsEndpoint);
    replacements.put("pageTitle", altairProperties.getPageTitle());
    replacements.put("pageFavicon", getResourceUrl("favicon.ico", "favicon.ico"));
    replacements.put(
        "altairBaseUrl",
        getResourceUrl(
            StringUtils.join(altairProperties.getBasePath(), "/vendor/altair/"),
            joinJsUnpkgPath(ALTAIR, altairProperties.getCdn().getVersion(), "build/dist/")));
    replacements.put(
        "altairLogoUrl", getResourceUrl("assets/img/logo_350.svg", "assets/img/logo_350.svg"));
    replacements.put("altairCssUrl", getResourceUrl("styles.css", "styles.css"));
    replacements.put("altairMainJsUrl", getResourceUrl("main.js", "main.js"));
    replacements.put("altairPolyfillsJsUrl", getResourceUrl("polyfills.js", "polyfills.js"));
    replacements.put("altairRuntimeJsUrl", getResourceUrl("runtime.js", "runtime.js"));
    replacements.put("props", props);
    replacements.put("headers", headers);
    return replacements;
  }

  private String getResourceUrl(String staticFileName, String cdnUrl) {
    if (altairProperties.getCdn().isEnabled() && StringUtils.isNotBlank(cdnUrl)) {
      return cdnUrl;
    }
    return staticFileName;
  }

  private String joinJsUnpkgPath(String library, String cdnVersion, String cdnFileName) {
    return CDN_UNPKG + library + "@" + cdnVersion + "/" + cdnFileName;
  }

  private String constructGraphQlEndpoint(
      HttpServletRequest request, @RequestParam Map<String, String> params) {
    String endpoint = altairProperties.getEndpoint().getGraphql();
    for (Map.Entry<String, String> param : params.entrySet()) {
      endpoint = endpoint.replaceAll("\\{" + param.getKey() + "}", param.getValue());
    }
    if (StringUtils.isNotBlank(request.getContextPath())
        && !endpoint.startsWith(request.getContextPath())) {
      return request.getContextPath() + endpoint;
    }
    return endpoint;
  }
}
