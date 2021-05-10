package com.graphql.sample.boot;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

@Component
class Query implements GraphQLQueryResolver {

  Post getPost(Long id) {
    return new Post(id);
  }
}
