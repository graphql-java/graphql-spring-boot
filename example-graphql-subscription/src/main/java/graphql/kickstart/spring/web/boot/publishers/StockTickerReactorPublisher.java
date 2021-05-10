package graphql.kickstart.spring.web.boot.publishers;

import graphql.kickstart.spring.web.boot.resolvers.StockPriceUpdate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Slf4j
@Component
public class StockTickerReactorPublisher {

  private static final Map<String, BigDecimal> CURRENT_STOCK_PRICES = new ConcurrentHashMap<>();
  private static final Random rand = new SecureRandom();

  static {
    CURRENT_STOCK_PRICES.put("TEAM", dollars(39, 64));
    CURRENT_STOCK_PRICES.put("IBM", dollars(147, 10));
    CURRENT_STOCK_PRICES.put("AMZN", dollars(1002, 94));
    CURRENT_STOCK_PRICES.put("MSFT", dollars(77, 49));
    CURRENT_STOCK_PRICES.put("GOOGL", dollars(1007, 87));
  }

  private final Flux<StockPriceUpdate> publisher;

  public StockTickerReactorPublisher() {
    Flux<StockPriceUpdate> stockPriceUpdateFlux =
        Flux.create(
            emitter -> {
              ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
              executorService.scheduleAtFixedRate(newStockTick(emitter), 0, 2, TimeUnit.SECONDS);
            },
            FluxSink.OverflowStrategy.BUFFER);
    ConnectableFlux<StockPriceUpdate> connectableFlux = stockPriceUpdateFlux.share().publish();
    connectableFlux.connect();

    publisher = Flux.from(connectableFlux);
  }

  private static BigDecimal dollars(int dollars, int cents) {
    return truncate("" + dollars + "." + cents);
  }

  private static BigDecimal truncate(final String text) {
    BigDecimal bigDecimal = new BigDecimal(text);
    if (bigDecimal.scale() > 2) {
      bigDecimal = new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
    }
    return bigDecimal.stripTrailingZeros();
  }

  private static int rollDice(int min, int max) {
    return rand.nextInt((max - min) + 1) + min;
  }

  private Runnable newStockTick(FluxSink<StockPriceUpdate> emitter) {
    return () -> {
      List<StockPriceUpdate> stockPriceUpdates = getUpdates(rollDice(0, 5));
      if (stockPriceUpdates != null) {
        emitStocks(emitter, stockPriceUpdates);
      }
    };
  }

  private void emitStocks(
      FluxSink<StockPriceUpdate> emitter, List<StockPriceUpdate> stockPriceUpdates) {
    for (StockPriceUpdate stockPriceUpdate : stockPriceUpdates) {
      try {
        emitter.next(stockPriceUpdate);
      } catch (RuntimeException e) {
        log.error("Cannot send StockUpdate", e);
      }
    }
  }

  public Flux<StockPriceUpdate> getPublisher() {
    return publisher;
  }

  public Flux<StockPriceUpdate> getPublisher(List<String> stockCodes) {
    if (stockCodes != null) {
      return publisher.filter(
          stockPriceUpdate -> stockCodes.contains(stockPriceUpdate.getStockCode()));
    }
    return publisher;
  }

  private List<StockPriceUpdate> getUpdates(int number) {
    List<StockPriceUpdate> updates = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      updates.add(rollUpdate());
    }
    return updates;
  }

  private StockPriceUpdate rollUpdate() {
    ArrayList<String> stockQuotes = new ArrayList<>(CURRENT_STOCK_PRICES.keySet());

    String stockCode = stockQuotes.get(rollDice(0, stockQuotes.size() - 1));
    BigDecimal currentPrice = CURRENT_STOCK_PRICES.get(stockCode);

    BigDecimal incrementDollars = dollars(rollDice(0, 1), rollDice(0, 99));
    if (rollDice(0, 10) > 7) {
      // 0.3 of the time go down
      incrementDollars = incrementDollars.negate();
    }
    BigDecimal newPrice = currentPrice.add(incrementDollars);

    CURRENT_STOCK_PRICES.put(stockCode, newPrice);
    return new StockPriceUpdate(stockCode, LocalDateTime.now(), newPrice, incrementDollars);
  }
}
