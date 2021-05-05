package graphql.kickstart.graphiql.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("graphiql")
class GraphiQLProperties {

  private Endpoint endpoint = new Endpoint();
  private CodeMirror codeMirror = new CodeMirror();
  private Props props = new Props();
  private String pageTitle = "GraphiQL";
  private String mapping = "/graphiql";
  private Subscriptions subscriptions = new Subscriptions();
  private Cdn cdn = new Cdn();
  private String basePath = "/";

  @Data
  static class Endpoint {

    private String graphql = "/graphql";
    private String subscriptions = "/subscriptions";
  }

  @Data
  static class CodeMirror {

    private String version = "5.47.0";
  }

  @Data
  static class Props {

    private GraphiQLVariables variables = new GraphiQLVariables();

    /** See https://github.com/graphql/graphiql/tree/main/packages/graphiql#props */
    @Data
    static class GraphiQLVariables {

      private String query;
      private String variables;
      private String headers;
      private String operationName;
      private String response;
      private String defaultQuery;
      private boolean defaultVariableEditorOpen;
      private boolean defaultSecondaryEditorOpen;
      private String editorTheme;
      private boolean readOnly;
      private boolean docsExplorerOpen;
      private boolean headerEditorEnabled;
      private boolean shouldPersistHeaders;
    }
  }

  @Data
  static class Cdn {

    private boolean enabled = false;
    private String version = "1.0.6";
  }

  @Data
  static class Subscriptions {

    private int timeout = 30;
    private boolean reconnect = false;
  }
}
