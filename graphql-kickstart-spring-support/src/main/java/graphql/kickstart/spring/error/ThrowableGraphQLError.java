package graphql.kickstart.spring.error;

import graphql.kickstart.execution.error.GenericGraphQLError;
import java.util.Objects;

public class ThrowableGraphQLError extends GenericGraphQLError {

  private final Throwable throwable;

  public ThrowableGraphQLError(Throwable throwable) {
    this(throwable, throwable.getMessage());
  }

  public ThrowableGraphQLError(Throwable throwable, String message) {
    super(message);

    this.throwable = throwable;
  }

  public String getType() {
    return throwable.getClass().getSimpleName();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ThrowableGraphQLError)) {
      return false;
    }
    ThrowableGraphQLError that = (ThrowableGraphQLError) o;
    return Objects.equals(throwable, that.throwable)
        && Objects.equals(getMessage(), that.getMessage());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(throwable, getMessage());
  }
}
