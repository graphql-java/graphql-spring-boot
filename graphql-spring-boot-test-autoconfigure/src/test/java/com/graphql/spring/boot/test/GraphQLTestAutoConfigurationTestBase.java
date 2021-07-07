package com.graphql.spring.boot.test;

import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GraphQLTestAutoConfigurationTestBase {

  static final String FOO = "foo";

  @Autowired ApplicationContext applicationContext;

  void assertThatTestSubscriptionWorksCorrectly() {
    // GIVEN
    final GraphQLTestSubscription testSubscription =
        applicationContext.getBean(GraphQLTestSubscription.class);
    // WHEN - THEN
    testSubscription
        .start("test-subscription.graphql")
        .awaitAndGetNextResponse(Duration.ofSeconds(1))
        .assertThatNoErrorsArePresent()
        .assertThatField("$.data.testSubscription")
        .asString()
        .isEqualTo(FOO);
  }

  void assertThatTestTemplateAutoConfigurationWorksCorrectly() throws IOException {
    // GIVEN
    final GraphQLTestTemplate testTemplate = applicationContext.getBean(GraphQLTestTemplate.class);
    // WHEN - THEN
    testTemplate
        .postForResource("test-query.graphql")
        .assertThatNoErrorsArePresent()
        .assertThatField("$.data.testQuery")
        .asString()
        .isEqualTo(FOO);
  }
}
