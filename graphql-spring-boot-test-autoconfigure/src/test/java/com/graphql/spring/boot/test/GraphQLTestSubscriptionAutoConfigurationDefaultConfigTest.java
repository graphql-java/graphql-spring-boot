package com.graphql.spring.boot.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName(
    "Testing the auto-configuration of the GraphQLTestSubscription bean / default settings.")
class GraphQLTestSubscriptionAutoConfigurationDefaultConfigTest
    extends GraphQLTestAutoConfigurationTestBase {

  @Test
  @DisplayName("Should provide a GraphQLTestTemplate bean.")
  void shouldProvideGraphQLTestSubscriptionBean() {
    assertThatTestSubscriptionWorksCorrectly();
  }
}
