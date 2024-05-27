package com.gabriel.helpdesk.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gabriel.helpdesk.model.Chamado;
import com.gabriel.helpdesk.model.dto.ChamadoDto;
import com.gabriel.helpdesk.services.ChamadoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/chamados")
public class ChamadoController {

	@Autowired
	private ChamadoService service;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<ChamadoDto>> findAll() {
		List<Chamado> l = service.findAll();
		return ResponseEntity.ok().body(l.stream().map(x -> new ChamadoDto(x)).collect(Collectors.toList()));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<ChamadoDto> findById(@PathVariable Integer id) {
		Chamado ch = service.findById(id);
		return ResponseEntity.ok().body(new ChamadoDto(ch));

	}

	@PostMapping
	public ResponseEntity<ChamadoDto> create(@Valid @RequestBody ChamadoDto dto) {
		Chamado chamado = service.create(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id)").buildAndExpand(chamado.getId())
				.toUri();
		return ResponseEntity.created(uri).build();
	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ChamadoDto> update(@PathVariable Integer id, @RequestBody ChamadoDto dto) {
		return ResponseEntity.ok().body(new ChamadoDto(service.update(id, dto)));
	}

	@GetMapping("/meus-chamados")
	public ResponseEntity<List<ChamadoDto>> findByLoggedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		List<Chamado> chamados = service.findByLoggedUser(email);
		List<ChamadoDto> chamadosDto = chamados.stream().map(ChamadoDto::new).collect(Collectors.toList());
		return ResponseEntity.ok().body(chamadosDto);
	}
}
