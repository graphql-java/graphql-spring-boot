package com.oembedler.moon.graphiql.boot;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public void graphiql(HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");

        String template = StreamUtils.copyToString(new ClassPathResource("graphiql.html").getInputStream(), Charset.defaultCharset());
        Map<String, String> replacements = new HashMap<>();
        replacements.put("graphqlEndpoint", graphqlEndpoint);
        replacements.put("pageTitle", pageTitle);

        response.getOutputStream().write(StrSubstitutor.replace(template, replacements).getBytes(Charset.defaultCharset()));
    }
}
