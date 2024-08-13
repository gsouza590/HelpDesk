package com.gabriel.helpdesk.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.dto.ClienteDto;
import com.gabriel.helpdesk.services.ClienteService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/clientes")
public class ClienteController {

	@Autowired
	private ClienteService cliService;

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<ClienteDto> findById(@PathVariable Integer id) {

		Cliente cli = cliService.findById(id);
		return ResponseEntity.ok(new ClienteDto(cli));
	}

	@Transactional
	@GetMapping
	public ResponseEntity<List<ClienteDto>> findAll() {
		List<Cliente> list = cliService.findAll();
		List<ClienteDto> listDto = list.stream().map(x -> new ClienteDto(x)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ClienteDto> create(@Valid @RequestBody ClienteDto dto) {
		Cliente newDto = cliService.create(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDto.getId()).toUri();
		return ResponseEntity.created(uri).build();

	}

	@Transactional
	@PutMapping(value = "/{id}")
	public ResponseEntity<ClienteDto> update(@PathVariable Integer id, @Valid @RequestBody ClienteDto dto) {
		Cliente cli = cliService.update(id, dto);
		return ResponseEntity.ok().body(new ClienteDto(cli));
	}

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<ClienteDto> delete(@PathVariable Integer id) {
		cliService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
