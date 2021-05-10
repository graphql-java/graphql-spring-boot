package graphql.kickstart.spring.web.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("graphql.servlet.subscriptions.apollo")
class GraphQLSubscriptionApolloProperties {

  private boolean keepAliveEnabled = true;

  private int keepAliveIntervalSeconds = 15;
}
