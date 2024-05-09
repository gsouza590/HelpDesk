package com.gabriel.helpdesk.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.dto.ClienteDto;
import com.gabriel.helpdesk.repository.ClienteRepository;
import com.gabriel.helpdesk.services.utils.Validators;

import jakarta.validation.Valid;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;
	@Autowired
	private Validators validator;
	@Autowired
	private BCryptPasswordEncoder encoder;

	public Cliente findById(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundExceptions("Técnico Não Encontrado"));
	}

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Cliente create(ClienteDto dto) {
		dto.setId(null);
		dto.setSenha(encoder.encode(dto.getSenha()));
		validator.validaCpfEEmail(dto);
		Cliente newDto = new Cliente(dto);
		return repository.save(newDto);
	}

	public Cliente update(Integer id, @Valid ClienteDto dto) {
		dto.setId(id);
		Cliente cli = findById(id);
		if (!dto.getSenha().equals(cli.getSenha())) {
			dto.setSenha(encoder.encode(dto.getSenha()));
		}
		validator.validaCpfEEmail(dto);
		cli = new Cliente(dto);
		return repository.save(cli);
	}

	public void delete(Integer id) {
		Cliente tec = findById(id);
		if (tec.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Cliente possui ordens de serviço e não pode ser deletado");
		}
		repository.deleteById(id);
	}
}
