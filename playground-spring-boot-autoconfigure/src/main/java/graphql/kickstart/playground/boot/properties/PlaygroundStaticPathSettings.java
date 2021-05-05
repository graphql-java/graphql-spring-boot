package graphql.kickstart.playground.boot.properties;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaygroundStaticPathSettings {

  @NotBlank private String base = "/vendor/playground";
}
