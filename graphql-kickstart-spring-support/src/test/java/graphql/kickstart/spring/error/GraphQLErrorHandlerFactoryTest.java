package graphql.kickstart.spring.error;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import graphql.GraphQLError;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ExtendWith(MockitoExtension.class)
class GraphQLErrorHandlerFactoryTest {

  @Mock private ConfigurableApplicationContext applicationContext;
  @Mock private ConfigurableListableBeanFactory beanFactory;

  private GraphQLErrorHandlerFactory errorHandlerFactory;

  @BeforeEach
  public void setup() {
    Mockito.when(applicationContext.getBeanFactory()).thenReturn(beanFactory);
    Mockito.when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[] {"Test"});
    Mockito.when(applicationContext.containsBean("Test")).thenReturn(true);
    Mockito.doReturn(TestClass.class).when(applicationContext).getType("Test");

    errorHandlerFactory = new GraphQLErrorHandlerFactory();
  }

  @Test
  void createFindsCollectionHandler() {
    GraphQLErrorHandler handler = errorHandlerFactory.create(applicationContext, true);
    assertThat(handler).isInstanceOf(GraphQLErrorFromExceptionHandler.class);
    GraphQLErrorFromExceptionHandler errorHandler = (GraphQLErrorFromExceptionHandler) handler;
    assertThat(errorHandler.getFactories())
        .as("handler.factories should not be empty")
        .isNotEmpty();
  }

  public static class TestClass {

    @ExceptionHandler(IllegalArgumentException.class)
    List<GraphQLError> handle(IllegalArgumentException e) {
      return singletonList(new ThrowableGraphQLError(e, "Illegal argument"));
    }
  }
}
