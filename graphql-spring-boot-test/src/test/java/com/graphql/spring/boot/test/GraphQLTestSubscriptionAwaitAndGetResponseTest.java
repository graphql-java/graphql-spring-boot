package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GraphQLTestSubscriptionAwaitAndGetResponseTest extends GraphQLTestSubscriptionTestBase {

  @Test
  @DisplayName("Should await and get single response.")
  void shouldAwaitAndGetResponse() {
    // WHEN
    graphQLTestSubscription
        .start(TIMER_SUBSCRIPTION_RESOURCE)
        .awaitAndGetNextResponse(TIMEOUT)
        .assertThatField(DATA_TIMER_FIELD)
        .asLong()
        .isZero();
    // THEN
    assertThatSubscriptionWasStopped();
  }

  @Test
  @DisplayName("Should await and get multiple responses.")
  void shouldAwaitAndGetMultipleResponses() {
    // WHEN
    final List<GraphQLResponse> graphQLResponses =
        graphQLTestSubscription
            .start(TIMER_SUBSCRIPTION_RESOURCE)
            .awaitAndGetNextResponses(TIMEOUT, 5);
    // THEN
    assertThat(graphQLResponses)
        .extracting(response -> response.get(DATA_TIMER_FIELD, Long.class))
        .containsExactly(0L, 1L, 2L, 3L, 4L);
    assertThatSubscriptionWasStopped();
  }

  @Test
  @DisplayName("Should await and get all responses / default stopAfter.")
  void shouldAwaitAndGetAllResponsesDefaultStopAfter() {
    // GIVEN
    final Instant timeBeforeTest = Instant.now();
    // WHEN
    final List<GraphQLResponse> graphQLResponses =
        graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE).awaitAndGetAllResponses(TIMEOUT);
    // THEN
    assertThat(graphQLResponses)
        .extracting(response -> response.get(DATA_TIMER_FIELD, Long.class))
        .containsExactly(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    assertThatSubscriptionWasStopped();
    assertThatMinimumRequiredTimeElapsedSince(timeBeforeTest);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  @DisplayName("Should await and get all responses.")
  void shouldAwaitAndGetAllResponses(final boolean stopAfter) {
    // GIVEN
    final Instant timeBeforeTest = Instant.now();
    // WHEN
    final List<GraphQLResponse> graphQLResponses =
        graphQLTestSubscription
            .start(TIMER_SUBSCRIPTION_RESOURCE)
            .awaitAndGetAllResponses(TIMEOUT, stopAfter);
    // THEN
    assertThat(graphQLResponses)
        .extracting(response -> response.get(DATA_TIMER_FIELD, Long.class))
        .containsExactly(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
    assertThatSubscriptionStoppedStatusIs(stopAfter);
    assertThatMinimumRequiredTimeElapsedSince(timeBeforeTest);
  }

  @Test
  @DisplayName(
      "Should handle multiple subsequent await and get calls, assuming stopAfter was false.")
  void shouldWorkWithMultipleAwaitAndGetCalls() {
    // GIVEN
    graphQLTestSubscription.start(TIMER_SUBSCRIPTION_RESOURCE);

    // WHEN
    graphQLTestSubscription
        .awaitAndGetNextResponse(TIMEOUT, false)
        .assertThatField(DATA_TIMER_FIELD)
        .asLong()
        .isZero();
    // THEN
    assertThatSubscriptionWasNotStopped();

    // WHEN
    final List<GraphQLResponse> graphQLResponses =
        graphQLTestSubscription.awaitAndGetNextResponses(TIMEOUT, 3, false);
    // THEN
    assertThat(graphQLResponses)
        .extracting(response -> response.get(DATA_TIMER_FIELD, Long.class))
        .containsExactly(1L, 2L, 3L);
    assertThatSubscriptionWasNotStopped();

    // WHEN
    final List<GraphQLResponse> graphQLResponses2 =
        graphQLTestSubscription.awaitAndGetAllResponses(TIMEOUT);
    // THEN
    assertThat(graphQLResponses2)
        .extracting(response -> response.get(DATA_TIMER_FIELD, Long.class))
        .containsExactly(4L, 5L, 6L, 7L, 8L, 9L);
    assertThatSubscriptionWasStopped();
  }

  @Test
  @DisplayName("Should properly handle subscriptions with input variables.")
  void shouldHandleSubscriptionWithParameters() {
    // GIVEN
    final String param = String.valueOf(UUID.randomUUID());
    final Map<String, String> startPayload = Collections.singletonMap("param", param);
    // WHEN - THEN
    graphQLTestSubscription
        .start(SUBSCRIPTION_WITH_PARAMETER_RESOURCE, startPayload)
        .awaitAndGetNextResponse(TIMEOUT)
        .assertThatField(DATA_SUBSCRIPTION_WITH_PARAMETER_FIELD)
        .asString()
        .isEqualTo(param);
  }

  @Test
  @DisplayName("Should properly work with subscriptions that expect an init payload.")
  void shouldSubscriptionWithInitPayload() {
    // GIVEN
    final String initParamValue = String.valueOf(UUID.randomUUID());
    final Map<String, String> initPayload =
        Collections.singletonMap("initParamValue", initParamValue);
    // WHEN - THEN
    graphQLTestSubscription
        .init(initPayload)
        .start(SUBSCRIPTION_WITH_INIT_PAYLOAD_RESOURCE)
        .awaitAndGetNextResponse(TIMEOUT)
        .assertThatField(DATA_SUBSCRIPTION_WITH_INIT_PAYLOAD_FIELD)
        .asString()
        .isEqualTo(initParamValue);
  }
}
