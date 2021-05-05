package graphql.kickstart.voyager.boot;

import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Max David Günther */
@Controller
@RequiredArgsConstructor
public class ReactiveVoyagerController {

  @Autowired private VoyagerIndexHtmlTemplate indexTemplate;

  @GetMapping(path = "${voyager.mapping:/voyager}")
  public ResponseEntity<String> voyager(@PathVariable Map<String, String> params)
      throws IOException {
    // no context path in spring-webflux
    String indexHtmlContent = indexTemplate.fillIndexTemplate("", params);
    return ResponseEntity.ok()
        .contentType(MediaType.valueOf("text/html; charset=UTF-8"))
        .body(indexHtmlContent);
  }
}
