package com.graphql.spring.boot.test.assertions;

import com.graphql.spring.boot.test.GraphQLResponse;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.assertj.core.api.FactoryBasedNavigableListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertFactory;

@EqualsAndHashCode(callSuper = true)
public class GraphQLListAssert<T>
    extends FactoryBasedNavigableListAssert<
        GraphQLListAssert<T>, List<? extends T>, T, ObjectAssert<T>>
    implements GraphQLResponseAssertion {

  private final GraphQLResponse graphQlResponse;

  public GraphQLListAssert(final GraphQLResponse graphQLResponse, final List<T> actual) {
    super(actual, GraphQLListAssert.class, new ObjectAssertFactory<>());
    this.graphQlResponse = graphQLResponse;
  }

  @Override
  public GraphQLResponse and() {
    return graphQlResponse;
  }
}
