package com.gabriel.helpdesk.model.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabriel.helpdesk.model.Tecnico;
import com.gabriel.helpdesk.model.dto.interfaces.IPessoaDto;
import com.gabriel.helpdesk.model.dto.interfaces.ITecnicoDto;
import com.gabriel.helpdesk.model.enums.Perfil;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TecnicoDto implements Serializable,ITecnicoDto,IPessoaDto  {

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

	public TecnicoDto() {
		super();
		addPerfil(Perfil.CLIENTE);
	}

	public TecnicoDto(Tecnico tec) {
		super();
		this.id = tec.getId();
		this.nome = tec.getNome();
		this.cpf = tec.getCpf();
		this.email = tec.getEmail();
		this.senha = tec.getSenha();
		this.perfis = tec.getPerfis().stream().map(x -> x.getCodigo()).collect(Collectors.toSet());
		this.dataCriacao = tec.getDataCriacao();
		addPerfil(Perfil.CLIENTE);
	}

	public Set<Perfil> getPerfis() {
		return perfis.stream().map(x -> Perfil.toEnum(x)).collect(Collectors.toSet());
	}

	public void addPerfil(Perfil perfil) {
		this.perfis.add(perfil.getCodigo());
	}

}
