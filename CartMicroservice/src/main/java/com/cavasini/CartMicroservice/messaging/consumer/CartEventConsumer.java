package com.cavasini.CartMicroservice.messaging.consumer;

import com.cavasini.CartMicroservice.config.messaging.MessagingConfig;
import com.cavasini.CartMicroservice.dto.event.CartUpdateEvent;
import com.cavasini.CartMicroservice.model.CartItem;
import com.cavasini.CartMicroservice.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CartEventConsumer {

    @Autowired
    private DatabaseService databaseService;

    private final Logger logger = LoggerFactory.getLogger(CartEventConsumer.class);


    @RabbitListener(queues = MessagingConfig.CART_PERSIST_QUEUE)
    public void consumeMessage(CartUpdateEvent event, @Header("messageType") String messageType){

        if (event == null) {
            throw new IllegalArgumentException("CartUpdateEvent cannot be null");
        }

        switch (messageType) {
            case "cart.addItem":
                addItemToCart(event);
                break;
            case "cart.updateItemQuantity":
                updateItemQuantity(event);
                break;
            case "cart.removeItem":
                removeItemFromCart(event.id());
                break;
            default:
                handleUnknownAction(messageType);
        }
    }

    private void addItemToCart(CartUpdateEvent event) {
        CartItem newItem = toCartItem(event);
        newItem.setId(event.id());
        databaseService.addItemToTheCart(newItem);
        logger.info("Item added to the cart: {}", newItem);
    }

    private void removeItemFromCart(UUID id) {
        databaseService.deleteItem(id);
        logger.info("Item removed from cart with ID: {}", id);
    }

    private void updateItemQuantity(CartUpdateEvent event) {
        databaseService.updateQuantity(event.id(), event.quantity());
        logger.info("Quantity updated for cart item ID {}: new quantity is {}", event.id(), event.quantity());
    }


    private void handleUnknownAction(String messageType) {
        logger.warn("Received unknown message type: {}", messageType);
    }


    public CartItem toCartItem(CartUpdateEvent event) {
        return new CartItem(
                event.productId(),
                event.name(),
                event.quantity(),
                event.price(),
                event.userId()
        );
    }

}
