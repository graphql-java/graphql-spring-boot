package graphql.kickstart.spring.webflux.boot;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import graphql.schema.DataFetchingEnvironment;
import java.time.Duration;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
class MySubscriptionResolver implements GraphQLSubscriptionResolver {

  Publisher<Integer> hello(DataFetchingEnvironment env) {
    return Flux.range(0, 100).delayElements(Duration.ofSeconds(1));
  }
}
