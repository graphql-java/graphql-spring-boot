package com.graphql.spring.boot.test.assertions;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.ERRORS_PATH;
import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.fail;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.jayway.jsonpath.PathNotFoundException;
import graphql.GraphQLError;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import org.assertj.core.api.FactoryBasedNavigableListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertFactory;

@EqualsAndHashCode(callSuper = true)
public class GraphQLErrorListAssertion
    extends FactoryBasedNavigableListAssert<
        GraphQLErrorListAssertion,
        List<? extends GraphQLError>,
        GraphQLError,
        ObjectAssert<GraphQLError>>
    implements GraphQLResponseAssertion {

  private final GraphQLResponse graphQLResponse;

  public GraphQLErrorListAssertion(final GraphQLResponse graphQLResponse) {
    super(
        getGraphQLErrors(graphQLResponse),
        GraphQLErrorListAssertion.class,
        new ObjectAssertFactory<>());
    this.graphQLResponse = graphQLResponse;
  }

  private static List<? extends GraphQLError> getGraphQLErrors(
      final GraphQLResponse graphQLResponse) {
    List<GraphQLTestError> errorList = null;
    try {
      errorList = graphQLResponse.getList(ERRORS_PATH, GraphQLTestError.class);
    } catch (PathNotFoundException e) {
      // do nothing, error list is still null
    }
    return errorList;
  }

  public GraphQLResponseAssertion hasNoErrors() {
    final List<? extends GraphQLError> graphQLErrors = getGraphQLErrors(graphQLResponse);
    if (nonNull(graphQLErrors) && !graphQLErrors.isEmpty()) {
      final String combinedMessage =
          graphQLErrors.stream()
              .map(GraphQLError::toString)
              .collect(Collectors.joining(System.lineSeparator()));
      fail(
          String.format(
              "Expected no GraphQL errors, but got %s: %s", graphQLErrors.size(), combinedMessage));
    }
    return this;
  }

  @Override
  public GraphQLResponse and() {
    return graphQLResponse;
  }
}
