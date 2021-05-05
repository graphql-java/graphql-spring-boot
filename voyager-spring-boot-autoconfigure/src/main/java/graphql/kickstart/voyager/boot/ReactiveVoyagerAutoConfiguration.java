package graphql.kickstart.voyager.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/** @author Max David Günther */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(value = "voyager.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(VoyagerPropertiesConfiguration.class)
public class ReactiveVoyagerAutoConfiguration {

  @Bean
  ReactiveVoyagerController voyagerController() {
    return new ReactiveVoyagerController();
  }

  @Bean
  public RouterFunction<ServerResponse> voyagerStaticFilesRouter() {
    return RouterFunctions.resources(
        "/vendor/voyager/**", new ClassPathResource("static/vendor/voyager/"));
  }

  @Bean
  VoyagerIndexHtmlTemplate voyagerIndexHtmlTemplate(
      final VoyagerPropertiesConfiguration voyagerPropertiesConfiguration) {
    return new VoyagerIndexHtmlTemplate(voyagerPropertiesConfiguration);
  }
}
