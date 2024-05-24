package com.gabriel.helpdesk.model.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

import com.gabriel.helpdesk.model.Pessoa;
import com.gabriel.helpdesk.model.enums.Perfil;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PessoaDto implements Serializable {

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

	protected Set<Perfil> perfis;
	protected LocalDate dataCriacao;

	public PessoaDto() {}

	public PessoaDto(Pessoa pessoa) {
		this.id = pessoa.getId();
		this.nome = pessoa.getNome();
		this.cpf = pessoa.getCpf();
		this.email = pessoa.getEmail();
		this.senha= pessoa.getSenha();
		this.perfis = pessoa.getPerfis();
		this.dataCriacao = pessoa.getDataCriacao();
	}
}
