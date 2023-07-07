package br.coop.integrada.auth.controller.usuarios;

import br.coop.integrada.auth.query.enums.Situacao;
import lombok.Data;

@Data
public class UsuarioFiltro {
	private String pesquisar;
	private Situacao situacao;
}
