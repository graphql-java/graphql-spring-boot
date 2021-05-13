package graphql.kickstart.spring.rsocket.boot;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class QueryResolver implements GraphQLQueryResolver {

  public Mono<String> hello() {
    return Mono.just("Hello world");
  }
}
