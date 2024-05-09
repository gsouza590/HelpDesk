package com.gabriel.helpdesk.services.utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gabriel.helpdesk.exceptions.DataIntegrityViolationException;
import com.gabriel.helpdesk.model.Pessoa;
import com.gabriel.helpdesk.model.dto.interfaces.IPessoaDto;
import com.gabriel.helpdesk.repository.PessoaRepository;

@Component
public class Validators {
	@Autowired
	private PessoaRepository pessoaRepository;

	public void validaCpfEEmail(IPessoaDto dto) {
		Optional<Pessoa> obj = pessoaRepository.findByCpf(dto.getCpf());
		if (obj.isPresent() && !obj.get().getId().equals(dto.getId())) {
			throw new DataIntegrityViolationException("CPF já cadastrado no sistema!");
		}

		obj = pessoaRepository.findByEmail(dto.getEmail());
		if (obj.isPresent() && !obj.get().getId().equals(dto.getId())) {
			throw new DataIntegrityViolationException("E-mail já cadastrado no sistema!");
		}
	}
}
