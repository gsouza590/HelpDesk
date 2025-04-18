package com.gabriel.helpdesk.controller;

import com.gabriel.helpdesk.services.producer.RelatorioProducer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorio")
public class RelatorioController {

    private final RelatorioProducer relatorioProducer;
    private static final String DIRETORIO_RELATORIOS = "relatorios/";

    public RelatorioController(RelatorioProducer relatorioProducer) {
        this.relatorioProducer = relatorioProducer;
    }

    // 📤 Enviar solicitação para gerar relatório
    @PostMapping("/chamado/{id}")
    public ResponseEntity<Map<String, String>> solicitarRelatorio(@PathVariable Integer id) {
        relatorioProducer.enviarMensagemRelatorio(id);

        // Retorna um JSON válido
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Relatório solicitado com sucesso para o chamado: " + id);

        return ResponseEntity.ok(response);
    }


    // 📥 Baixar o relatório gerado pelo RabbitMQ
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> baixarRelatorio(@PathVariable Integer id) {
        try {
            Path caminhoArquivo = Paths.get(DIRETORIO_RELATORIOS + "chamado_" + id + ".pdf");

            // 🔎 Verifica se o arquivo existe
            if (!Files.exists(caminhoArquivo)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(("❌ Relatório do chamado " + id + " não encontrado.").getBytes());
            }

            byte[] pdfBytes = Files.readAllBytes(caminhoArquivo);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=chamado_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("❌ Erro ao baixar o relatório do chamado " + id).getBytes());
        }
    }
}

// Utilizando via HTTP
//    @GetMapping("/chamado/{id}")
//    public ResponseEntity<byte[]> gerarRelatorio(@PathVariable Integer id) {
//        byte[] pdfBytes = relatorioService.gerarRelatorioChamado(id);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Disposition", "attachment; filename=chamado_" + id + ".pdf");
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
//                .body(pdfBytes);
//    }

