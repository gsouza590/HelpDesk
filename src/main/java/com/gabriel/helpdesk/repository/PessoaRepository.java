package com.gabriel.helpdesk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabriel.helpdesk.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {

	Optional<Pessoa> findByCpf(String cpf);
	Optional<Pessoa> findByEmail(String email);
}
