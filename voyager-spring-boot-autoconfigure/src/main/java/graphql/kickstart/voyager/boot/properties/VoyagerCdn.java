package graphql.kickstart.voyager.boot.properties;

import lombok.Data;

@Data
public class VoyagerCdn {

  private boolean enabled;
  private String version = "latest";
}
