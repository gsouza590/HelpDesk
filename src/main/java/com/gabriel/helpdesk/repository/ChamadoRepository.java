package com.gabriel.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabriel.helpdesk.model.Chamado;

public interface ChamadoRepository extends JpaRepository<Chamado, Integer> {

}
