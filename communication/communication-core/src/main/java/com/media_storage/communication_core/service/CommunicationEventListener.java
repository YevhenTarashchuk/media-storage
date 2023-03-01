package com.media_storage.communication_core.service;

import com.media_storage.communication_data.model.EmailModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationEventListener {

    private final EmailSender emailSender;

    @RabbitListener(queues = {"${spring.rabbitmq.queue}"})
    public void listenQueue(EmailModel event) {
        log.debug("Received: {}", event);

        emailSender.sendEmails(event);
    }
}
