package com.gabriel.helpdesk.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.exceptions.ObjectNotFoundExceptions;
import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.TecnicoDto;
import com.gabriel.helpdesk.repository.TecnicoRepository;
import com.gabriel.helpdesk.services.utils.Validators;

import jakarta.validation.Valid;

@Service
public class TecnicoService {

	@Autowired
	private TecnicoRepository repository;;
	@Autowired
	private BCryptPasswordEncoder encoder;
	@Autowired
	private Validators validator;

	public Tecnico findById(Integer id) {
		return repository.findById(id).orElseThrow(() -> new ObjectNotFoundExceptions("Técnico Não Encontrado"));
	}

	public List<Tecnico> findAll() {
		return repository.findAll();
	}

	public Tecnico create(TecnicoDto dto) {
		dto.setId(null);
		dto.setSenha(encoder.encode(dto.getSenha()));
		validator.validaCpfEEmail(dto);
		Tecnico newDto = new Tecnico(dto);
		return repository.save(newDto);
	}

	public Tecnico update(Integer id, @Valid TecnicoDto dto) {
		dto.setId(id);
		Tecnico tec = findById(id);
		if (!dto.getSenha().equals(tec.getSenha())) {
			dto.setSenha(encoder.encode(dto.getSenha()));
		}
		validator.validaCpfEEmail(dto);
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
