package br.coop.integrada.auth.service;

import br.coop.integrada.auth.AuthApplication;
import br.coop.integrada.auth.model.item.Item;
import br.coop.integrada.auth.model.menu.Menu;
import br.coop.integrada.auth.model.theme.Options;
import br.coop.integrada.auth.modelDto.item.ItemSimplesDto;
import br.coop.integrada.auth.modelDto.menu.MenuSimplesDto;
import br.coop.integrada.auth.repository.OptionRep;
import br.coop.integrada.auth.repository.ItemRep;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class OptionsService {	
	private static final Logger logger = LoggerFactory.getLogger(AuthApplication.class);

	@Autowired
	private OptionRep todoOption;
	
	@Autowired
	private ItemRep todoItem;
	
	public Options salvarOptions(Options options) {	
		logger.info("Salvando opções do template...");
		return todoOption.save(options);
	}
	
	public Options buscarOptions(Long idUsuario) {
		Options options = todoOption.getReferenceById(idUsuario);
		if(options == null) throw new NullPointerException("Usuário não possui predefinições");
		logger.info("Buscando opções do template...");
		return todoOption.save(options);
	}

	public List<MenuSimplesDto> buscarMenus(Long idUsuario) {
		logger.info("Carregando menus do usuário...");

		Map<Long, MenuSimplesDto> menus = new HashMap<>();
		List<Item> itens = todoItem.buscarMenuItensPorIdUsuario(idUsuario);

		for(Item item : itens) {
			Menu menu = item.getMenu();

			if(!menus.containsKey(menu.getId())) {
				MenuSimplesDto menuDto = MenuSimplesDto.construir(menu);
				ItemSimplesDto itemDto = ItemSimplesDto.construir(item);
				menuDto.getItens().add(itemDto);
				menus.put(menu.getId(), menuDto);
			}
			else {
				MenuSimplesDto menuDto = menus.get(menu.getId());
				ItemSimplesDto itemDto = ItemSimplesDto.construir(item);
				menuDto.getItens().add(itemDto);
			}
		}

		return menus.values()
				.stream()
				.sorted(Comparator.comparingInt(MenuSimplesDto::getSequencia))
				.collect(Collectors.toList());
	}

}
