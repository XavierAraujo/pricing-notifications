package com.araujo.xavier.pricingnotifications.publisher;

import com.araujo.xavier.pricingnotifications.model.MarketPrice;
import org.reactivestreams.Publisher;

import java.util.Set;

public interface MarketPricesPublisher {

    Publisher<MarketPrice> getPublisher(Set<String> marketIds);

}
