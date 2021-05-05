package graphql.kickstart.spring.error;

import graphql.ErrorClassification;
import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;

public class ErrorContext {

  private final List<SourceLocation> locations;
  private final List<Object> path;
  private final Map<String, Object> extensions;
  private final ErrorClassification errorType;

  public ErrorContext(
      List<SourceLocation> locations,
      List<Object> path,
      Map<String, Object> extensions,
      ErrorClassification errorType) {
    this.locations = locations;
    this.path = path;
    this.extensions = extensions;
    this.errorType = errorType;
  }

  public List<SourceLocation> getLocations() {
    return locations;
  }

  public List<Object> getPath() {
    return path;
  }

  public Map<String, Object> getExtensions() {
    return extensions;
  }

  public ErrorClassification getErrorType() {
    return errorType;
  }
}
