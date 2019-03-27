package com.oembedler.moon.altair.boot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Moncef AOUDIA
 */
@Controller
public class AltairController {

    private static final String CDN_UNPKG = "//unpkg.com/";
    private static final String ALTAIR = "altair-static";

    @Value("${altair.endpoint.graphql:/graphql}")
    private String graphqlEndpoint;

    @Value("${altair.endpoint.subscriptions:/subscriptions}")
    private String subscriptionsEndpoint;

    @Value("${altair.static.basePath:/}")
    private String staticBasePath;

    @Value("${altair.pageTitle:Altair}")
    private String pageTitle;

    @Value("${altair.cdn.enabled:false}")
    private Boolean altairCdnEnabled;

    @Value("${altair.cdn.version:2.1.4}")
    private String altairCdnVersion;

    @Value("${altair.google-analytics.enabled:true}")
    private boolean altairGoogleAnalyticsEnabled;

    @Autowired
    private Environment environment;

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
        props = new PropsLoader(environment).load();
    }

    private void loadHeaders() throws JsonProcessingException {
        PropertyGroupReader propertyReader = new PropertyGroupReader(environment, "graphiql.headers.");
        Properties headerProperties = propertyReader.load();
        addIfAbsent(headerProperties, "Accept");
        addIfAbsent(headerProperties, "Content-Type");
        this.headers = new ObjectMapper().writeValueAsString(headerProperties);
    }


    private void addIfAbsent(Properties headerProperties, String header) {
        if (!headerProperties.containsKey(header)) {
            headerProperties.setProperty(header, MediaType.APPLICATION_JSON_VALUE);
        }
    }

    @RequestMapping(value = "${altair.mapping:/altair}")
    public void altair(HttpServletRequest request, HttpServletResponse response, @PathVariable Map<String, String> params) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        Map<String, String> replacements = getReplacements(
                constructGraphQlEndpoint(request, params),
                request.getContextPath() + subscriptionsEndpoint
        );

        String populatedTemplate = StrSubstitutor.replace(template, replacements);
        response.getOutputStream().write(populatedTemplate.getBytes(Charset.defaultCharset()));
    }

    private Map<String, String> getReplacements(String graphqlEndpoint, String subscriptionsEndpoint) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("graphqlEndpoint", graphqlEndpoint);
        replacements.put("subscriptionsEndpoint", subscriptionsEndpoint);
        replacements.put("pageTitle", pageTitle);
        replacements.put("altairGoogleAnalyticsEnabled", Boolean.toString(altairGoogleAnalyticsEnabled));
        replacements.put("pageFavicon", getResourceUrl("favicon.ico", "favicon.ico"));
        replacements.put("altairBaseUrl", getResourceUrl(String.join(staticBasePath, "/vendor/altair/"),
                joinJsUnpkgPath(ALTAIR, altairCdnVersion, "build/dist/")));
        replacements.put("altairLogoUrl", getResourceUrl("assets/img/logo_350.svg", "assets/img/logo_350.svg"));
        replacements.put("altairCssUrl", getResourceUrl("styles.css", "styles.css"));
        replacements.put("altairMainJsUrl", getResourceUrl("main.js", "main.js"));
        replacements.put("altairPolyfillsJsUrl", getResourceUrl("polyfills.js", "polyfills.js"));
        replacements.put("altairRuntimeJsUrl", getResourceUrl("runtime.js", "runtime.js"));
        replacements.put("props", props);
        replacements.put("headers", headers);
        return replacements;
    }

    private String getResourceUrl(String staticFileName, String cdnUrl) {
        if (altairCdnEnabled && StringUtils.isNotBlank(cdnUrl)) {
            return cdnUrl;
        }
        return staticFileName;
    }

    private String joinJsUnpkgPath(String library, String cdnVersion, String cdnFileName) {
        return CDN_UNPKG + library + "@" + cdnVersion + "/" + cdnFileName;
    }

    private String constructGraphQlEndpoint(HttpServletRequest request, @RequestParam Map<String, String> params) {
        String endpoint = graphqlEndpoint;
        for (Map.Entry<String, String> param : params.entrySet()) {
            endpoint = endpoint.replaceAll("\\{" + param.getKey() + "}", param.getValue());
        }
        if (StringUtils.isNotBlank(request.getContextPath()) && !endpoint.startsWith(request.getContextPath())) {
            return request.getContextPath() + endpoint;
        }
        return endpoint;
    }

}
