package graphql.kickstart.spring.error;

import graphql.kickstart.execution.error.GraphQLErrorHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

class GraphQLErrorStartupListenerTest {

  @Test
  void error_handler_is_not_overridden_when_present() {
    GraphQLErrorHandler expectedErrorHandler = Mockito.mock(GraphQLErrorHandler.class);
    ErrorHandlerSupplier errorHandlerSupplier = new ErrorHandlerSupplier(expectedErrorHandler);
    GraphQLErrorStartupListener graphQLErrorStartupListener =
        new GraphQLErrorStartupListener(errorHandlerSupplier, false);
    graphQLErrorStartupListener.onApplicationEvent(getApplicationReadyEvent());
    Assertions.assertThat(errorHandlerSupplier.get()).isEqualTo(expectedErrorHandler);
  }

  @Test
  void error_handler_is_set_when_not_present() {
    ErrorHandlerSupplier errorHandlerSupplier = new ErrorHandlerSupplier(null);
    GraphQLErrorStartupListener graphQLErrorStartupListener =
        new GraphQLErrorStartupListener(errorHandlerSupplier, false);
    graphQLErrorStartupListener.onApplicationEvent(getApplicationReadyEvent());
    Assertions.assertThat(errorHandlerSupplier.get()).isNotNull();
  }

  private ApplicationReadyEvent getApplicationReadyEvent() {
    AnnotationConfigWebApplicationContext annotationConfigWebApplicationContext =
        new AnnotationConfigWebApplicationContext();
    annotationConfigWebApplicationContext.refresh();
    return new ApplicationReadyEvent(
        new SpringApplication(), new String[0], annotationConfigWebApplicationContext);
  }
}
