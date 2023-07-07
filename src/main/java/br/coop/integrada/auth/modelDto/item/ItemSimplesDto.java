package br.coop.integrada.auth.modelDto.item;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import br.coop.integrada.auth.model.item.Item;

import java.io.Serializable;

@Data
public class ItemSimplesDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String icon;
	private String label;
	private String path;
	private Integer sequencia;

	public static ItemSimplesDto construir(Item obj) {
		ItemSimplesDto objDto = new ItemSimplesDto();
		BeanUtils.copyProperties(obj, objDto);
		return objDto;
	}
}
