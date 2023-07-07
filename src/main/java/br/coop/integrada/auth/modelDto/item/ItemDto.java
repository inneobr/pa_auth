package br.coop.integrada.auth.modelDto.item;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import br.coop.integrada.auth.model.item.Item;
import lombok.Data;

@Data
public class ItemDto {
	private Long id;
	
	private Long idMenu;
	private String icon;
	
	@NotNull(message = "Campo obrigatório!")
	private String label;
	
	@NotNull(message = "Campo obrigatório!")
	private String path;	
	
	private Integer sequencia;

	public static ItemDto construir(Item obj) {
		ItemDto objDto = new ItemDto();
		BeanUtils.copyProperties(obj, objDto);

		if(obj.getMenu() != null) {
			objDto.setIdMenu(obj.getMenu().getId());
		}

		return objDto;
	}

	public static List<ItemDto> construir(List<Item> objs) {
		return objs.stream().map(item -> {
			return ItemDto.construir(item);
		}).collect(Collectors.toList());
	}
}
