package graphql.kickstart.graphiql.boot;

import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Andrew Potter */
@Slf4j
@Controller
public class ServletGraphiQLController extends GraphiQLController {

  @Override
  @PostConstruct
  public void onceConstructed() throws IOException {
    super.onceConstructed();
  }

  @GetMapping(value = "${graphiql.mapping:/graphiql}")
  public void graphiql(
      HttpServletRequest request,
      HttpServletResponse response,
      @PathVariable Map<String, String> params)
      throws IOException {
    response.setContentType("text/html; charset=UTF-8");
    Object csrf = request.getAttribute("_csrf");
    byte[] graphiqlBytes = super.graphiql(request.getContextPath(), params, csrf);
    response.getOutputStream().write(graphiqlBytes);
  }
}
