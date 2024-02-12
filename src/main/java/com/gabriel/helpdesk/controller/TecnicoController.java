package com.gabriel.helpdesk.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.TecnicoDto;
import com.gabriel.helpdesk.services.TecnicoService;

@Controller
@RequestMapping(value = "/tecnicos")
public class TecnicoController {

	@Autowired
	private TecnicoService tecService;

	@GetMapping(value = "/{id}")
	public ResponseEntity<TecnicoDto> findById(@PathVariable Integer id) {

		Tecnico tec = tecService.findById(id);
		return ResponseEntity.ok(new TecnicoDto(tec));
	}

	@GetMapping
	public ResponseEntity<List<TecnicoDto>> findAll() {
		List<Tecnico> list = tecService.findAll();
		List<TecnicoDto> listDto = list.stream().map(x -> new TecnicoDto(x)).collect(Collectors.toList());
		return ResponseEntity.ok().body(listDto);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@PostMapping
	public ResponseEntity<TecnicoDto> create(@Valid @RequestBody TecnicoDto dto) {
		Tecnico newDto = tecService.create(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("{/id}").buildAndExpand(newDto.getId()).toUri();
		return ResponseEntity.created(uri).build();

	}

	@PreAuthorize("hasAnyRole('ADMIN')")

	@PutMapping(value = "/{id}")
	public ResponseEntity<TecnicoDto> update(@PathVariable Integer id, @Valid @RequestBody TecnicoDto dto) {
		Tecnico tec = tecService.update(id, dto);
		return ResponseEntity.ok().body(new TecnicoDto(tec));
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<TecnicoDto> delete(@PathVariable Integer id) {
		tecService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
