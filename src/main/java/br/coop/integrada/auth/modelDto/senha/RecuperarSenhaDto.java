package br.coop.integrada.auth.modelDto.senha;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecuperarSenhaDto {
	private String username;
	private String token;
	private String senha;
}
