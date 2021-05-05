package graphql.kickstart.spring.web.boot.resolvers;

import graphql.kickstart.spring.web.boot.publishers.StockTickerRxPublisher;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import java.util.List;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
class Subscription implements GraphQLSubscriptionResolver {

  private StockTickerRxPublisher stockTickerPublisher;

  Subscription(StockTickerRxPublisher stockTickerPublisher) {
    this.stockTickerPublisher = stockTickerPublisher;
  }

  Publisher<StockPriceUpdate> stockQuotes(List<String> stockCodes) {
    return stockTickerPublisher.getPublisher(stockCodes);
  }
}
