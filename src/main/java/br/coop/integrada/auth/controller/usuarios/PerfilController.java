package br.coop.integrada.auth.controller.usuarios;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.coop.integrada.auth.model.item.Item;
import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.model.perfil.enums.TipoFiltro;
import br.coop.integrada.auth.modelDto.item.ItemDto;
import br.coop.integrada.auth.modelDto.perfil.PerfilDto;
import br.coop.integrada.auth.modelDto.perfil.PerfilSalvarDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioDto;
import br.coop.integrada.auth.query.enums.Situacao;
import br.coop.integrada.auth.service.PerfilService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/agricola/auth/v1/perfil")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "Perfil", description = "Serviçoes de perfil e permições.")
public class PerfilController {
	@Autowired
	private PerfilService perfilService;
	
	@PostMapping
	public ResponseEntity<?> cadastrarPerfil(@RequestBody @Valid PerfilSalvarDto perfil){
		Perfil perfilRet = perfilService.cadastrarPerfil(perfil);
		return ResponseEntity.ok().body("{\"mensagem\": \"Perfil cadastrado com sucesso!\", \"perfilId\":" + perfilRet.getId() + "}");
	}
	
	@GetMapping
	public ResponseEntity<List<Perfil>> listarPerfis(){
		return new ResponseEntity<List<Perfil>>(perfilService.buscarTodosPerfil(), HttpStatus.ACCEPTED);		
	}
	
	@PutMapping("/ativar/{idPerfil}")
	public ResponseEntity<?> ativarPerfil(@PathVariable(name="idPerfil") Long idPerfil){
		try {
			perfilService.ativarPerfil(idPerfil);
			return new ResponseEntity<>("Perfil ativado!", HttpStatus.CREATED);
		}catch(Exception e) {
			return new ResponseEntity<>("não foi possivel ativar o perfil!", HttpStatus.BAD_REQUEST);
		}		
	}
	
	@PutMapping("/inativar/{idPerfil}")
	public ResponseEntity<?> inativarPerfil(@PathVariable(name="idPerfil") Long idPerfil){
		try {
			perfilService.inativarPerfil(idPerfil);
			return new ResponseEntity<>("Perfil inativado!", HttpStatus.CREATED);
		}catch(Exception e) {
			return new ResponseEntity<>("não foi possivel inativar o perfil!", HttpStatus.BAD_REQUEST);
		}		
	}
	
	@GetMapping("/id/{idPerfil}")
	public ResponseEntity<Perfil> buscarId(@PathVariable(name="idPerfil") Long idPerfil){
		return new ResponseEntity<Perfil>(perfilService.buscarId(idPerfil), HttpStatus.ACCEPTED);	
	}
	
	@PutMapping
	public ResponseEntity<?> atualizarPerfil(@RequestBody @Valid PerfilSalvarDto perfil){
		perfilService.atualizarPerfil(perfil);
		return ResponseEntity.ok().body("Perfil atualizado com sucesso!");
	}
	
	@PostMapping("/cadastrar-varios")
	public ResponseEntity<?> cadastrarPerfil(@RequestBody @Valid List<Perfil> lista){
		try {
			perfilService.cadastrarListaPerfil(lista);
			return ResponseEntity.ok().body("lista de perfil cadastrado com sucesso!");
		}catch(Exception e){
			return ResponseEntity.ok().body("Não foi possivel cadastrar a lista de perfil.");
		}
	}

	@PostMapping("/vincular-menu-item/{idPerfil}")
	public ResponseEntity<String> vincularVariosMenuItensNoPerfil(@PathVariable(name="idPerfil")Long idPerfil, @RequestBody List<Item> menuItens){
		try {
			perfilService.vincularPerfilEmVariosMenuItens(idPerfil, menuItens);
			return ResponseEntity.ok().body("Autorizando lista de perfis...");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel autorizar a lista!");
		}
	}

	@GetMapping("/busca-paginada")
	public ResponseEntity<?> buscarTodosPaginado(
			@PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam(value="buscar", defaultValue = "") String filtro,
			@RequestParam(value="tipoFiltro", defaultValue = "PERFIL") TipoFiltro tipoFiltro,
			@RequestParam(value="situacao", defaultValue = "ATIVO") Situacao situacao
	){
		Page<PerfilDto> perfilDtoPage = perfilService.buscarTodos(filtro, tipoFiltro, situacao, pageable);
		return ResponseEntity.ok(perfilDtoPage);
	}

	@GetMapping("/{id}/usuarios-vinculados")
	public ResponseEntity<?> usuariosPorPerfil(
			@PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
			@PathVariable(name="id")Long id
	){
		Page<UsuarioDto> usuarioDtoPage = perfilService.usuariosPorPerfil(id, pageable);
		return ResponseEntity.ok(usuarioDtoPage);
	}

	@GetMapping("/{id}/menu-itens-vinculados")
	public ResponseEntity<?> menuItensPorPerfil(
			@PageableDefault(page = 0, size = 10, sort = "label", direction = Sort.Direction.ASC) Pageable pageable,
			@PathVariable(name="id")Long id
	){
		Page<ItemDto> itemDtoPage = perfilService.menuItensPorPerfil(id, pageable);
		return ResponseEntity.ok(itemDtoPage);
	}
}
