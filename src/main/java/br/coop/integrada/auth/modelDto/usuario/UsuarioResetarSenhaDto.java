package br.coop.integrada.auth.modelDto.usuario;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UsuarioResetarSenhaDto implements Serializable {

    private static final long serialVersionUID = 1L;

	@NotNull
    private Long id;

    @NotNull
    @NotBlank
    private String novaSenha;
}
