package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.Duration;
import org.awaitility.core.ConditionTimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testing if usage errors are properly handled.")
class GraphQLTestSubscriptionUsageErrorHandlingTest extends GraphQLTestSubscriptionTestBase {

  @Test
  @DisplayName("Should raise an assertion error if init is called after init.")
  void shouldRaiseAssertionErrorIfInitAfterInit() {
    graphQLTestSubscription.init();
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.init());
  }

  @Test
  @DisplayName("Should raise an assertion error if awaitAndGet times out.")
  void shouldRaiseAssertionErrorIfAwaitAndGetTimesOut() {
    graphQLTestSubscription.start(SUBSCRIPTION_THAT_TIMES_OUT_RESOURCE);
    assertThatExceptionOfType(ConditionTimeoutException.class)
        .isThrownBy(() -> graphQLTestSubscription.awaitAndGetNextResponse(Duration.ofMillis(110)));
  }

  @Test
  @DisplayName("Should raise an assertion error if awaitAndGet methods are called before start.")
  void shouldRaiseAssertionErrorIfGettingResponseWithoutStart() {
    graphQLTestSubscription.init();
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.awaitAndGetNextResponse(TIMEOUT));
  }

  @Test
  @DisplayName("Should raise an assertion error if stop is called before init.")
  void shouldRaiseAssertionErrorIfStopWithoutStart() {
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.stop());
  }

  @Test
  @DisplayName("Should raise an assertion error if stop is called after stop.")
  void shouldRaiseAssertionErrorIfStopAfterStop() {
    graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE).stop();
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.stop());
  }

  @Test
  @DisplayName("Should raise an assertion error if awaitAndGet methods are called after stop.")
  void shouldRaiseAssertionErrorIfGettingResponseAfterStop() {
    graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE).stop();
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.awaitAndGetNextResponse(TIMEOUT));
  }

  @Test
  @DisplayName(
      "Should raise an assertion error if the provided GraphQL resource could not be found.")
  void shouldRaiseAssertionErrorIfGraphQLResourceCouldNotBeFound() {
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> graphQLTestSubscription.start("non-existing-file.graphql"));
  }
}
