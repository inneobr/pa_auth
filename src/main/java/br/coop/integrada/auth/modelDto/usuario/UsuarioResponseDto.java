package br.coop.integrada.auth.modelDto.usuario;

import lombok.Data;

@Data
public class UsuarioResponseDto {

	private String username;
	private Boolean integrated;
	private String message;
	private String exception;

	public static UsuarioResponseDto construir(UsuarioDto usuarioDto, Boolean integrated, String message) {
		return UsuarioResponseDto.construir(usuarioDto, integrated, message, null);
	}
	public static UsuarioResponseDto construir(UsuarioDto usuarioDto, Boolean integrated, String message, String exception) {
		UsuarioResponseDto responseDto = new UsuarioResponseDto();
		responseDto.setUsername(usuarioDto.getUsername());
		responseDto.setIntegrated(integrated);
		responseDto.setMessage(message);
		responseDto.setException(exception);
		return responseDto;
	}
}
