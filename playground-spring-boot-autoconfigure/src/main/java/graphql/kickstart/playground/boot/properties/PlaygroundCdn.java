package graphql.kickstart.playground.boot.properties;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaygroundCdn {

  private boolean enabled;
  @NotBlank private String version = "latest";
}
