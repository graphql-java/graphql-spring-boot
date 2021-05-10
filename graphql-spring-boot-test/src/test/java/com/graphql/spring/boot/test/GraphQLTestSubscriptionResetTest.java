package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("Testing reset")
class GraphQLTestSubscriptionResetTest extends GraphQLTestSubscriptionTestBase {

  @Test
  @DisplayName("Should work if subscription was not yet started.")
  void shouldWorkIfSubscriptionWasNotStarted() {
    // WHEN
    final int firstId = getSubscriptionId();
    graphQLTestSubscription.reset();
    // THEN
    assertThatSubscriptionWasReset();
    assertThatNewIdWasGenerated(firstId);
  }

  @Test
  @DisplayName("Should work if subscription is still active.")
  void shouldWorkIfSubscriptionIsStillActive() {
    // GIVEN
    final int firstId = getSubscriptionId();
    graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE);
    // WHEN
    graphQLTestSubscription.reset();
    // THEN
    assertThatSubscriptionWasReset();
    assertThatNewIdWasGenerated(firstId);
  }

  @Test
  @DisplayName("Should work if subscription was stopped.")
  void shouldWorkIfSubscriptionWasAlreadyStopped() {
    // GIVEN
    final int firstId = getSubscriptionId();
    graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE).stop();
    // WHEN
    graphQLTestSubscription.reset();
    // THEN
    assertThatSubscriptionWasReset();
    assertThatNewIdWasGenerated(firstId);
  }

  @Test
  @DisplayName("Should allow starting a new subscription after reset.")
  void shouldAllowStartingNewSubscriptionAfterReset() {
    // GIVEN
    startAndAssertThatNewSubscriptionWorks();
    // WHEN
    graphQLTestSubscription.reset();
    // THEN
    startAndAssertThatNewSubscriptionWorks();
  }

  private void startAndAssertThatNewSubscriptionWorks() {
    final Integer actual =
        graphQLTestSubscription
            .start(TIMER_SUBSCRIPTION_RESOURCE)
            .awaitAndGetNextResponse(TIMEOUT)
            .get("$.data.timer", Integer.class);
    assertThat(actual).isZero();
  }

  private void assertThatSubscriptionWasReset() {
    assertThat(graphQLTestSubscription.isInitialized()).isFalse();
    assertThat(graphQLTestSubscription.isAcknowledged()).isFalse();
    assertThat(graphQLTestSubscription.isStarted()).isFalse();
    assertThat(graphQLTestSubscription.isStopped()).isFalse();
    assertThat(graphQLTestSubscription.isCompleted()).isFalse();
    assertThat(
            ((SubscriptionState)
                    ReflectionTestUtils.getField(
                        graphQLTestSubscription, GraphQLTestSubscription.class, "state"))
                .getResponses())
        .isEmpty();
    assertThat(graphQLTestSubscription.getSession()).isNull();
  }

  private void assertThatNewIdWasGenerated(int previousId) {
    assertThat(getSubscriptionId()).isEqualTo(previousId + 1);
  }

  private int getSubscriptionId() {
    return ((SubscriptionState)
            ReflectionTestUtils.getField(
                graphQLTestSubscription, GraphQLTestSubscription.class, "state"))
        .getId();
  }
}
