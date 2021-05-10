package graphql.kickstart.spring.error;

import static org.junit.jupiter.api.Assertions.assertEquals;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorException;
import graphql.language.SourceLocation;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class GraphQLErrorFromExceptionHandlerTest {
  @Test
  void allows_errors_with_null_path() {
    GraphQLErrorFromExceptionHandler sut = new GraphQLErrorFromExceptionHandler(new ArrayList<>());

    List<GraphQLError> errors = new ArrayList<>();
    errors.add(
        GraphqlErrorException.newErrorException()
            .message("Error without a path")
            .sourceLocation(new SourceLocation(0, 0))
            .build());
    errors.add(
        GraphqlErrorException.newErrorException()
            .message("Error with path")
            .sourceLocation(new SourceLocation(0, 0))
            .errorClassification(ErrorType.ValidationError)
            .path(new ArrayList<>())
            .build());

    List<GraphQLError> processedErrors = sut.filterGraphQLErrors(errors);

    for (int i = 0; i < errors.size(); i++) {
      GraphQLError error = errors.get(i);
      GraphQLError processedError = processedErrors.get(i);
      assertEquals(error.getMessage(), processedError.getMessage());
      assertEquals(error.getErrorType(), processedError.getErrorType());
      assertEquals(error.getLocations(), processedError.getLocations());
      assertEquals(error.getPath(), processedError.getPath());
    }
  }
}
