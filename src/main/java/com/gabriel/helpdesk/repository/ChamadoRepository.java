package com.gabriel.helpdesk.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabriel.helpdesk.model.Chamado;

public interface ChamadoRepository extends JpaRepository<Chamado, Integer> {
	List<Chamado> findByTecnicoId(Integer tecnicoId);
    List<Chamado> findByClienteId(Integer clienteId);
}
