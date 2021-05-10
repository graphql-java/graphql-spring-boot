package graphql.kickstart.voyager.boot;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestParam;

/** @author Guilherme Blanco */
@RequiredArgsConstructor
public class VoyagerIndexHtmlTemplate {

  private static final String CDNJS_CLOUDFLARE_COM_AJAX_LIBS = "//cdnjs.cloudflare.com/ajax/libs/";
  private static final String CDN_JSDELIVR_NET_NPM = "//cdn.jsdelivr.net/npm/";
  private static final String VOYAGER = "graphql-voyager";
  private static final String FAVICON_APIS_GURU =
      "//apis.guru/graphql-voyager/icons/favicon-16x16.png";

  private final VoyagerPropertiesConfiguration voyagerConfiguration;

  public String fillIndexTemplate(String contextPath, Map<String, String> params)
      throws IOException {
    String template =
        StreamUtils.copyToString(
            new ClassPathResource("voyager.html").getInputStream(), Charset.defaultCharset());

    String basePath = voyagerConfiguration.getBasePath();
    String voyagerCdnVersion = voyagerConfiguration.getCdn().getVersion();

    Map<String, String> replacements = new HashMap<>();
    replacements.put("graphqlEndpoint", constructGraphQlEndpoint(contextPath, params));
    replacements.put("pageTitle", voyagerConfiguration.getPageTitle());
    replacements.put("pageFavicon", getResourceUrl(basePath, "favicon.ico", FAVICON_APIS_GURU));
    replacements.put(
        "es6PromiseJsUrl",
        getResourceUrl(
            basePath,
            "es6-promise.auto.min.js",
            joinCdnjsPath("es6-promise", "4.1.1", "es6-promise.auto.min.js")));
    replacements.put(
        "fetchJsUrl",
        getResourceUrl(basePath, "fetch.min.js", joinCdnjsPath("fetch", "2.0.4", "fetch.min.js")));
    replacements.put(
        "reactJsUrl",
        getResourceUrl(
            basePath,
            "react.min.js",
            joinCdnjsPath("react", "16.8.3", "umd/react.production.min.js")));
    replacements.put(
        "reactDomJsUrl",
        getResourceUrl(
            basePath,
            "react-dom.min.js",
            joinCdnjsPath("react-dom", "16.8.3", "umd/react-dom.production.min.js")));
    replacements.put(
        "voyagerCssUrl",
        getResourceUrl(
            basePath, "voyager.css", joinJsDelivrPath(voyagerCdnVersion, "dist/voyager.css")));
    replacements.put(
        "voyagerJsUrl",
        getResourceUrl(
            basePath,
            "voyager.min.js",
            joinJsDelivrPath(voyagerCdnVersion, "dist/voyager.min.js")));
    replacements.put(
        "voyagerWorkerJsUrl",
        getResourceUrl(
            basePath,
            "voyager.worker.js",
            joinJsDelivrPath(voyagerCdnVersion, "dist/voyager.worker.min.js")));
    replacements.put("contextPath", contextPath);
    replacements.put(
        "voyagerDisplayOptionsSkipRelay",
        Boolean.toString(voyagerConfiguration.getDisplayOptions().isSkipRelay()));
    replacements.put(
        "voyagerDisplayOptionsSkipDeprecated",
        Boolean.toString(voyagerConfiguration.getDisplayOptions().isSkipDeprecated()));
    replacements.put(
        "voyagerDisplayOptionsRootType", voyagerConfiguration.getDisplayOptions().getRootType());
    replacements.put(
        "voyagerDisplayOptionsSortByAlphabet",
        Boolean.toString(voyagerConfiguration.getDisplayOptions().isSortByAlphabet()));
    replacements.put(
        "voyagerDisplayOptionsShowLeafFields",
        Boolean.toString(voyagerConfiguration.getDisplayOptions().isShowLeafFields()));
    replacements.put(
        "voyagerDisplayOptionsHideRoot",
        Boolean.toString(voyagerConfiguration.getDisplayOptions().isHideRoot()));
    replacements.put("voyagerHideDocs", Boolean.toString(voyagerConfiguration.isHideDocs()));
    replacements.put(
        "voyagerHideSettings", Boolean.toString(voyagerConfiguration.isHideSettings()));

    return StringSubstitutor.replace(template, replacements);
  }

  private String constructGraphQlEndpoint(
      String contextPath, @RequestParam Map<String, String> params) {
    String endpoint = voyagerConfiguration.getEndpoint();
    for (Map.Entry<String, String> param : params.entrySet()) {
      endpoint = endpoint.replaceAll("\\{" + param.getKey() + "}", param.getValue());
    }
    if (StringUtils.isNotBlank(contextPath) && !endpoint.startsWith(contextPath)) {
      return contextPath + endpoint;
    }
    return endpoint;
  }

  private String getResourceUrl(String staticBasePath, String staticFileName, String cdnUrl) {
    if (voyagerConfiguration.getCdn().isEnabled() && StringUtils.isNotBlank(cdnUrl)) {
      return cdnUrl;
    }
    return joinStaticPath(staticBasePath, staticFileName);
  }

  private String joinStaticPath(String staticBasePath, String staticFileName) {
    return staticBasePath + "vendor/voyager/" + staticFileName;
  }

  private String joinCdnjsPath(String library, String cdnVersion, String cdnFileName) {
    return CDNJS_CLOUDFLARE_COM_AJAX_LIBS + library + "/" + cdnVersion + "/" + cdnFileName;
  }

  private String joinJsDelivrPath(String cdnVersion, String cdnFileName) {
    return CDN_JSDELIVR_NET_NPM
        + VoyagerIndexHtmlTemplate.VOYAGER
        + "@"
        + cdnVersion
        + "/"
        + cdnFileName;
  }
}
