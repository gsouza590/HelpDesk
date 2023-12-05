package com.gabriel.helpdesk.model.dto;

import java.io.Serializable;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabriel.helpdesk.model.Chamado;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChamadoDto implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataAbertura = LocalDate.now();
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataFechamento;
	@NotNull(message= "O campo não pode ser nulo")
	private Integer prioridade;
	@NotNull(message= "O campo não pode ser nulo")
	private Integer status;
	@NotNull(message= "O campo não pode ser nulo")
	private String titulo;
	@NotNull(message= "O campo não pode ser nulo")
	private String observacoes;
	@NotNull(message= "O campo não pode ser nulo")
	private Integer tecnico;
	@NotNull(message= "O campo não pode ser nulo")
	private Integer cliente;
	private String nomeTecnico;
	private String nomeCliente;

	public ChamadoDto(Chamado ch) {
		super();
		this.id = ch.getId();
		this.dataAbertura = ch.getDataAbertura();
		this.dataFechamento = ch.getDataFechamento();
		this.prioridade = ch.getPrioridade().getCodigo();
		this.status = ch.getStatus().getCodigo();
		this.titulo = ch.getTitulo();
		this.observacoes = ch.getObservacoes();
		this.tecnico = ch.getTecnico().getId();
		this.cliente = ch.getCliente().getId();
		this.nomeTecnico = ch.getTecnico().getNome();
		this.nomeCliente = ch.getCliente().getNome();
	}

}
