package graphql.kickstart.altair.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("altair")
public class AltairProperties {

  private boolean enabled = true;
  private Endpoint endpoint = new Endpoint();
  private Cdn cdn = new Cdn();
  private String pageTitle = "Altair";
  private String mapping = "/altair";
  private String basePath = "";

  @Data
  static class Endpoint {

    private String graphql = "/graphql";
    private String subscriptions = "/subscriptions";
  }

  @Data
  static class Cdn {

    private boolean enabled = false;
    private String version = "2.4.11";
  }
}
