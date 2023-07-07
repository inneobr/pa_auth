package br.coop.integrada.auth.controller.usuarios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.modelDto.usuario.UsuarioDto;
import br.coop.integrada.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/agricola/auth/v1/autorizacao")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "Perfis Vs Usuários", description = "Vincular Perfis vs Usuários.")
public class AutorizationController {

	@Autowired
	private UsuarioService usuarioService;
	
	@PostMapping("/autorizar/{idUsuario}")
	public ResponseEntity<String> autorizarVariosPerfil(@PathVariable(name="idUsuario") Long idUsuario, @RequestBody List<Perfil> listaPerfil){
		System.out.println(idUsuario);
		try {
			usuarioService.autorizarLista(idUsuario, listaPerfil);
			return ResponseEntity.ok().body("Autorizando lista de perfis...");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel autorizar a lista!");
		}			
	}
	
	@DeleteMapping("/revogar/{idUsuario}")
	public ResponseEntity<String> revogarVariosPerfil(@PathVariable(name="idUsuario") Long idUsuario, @RequestBody List<Perfil> listaPerfil){
		try {
			usuarioService.revogarLista(idUsuario, listaPerfil);
			return ResponseEntity.ok().body("Revogada lista de perfis...");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel revogar a lista!");
		}			
	}

	@PostMapping("/usuarios/perfil/{idperfil}")
	public ResponseEntity<String> vincularPerfilEmVariosUsuarios(
			@PathVariable(name="idperfil") Long idperfil,
			@RequestBody List<UsuarioDto> usuarios
	){
		try {
			usuarioService.vincularPerfilEmVariosUsuarios(idperfil, usuarios);
			return ResponseEntity.ok().body("Autorizando lista de perfil...");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel autorizar a lista!");
		}
	}
}
