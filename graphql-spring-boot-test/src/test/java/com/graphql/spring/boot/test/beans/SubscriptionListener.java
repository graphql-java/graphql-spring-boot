package com.graphql.spring.boot.test.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionListener implements ApolloSubscriptionConnectionListener {

  private final ObjectMapper objectMapper;

  @Getter private String expectedConnectionInitParamValue;

  @Override
  public void onConnect(final SubscriptionSession session, final OperationMessage message) {
    final InitPayload initPayload =
        objectMapper.convertValue(message.getPayload(), InitPayload.class);
    expectedConnectionInitParamValue = initPayload.getInitParamValue();
  }

  @Data
  private static class InitPayload {

    private String initParamValue;
  }
}
