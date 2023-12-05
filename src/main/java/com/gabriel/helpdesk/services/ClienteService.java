package com.gabriel.helpdesk.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.Pessoa;
import com.gabriel.helpdesk.model.dto.ClienteDto;
import com.gabriel.helpdesk.repository.ClienteRepository;
import com.gabriel.helpdesk.repository.PessoaRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;

	public Cliente findById(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundExceptions("Técnico Não Encontrado"));
	}

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Cliente create(ClienteDto dto) {
		dto.setId(null);
		validaCpfeEmail(dto);
		Cliente newDto = new Cliente(dto);
		return repository.save(newDto);
	}

	private void validaCpfeEmail(ClienteDto dto) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(dto.getCpf());
		if (obj.isPresent() && obj.get().getId() != dto.getId()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}

		obj = pessoaRepository.findByEmail(dto.getEmail());
		if (obj.isPresent() && obj.get().getId() != dto.getId()) {
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
		}
	}

	public Cliente update(Integer id, @Valid ClienteDto dto) {
		dto.setId(id);
		Cliente cli = findById(id);
		validaCpfeEmail(dto);
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
