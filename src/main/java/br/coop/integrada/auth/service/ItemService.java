package br.coop.integrada.auth.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.coop.integrada.auth.AuthApplication;
import br.coop.integrada.auth.model.item.Item;
import br.coop.integrada.auth.model.menu.Menu;
import br.coop.integrada.auth.modelDto.item.ItemDto;
import br.coop.integrada.auth.repository.MenuRep;
import br.coop.integrada.auth.repository.ItemRep;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor @Transactional
public class ItemService {
	private static final Logger logger = LoggerFactory.getLogger(AuthApplication.class);

	@Autowired
	private ItemRep itemRep;

	@Autowired
	private MenuRep menuRep;
	
	public Item cadastrarItem(ItemDto itemDto) {
		if(itemDto.getIdMenu() == null) throw new NullPointerException("Necessário informar o código do menu para cadastrar um submenu!");

		Menu menu = menuRep.getReferenceById(itemDto.getIdMenu());

		if(menu == null) throw new NullPointerException("Menu invalido.");

		Item menuItem = new Item();
		BeanUtils.copyProperties(itemDto, menuItem);
		menuItem.setMenu(menu);
		menuItem.setDataCadastro(new Date());	
		logger.info("Item cadastrado.");
		return itemRep.save(menuItem);
	}
	
	public Item buscarItem(Long idItem) {
		Item item = itemRep.getReferenceById(idItem);
		if(item == null) throw new NullPointerException("Menu item isdisponivel.");
		logger.info("Menu item encontrado...");
		return item;
	}
	
	public Item atualizarItem(ItemDto itemDto) {
		if(itemDto.getId() == null) throw new NullPointerException("Atualização não permitida, id null");

		Item menuItem = new Item();
		menuItem = itemRep.getReferenceById(itemDto.getId());
		BeanUtils.copyProperties(itemDto, menuItem);

		if(menuItem.getMenu() != null && itemDto.getIdMenu() != null && menuItem.getMenu().getId() != itemDto.getIdMenu()) {
			Menu menu = menuRep.getReferenceById(itemDto.getIdMenu());
			if(menu == null) throw new NullPointerException("Menu invalido.");
			menuItem.setMenu(menu);
		}

		menuItem.setDataAtualizacao(new Date());
		menuItem.setDataInativacao(null);
		logger.info("Menu atualizado.");
		return itemRep.save(menuItem);
	}
	
	public void inativarItemMenu(Long idMenu) {
		Item menuItem = itemRep.getReferenceById(idMenu);
		if(menuItem == null) throw new NullPointerException("Menu invalido");
		menuItem.setDataInativacao(new Date());
		menuItem.setDataAtualizacao(new Date());
		logger.info("Menu inativado com sucesso!");
		itemRep.save(menuItem);
	}
	
	public void ativarItemMenu(Long idMenu) {
		Item menuItem = itemRep.getReferenceById(idMenu);
		if(menuItem == null) throw new NullPointerException("Menu invalido");
		menuItem.setDataInativacao(null);
		menuItem.setDataAtualizacao(new Date());
		logger.info("Menu ativado com sucesso!");
		itemRep.save(menuItem);
	}
	
	public List<Item> listarItemMenu() {
		return itemRep.findByAndDataInativacao(null);
	}
	
	public List<Item> listarItemMenuInativo() {
		List<Item> inativos = new ArrayList<>();
		for(Item menuItem:  itemRep.findAll()) {
			if(menuItem.getAtivo() == false) {
				inativos.add(menuItem);
			}
		}
		return inativos;
	}
	
	
}
