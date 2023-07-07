package br.coop.integrada.auth.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.coop.integrada.auth.AuthApplication;
import br.coop.integrada.auth.exception.ObjectNotFoundException;
import br.coop.integrada.auth.model.item.Item;
import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.model.perfil.enums.TipoFiltro;
import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.modelDto.item.ItemDto;
import br.coop.integrada.auth.modelDto.perfil.PerfilDto;
import br.coop.integrada.auth.modelDto.perfil.PerfilSalvarDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioDto;
import br.coop.integrada.auth.query.enums.Situacao;
import br.coop.integrada.auth.repository.ItemRep;
import br.coop.integrada.auth.repository.PerfilRep;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor @Transactional
public class PerfilService{
	private static final Logger logger = LoggerFactory.getLogger(AuthApplication.class);
	
	@Autowired
	private PerfilRep todosPerfil;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ItemRep todoItem;
	
	public Perfil cadastrarPerfil(PerfilSalvarDto objDto) {
		Perfil perfil = new Perfil();
		BeanUtils.copyProperties(objDto, perfil);

		List<Item> menuItens = objDto.getMenuItens().stream()
						.map(itemDto -> {
							Item item = new Item();
							BeanUtils.copyProperties(itemDto, item);
							return item;
						}).collect(Collectors.toList());

		perfil.setMenuItens(menuItens);

		logger.info("Criando perfil...");
		perfil = todosPerfil.save(perfil);


		List<UsuarioDto> usuarioDtos = objDto.getUsuarios();
		logger.info("Vinculando usuários ao perfil...");
		usuarioService.vincularPerfilEmVariosUsuarios(perfil.getId(), usuarioDtos);
		return perfil;
	}
	
	public Perfil atualizarPerfil(PerfilSalvarDto objDto) {
		if(objDto.getId() == null) {
			throw new ObjectNotFoundException("Necessário informar o ID do perfil para atualizar as informações, Tipo: " + Perfil.class.getName());
		}

		Perfil perfil = buscarId(objDto.getId());
		BeanUtils.copyProperties(objDto, perfil);

		List<Item> menuItens = objDto.getMenuItens().stream()
				.map(itemDto -> {
					Item item = new Item();
					BeanUtils.copyProperties(itemDto, item);
					return item;
				}).collect(Collectors.toList());

		perfil.getMenuItens().clear();
		perfil.setMenuItens(menuItens);

		logger.info("Atualizando perfil...");
		perfil = todosPerfil.save(perfil);

		List<UsuarioDto> usuarioDtos = objDto.getUsuarios();
		logger.info("Vinculando usuários ao perfil...");
		usuarioService.vincularPerfilEmVariosUsuarios(perfil.getId(), usuarioDtos);
		return perfil;
	}
	
	public List<Perfil> cadastrarListaPerfil(List<Perfil> listaPerfil) {			
		for(Perfil todos: listaPerfil) {					
			if(todosPerfil.findByNome(todos.getNome()) == null) {
				todos.setDataCadastro(new Date());
				todosPerfil.save(todos);				
			}	
		}
		logger.info("Perfis salvos: {}", listaPerfil.size());
		return listaPerfil;
	}

	public void inativarPerfil(Long idPerfil) {		
		Perfil perfil = todosPerfil.getReferenceById(idPerfil);
		if(perfil == null) throw new NullPointerException("Perfil invalido.");
		logger.info("Perfil inativado.");
		perfil.setDataInativacao(new Date());
		perfil.setDataAtualizacao(new Date());
		todosPerfil.save(perfil);
	}
	
	public void ativarPerfil(Long idPerfil) {		
		Perfil perfil = todosPerfil.getReferenceById(idPerfil);
		if(perfil == null) throw new NullPointerException("Perfil invalido.");
		logger.info("Perfil ativado.");
		perfil.setDataInativacao(null);
		perfil.setDataAtualizacao(new Date());
		todosPerfil.save(perfil);
	}

	public List<Perfil> buscarTodosPerfil() {	
		return todosPerfil.findByAndDataInativacao(null);
	}

	public Perfil buscarId(Long idPerfil) {
		Perfil perfil = todosPerfil.getReferenceById(idPerfil);

		if(perfil == null) {
			throw new ObjectNotFoundException("O perfil não foi encontrado! ID: " + idPerfil + ", Tipo: " + Perfil.class.getName());
		}

		return perfil;
	}

	public Perfil vincularPerfilEmVariosMenuItens(Long idPerfil, List<Item> menuItens) {
		Perfil perfil = todosPerfil.getReferenceById(idPerfil);
		perfil.getMenuItens().clear();

		for(Item menuItem : menuItens) {
			Item item = todoItem.getReferenceById(menuItem.getId());
			perfil.getMenuItens().add(item);
		}

		return todosPerfil.save(perfil);
	}

	public Page<PerfilDto> buscarTodos(String filtro, TipoFiltro tipoFiltro, Situacao situacao, Pageable pageable) {
		Page<Perfil> perfilPage = null;

		if(tipoFiltro.equals(TipoFiltro.USUARIO)) {
			perfilPage = todosPerfil.buscarPerfisPorUsuario(filtro, situacao, pageable);
		}
		else if(tipoFiltro.equals(TipoFiltro.MENU_ITENS)) {
			perfilPage = todosPerfil.buscarPerfisPorMenuItem(filtro, situacao, pageable);
		}
		else {
			perfilPage = todosPerfil.buscarPerfisFiltroGlobal(filtro, situacao, pageable);
		}

		List<PerfilDto> perfilDtoList = PerfilDto.contruir(perfilPage.getContent());
		Page<PerfilDto> perfilDtoPage = new PageImpl<>(
				perfilDtoList,
				pageable,
				perfilPage.getTotalElements()
		);
		return perfilDtoPage;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Page<UsuarioDto> usuariosPorPerfil(Long id, Pageable pageable) {
		Perfil perfil = buscarId(id);
		Page<Usuario> usuarioPage = usuarioService.buscarUsuariosPorPerfil(perfil, pageable);
		List<UsuarioDto> usuarioDtoList = UsuarioDto.construir(usuarioPage.getContent());

		return new PageImpl(usuarioDtoList, pageable, usuarioPage.getTotalElements());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Page<ItemDto> menuItensPorPerfil(Long id, Pageable pageable) {
		Perfil perfil = buscarId(id);
		Page<Item> itemPage = todoItem.buscarItensPorPerfil(perfil.getId(), pageable);
		List<ItemDto> itemDtoList = ItemDto.construir(itemPage.getContent());

		return new PageImpl(itemDtoList, pageable, itemPage.getTotalElements());
	}
}
