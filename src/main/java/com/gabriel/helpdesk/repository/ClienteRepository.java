package com.gabriel.helpdesk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabriel.helpdesk.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Cliente findByEmail(String email);

}
