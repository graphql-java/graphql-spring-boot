package com.graphql.sample.boot;

import static java.util.Collections.singletonList;

import graphql.kickstart.tools.GraphQLResolver;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
class PostResolver implements GraphQLResolver<Post> {

  private final Map<Long, List<Comment>> comments = new HashMap<>();

  PostResolver() {
    comments.put(1L, singletonList(new Comment(1L, "Some comment")));
  }

  public List<Comment> getComments(Post post) {
    return Optional.ofNullable(comments.get(post.getId())).orElseGet(Collections::emptyList);
  }
}
