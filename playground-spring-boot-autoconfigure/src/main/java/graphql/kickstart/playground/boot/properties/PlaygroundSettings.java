package graphql.kickstart.playground.boot.properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import graphql.kickstart.playground.boot.properties.settings.PlaygroundEditorSettings;
import graphql.kickstart.playground.boot.properties.settings.PlaygroundPrettierSettings;
import graphql.kickstart.playground.boot.properties.settings.PlaygroundRequestSettings;
import graphql.kickstart.playground.boot.properties.settings.PlaygroundSchemaSettings;
import graphql.kickstart.playground.boot.properties.settings.PlaygroundTracingSettings;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaygroundSettings {

  @NestedConfigurationProperty
  @JsonUnwrapped(prefix = "editor.")
  private PlaygroundEditorSettings editor;

  @NestedConfigurationProperty
  @JsonUnwrapped(prefix = "prettier.")
  private PlaygroundPrettierSettings prettier;

  @NestedConfigurationProperty
  @JsonUnwrapped(prefix = "request.")
  private PlaygroundRequestSettings request;

  @NestedConfigurationProperty
  @JsonUnwrapped(prefix = "schema.")
  private PlaygroundSchemaSettings schema;

  @NestedConfigurationProperty
  @JsonUnwrapped(prefix = "tracing.")
  private PlaygroundTracingSettings tracing;
}
