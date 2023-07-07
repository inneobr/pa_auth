package br.coop.integrada.auth.modelDto.usuario;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UsuarioSimplesDto {
	private Long id;
	private String matricula;
    private String codUsuario;
	private String username;
	private String nome;	
	private String regional;	
	private String Estabelecimento;
	private Date dataIntegracao;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Date dataInativacao;	
	
	public Boolean getAtivo() {
		if(this.dataInativacao == null) {
			return true;
		}else {
			return false;
		}
	}
	
}
