package com.gabriel.helpdesk.controller;

import com.gabriel.helpdesk.services.ChamadoService;
import com.gabriel.helpdesk.services.ClienteService;
import com.gabriel.helpdesk.services.TecnicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ChamadoService chamadoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private TecnicoService tecnicoService;

    @GetMapping("/chamados-por-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO', 'CLIENTE')")
    public ResponseEntity<Map<String, Long>> getChamadosPorStatus() {
        return ResponseEntity.ok(chamadoService.getChamadosPorStatus());
    }


    @GetMapping("/chamados-por-prioridade")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO', 'CLIENTE')")
    public ResponseEntity<Map<String, Long>> getChamadosPorPrioridade() {
        return ResponseEntity.ok(chamadoService.getChamadosPorPrioridade());
    }


    @GetMapping("/chamados-por-mes")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<Map<String, Long>> getChamadosPorMes() {
        return ResponseEntity.ok(chamadoService.getChamadosPorMes());
    }


    @GetMapping("/chamados-por-tecnico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getChamadosPorTecnico() {
        return ResponseEntity.ok(chamadoService.getChamadosPorTecnico());
    }


    @GetMapping("/chamados-por-cliente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getChamadosPorCliente() {
        return ResponseEntity.ok(chamadoService.getChamadosPorCliente());
    }

    @GetMapping("/total-usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getTotalUsuarios() {
        Map<String, Long> totalUsuarios = Map.of(
                "totalClientes", (long) clienteService.findAll().size(),
                "totalTecnicos", (long) tecnicoService.findAll().size()
        );
        return ResponseEntity.ok(totalUsuarios);
    }
}
