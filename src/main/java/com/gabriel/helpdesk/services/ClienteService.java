package com.gabriel.helpdesk.services;

import java.util.List;
import java.util.Optional;

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
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundExceptions("Cliente Não Encontrado"));
	}

    public Cliente findByEmail(String email) {
        return Optional.ofNullable(repository.findByEmail(email))
                .orElseThrow(() -> new ObjectNotFoundExceptions("Cliente Não Encontrado"));
    }

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Cliente create(ClienteDto dto) {
		dto.setId(null);
		dto.setSenha(encoder.encode(dto.getSenha()));
		validator.validaCpfEEmail(dto);
		Cliente newCliente = new Cliente(dto);
		return repository.save(newCliente);
	}

	public Cliente update(Integer id, @Valid ClienteDto dto) {
		dto.setId(id);
		Cliente oldObj = findById(id);

		if (!dto.getSenha().equals(oldObj.getSenha()))
			dto.setSenha(encoder.encode(dto.getSenha()));
		validator.validaCpfEEmail(dto);
		oldObj = new Cliente(dto);
		return repository.save(oldObj);

	}

	public void delete(Integer id) {
		Cliente cliente = findById(id);
		if (!cliente.getChamados().isEmpty()) {
			throw new DataIntegrityViolationException("Cliente possui ordens de serviço e não pode ser deletado");
		}
		repository.deleteById(id);
	}
}
