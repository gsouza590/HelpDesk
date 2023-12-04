package com.gabriel.helpdesk.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.model.Pessoa;
import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.TecnicoDto;
import com.gabriel.helpdesk.repository.PessoaRepository;
import com.gabriel.helpdesk.repository.TecnicoRepository;

@Service
public class TecnicoService {

	@Autowired
	private TecnicoRepository repository;
	@Autowired
	private PessoaRepository pessoaRepository;

	public Tecnico findById(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundExceptions("Técnico Não Encontrado"));
	}

	public List<Tecnico> findAll() {
		return repository.findAll();
	}

	public Tecnico create(TecnicoDto dto) {
		dto.setId(null);
		validaCpfeEmail(dto);
		Tecnico newDto = new Tecnico(dto);
		return repository.save(newDto);
	}

	private void validaCpfeEmail(TecnicoDto dto) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(dto.getCpf());
		if (obj.isPresent() && obj.get().getId() != dto.getId()) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}

		obj = pessoaRepository.findByEmail(dto.getEmail());
		if (obj.isPresent() && obj.get().getId() != dto.getId()) {
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
		}
	}

	public Tecnico update(Integer id, @Valid TecnicoDto dto) {
		dto.setId(id);
		Tecnico tec = findById(id);
		validaCpfeEmail(dto);
		tec = new Tecnico(dto);
		return repository.save(tec);
	}

	public void delete(Integer id) {
		Tecnico tec = findById(id);
		if (tec.getChamados().size() > 0) {
			throw new DataIntegrityViolationException("Tecnico possui ordens de serviço e não pode ser deletado");
		}
		repository.deleteById(id);
	}
}
