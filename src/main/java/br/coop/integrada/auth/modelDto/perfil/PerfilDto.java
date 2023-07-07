package br.coop.integrada.auth.modelDto.perfil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import br.coop.integrada.auth.model.perfil.Perfil;
import lombok.Data;

@Data
public class PerfilDto implements Serializable {
    private static final long serialVersionUID = 1L;
	private Long id;
    private String nome;
    private String descricao;
    private Date dataCadastro;
    private Date dataAtualizacao;
    private Boolean ativo;

    public static PerfilDto construir(Perfil perfil) {
        PerfilDto perfilDto = new PerfilDto();
        BeanUtils.copyProperties(perfil, perfilDto);
        return perfilDto;
    }

    public static List<PerfilDto> contruir(List<Perfil> perfis) {
        return perfis.stream()
                .map(perfil -> {
                    return PerfilDto.construir(perfil);
                })
                .collect(Collectors.toList());
    }
}
