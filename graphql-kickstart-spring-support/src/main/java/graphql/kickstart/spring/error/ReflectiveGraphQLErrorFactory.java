package graphql.kickstart.spring.error;

import static java.util.Collections.singletonList;

import graphql.GraphQLError;
import graphql.kickstart.execution.error.GenericGraphQLError;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
class ReflectiveGraphQLErrorFactory implements GraphQLErrorFactory {

  private final boolean singularReturnType;
  private final boolean withErrorContext;
  private final Object object;
  private final Method method;
  private final Throwables throwables;

  ReflectiveGraphQLErrorFactory(Object object, Method method) {
    this.object = object;
    this.method = method;
    singularReturnType = GraphQLError.class.isAssignableFrom(method.getReturnType());
    withErrorContext = method.getParameterCount() == 2;
    throwables = new Throwables(method.getAnnotation(ExceptionHandler.class).value());
  }

  @Override
  public Optional<Class<? extends Throwable>> mostConcrete(Throwable t) {
    return throwables.mostConcrete(t);
  }

  @Override
  public Collection<GraphQLError> create(Throwable t, ErrorContext errorContext) {
    try {
      if (singularReturnType) {
        return singletonList((GraphQLError) invoke(t, errorContext));
      }
      //noinspection unchecked
      return (Collection<GraphQLError>) invoke(t, errorContext);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("Cannot create GraphQLError from throwable {}", t.getClass().getSimpleName(), e);
      return singletonList(new GenericGraphQLError(t.getMessage()));
    }
  }

  private Object invoke(Throwable t, ErrorContext errorContext)
      throws IllegalAccessException, InvocationTargetException {
    if (withErrorContext) {
      return method.invoke(object, t, errorContext);
    } else {
      return method.invoke(object, t);
    }
  }
}
