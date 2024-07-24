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
		this.perfis = cli.getPerfis().stream().map(Perfil::getCodigo).collect(Collectors.toSet());
		this.dataCriacao = cli.getDataCriacao();
		addPerfil(Perfil.CLIENTE);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Set<Perfil> getPerfis() {
		return perfis.stream().map(Perfil::toEnum).collect(Collectors.toSet());
	}

	public void setPerfis(Set<Integer> perfis) {
		this.perfis = perfis;
	}

	public void addPerfil(Perfil perfil) {
		this.perfis.add(perfil.getCodigo());
	}

	public LocalDate getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDate dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
}
