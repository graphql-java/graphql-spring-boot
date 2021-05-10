package graphql.kickstart.playground.boot.properties.settings;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaygroundEditorSettings {

  private PlaygroundEditorCursorShape cursorShape;
  private String fontFamily;
  private Integer fontSize;
  private PlaygroundEditorTheme theme;
  private Boolean reuseHeaders;
}
