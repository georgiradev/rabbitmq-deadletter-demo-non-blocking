package com.example.rabbitmqdeadletterdemononblocking.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.example.rabbitmqdeadletterdemononblocking.config.RabbitConfiguration.STORAGE_QUEUE;
import static com.example.rabbitmqdeadletterdemononblocking.config.RabbitConfiguration.PRIMARY_QUEUE;

@Component
public class RetryingRabbitConsumer {
    private final Logger logger = LoggerFactory.getLogger(RetryingRabbitConsumer.class);

    private final RabbitTemplate rabbitTemplate;

    public RetryingRabbitConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = PRIMARY_QUEUE)
    public void primary(Message message) throws RuntimeException, InterruptedException {
        logger.info("Message read from workerQueue: " + message);
        if (hasExceededRetryCount(message)) {
            putIntoStorage(message);
        } else {
            Thread.sleep(5000);
            throw new RuntimeException("There was an error processing the message. Moving message to waiting queue");
        }
    }

    private boolean hasExceededRetryCount(Message message) {
        List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();
        if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
            Long count = (Long) xDeathHeader.get(0).get("count");
            return count >= 3;
        }

        return false;
    }

    private void putIntoStorage(Message failedMessage) {
        logger.info("Retries exeeded putting into parking lot queue");
        this.rabbitTemplate.send(STORAGE_QUEUE, failedMessage);
    }
}
