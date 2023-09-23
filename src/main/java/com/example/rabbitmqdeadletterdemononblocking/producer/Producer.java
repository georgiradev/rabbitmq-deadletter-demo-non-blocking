package com.example.rabbitmqdeadletterdemononblocking.producer;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.example.rabbitmqdeadletterdemononblocking.util.Constants.EXCHANGE_NAME;
import static com.example.rabbitmqdeadletterdemononblocking.util.Constants.PRIMARY_ROUTING_KEY;

@Component
@AllArgsConstructor
public class Producer implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        do {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, PRIMARY_ROUTING_KEY, "Hello, world!");
            Thread.sleep(60000);
        } while (true);
    }
}
