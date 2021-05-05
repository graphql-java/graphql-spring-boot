package com.graphql.spring.boot.test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testing getRemainingResponses")
class GraphQLTestSubscriptionGetRemainingResponsesTest extends GraphQLTestSubscriptionTestBase {

  @Test
  @DisplayName("Should properly return remaining responses after the Subscription was stopped.")
  void shouldGetRemainingResponses() {
    // WHEN
    graphQLTestSubscription
        .start(TIMER_SUBSCRIPTION_RESOURCE)
        .awaitAndGetNextResponse(TIMEOUT, false);
    await().atMost(TIMEOUT, MILLISECONDS);
    graphQLTestSubscription.stop();
    // THEN
    assertThatSubscriptionWasStopped();
    assertThat(graphQLTestSubscription.getRemainingResponses())
        .extracting(graphQLResponse -> graphQLResponse.get(DATA_TIMER_FIELD, Long.class))
        .containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
  }

  @Test
  @DisplayName("Should raise assertion error if called before the subscription was stopped.")
  void shouldRaiseAssertionErrorIfCalledBeforeSubscriptionIsStopped() {
    // WHEN - THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.getRemainingResponses());
  }
}
