package com.example.rabbitmqdeadletterdemononblocking.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Producer implements CommandLineRunner {

    private RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        do {
            rabbitTemplate.convertAndSend("tutorial-exchange", "primaryRoutingKey", "Hello, world!");
            Thread.sleep(60000);
        } while (true);
    }
}
