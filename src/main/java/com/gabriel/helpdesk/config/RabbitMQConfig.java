package com.gabriel.helpdesk.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String RELATORIO_REQUEST_QUEUE = "relatorio_queue";
    public static final String RELATORIO_RESPONSE_QUEUE = "relatorio_response_queue";

    @Bean
    public Queue requestQueue() {
        return new Queue(RELATORIO_REQUEST_QUEUE, true);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(RELATORIO_RESPONSE_QUEUE, true);
    }
}
