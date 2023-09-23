package com.example.rabbitmqdeadletterdemononblocking.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import static com.example.rabbitmqdeadletterdemononblocking.util.Constants.*;

@Slf4j
@Component
@AllArgsConstructor
public class Consumer {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = PRIMARY_QUEUE)
    public void primary(Message message) throws RuntimeException, InterruptedException {
        log.info(MessageFormat.format("Sending message [{0}] to primary queue with name: [{1}]", message, PRIMARY_QUEUE));

        if (hasExceededRetryCount(message)) {
            putIntoStorageQueue(message);
        } else {
            Thread.sleep(5000);
            throwException();
        }
    }

    private void throwException() {
        // method to simulate error while processing the message
        log.error("There was an error processing the message. Moving message to waiting queue.");
        throw new RuntimeException("Processing error occurred");
    }

    private boolean hasExceededRetryCount(Message message) {
        List<Map<String, ?>> xDeathHeader = message.getMessageProperties().getXDeathHeader();

        if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
            Long count = (Long) xDeathHeader.get(0).get("count");

            return count >= RETRIES_COUNT_LIMIT;
        }

        return false;
    }

    private void putIntoStorageQueue(Message failedMessage) {
        log.info("Retries exceeded! Putting message into storage queue");

        this.rabbitTemplate.send(STORAGE_QUEUE, failedMessage);
    }
}
