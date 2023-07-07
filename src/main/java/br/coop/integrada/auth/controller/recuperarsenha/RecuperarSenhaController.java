package br.coop.integrada.auth.controller.recuperarsenha;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.coop.integrada.auth.exception.ObjectNotFoundException;
import br.coop.integrada.auth.exception.TokenExpiredException;
import br.coop.integrada.auth.exception.TokenNotFoudException;
import br.coop.integrada.auth.exception.UsuarioNotActiveException;
import br.coop.integrada.auth.modelDto.senha.RecuperarSenhaDto;
import br.coop.integrada.auth.service.RecuperarSenhaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/agricola/auth/v1")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "Recuperar Senha", description = "Serviços de recuperação de senha do usuário.")
public class RecuperarSenhaController {
	private final RecuperarSenhaService recuperarSenhaService;
	
	//Enviará um e-mail para o usuário para permitir a troca da senha.
	@GetMapping("/recuperar-senha/{login}")
	public ResponseEntity<String> salvarRecuperarSenha(@PathVariable(name = "login") String login) {
		try {
			String email = recuperarSenhaService.processar(login);
			return new ResponseEntity<>(email, HttpStatus.CREATED);
		} 
		catch (ObjectNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch (UsuarioNotActiveException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
		catch (Exception ex) {
			return new ResponseEntity<>("Não foi possível enviar e-mail no momento, tente mais tarde novamente", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Usuário clicará no link do e-mail e será redicionado a pagina a qual validará o token, se ok, exibirá formulário para edição da senha, caso contrário, mensagem de erro.
	@GetMapping("/recuperar-senha/token/{token}")
	public ResponseEntity<?> buscarCooperadoPorToken(@PathVariable(name = "token") String token) {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(recuperarSenhaService.buscarUsuarioPorToken(token));
		} 
		catch (ObjectNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} 
		catch (TokenNotFoudException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} 
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	//Envio do formulário contendo o login, token e senha nova. Se tudo conferir, mudará a senha
	@PutMapping("/recuperar-senha")
	public ResponseEntity<?> alterarSenha(@RequestBody RecuperarSenhaDto recuperarSenhaDto) {
		try {
			
			recuperarSenhaService.alterarSenha(recuperarSenhaDto);
			
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		catch (TokenNotFoudException e) {
			
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} 
		catch (TokenExpiredException e) {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
		catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
	}
}
