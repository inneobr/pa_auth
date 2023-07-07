package br.coop.integrada.auth.validation;

import java.util.*;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import static java.util.Arrays.stream;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import br.coop.integrada.auth.security.JWTUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.filter.OncePerRequestFilter;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter{

	private JWTUtil jwtUtil;

	public AuthorizationFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		
		if(request.getServletPath().equals("/authentication/login") || request.getServletPath().equals("/authentication/refresh")){
			filterChain.doFilter(request, response);
		}
		else {
			String authorizationHeader = request.getHeader(AUTHORIZATION);
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				try {
					String token = authorizationHeader.substring("Bearer ".length());
					UsernamePasswordAuthenticationToken auth = getAuthentication(token);

					if (auth != null) {
						SecurityContextHolder.getContext().setAuthentication(auth);
						filterChain.doFilter(request, response);
					}
				} catch (Exception e) {
					log.info("Token invalido: {}", e.getMessage());
					response.setHeader("message_error", e.getMessage());
					response.setStatus(HttpStatus.FORBIDDEN.value());

					Map<String, String> message_error = new HashMap<>();
					message_error.put("message_error", e.getMessage());
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);

					new ObjectMapper().writeValue(response.getOutputStream(), message_error);
				}
			}
			else {
				filterChain.doFilter(request, response);
			}
		}
	}

	private UsernamePasswordAuthenticationToken getAuthentication(String token) {
		if(jwtUtil.tokenValido(token)) {
			String username = jwtUtil.getUsername(token);
			String[] perfis = jwtUtil.getPerfis(token);

			Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

			stream(perfis).forEach(perfil -> {
				authorities.add(new SimpleGrantedAuthority(perfil));
			});

			return new UsernamePasswordAuthenticationToken(username, null, authorities);
		}

		return null;
	}

}
