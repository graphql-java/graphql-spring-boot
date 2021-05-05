package graphql.kickstart.spring.error;

import graphql.kickstart.execution.error.GraphQLErrorHandler;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

public class GraphQLErrorStartupListener implements ApplicationListener<ApplicationReadyEvent> {

  private final ErrorHandlerSupplier errorHandlerSupplier;
  private final boolean exceptionHandlersEnabled;

  public GraphQLErrorStartupListener(
      ErrorHandlerSupplier errorHandlerSupplier, boolean exceptionHandlersEnabled) {
    this.errorHandlerSupplier = errorHandlerSupplier;
    this.exceptionHandlersEnabled = exceptionHandlersEnabled;
  }

  @Override
  public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
    if (!errorHandlerSupplier.isPresent()) {
      ConfigurableApplicationContext context = event.getApplicationContext();
      GraphQLErrorHandler errorHandler =
          new GraphQLErrorHandlerFactory().create(context, exceptionHandlersEnabled);
      context
          .getBeanFactory()
          .registerSingleton(errorHandler.getClass().getCanonicalName(), errorHandler);
      errorHandlerSupplier.setErrorHandler(errorHandler);
    }
  }
}
