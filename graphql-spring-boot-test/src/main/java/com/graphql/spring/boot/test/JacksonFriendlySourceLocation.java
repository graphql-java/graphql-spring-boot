package com.graphql.spring.boot.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.language.SourceLocation;
import org.springframework.lang.Nullable;

/** Allow deserialization of {@link graphql.language.SourceLocation}. */
public class JacksonFriendlySourceLocation extends SourceLocation {

  @JsonCreator
  public JacksonFriendlySourceLocation(
      final @JsonProperty("line") int line,
      final @JsonProperty("column") int column,
      final @Nullable @JsonProperty("sourceName") String sourceName) {
    super(line, column, sourceName);
  }
}
