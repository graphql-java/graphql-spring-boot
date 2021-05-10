package com.graphql.spring.boot.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.kickstart.execution.GraphQLObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtils {

  private static final ObjectMapper mapper = new ObjectMapper();

  public static Map<String, Object> assertNoGraphQLErrors(GraphQL gql, String query) {
    return assertNoGraphQLErrors(gql, new HashMap<>(), new Object(), query);
  }

  public static Map<String, Object> assertNoGraphQLErrors(
      GraphQL gql, Map<String, Object> args, Object context, String query) {
    ExecutionResult result = execute(gql, args, context, query);

    if (!result.getErrors().isEmpty()) {
      String errors = formatErrors(result);
      throw new AssertionError("GraphQL result contained errors!\n" + errors);
    }

    return result.getData();
  }

  private static String formatErrors(ExecutionResult result) {
    return result.getErrors().stream().map(TestUtils::toString).collect(Collectors.joining("\n"));
  }

  private static ExecutionResult execute(
      GraphQL gql, Map<String, Object> args, Object context, String query) {
    return gql.execute(
        ExecutionInput.newExecutionInput()
            .query(query)
            .context(context)
            .root(context)
            .variables(args));
  }

  public static void assertGraphQLError(
      GraphQL gql, String query, GraphQLError error, GraphQLObjectMapper objectMapper) {
    ExecutionResult result =
        objectMapper.sanitizeErrors(execute(gql, new HashMap<>(), new Object(), query));

    String expectedError = toString(error);
    if (result.getErrors().isEmpty()) {
      throw new AssertionError(
          "GraphQL result did not contain any errors!Expected: \n" + expectedError);
    }

    if (result.getErrors().stream()
        .map(TestUtils::toString)
        .noneMatch(e -> e.equals(expectedError))) {
      throw new AssertionError(
          "GraphQL result did not contain expected error!\nExpected:"
              + expectedError
              + "\nActual:"
              + formatErrors(result));
    }
  }

  private static String toString(GraphQLError error) {
    try {
      return mapper.writeValueAsString(error.toSpecification());
    } catch (JsonProcessingException e) {
      log.error("Cannot write error {} as string", error, e);
      return null;
    }
  }
}
