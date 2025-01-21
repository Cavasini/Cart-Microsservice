package com.cavasini.CartMicroservice.messaging.producer;

import com.cavasini.CartMicroservice.dto.event.CartUpdateEvent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.cavasini.CartMicroservice.config.messaging.MessagingConfig.CART_PERSIST_QUEUE;

@Service
public class CartEventPublisher {

    @Autowired
    private RabbitTemplate  rabbitTemplate;

    public void sendMessage(CartUpdateEvent payload, String messageType){
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("messageType", messageType);
        Message message = rabbitTemplate.getMessageConverter().toMessage(payload, messageProperties);
        rabbitTemplate.send(CART_PERSIST_QUEUE, message);
    }
}
