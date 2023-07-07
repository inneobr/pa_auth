package br.coop.integrada.auth.modelDto.usuario;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UsuarioAlterarSenhaDto implements Serializable {

    private static final long serialVersionUID = 1L;

	@NotNull @NotBlank
    private String senhaAntiga;

    @NotNull @NotBlank
    private String senhaNova;
    
}
