package graphql.kickstart.spring.web.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("graphql.servlet.subscriptions.websocket")
class GraphQLSubscriptionWebsocketProperties {

  private String path = "/subscriptions";
}
