package br.coop.integrada.auth.controller.usuarios;

import br.coop.integrada.auth.model.usuario.*;
import br.coop.integrada.auth.modelDto.usuario.UsuarioAlterarSenhaDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioIntegrationSaveDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioResetarSenhaDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioResponseDto;
import br.coop.integrada.auth.query.enums.Situacao;
import br.coop.integrada.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/agricola/auth/v1/usuario")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "Usuário", description = "Serviços de authenticação de usuário.")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping
	public ResponseEntity<UsuarioResponseDto> cadastrarUsuario(@RequestBody @Valid UsuarioDto usuarioDto){	
				Usuario usuario = usuarioService.findById(usuarioDto.getId());
		if(usuario != null) {
			usuarioService.atualizarUsuario(usuario, usuarioDto);
			return ResponseEntity.ok().body(UsuarioResponseDto.construir(usuarioDto, true, "Usuário atualizado com sucesso."));
		}else {
			usuarioService.cadastrarUsuario(usuarioDto);
			return ResponseEntity.ok().body(UsuarioResponseDto.construir(usuarioDto, true, "Usuário cadastrado com sucesso."));
		}
	}
	
	@PutMapping
	public ResponseEntity<UsuarioResponseDto> atualizarUsuario(@RequestBody @Valid UsuarioDto usuarioDto){
		try {
			Usuario usuario = usuarioService.findByUsername(usuarioDto.getUsername());
			usuario.setUsername(usuario.getUsername().replaceAll(" ", "").toLowerCase());
			usuarioService.atualizarUsuario(usuario, usuarioDto);
			return ResponseEntity.ok().body(UsuarioResponseDto.construir(usuarioDto, true, "Usuário atualizado com sucesso."));		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(UsuarioResponseDto.construir(usuarioDto, false, e.getMessage()));
		}
	}
	
	@GetMapping("/nome/{nome}")
	public ResponseEntity<List<Usuario>> buscarNome(@PathVariable(name="nome") String nome){
		return new ResponseEntity<List<Usuario>>(usuarioService.buscarNome(nome), HttpStatus.ACCEPTED);		
	}
	
	@GetMapping("/username/{username}")
	public ResponseEntity<Usuario> buscarUnico(@PathVariable(name="username") String username){
		return new ResponseEntity<Usuario>(usuarioService.buscarUnicoUsuario(username), HttpStatus.ACCEPTED);		
	}
	
	@GetMapping("/matricula/{matricula}")
	public ResponseEntity<Usuario> buscarMatricula(@PathVariable(name="matricula") String matriculaUsuario){
		return new ResponseEntity<Usuario>(usuarioService.buscarMatriculaUsuario(matriculaUsuario), HttpStatus.ACCEPTED);		
	}
	
	@GetMapping("/perfil/{nomePerfil}")
	public ResponseEntity<List<Usuario>> buscarUmDoPerfil(@PathVariable(name="nomePerfil") String nomePerfil){
		return new ResponseEntity<List<Usuario>>(usuarioService.buscarUsuarioDoPerfil(nomePerfil), HttpStatus.ACCEPTED);		
	}

	@GetMapping("/perfil/id/{idPerfil}")
	public ResponseEntity<List<UsuarioDto>> buscarUsuariosPorPerfil(@PathVariable(name="idPerfil") Long idPerfil){
		List<Usuario> usuarios = usuarioService.buscarUsuariosPorPerfil(idPerfil);
		List<UsuarioDto> usuariosDto = usuarios.stream().map(usuarioItem -> {
			UsuarioDto usuarioDto = new UsuarioDto();
			BeanUtils.copyProperties(usuarioItem, usuarioDto);
			return usuarioDto;
		}).collect(Collectors.toList());
		return new ResponseEntity<List<UsuarioDto>>(usuariosDto, HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/inativar/{idUsuario}")
	public ResponseEntity<String> inativarUsuario(@PathVariable(name="idUsuario") Long idUsuario){
		try {
			usuarioService.inativarUsuario(idUsuario);
			return ResponseEntity.ok().body("Usuário inativado com sucesso!");	
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel inativar o usuário!");
		}			
	}
	
	@PutMapping("/ativar/{idUsuario}")
	public ResponseEntity<String> ativarUsuario(@PathVariable(name="idUsuario") Long idUsuario){
		try {
			usuarioService.ativarUsuario(idUsuario);
			return ResponseEntity.ok().body("Usuário ativado com sucesso!");	
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel ativar o usuário!");
		}			
	}
	
	@GetMapping
	public ResponseEntity<List<Usuario>> listarTodos(){
		return new ResponseEntity<List<Usuario>>(usuarioService.buscarTodosUsuario(), HttpStatus.ACCEPTED);		
	}
	
	@PostMapping("/salvar-lista")
	public ResponseEntity<List<UsuarioResponseDto>> salvarListaDeUsuarios(@RequestBody List<UsuarioDto> listaUsuario){
		return new ResponseEntity<List<UsuarioResponseDto>>(usuarioService.salvarListaDeUsuarios(listaUsuario), HttpStatus.OK);
	}

	@PostMapping("/integration/save-all")
	public ResponseEntity<List<UsuarioResponseDto>> salvarVariosUsuarios(@RequestBody UsuarioIntegrationSaveDto objDto){
		List<UsuarioResponseDto> response = new ArrayList<>();

		for(UsuarioDto usuarioDto: objDto.getUsuarios()) {
			try {
				if(usuarioDto.getUsername() == null || usuarioDto.getUsername().isEmpty()) {
					throw new NullPointerException("O {username} é obrigatório!");
				}

				Usuario usuario = usuarioService.findByUsername(usuarioDto.getUsername());

				if(usuario != null) {
					usuarioService.atualizarUsuario(usuario, usuarioDto);
					response.add(UsuarioResponseDto.construir(usuarioDto, true, "Usuário atualizado com sucesso."));
				}else {
					usuarioService.cadastrarUsuario(usuarioDto);
					response.add(UsuarioResponseDto.construir(usuarioDto, true, "Usuário cadastrado com sucesso."));
				}
			}
			catch(ConstraintViolationException e) {
				response.add(UsuarioResponseDto.construir(usuarioDto, false, "Falha ao salvar, violação de restrição!", e.getMessage()));
			}
			catch(DataIntegrityViolationException e) {
				response.add(UsuarioResponseDto.construir(usuarioDto, false, "Falha ao salvar, violação de integridade de dados!", e.getMessage()));
			}
			catch(Exception e) {
				response.add(UsuarioResponseDto.construir(usuarioDto, false, "Falha ao salvar", e.getMessage()));
			}
		}

		return ResponseEntity.ok(response);
	}	

	@GetMapping("/{id}/perfis")
	public ResponseEntity<?> buscarPerfisPorUsuario(
			@PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
			@PathVariable(name="id") Long idUsuario){
		return ResponseEntity.ok(usuarioService.buscarPerfisPorUsuario(idUsuario, pageable));
	}

	@PutMapping("/resetar-senha")
	public ResponseEntity<?> resetarSenha(@RequestBody @Valid UsuarioResetarSenhaDto objDto){
		usuarioService.resetarSenha(objDto);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/alterar-senha")
	public ResponseEntity<?> resetarSenha(@RequestBody @Valid UsuarioAlterarSenhaDto objDto){
		try{
			usuarioService.alterarSenhaUsuario(objDto);
			return ResponseEntity.ok("Senha alterada com sucesso");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel alterar a senha deste usuário.");
		}
		
	}
	
	@GetMapping("/busca-paginada")
	public ResponseEntity<?> buscarTodosPaginado(
		@PageableDefault(page = 0, size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
		@RequestParam(value="buscar", defaultValue = "") String filtro,
		@RequestParam(value="situacao", defaultValue = "ATIVO") Situacao situacao){
			Page<UsuarioDto> pageUsuarioDto = usuarioService.buscarTodos(filtro, situacao, pageable);
			return ResponseEntity.ok(pageUsuarioDto);
	}
	
	@GetMapping("/filtro/descricao")
	public ResponseEntity<?> buscarTodosPaginado(
			UsuarioFiltro filter,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
		return ResponseEntity.ok(usuarioService.findByFilterDescricao(filter, pageable));
	}
	
	@GetMapping("/{dados}")
	public ResponseEntity<?> findBySearch(@PathVariable(name="dados") String dados){
		return ResponseEntity.ok(usuarioService.findBySearch(dados));
	}
}
