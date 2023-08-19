# Pricing Notifications

This project is a demonstration of the GraphQL subscription functionality.
Subscriptions are a GraphQL feature that allows a server to send data to its clients 
when a specific event happens. In this project we implemented these subscriptions 
using WebSockets. WebSocket is a bidirectional communication protocol that allows to send 
the data from the client to the server or from the server to the client by reusing the 
established connection channel. The connection is kept alive until terminated by either 
the client or the server or a timeout occurs.

To demonstrate this functionality we've come up with a scenario in which the client can
subscribe to price notifications for specific markets.

# Run the project

To run the server you can simply run
```
mvn spring-boot:run
```

Then you can connect to the server using any websocket client. An example would be 
[websocat](https://github.com/vi/websocat). After successfully connecting to the server you
can subscribe the data you want.

If you want to receive the price changes notification of market 2 and 4 you can simply send
the following query to the server:
```
subscription { marketPrices(marketIds: ["2","4"]) { id price } }
```

If you only care about one market and just want to receive the price of it and not its ID you
can send the following query to the server:
```
subscription { marketPrices(marketIds: ["2"]) { price } }
```

If you care about the name of the market you can request that information also:
```
subscription { marketPrices(marketIds: ["2"]) { price name } }
```

The following dummy markets can be subscribed:

- ID: "1", Name: "Porto vs Benfica"
- ID: "2", Name: "Liverpool vs Manchester United"
- ID: "3", Name: "Braga vs Madrid"
- ID: "4", Name: "Ajax vs Barcelona"
- ID: "5", Name: "Arsenal vs Milan"
