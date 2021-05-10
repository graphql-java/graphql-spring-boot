package com.graphql.spring.boot.test.assertions;

import static com.graphql.spring.boot.test.helper.GraphQLTestConstantsHelper.ERRORS_PATH;
import static java.util.Objects.nonNull;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestError;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.assertj.core.api.AbstractIntegerAssert;

@EqualsAndHashCode(callSuper = true)
public class NumberOfErrorsAssertion extends AbstractIntegerAssert<NumberOfErrorsAssertion>
    implements GraphQLResponseAssertion {

  private final GraphQLResponse graphQLResponse;

  public NumberOfErrorsAssertion(final GraphQLResponse response) {
    super(getNumberOfErrors(response), NumberOfErrorsAssertion.class);
    graphQLResponse = response;
  }

  private static Integer getNumberOfErrors(final GraphQLResponse response) {
    int numErrors = 0;
    try {
      final List<GraphQLTestError> errorList =
          response.getList(ERRORS_PATH, GraphQLTestError.class);
      if (nonNull(errorList)) {
        numErrors = errorList.size();
      }
    } catch (PathNotFoundException e) {
      // do nothing, number of errors is zero
    }
    return numErrors;
  }

  @Override
  public GraphQLResponse and() {
    return graphQLResponse;
  }
}
