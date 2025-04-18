package com.gabriel.helpdesk.services.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.StandardOpenOption;

@Service
public class RelatorioConsumer {

    private static final String DIRETORIO_RELATORIOS = "relatorios/";

    @RabbitListener(queues = "relatorio_response_queue")
    public void receberRelatorio(String mensagem) {
        try {
            System.out.println("📥 Mensagem recebida do RabbitMQ: " + mensagem.substring(0, Math.min(mensagem.length(), 100)) + "...");

            // Separar ID e PDF Base64
            String[] partes = mensagem.split("\\|", 2);
            if (partes.length < 2) {
                System.err.println("❌ Erro: Mensagem mal formatada.");
                return;
            }

            Integer chamadoId = Integer.parseInt(partes[0]);
            byte[] pdfBytes = Base64.getDecoder().decode(partes[1]);

            // Verificar se o PDF foi decodificado corretamente
            if (pdfBytes.length < 100) {
                System.err.println("⚠️ Aviso: O PDF recebido é muito pequeno, pode estar corrompido.");
            }

            // Salvar o PDF
            salvarRelatorio(pdfBytes, chamadoId);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao processar relatório do RabbitMQ.");
        }
    }

    private void salvarRelatorio(byte[] pdfBytes, Integer chamadoId) {
        try {
            Files.createDirectories(Paths.get(DIRETORIO_RELATORIOS));
            Files.write(Paths.get(DIRETORIO_RELATORIOS + "chamado_" + chamadoId + ".pdf"), pdfBytes, StandardOpenOption.CREATE);
            System.out.println("✅ Relatório do chamado " + chamadoId + " salvo com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Erro ao salvar relatório.");
        }
    }
}
