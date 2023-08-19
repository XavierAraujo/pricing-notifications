package com.araujo.xavier.pricingnotifications.publisher;

import com.araujo.xavier.pricingnotifications.model.MarketPrice;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MarketPricesDummyPublisher implements MarketPricesPublisher {

    private static final int PRICE_UPDATE_INTERVAL_SECONDS = 1;
    private static final double MIN_PRICE = 0;
    private static final double MAX_PRICE = 100;

    private record DummyMarket(String id, String name) {}

    private final List<DummyMarket> dummyMarkets = List.of(
            new DummyMarket("1", "Porto vs Benfica"),
            new DummyMarket("2", "Liverpool vs Manchester United"),
            new DummyMarket("3", "Braga vs Madrid"),
            new DummyMarket("4", "Ajax vs Barcelona"),
            new DummyMarket("5", "Arsenal vs Milan")
    );

    private final Flux<MarketPrice> publisher;

    /**
     * Creates a Reactor flux that publishes new prices for the dummy markets defined above
     * every second
     */
    public MarketPricesDummyPublisher() {
        publisher = Flux.interval(Duration.ofSeconds(PRICE_UPDATE_INTERVAL_SECONDS))
                .flatMapIterable(value -> dummyMarkets.stream()
                        .map(dummyMarket -> new MarketPrice(dummyMarket.id, dummyMarket.name, calculateRandomPrice()))
                        .collect(Collectors.toList())
                );
    }

    public Publisher<MarketPrice> getPublisher(Set<String> marketIds) {
        return publisher.filter(marketPrice -> marketIds.contains(marketPrice.id()));
    }

    private double calculateRandomPrice() {
        return ThreadLocalRandom.current().nextDouble(MIN_PRICE, MAX_PRICE);
    }
}
