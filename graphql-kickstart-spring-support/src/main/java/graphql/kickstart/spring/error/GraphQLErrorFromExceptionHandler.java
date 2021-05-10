package graphql.kickstart.spring.error;

import static java.util.Collections.singletonList;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.GraphQLException;
import graphql.GraphqlErrorBuilder;
import graphql.SerializationError;
import graphql.kickstart.execution.error.DefaultGraphQLErrorHandler;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
class GraphQLErrorFromExceptionHandler extends DefaultGraphQLErrorHandler {

  private final List<GraphQLErrorFactory> factories;

  GraphQLErrorFromExceptionHandler(List<GraphQLErrorFactory> factories) {
    this.factories = factories;
  }

  @Override
  protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
    return errors.stream()
        .map(this::transform)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private Collection<GraphQLError> transform(GraphQLError error) {
    ErrorContext errorContext =
        new ErrorContext(
            error.getLocations(), error.getPath(), error.getExtensions(), error.getErrorType());
    return extractException(error)
        .map(throwable -> transform(throwable, errorContext))
        .orElse(singletonList(error));
  }

  private Optional<Throwable> extractException(GraphQLError error) {
    if (error instanceof ExceptionWhileDataFetching) {
      return Optional.of(((ExceptionWhileDataFetching) error).getException());
    } else if (error instanceof SerializationError) {
      return Optional.of(((SerializationError) error).getException());
    } else if (error instanceof GraphQLException) {
      return Optional.of((GraphQLException) error);
    }
    return Optional.empty();
  }

  private Collection<GraphQLError> transform(Throwable throwable, ErrorContext errorContext) {
    Map<Class<? extends Throwable>, GraphQLErrorFactory> applicables = new HashMap<>();
    factories.forEach(
        factory -> factory.mostConcrete(throwable).ifPresent(t -> applicables.put(t, factory)));
    return applicables.keySet().stream()
        .min(new ThrowableComparator())
        .map(applicables::get)
        .map(factory -> factory.create(throwable, errorContext))
        .orElseGet(() -> withThrowable(throwable, errorContext));
  }

  private Collection<GraphQLError> withThrowable(Throwable throwable, ErrorContext errorContext) {
    Map<String, Object> extensions =
        Optional.ofNullable(errorContext.getExtensions()).orElseGet(HashMap::new);
    extensions.put("type", throwable.getClass().getSimpleName());
    GraphqlErrorBuilder builder = GraphqlErrorBuilder.newError().extensions(extensions);
    Optional.ofNullable(throwable.getMessage()).ifPresent(builder::message);
    Optional.ofNullable(errorContext.getErrorType()).ifPresent(builder::errorType);
    Optional.ofNullable(errorContext.getLocations()).ifPresent(builder::locations);
    Optional.ofNullable(errorContext.getPath()).ifPresent(builder::path);
    return singletonList(builder.build());
  }
}
