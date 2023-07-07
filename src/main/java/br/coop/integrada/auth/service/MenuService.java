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
import br.coop.integrada.auth.model.menu.Menu;
import br.coop.integrada.auth.modelDto.menu.MenuDto;
import br.coop.integrada.auth.repository.MenuRep;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor @Transactional
public class MenuService{
	private static final Logger logger = LoggerFactory.getLogger(AuthApplication.class);

	@Autowired
	private MenuRep menuRep;	
		
	public Menu cadastrarMenu(MenuDto menuDto) {
		Menu menu = new Menu();	
		BeanUtils.copyProperties(menuDto, menu);
		menu.setDataCadastro(new Date());	
		logger.info("Menu cadastrado.");
		return menuRep.save(menu);
	}
	
	public Menu atualizarMenu(MenuDto menuDto) {
		Menu menu = new Menu();
		if(menuDto.getSequencia() == null) throw new NullPointerException("Atualização não permitida, id null");			
		menu = menuRep.getReferenceById(menuDto.getId());
		BeanUtils.copyProperties(menuDto, menu);
		menu.setDataAtualizacao(new Date());
		menu.setDataInativacao(null);
		logger.info("Menu atualizado.");
		return menuRep.save(menu);
	}
	
	public Menu unicoMenu(Long idMenu) {
		Menu menu = menuRep.getReferenceById(idMenu);
		if(menu == null) throw new NullPointerException("Menu invalido");
		logger.info("Menu localizado com sucesso!");
		return menu;
	}

	public void inativarMenu(Long idMenu) {
		Menu menu = menuRep.getReferenceById(idMenu);
		if(menu == null) throw new NullPointerException("Menu invalido");
		menu.setDataInativacao(new Date());
		menu.setDataAtualizacao(new Date());
		logger.info("Menu inativado com sucesso!");
		menuRep.save(menu);
	}
	
	public void ativarMenu(Long idMenu) {
		Menu menu = menuRep.getReferenceById(idMenu);
		if(menu == null) throw new NullPointerException("Menu invalido");
		menu.setDataInativacao(null);
		menu.setDataAtualizacao(new Date());
		logger.info("Menu ativado com sucesso!");
		menuRep.save(menu);
	}

	public List<Menu> listarMenu() {
		return menuRep.findByAndDataInativacao(null);
	}	
	
	public List<Menu> listarMenuInativo() {
		List<Menu> inativos = new ArrayList<>();
		for(Menu menu: menuRep.findAll()) {
			if(menu.getAtivo() == false) {
				inativos.add(menu);
			}
		}
		return inativos;
	}
}
