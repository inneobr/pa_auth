package br.coop.integrada.auth.modelDto.usuario;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.BeanUtils;

import br.coop.integrada.auth.model.usuario.Usuario;
import lombok.Data;

@Data
public class UsuarioDto {
	private Long id;
	
	private String matricula;
	
	private String codUsuario;
	
	@NotNull @NotBlank
	private String username;

	@CPF
	private String cpf;	

	private String nome; 	

	private String regional;
	
	private String estabelecimento;
	
	private String status;
	
	private String email;

	private Boolean ativo;
	
	private Boolean trocarSenha;

	public static UsuarioDto construir(Usuario usuario) {
		UsuarioDto usuarioDto = new UsuarioDto();
		BeanUtils.copyProperties(usuario, usuarioDto);
		return usuarioDto;
	}

	public static List<UsuarioDto> construir(List<Usuario> usuarios) {
		List<UsuarioDto> listUsuarioDto = new ArrayList<>();

		for(Usuario usuario : usuarios) {
			UsuarioDto usuarioDto = UsuarioDto.construir(usuario);
			listUsuarioDto.add(usuarioDto);
		}

		return listUsuarioDto;
	}
}
