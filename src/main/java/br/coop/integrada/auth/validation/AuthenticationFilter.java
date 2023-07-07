package br.coop.integrada.auth.validation;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.FilterChain;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import org.springframework.http.MediaType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import br.coop.integrada.auth.security.JWTUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.coop.integrada.auth.model.usuario.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	public static final String secret_token = "463408a1-54c9-4307-bb1c-6cced559f5a7";	
	private final AuthenticationManager authenticationManager;

	private JWTUtil jwtUtil;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request,
			HttpServletResponse response
	) throws AuthenticationException {

		try{
			Usuario usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(usuario.getUsername(), usuario.getPassword(), new ArrayList<>());
			return authenticationManager.authenticate(authenticationToken);
		}catch(IOException e) {
			log.info("Falha na leitura do usuario e senha: ", e);
			throw new RuntimeException("Dados incorretos: " + e);
		}
	}
	
	@Override
	protected void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authentication
	) throws IOException {

		User user = (User) authentication.getPrincipal();
		
		Map<String, String> tokens = jwtUtil.gerarTokens(
				user.getUsername(),
				request.getRequestURL().toString(),
				user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
		);

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}
	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		log.info("Dados incorretos! tente novamente.");
		Map<String, String> mensage_error = new HashMap<>();
		mensage_error.put("mensage_error", "Dados incorretos, tente novamente!");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), mensage_error);		
	}
}
