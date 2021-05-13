package graphql.kickstart.spring.rsocket.boot;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import graphql.schema.DataFetchingEnvironment;
import java.time.Duration;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
class SubscriptionResolver implements GraphQLSubscriptionResolver {

  Publisher<Integer> hello(DataFetchingEnvironment env) {
    return Flux.range(0, 100).delayElements(Duration.ofSeconds(1));
  }
}
