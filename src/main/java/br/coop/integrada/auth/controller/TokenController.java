package br.coop.integrada.auth.controller;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.coop.integrada.auth.security.JWTUtil;
import br.coop.integrada.auth.service.UsuarioService;
import br.coop.integrada.auth.model.token.Token;
import br.coop.integrada.auth.model.perfil.Perfil;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.coop.integrada.auth.model.usuario.Usuario;
import com.fasterxml.jackson.databind.DatabindException;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "Token", description = "Serviço de atualização de token.")
public class TokenController {
	private final UsuarioService usuarioService;

	@Autowired
	private JWTUtil jwtUtil;
	
	@PostMapping("/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException{	
		Token token = new ObjectMapper().readValue(request.getInputStream(), Token.class);
		
		if(token != null) {
			try {
				String refreshToken = token.getCurrentToken().substring("Bearer ".length());
				String username = jwtUtil.getUsername(refreshToken);
				Usuario usuario = usuarioService.buscarUnicoUsuario(username);

				Map<String, String> tokens = jwtUtil.gerarTokens(
						usuario.getUsername(),
						request.getRequestURL().toString(),
						usuario.getListPerfil().stream().map(Perfil::getNome).collect(Collectors.toList())
				);

				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
			}catch( Exception e ) {
				response.setHeader("mensage_error",e.getMessage());
				response.setStatus(HttpStatus.FORBIDDEN.value());
				
				Map<String, String> error = new HashMap<>();		
				error.put("mensage_error", e.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		}else {
			throw new RuntimeException("Seu Token precisa de atualização!");
		}
	}
}