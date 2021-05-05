package com.graphql.spring.boot.test;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.NumberUtils;

/** An implementation of the {@link GraphQLError} interface for testing purposes. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphQLTestError implements GraphQLError {

  private String message;

  @JsonTypeInfo(defaultImpl = JacksonFriendlySourceLocation.class, use = Id.NAME)
  private List<SourceLocation> locations;

  @JsonTypeInfo(defaultImpl = ErrorType.class, use = Id.NAME)
  private transient ErrorClassification errorType;

  private transient List<Object> path;
  private transient Map<String, Object> extensions;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(
        Optional.ofNullable(errorType)
            .map(ErrorClassification::toString)
            .orElse("<Unspecified error>"));
    sb.append(": ");
    sb.append(Optional.ofNullable(message).orElse("<error message not provided>"));
    if (nonNull(locations) && !locations.isEmpty()) {
      sb.append(" at line ");
      locations.forEach(
          location ->
              sb.append(location.getLine())
                  .append(", column ")
                  .append(location.getColumn())
                  .append(" in ")
                  .append(
                      Optional.ofNullable(location.getSourceName())
                          .orElse("unnamed/unspecified source")));
    }
    if (nonNull(path) && !path.isEmpty()) {
      sb.append(". Selection path: ");
      sb.append(
          path.stream()
              .map(Object::toString)
              .map(this::toNumericIndexIfPossible)
              .collect(Collectors.joining("/"))
              .replace("/[", "["));
    }
    return sb.toString();
  }

  private String toNumericIndexIfPossible(final String s) {
    try {
      return "[" + NumberUtils.parseNumber(s, Long.class) + "]";
    } catch (IllegalArgumentException e) {
      return s;
    }
  }
}
