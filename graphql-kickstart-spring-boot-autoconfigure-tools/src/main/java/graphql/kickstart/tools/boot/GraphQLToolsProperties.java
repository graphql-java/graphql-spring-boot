package graphql.kickstart.tools.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "graphql.tools")
class GraphQLToolsProperties {

  private String schemaLocationPattern = "**/*.graphqls";
  /**
   * Enable or disable the introspection query. Disabling it puts your server in contravention of
   * the GraphQL specification and expectations of most clients, so use this option with caution
   */
  private boolean introspectionEnabled = true;

  private boolean useDefaultObjectmapper = true;
}
