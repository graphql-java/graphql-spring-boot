package graphql.kickstart.playground.boot.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PlaygroundProperties {

  @NotBlank private String endpoint = "/graphql";

  @NotBlank private String subscriptionEndpoint = "/subscriptions";

  @NestedConfigurationProperty @JsonIgnore private PlaygroundCdn cdn = new PlaygroundCdn();

  @NestedConfigurationProperty @JsonIgnore
  private PlaygroundStaticPathSettings staticPath = new PlaygroundStaticPathSettings();

  @JsonIgnore private String pageTitle = "Playground";

  @NestedConfigurationProperty private PlaygroundSettings settings;

  private Map<String, String> headers = Collections.emptyMap();

  @NestedConfigurationProperty private List<PlaygroundTab> tabs = Collections.emptyList();
}
