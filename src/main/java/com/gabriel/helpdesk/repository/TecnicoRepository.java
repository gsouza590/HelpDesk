package com.gabriel.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabriel.helpdesk.model.Tecnico;

public interface TecnicoRepository extends JpaRepository<Tecnico, Integer> {

}
