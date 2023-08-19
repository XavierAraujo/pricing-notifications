package com.araujo.xavier.pricingnotifications.graphql;

import com.araujo.xavier.pricingnotifications.model.MarketPrice;
import com.araujo.xavier.pricingnotifications.publisher.MarketPricesPublisher;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;
import org.reactivestreams.Publisher;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

public class GraphQLMarketPricesInitializer {

    private static final String SUBSCRIPTION_WIRING = "Subscription";

    /**
     * GraphQL market prices subscription name. Must match with the name provided in the GraphQL schema.
     */
    private static final String MARKET_PRICES_SUBSCRIPTION = "marketPrices"; // Must match GraphQL subscription name

    /**
     * GraphQL market prices subscription market-ids parameter name.
     * Must match with the name provided in the GraphQL schema.
     */
    private static final String MARKET_PRICES_SUBSCRIPTION_MARKET_IDS = "marketIds";

    public static GraphQL build(MarketPricesPublisher marketPricesPublisher, String graphQlSchemaFile) {
        InputStream graphQlSchemaStream = GraphQLMarketPricesInitializer.class.getClassLoader().getResourceAsStream(graphQlSchemaFile);
        Reader graphQlSchemaReader = new InputStreamReader(graphQlSchemaStream);
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(graphQlSchemaReader);

        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type(TypeRuntimeWiring
                        .newTypeWiring(SUBSCRIPTION_WIRING)
                        .dataFetcher(MARKET_PRICES_SUBSCRIPTION, marketPricesSubscriptionFetcher(marketPricesPublisher))
                )
                .build();

        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
        return GraphQL.newGraphQL(schema).build();
    }

    private static DataFetcher<Publisher<MarketPrice>> marketPricesSubscriptionFetcher(MarketPricesPublisher marketPricesPublisher) {
        return environment -> {
            Set<String> marketIds = Set.copyOf(environment.getArgument(MARKET_PRICES_SUBSCRIPTION_MARKET_IDS));
            return marketPricesPublisher.getPublisher(marketIds);
        };
    }
}
