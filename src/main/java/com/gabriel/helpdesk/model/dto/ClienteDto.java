package com.gabriel.helpdesk.model.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.gabriel.helpdesk.model.dto.interfaces.IClienteDto;
import com.gabriel.helpdesk.model.dto.interfaces.IPessoaDto;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabriel.helpdesk.model.Cliente;
import com.gabriel.helpdesk.model.enums.Perfil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDto implements Serializable , IClienteDto, IPessoaDto {

	private static final long serialVersionUID = 1L;

	protected Integer id;
	@NotNull(message = "O campo deve ser preenchido ")
	protected String nome;
	@NotNull(message = "O campo deve ser preenchido ")
	protected String cpf;
	@NotNull(message = "O campo deve ser preenchido ")
	protected String email;
	@NotNull(message = "O campo deve ser preenchido ")
	protected String senha;
	protected Set<Integer> perfis = new HashSet<>();
	@JsonFormat(pattern = "dd/MM/yyyy")
	protected LocalDate dataCriacao = LocalDate.now();

	public ClienteDto() {
		super();
		addPerfil(Perfil.CLIENTE);
	}

	public ClienteDto(Cliente cli) {
		super();
		this.id = cli.getId();
		this.nome = cli.getNome();
		this.cpf = cli.getCpf();
		this.email = cli.getEmail();
		this.senha = cli.getSenha();
		this.perfis = cli.getPerfis().stream().map(x -> x.getCodigo()).collect(Collectors.toSet());
		this.dataCriacao = cli.getDataCriacao();
		addPerfil(Perfil.CLIENTE);
	}



	public Set<Perfil> getPerfis() {
		return perfis.stream().map(x -> Perfil.toEnum(x)).collect(Collectors.toSet());
	}

	public void addPerfil(Perfil perfil) {
		this.perfis.add(perfil.getCodigo());
	}
}
