package br.coop.integrada.auth.modelDto.menu;

import br.coop.integrada.auth.model.menu.Menu;
import br.coop.integrada.auth.modelDto.item.ItemSimplesDto;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuSimplesDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String label;
	private String icon;
	private Integer sequencia;	
	private List<ItemSimplesDto> itens = new ArrayList<>();

	public static MenuSimplesDto construir(Menu obj) {
		MenuSimplesDto objDto = new MenuSimplesDto();
		BeanUtils.copyProperties(obj, objDto);
		return objDto;
	}
}
