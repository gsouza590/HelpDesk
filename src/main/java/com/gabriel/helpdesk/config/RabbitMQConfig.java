package com.gabriel.helpdesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;

    @Configuration
    public class RabbitMQConfig {

        public static final String RELATORIO_QUEUE = "relatorio_queue";

        @Bean
        public Queue queue() {
            return new Queue(RELATORIO_QUEUE, true);
        }
    }

