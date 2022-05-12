package com.example.rabbitmqdeadletterdemononblocking.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Producer implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        do {
            rabbitTemplate.convertAndSend("tutorial-exchange", "primaryRoutingKey", "Hello, world!");
            Thread.sleep(60000);
        } while (true);
    }
}
