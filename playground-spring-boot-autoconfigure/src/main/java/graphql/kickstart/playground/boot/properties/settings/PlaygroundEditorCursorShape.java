package graphql.kickstart.playground.boot.properties.settings;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum PlaygroundEditorCursorShape {
  @JsonProperty("line")
  LINE,
  @JsonProperty("block")
  BLOCK,
  @JsonProperty("underline")
  UNDERLINE
}
