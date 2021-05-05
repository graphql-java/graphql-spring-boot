package graphql.kickstart.voyager.boot.properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoyagerDisplayOptions {

  private boolean skipRelay = true;
  private boolean skipDeprecated = true;
  private String rootType = "Query";
  private boolean sortByAlphabet = false;
  private boolean showLeafFields = true;
  private boolean hideRoot = false;
}
