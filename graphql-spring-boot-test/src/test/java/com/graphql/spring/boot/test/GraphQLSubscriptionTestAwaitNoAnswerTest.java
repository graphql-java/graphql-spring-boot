package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Testing awaitNoResponse methods")
class GraphQLSubscriptionTestAwaitNoAnswerTest extends GraphQLTestSubscriptionTestBase {

  @Test
  @DisplayName("Should succeed if no responses arrived / default stopAfter.")
  void shouldAwaitNoResponseSucceedIfNoResponsesArrivedDefaultStopAfter() {
    // GIVEN
    final Instant timeBeforeTestStart = Instant.now();
    // WHEN
    graphQLTestSubscription
        .start(SUBSCRIPTION_THAT_TIMES_OUT_RESOURCE)
        .waitAndExpectNoResponse(TIMEOUT);
    // THEN
    assertThatSubscriptionWasStopped();
    assertThatMinimumRequiredTimeElapsedSince(timeBeforeTestStart);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @DisplayName("Should succeed if no responses arrive.")
  void shouldAwaitNoResponseSucceedIfNoResponsesArrived(final boolean stopAfter) {
    // GIVEN
    final Instant timeBeforeTestStart = Instant.now();
    // WHEN
    graphQLTestSubscription
        .start(SUBSCRIPTION_THAT_TIMES_OUT_RESOURCE)
        .waitAndExpectNoResponse(TIMEOUT, stopAfter);
    // THEN
    assertThatSubscriptionStoppedStatusIs(stopAfter);
    assertThatMinimumRequiredTimeElapsedSince(timeBeforeTestStart);
  }

  @Test
  @DisplayName("Should raise assertion error if any response arrived / default stop after.")
  void shouldRaiseAssertionErrorIfResponseArrivedDefaultStopAfter() {
    // WHEN
    graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE);
    // THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.waitAndExpectNoResponse(TIMEOUT));
    assertThatSubscriptionWasStopped();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @DisplayName("Should raise assertion error if any response arrived.")
  void shouldRaiseAssertionErrorIfResponseArrived(final boolean stopAfter) {
    // WHEN
    graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE);
    // THEN
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.waitAndExpectNoResponse(TIMEOUT, stopAfter));
    assertThatSubscriptionStoppedStatusIs(stopAfter);
  }
}
