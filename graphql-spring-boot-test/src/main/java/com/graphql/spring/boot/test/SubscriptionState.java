package com.graphql.spring.boot.test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SubscriptionState {

  private boolean initialized;
  private boolean acknowledged;
  private boolean started;
  private boolean stopped;
  private boolean completed;
  @Builder.Default private Queue<GraphQLResponse> responses = new ConcurrentLinkedQueue<>();
  private int id;
}
