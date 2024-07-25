package com.gabriel.helpdesk.model.dto.interfaces;

import java.util.Set;

import com.gabriel.helpdesk.model.enums.Perfil;

public interface IClienteDto {
	Integer getId();

	String getNome();

	String getCpf();

	String getEmail();

	String getSenha();

	Set<Perfil> getPerfis();  // Ajuste aqui para Set<Integer>
}