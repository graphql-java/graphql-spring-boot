package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles({"test", "custom-subscription-path"})
@DisplayName(
    "Testing the auto-configuration of the GraphQLTestSubscription bean / custom subscription endpoint.")
class GraphQLTestSubscriptionAutoConfigurationCustomConfigTest
    extends GraphQLTestAutoConfigurationTestBase {

  @Test
  @DisplayName("Should provide a GraphQLTestTemplate bean.")
  void shouldProvideGraphQLTestSubscriptionBean() {
    assertThatTestSubscriptionWorksCorrectly();
    assertThat(
            ReflectionTestUtils.getField(
                applicationContext.getBean(GraphQLTestSubscription.class), "subscriptionPath"))
        .as("Should use the configured subscription path.")
        .isEqualTo("/myCustomPath");
  }
}
