package com.graphql.spring.boot.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Timeout(60)
public class GraphQLTestSubscriptionTestBase {

  protected static final String TIMER_SUBSCRIPTION_RESOURCE = "timer-subscription-resource.graphql";
  protected static final String SUBSCRIPTION_WITH_PARAMETER_RESOURCE =
      "subscription-with-param-resource.graphql";
  protected static final String SUBSCRIPTION_WITH_INIT_PAYLOAD_RESOURCE =
      "subscription-with-init-payload-resource.graphql";
  protected static final String SUBSCRIPTION_THAT_TIMES_OUT_RESOURCE =
      "subscription-that-times-out-resource.graphql";
  protected static final String SUBSCRIPTION_THAT_THROWS_EXCEPTION =
      "subscription-that-throws-exception-resource.graphql";
  protected static final String DATA_TIMER_FIELD = "$.data.timer";
  protected static final String DATA_SUBSCRIPTION_WITH_PARAMETER_FIELD =
      "$.data.subscriptionWithParameter";
  protected static final String DATA_SUBSCRIPTION_WITH_INIT_PAYLOAD_FIELD =
      "$.data.subscriptionWithInitPayload";
  protected static final int TIMEOUT = 2000;

  @Autowired protected Environment environment;

  @Autowired protected ObjectMapper objectMapper;

  protected GraphQLTestSubscription graphQLTestSubscription;

  @BeforeEach
  protected void setUp() {
    graphQLTestSubscription =
        new GraphQLTestSubscription(environment, objectMapper, "subscriptions");
  }

  @AfterEach
  protected void tearDown() {
    graphQLTestSubscription.reset();
  }

  protected void assertThatSubscriptionStoppedStatusIs(boolean isStopped) {
    if (isStopped) {
      assertThatSubscriptionWasStopped();
    } else {
      assertThatSubscriptionWasNotStopped();
    }
  }

  protected void assertThatSubscriptionWasStopped() {
    assertThat(graphQLTestSubscription.isStopped()).as("Subscription should be stopped.").isTrue();
  }

  protected void assertThatSubscriptionWasNotStopped() {
    assertThat(graphQLTestSubscription.isStopped())
        .as("Subscription should not be stopped.")
        .isFalse();
  }

  protected void assertThatMinimumRequiredTimeElapsedSince(final Instant timeBeforeTestStart) {
    assertThat(Duration.between(timeBeforeTestStart, Instant.now()))
        .as("Should wait the specified amount of time")
        .isGreaterThanOrEqualTo(Duration.ofMillis(TIMEOUT));
  }
}
