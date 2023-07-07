package br.coop.integrada.auth.modelDto.perfil;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import br.coop.integrada.auth.modelDto.item.ItemDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioDto;
import lombok.Data;

@Data
public class PerfilSalvarDto implements Serializable {
    private static final long serialVersionUID = 1L;

	private Long id;

    @NotNull @NotBlank
    private String nome;

    @NotNull @NotBlank
    private String descricao;

    private List<ItemDto> menuItens;
    private List<UsuarioDto> usuarios;
}
