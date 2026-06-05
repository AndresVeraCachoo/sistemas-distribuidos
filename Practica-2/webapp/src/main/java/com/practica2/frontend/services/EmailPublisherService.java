package com.practica2.frontend.services;

import com.practica2.frontend.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public EmailPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishWelcomeEmail(String email, String nombre) {
        Map<String, String> payload = new HashMap<>();
        payload.put("action", "welcome");
        payload.put("email", email);
        payload.put("nombre", nombre);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, payload);
    }

    public void publishGoodbyeEmail(String email, String nombre) {
        Map<String, String> payload = new HashMap<>();
        payload.put("action", "goodbye");
        payload.put("email", email);
        payload.put("nombre", nombre);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, payload);
    }
}