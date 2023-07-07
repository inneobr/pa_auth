package br.coop.integrada.auth.modelDto.menu;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class MenuDto {
	private Long id;		

	@NotNull(message = "Campo obrigatório!")
	private String label;
	
	@NotNull(message = "Campo obrigatório!")
	private String icon;
	
	private Integer sequencia;
}
