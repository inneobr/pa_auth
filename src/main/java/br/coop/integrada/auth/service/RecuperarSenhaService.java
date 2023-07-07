package br.coop.integrada.auth.service;


import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.coop.integrada.auth.exception.ObjectNotFoundException;
import br.coop.integrada.auth.exception.TokenExpiredException;
import br.coop.integrada.auth.exception.TokenNotFoudException;
import br.coop.integrada.auth.model.recuperarsenha.RecuperarSenha;
import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.modelDto.senha.RecuperarSenhaDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioDto;
import br.coop.integrada.auth.property.AutenticacaoProperty;
import br.coop.integrada.auth.repository.EmailService;
import br.coop.integrada.auth.repository.RecuperarSenhaRep;
import br.coop.integrada.auth.repository.UsuarioRep;



@Service
public class RecuperarSenhaService {
	
	@Autowired
	private UsuarioRep todosUsuario;
	
	@Autowired
	private RecuperarSenhaRep recuperarSenhaRepository;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private AutenticacaoProperty autenticacaoProperty;
	
	@Autowired
	private EmailService emailService;
	
	
	public String processar(String username) throws ObjectNotFoundException, Exception {
		
		Usuario usuario = todosUsuario.findByUsername(username);
		
		if(usuario == null){
			throw new ObjectNotFoundException("O usuário não foi encontrado! Username: " + username);
		}
		
		if(usuario.getEmail() == null) {
			throw new ObjectNotFoundException("Favor, entrar em contato com a TI e solicitar o reset da senha.");
		}
			
		
				
		
		RecuperarSenha recuperarSenha = RecuperarSenha.construir(username);
		
		recuperarSenhaRepository.save(recuperarSenha);
		
		enviarEmail(usuario.getEmail(), usuario.getNome(), recuperarSenha.getToken());
		
		return mascararEmail(usuario.getEmail());
	}
	
	
	private void enviarEmail(String destinatario, String nomeUsuario, String token) throws Exception {

		String url = autenticacaoProperty.getUrlSistema() + "/#/recuperar-senha?token=" + token;
		
		String assunto = "Redefinir senha Gênesis";		
		String mensagem = "Caro " + toTitledCase(nomeUsuario) + ", "
				+ " <br /><br />Recebemos uma solicitação "
				+ "de redefinição de sua senha do Gênesis."
				+ " <br /><br />Se foi você quem solicitou, "
				+ " <a href=\""+ url +"\">clique aqui</a>. Se o link não funcionar, "
				+ " copie o link abaixo e cole no navegador: "
				+ " <br />" + url
				+ " <br /><br />Caso você ignore essa mensagem, sua senha não será alterada.";

		emailService.sendHtmlEmail(destinatario, assunto, mensagem);
	}
	
	private String toTitledCase(String nome) {

	    String[] words = nome.split("\\s");
	    StringBuilder builder = new StringBuilder();

	    for (int i = 0; i < words.length; i++) {
	    	builder.append(words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase() + " ");
	    } 
	    return builder.toString().trim();
	}
	
	private String mascararEmail(String email) {
		String nomeEmail = email.split("@")[0];
		String dominioEmail = email.split("@")[1];
		String caracterInicial = nomeEmail.substring(0, 1);
		String novoEmail = StringUtils.rightPad(caracterInicial, (nomeEmail.length() - 1), "*");
		
		return novoEmail.concat("@").concat(dominioEmail);
	}
	
	public UsuarioDto buscarUsuarioPorToken(String token) throws TokenNotFoudException, TokenExpiredException, Exception {
		
		RecuperarSenha recuperarSenha = recuperarSenhaRepository.findByToken(token).orElseThrow(() -> new TokenNotFoudException("Token inválido."));
		
		this.validarRecuperaSenha(recuperarSenha);
				
		Usuario usuario = todosUsuario.findByUsername(recuperarSenha.getUsername());
		
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setUsername(usuario.getUsername());
		usuarioDto.setNome(usuario.getNome());
		
		return usuarioDto;
	}

	
	public void alterarSenha(RecuperarSenhaDto recuperarSenhaDto) throws TokenNotFoudException, TokenExpiredException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		RecuperarSenha recuperarSenha = validarToken(recuperarSenhaDto.getToken(), recuperarSenhaDto.getUsername());
		
		usuarioService.recuperarSenha(recuperarSenhaDto);
		
		//Adiciona a data de utilização ao token.
		inativarToken(recuperarSenha);
	}
	
	private void validarRecuperaSenha(RecuperarSenha recuperarSenha) throws TokenNotFoudException, TokenExpiredException {
		
		if(recuperarSenha == null) {
			throw new TokenNotFoudException("Token não encontrado.");
		}
		
		LocalDateTime dataAgora = LocalDateTime.now();
		
		if (dataAgora.isAfter(recuperarSenha.getDataExpira())) {
			throw new TokenExpiredException("Token expirado.");
		}
		
		if(recuperarSenha.getDataRecuperacao() != null) {
			throw new TokenExpiredException("Token já utilizado.");
		}
		
		if(recuperarSenha.getDataInativacao() != null) {
			throw new TokenExpiredException("Token inutilizado.");
		}
		
	}
	
	private RecuperarSenha validarToken(String token, String username) throws TokenNotFoudException, TokenExpiredException {
				
		Optional<RecuperarSenha> recuperarSenha = recuperarSenhaRepository.findByToken(token);
		
		if(recuperarSenha.get() == null) {
			throw new TokenNotFoudException("Token não encontrado.");
		}
		
		LocalDateTime dataAgora = LocalDateTime.now();
		
		if (dataAgora.isAfter(recuperarSenha.get().getDataExpira())) {
			throw new TokenExpiredException("Token expirado.");
		}
		
		if(recuperarSenha.get().getDataRecuperacao() != null) {
			throw new TokenExpiredException("Token já utilizado.");
		}
		
		if(recuperarSenha.get().getDataInativacao() != null) {
			throw new TokenExpiredException("Token inutilizado.");
		}
		
		if(!recuperarSenha.get().getUsername().equals(username)) {
			throw new TokenNotFoudException("Token não encontrado.");
		}
				
		return recuperarSenha.get();
	}
	
	
	private void inativarToken(RecuperarSenha recuperarSenha) throws TokenNotFoudException {
		recuperarSenha.setDataRecuperacao(LocalDateTime.now());
		recuperarSenhaRepository.save(recuperarSenha);
	}
	

}
