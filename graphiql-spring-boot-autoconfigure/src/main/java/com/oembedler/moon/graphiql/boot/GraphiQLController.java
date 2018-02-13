package com.oembedler.moon.graphiql.boot;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrew Potter
 */
@Controller
public class GraphiQLController {

    @Value("${graphiql.endpoint:/graphql}")
    private String graphqlEndpoint;

    @Value("${graphiql.pageTitle:GraphiQL}")
    private String pageTitle;

    @RequestMapping(value = "${graphiql.mapping:/graphiql}")
    public void graphiql(HttpServletResponse response, @PathVariable Map<String, String> params) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        String template = StreamUtils.copyToString(new ClassPathResource("graphiql.html").getInputStream(), Charset.defaultCharset());
        Map<String, String> replacements = new HashMap<>();

        String endpoint = constructGraphQlEndpoint(params);

        replacements.put("graphqlEndpoint", endpoint);
        replacements.put("pageTitle", pageTitle);

        response.getOutputStream().write(StrSubstitutor.replace(template, replacements).getBytes(Charset.defaultCharset()));
    }

    private String constructGraphQlEndpoint(@RequestParam Map<String, String> params) {
        String endpoint = graphqlEndpoint;
        for (Map.Entry<String, String> param : params.entrySet()) {
            endpoint = endpoint.replaceAll("\\{" + param.getKey() + "}", param.getValue());
        }
        return endpoint;
    }
}
