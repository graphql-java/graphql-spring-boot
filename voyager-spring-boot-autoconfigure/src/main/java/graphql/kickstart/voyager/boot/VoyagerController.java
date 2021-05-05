package graphql.kickstart.voyager.boot;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Max David Günther */
@Controller
public class VoyagerController {

  @Autowired private VoyagerIndexHtmlTemplate indexTemplate;

  @GetMapping(value = "${voyager.mapping:/voyager}")
  public ResponseEntity<String> voyager(
      HttpServletRequest request, @PathVariable Map<String, String> params) throws IOException {
    String contextPath = request.getContextPath();
    String indexHtmlContent = indexTemplate.fillIndexTemplate(contextPath, params);
    return ResponseEntity.ok()
        .contentType(MediaType.valueOf("text/html; charset=UTF-8"))
        .body(indexHtmlContent);
  }
}
