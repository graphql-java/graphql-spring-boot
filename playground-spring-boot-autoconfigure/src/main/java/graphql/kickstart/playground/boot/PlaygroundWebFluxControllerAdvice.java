package graphql.kickstart.playground.boot;

import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@NoArgsConstructor
@ControllerAdvice
@ConditionalOnClass({CsrfToken.class, CsrfRequestDataValueProcessor.class})
@ConditionalOnBean(CsrfRequestDataValueProcessor.class)
public class PlaygroundWebFluxControllerAdvice {

  @ModelAttribute(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME)
  public Mono<CsrfToken> getCsrfToken(final ServerWebExchange exchange) {
    return exchange.getAttributeOrDefault(CsrfToken.class.getName(), Mono.empty());
  }
}
