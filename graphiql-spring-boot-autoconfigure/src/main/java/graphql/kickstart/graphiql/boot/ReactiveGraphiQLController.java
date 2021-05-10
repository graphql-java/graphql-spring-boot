package graphql.kickstart.graphiql.boot;

import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class ReactiveGraphiQLController extends GraphiQLController {

  private final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

  @Override
  @PostConstruct
  public void onceConstructed() throws IOException {
    super.onceConstructed();
  }

  @GetMapping(value = "${graphiql.mapping:/graphiql}")
  public Mono<Void> graphiql(
      ServerHttpRequest request,
      ServerHttpResponse response,
      @PathVariable Map<String, String> params) {
    response.getHeaders().setContentType(MediaType.TEXT_HTML);
    Object csrf = request.getQueryParams().getFirst("_csrf");
    return response.writeWith(
        Mono.just(request.getPath().contextPath().value())
            .map(contextPath -> super.graphiql(contextPath, params, csrf))
            .map(dataBufferFactory::wrap));
  }
}
