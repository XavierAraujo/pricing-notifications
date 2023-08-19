package com.araujo.xavier.pricingnotifications;

import com.araujo.xavier.pricingnotifications.graphql.GraphQLMarketPricesInitializer;
import com.araujo.xavier.pricingnotifications.graphql.GraphQLWebsocketHandler;
import com.araujo.xavier.pricingnotifications.publisher.MarketPricesDummyPublisher;
import graphql.GraphQL;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class PricingNotificationsApplicationConfig implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		GraphQL graphQL = GraphQLMarketPricesInitializer.build(
				new MarketPricesDummyPublisher(),
				"graphql/market_price.graphql");
		registry.addHandler(new GraphQLWebsocketHandler(graphQL), "/ws/graphql");
	}
}
