package graphql.kickstart.playground.boot.properties.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.Min;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaygroundSchemaPollingSettings {

  private Boolean enable;
  private String endpointFilter;

  @Min(1)
  private Integer interval;
}
