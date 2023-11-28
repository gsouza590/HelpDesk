package com.gabriel.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabriel.helpdesk.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {

}
