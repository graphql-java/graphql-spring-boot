package graphql.kickstart.playground.boot.properties;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import graphql.kickstart.playground.boot.ResourceSerializer;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
public class PlaygroundTab {

  /** The GraphQL endpoint for this tab. If not set, the default GraphQL endpoint will be used. */
  private String endpoint;

  /**
   * The GraphQL query (operation) to be initially displayed on the tab. It should be a graphql
   * resource.
   */
  @JsonSerialize(using = ResourceSerializer.class)
  private Resource query;

  /** The name of the tab. */
  private String name;

  /** The query variables. It should be a JSON resource. */
  @JsonSerialize(using = ResourceSerializer.class)
  private Resource variables;

  /**
   * The list of responses to be displayed under "responses". It should be a list of JSON resources.
   */
  @JsonSerialize(contentUsing = ResourceSerializer.class)
  private List<Resource> responses;

  /** HTTP headers. Key-value pairs expected. */
  private Map<String, String> headers;
}
