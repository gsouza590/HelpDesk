package com.gabriel.helpdesk.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import com.gabriel.helpdesk.model.dto.ClienteDto;
import com.gabriel.helpdesk.model.enums.Perfil;
import com.gabriel.helpdesk.model.interfaces.ICliente;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Pessoa implements ICliente {

	private static final long serialVersionUID = 1L;

	public Cliente() {
		super();
		addPerfil(Perfil.CLIENTE);
	}

	public Cliente(Integer id, String nome, String cpf, String email, String senha) {
		super(id, nome, cpf, email, senha);
		addPerfil(Perfil.CLIENTE);
	}

	public Cliente(ClienteDto cli) {
		super();
		this.id = cli.getId();
		this.nome = cli.getNome();
		this.cpf = cli.getCpf();
		this.email = cli.getEmail();
		this.senha = cli.getSenha();
		this.perfis = cli.getPerfis().stream().filter(Objects::nonNull).map(x -> x.getCodigo())
				.collect(Collectors.toSet());
		this.dataCriacao = cli.getDataCriacao();
	}

	@OneToMany(mappedBy = "cliente")
	private List<Chamado> chamados = new ArrayList<>();

	@Override
    public void addChamado(Chamado chamado) {
        if (chamado != null && !chamados.contains(chamado)) {
            chamados.add(chamado);
            chamado.setCliente(this); 
        }
    }


		
	}

