package com.graphql.spring.boot.test.beans;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import io.reactivex.Flowable;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestSubscriptions implements GraphQLSubscriptionResolver {

  private final SubscriptionListener subscriptionListener;

  public Flowable<Long> timer() {
    return Flowable.intervalRange(0, 10, 0, 1, TimeUnit.MILLISECONDS);
  }

  public Flowable<String> subscriptionWithParameter(final String param) {
    return Flowable.just(param);
  }

  public Flowable<String> subscriptionWithInitPayload() {
    return Flowable.just(subscriptionListener.getExpectedConnectionInitParamValue());
  }

  public Flowable<Long> subscriptionThatTimesOut() {
    return Flowable.interval(20000, 20000, TimeUnit.MILLISECONDS);
  }

  public Flowable<Boolean> subscriptionThatThrowsException() {
    throw new RuntimeException("Test exception.");
  }
}
