package com.gabriel.helpdesk.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gabriel.helpdesk.model.dto.TecnicoDto;
import com.gabriel.helpdesk.model.enums.Perfil;
import com.gabriel.helpdesk.model.interfaces.ITecnico;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@DiscriminatorValue("TECNICO")
@Entity
public class Tecnico extends Pessoa implements ITecnico {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "tecnico")
	@JsonIgnore
	private List<Chamado> chamados = new ArrayList<>();

	public Tecnico() {
		super();
		addPerfil(Perfil.TECNICO);
	}

	public Tecnico(Integer id, String nome, String cpf, String email, String senha) {
		super(id, nome, cpf, email, senha);
		addPerfil(Perfil.TECNICO);
	}

	public Tecnico(TecnicoDto tec) {
		super();
		this.id = tec.getId();
		this.nome = tec.getNome();
		this.cpf = tec.getCpf();
		this.email = tec.getEmail();
		this.senha = tec.getSenha();
		this.perfis = tec.getPerfis().stream().filter(Objects::nonNull).map(x -> x.getCodigo())
				.collect(Collectors.toSet());
		this.dataCriacao = tec.getDataCriacao();
	}

	@Override
	public void addChamado(Chamado chamado) {
		if (chamado != null && !chamados.contains(chamado)) {
			chamados.add(chamado);
			chamado.setTecnico(this);
		}
	}
}
