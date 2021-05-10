package com.graphql.sample.boot;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class Comment {

  private Long id;
  private String description;
}
