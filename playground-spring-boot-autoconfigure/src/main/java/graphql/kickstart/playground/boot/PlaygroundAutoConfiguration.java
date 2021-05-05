package graphql.kickstart.playground.boot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(PlaygroundPropertiesConfiguration.class)
public class PlaygroundAutoConfiguration {

  @Bean
  @ConditionalOnProperty(
      value = "graphql.playground.enabled",
      havingValue = "true",
      matchIfMissing = true)
  public PlaygroundController playgroundController(
      final PlaygroundPropertiesConfiguration playgroundPropertiesConfiguration,
      final ObjectMapper objectMapper) {
    return new PlaygroundController(playgroundPropertiesConfiguration, objectMapper);
  }
}
