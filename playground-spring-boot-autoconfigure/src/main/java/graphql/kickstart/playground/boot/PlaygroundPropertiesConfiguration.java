package graphql.kickstart.playground.boot;

import graphql.kickstart.playground.boot.properties.PlaygroundProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "graphql")
@Validated
public class PlaygroundPropertiesConfiguration {

  @NestedConfigurationProperty private PlaygroundProperties playground = new PlaygroundProperties();
}
