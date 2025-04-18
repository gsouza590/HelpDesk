package com.gabriel.helpdesk.services.producer;

import com.gabriel.helpdesk.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RelatorioProducer {
    private final RabbitTemplate rabbitTemplate;
    private static final String QUEUE_NAME = "relatorio_queue";

    public RelatorioProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarMensagemRelatorio(Integer chamadoId) {
        System.out.println("📤 Enviando solicitação de relatório para RabbitMQ: Chamado " + chamadoId);
        rabbitTemplate.convertAndSend(QUEUE_NAME, chamadoId.toString());
    }
}
