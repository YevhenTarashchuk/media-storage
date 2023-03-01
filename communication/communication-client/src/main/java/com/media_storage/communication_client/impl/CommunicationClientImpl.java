package com.media_storage.communication_client.impl;

import com.media_storage.communication_client.CommunicationClient;
import com.media_storage.communication_data.model.EmailModel;
import com.media_storage.rabbitmq.config.property.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommunicationClientImpl implements CommunicationClient {

    private final RabbitMQProperties rabbitMQProperties;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendEvent(EmailModel event) {
        log.debug("Send event: {}", event.toString());
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchange(), rabbitMQProperties.getKey(), event);
    }
}
