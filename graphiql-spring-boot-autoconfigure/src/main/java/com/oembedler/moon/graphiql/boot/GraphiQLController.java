package com.oembedler.moon.graphiql.boot;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Andrew Potter
 */
@Controller
public class GraphiQLController {
    @RequestMapping(value = "${graphiql.mapping:/graphiql}", produces = MediaType.TEXT_HTML_VALUE)
    public void graphiql(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        StreamUtils.copy(new ClassPathResource("graphiql.html").getInputStream(), response.getOutputStream());
    }
}
