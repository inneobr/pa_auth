package br.coop.integrada.auth.modelDto.usuario;

import lombok.Data;

@Data
public class UsuarioGetDto {
    private Long id;
    private String nome;
    private String username;
    private String codUsuario;  
     
}
