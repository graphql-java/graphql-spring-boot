package com.graphql.spring.boot.test.assertions;

import com.graphql.spring.boot.test.GraphQLResponse;

/**
 * Common interface for GraphQL assertions. The main purpose of this interface is to allow chaining
 * fluent assertions for a {@link GraphQLResponse}.
 */
public interface GraphQLResponseAssertion {

  /**
   * @return the instance of {@link GraphQLResponse} for which this assertion was created. Allows
   *     chaining fluent assertions.
   */
  GraphQLResponse and();
}
