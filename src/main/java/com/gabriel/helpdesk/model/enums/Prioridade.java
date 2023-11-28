package com.gabriel.helpdesk.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Prioridade {

	BAIXA(0, "ROLE_BAIXA"), MEDIA(1, "ROLE_MEDIA"), ALTA(2, "ROLE_ALTA");

	private Integer codigo;
	private String descricao;

	public static Prioridade toEnum(Integer cod) {
		if (cod == null) {
			return null;
		}

		for (Prioridade x : Prioridade.values()) {
			if (cod.equals(x.getCodigo()))
				return x;
		}

		throw new IllegalArgumentException("Prioridade inválida");
	}
}
