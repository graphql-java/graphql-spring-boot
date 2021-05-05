package graphql.kickstart.spring.error;

import graphql.kickstart.execution.error.GraphQLErrorHandler;
import java.util.function.Supplier;

public class ErrorHandlerSupplier implements Supplier<GraphQLErrorHandler> {

  private GraphQLErrorHandler errorHandler;

  public ErrorHandlerSupplier(GraphQLErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  @Override
  public GraphQLErrorHandler get() {
    return errorHandler;
  }

  public boolean isPresent() {
    return errorHandler != null;
  }

  public void setErrorHandler(GraphQLErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }
}
