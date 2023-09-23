package com.example.rabbitmqdeadletterdemononblocking.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.example.rabbitmqdeadletterdemononblocking.config.RabbitConfiguration.STORAGE_QUEUE;
import static com.example.rabbitmqdeadletterdemononblocking.config.RabbitConfiguration.PRIMARY_QUEUE;

@Slf4j
@Component
@AllArgsConstructor
public class Consumer {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = PRIMARY_QUEUE)
    public void primary(Message message) throws RuntimeException, InterruptedException {
        log.info("Message read from workerQueue: " + message);
        if (hasExceededRetryCount(message)) {
            putIntoStorage(message);
        } else {
            Thread.sleep(5000);
            try {
                throwException();
            } catch (RuntimeException ex) {
                log.error(ex.getMessage());
            }
        }
    }

    private void throwException() {
        // method to simulate error while processing the message
        throw new RuntimeException("There was an error processing the message. Moving message to waiting queue");
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
        log.info("Retries exceeded putting into parking lot queue");
        this.rabbitTemplate.send(STORAGE_QUEUE, failedMessage);
    }
}
