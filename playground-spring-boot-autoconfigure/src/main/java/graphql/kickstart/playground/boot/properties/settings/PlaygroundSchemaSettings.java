package graphql.kickstart.playground.boot.properties.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaygroundSchemaSettings {

  private Boolean disableComments;

  @NestedConfigurationProperty
  @JsonUnwrapped(prefix = "polling.")
  private PlaygroundSchemaPollingSettings polling;
}
