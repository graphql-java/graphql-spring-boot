package graphql.kickstart.playground.boot;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@Import(PlaygroundWebFluxControllerAdvice.class)
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnProperty(
    value = "graphql.playground.enabled",
    havingValue = "true",
    matchIfMissing = true)
@RequiredArgsConstructor
public class PlaygroundWebFluxAutoConfiguration implements WebFluxConfigurer {

  private final ApplicationContext applicationContext;

  @Bean
  public RouterFunction<ServerResponse> playgroundStaticFilesRouter() {
    return RouterFunctions.resources(
        "/vendor/playground/**", new ClassPathResource("static/vendor/playground/"));
  }

  @Override
  public void configureViewResolvers(final ViewResolverRegistry registry) {
    final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("templates/");
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode(TemplateMode.HTML);
    templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
    templateResolver.setOrder(1);
    templateResolver.setCheckExistence(true);
    final SpringWebFluxTemplateEngine springWebFluxTemplateEngine =
        new SpringWebFluxTemplateEngine();
    springWebFluxTemplateEngine.setTemplateResolver(templateResolver);
    final ThymeleafReactiveViewResolver thymeleafReactiveViewResolver =
        new ThymeleafReactiveViewResolver();
    thymeleafReactiveViewResolver.setDefaultCharset(StandardCharsets.UTF_8);
    thymeleafReactiveViewResolver.setApplicationContext(applicationContext);
    thymeleafReactiveViewResolver.setTemplateEngine(springWebFluxTemplateEngine);
    thymeleafReactiveViewResolver.setViewNames(new String[] {"playground"});
    registry.viewResolver(thymeleafReactiveViewResolver);
  }
}
