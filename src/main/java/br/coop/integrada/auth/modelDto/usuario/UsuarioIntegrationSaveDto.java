package br.coop.integrada.auth.modelDto.usuario;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class UsuarioIntegrationSaveDto implements Serializable {
    private List<UsuarioDto> usuarios;
}
