package com.gabriel.helpdesk.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class RelatorioService {

    @Value("${relatorio.api.url}")
    private String relatorioApiUrl;

    public byte[] gerarRelatorioChamado(Integer chamadoId) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(relatorioApiUrl + chamadoId, byte[].class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        throw new RuntimeException("Erro ao gerar o relatório");
    }
}
