package com.araujo.xavier.pricingnotifications.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.execution.reactive.SubscriptionPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Slf4j
public class GraphQLWebsocketHandler implements WebSocketHandler {

    private final GraphQL graphQL;

    public GraphQLWebsocketHandler(GraphQL graphQL) {
        this.graphQL = graphQL;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("WebSocket connection established"));
        log.info("New WebSocket connection established");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        handleNewGraphQlSubscription(session, ((TextMessage) message).getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info("New WebSocket terminated with an error");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        log.info("New WebSocket connection closed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void handleNewGraphQlSubscription(WebSocketSession session, String message) {
        ExecutionResult executionResult = graphQL.execute(ExecutionInput.newExecutionInput().query(message));
        SubscriptionPublisher subscriptionPublisher = executionResult.getData();
        Flux.from(subscriptionPublisher)
                .takeWhile(ignored -> session.isOpen())
                .subscribe(marketPrice -> {
                    try {
                        session.sendMessage(new TextMessage(marketPrice.toSpecification().toString()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
