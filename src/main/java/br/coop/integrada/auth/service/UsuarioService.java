package br.coop.integrada.auth.service;

import br.coop.integrada.auth.AuthApplication;
import br.coop.integrada.auth.controller.usuarios.UsuarioFiltro;
import br.coop.integrada.auth.exception.ObjectNotFoundException;
import br.coop.integrada.auth.model.historico.historicoAcesso.HistoricoAcessoService;
import br.coop.integrada.auth.model.historico.historicoAcessoFuncionalidadesService.HistoricoAcessoFuncionalidadesService;
import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.modelDto.perfil.PerfilDto;
import br.coop.integrada.auth.modelDto.senha.RecuperarSenhaDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioAlterarSenhaDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioResetarSenhaDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioResponseDto;
import br.coop.integrada.auth.modelDto.usuario.UsuarioSimplesDto;
import br.coop.integrada.auth.query.UsuarioSpec;
import br.coop.integrada.auth.query.enums.Situacao;
import br.coop.integrada.auth.repository.PerfilRep;
import br.coop.integrada.auth.repository.UsuarioRep;
import br.coop.integrada.auth.settings.Util;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class UsuarioService implements UserDetailsService{
	private static final Logger logger = LoggerFactory.getLogger(AuthApplication.class);
	private final PasswordEncoder passwordEncoder;
	Util util;
	
	@Autowired
	private UsuarioRep usuarioRep;	
	
	@Autowired
	private PerfilRep perfilRep;
	
	@Autowired
	private HistoricoAcessoService historicoAcessoService;
	
	@Autowired
	private HistoricoAcessoFuncionalidadesService historicoAcessoFuncionalidadesService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRep.findByUsername(username);

		if(usuario != null) {
			Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
			
			if(usuario.getListPerfil() != null) {
				usuario.getListPerfil().forEach(perfil -> { 
					authorities.add(new SimpleGrantedAuthority(perfil.getNome())); 
				});	
			}
			
			historicoAcessoService.salvarHistorico(usuario.getId());
			historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());
			logger.info("Procurando usuário, Por favor aguarde... ");
			return new org.springframework.security.core.userdetails.User(usuario.getUsername(),usuario.getPassword(), authorities);
		}else {
			return null;
		}
	}	
	
	public Usuario cadastrarUsuario(UsuarioDto usuarioDto) {
		usuarioDto.setUsername(usuarioDto.getUsername().toLowerCase());
		if(usuarioDto.getUsername() == null) {
			throw new NullPointerException("Necessário informar o username!");
		}	
		
		Usuario usuario = new Usuario();		
		BeanUtils.copyProperties(usuarioDto, usuario);
		logger.info(usuario.getEmail());
		usuario.setCodUsuario(usuario.getUsername());
		String senhaCriptografada = passwordEncoder.encode("@" + usuario.getUsername());
		usuario.setPassword(senhaCriptografada);
		usuario.setTrocarSenha(true);
		usuario = usuarioRep.save(usuario);				
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(),Thread.currentThread().getStackTrace());		
		logger.info("Novo Usuário cadastrado!");			
		return usuario;
		
	}
	
	public Usuario atualizarUsuario(Usuario usuario, UsuarioDto usuarioDto) {
		usuarioDto.setCodUsuario(usuario.getUsername());
		logger.info(usuarioDto.getEmail());
		Long id = usuario.getId();
		BeanUtils.copyProperties(usuarioDto, usuario);
		logger.info(usuario.getEmail());
		usuario.setId(id);
		usuario.setTrocarSenha(false);
		usuario.setCpf(usuarioDto.getCpf() != null ? usuarioDto.getCpf().replace(".", "").replace("-", "").replace(",", "") : usuarioDto.getCpf());		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());		
		logger.info("Usuário atualizado!");
		return usuarioRep.save(usuario);		
	}
		
	public List<UsuarioResponseDto> salvarListaDeUsuarios(List<UsuarioDto> listaUsuarios) {
		List<UsuarioResponseDto> response = new ArrayList<>();
		
		for(UsuarioDto usuarioDto: listaUsuarios) {			
			try {		
				Usuario usuario = usuarioRep.findByUsername(usuarioDto.getUsername());
				if(usuario != null) {
					atualizarUsuario(usuario, usuarioDto);
					response.add(UsuarioResponseDto.construir(usuarioDto, true, "Usuário atualizado com sucesso."));
				}else {
					cadastrarUsuario(usuarioDto);
					response.add(UsuarioResponseDto.construir(usuarioDto, true, "Usuário cadastrado com sucesso."));
				}
			}catch(Exception e) {				
				response.add(UsuarioResponseDto.construir(usuarioDto, false, e.getMessage()));
				logger.info("Usuário não cadastrado:"+ usuarioDto.getUsername() + ", Erro: "+ e.getMessage());
			}
		}
		return response;
	}
	
	public List<Usuario> buscarNome(String nome) {
		List<Usuario> usuario = new ArrayList<>();
			usuario = usuarioRep.findByNomeContainingIgnoreCaseOrCpfContainingIgnoreCase(nome, nome);
			if(usuario.isEmpty()) throw new NullPointerException("Usuário não encontrado!");	
			logger.info("Usuário encontrado: {}", usuario.size());
			return usuario;
	}
	
	public Usuario buscarUnicoUsuario(String username) {
		Usuario usuario = usuarioRep.findByUsernameIgnoreCase(username);
		if(usuario == null) throw new NullPointerException("Usuário não encontrado!");
		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());		
		logger.info("Usuário encontrado. {}", usuario.getUsername());
		return usuario;
	}
	
	public Usuario findByUsername(String username) {
		return usuarioRep.findByUsername(username);
	}	
	
	public Usuario findById(Long id) {
		return usuarioRep.getReferenceById(id);
	}	
	
	public Usuario buscarMatriculaUsuario(String matriculaUsuario) {
		Usuario usuario = usuarioRep.findByMatricula(matriculaUsuario);
		if(usuario == null) throw new NullPointerException("Usuário não encontrado!");
		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());		
		logger.info("Usuário encontrado: {}", usuario.getUsername());
		return usuario;
	}
	
	public void inativarUsuario(Long idUsuario) {		
		Usuario usuario = usuarioRep.getReferenceById(idUsuario);
		if(usuario == null) throw new NullPointerException("Usuário não encontrado!");
		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());		
		usuario.setDataInativacao(new Date());
		usuario.setDataAtualizacao(new Date());
		logger.info("Usuário inativado.");
		usuarioRep.save(usuario);
	}
	
	public void ativarUsuario(Long idUsuario) {		
		Usuario usuario = usuarioRep.getReferenceById(idUsuario);
		if(usuario == null) throw new NullPointerException("Usuário não encontrado!");
		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());		
		usuario.setDataInativacao(null);
		usuario.setDataAtualizacao(new Date());
		logger.info("Usuário inativado.");
		usuarioRep.save(usuario);
	}

	public List<Usuario> buscarTodosUsuario() {	
		List<Usuario> usuario = usuarioRep.findAll();
		if(usuario.isEmpty()) throw new NullPointerException("Não existem Usuários cadastrados!");
		
		logger.info("Usuários encontrados: {}", usuario.size());
		return usuario;
	}
	

	public Usuario autorizarUsuario(Long idUsuario, Long idPerfil) {		
		Usuario usuario =  usuarioRep.getReferenceById(idUsuario);
		Perfil perfil = perfilRep.getReferenceById(idPerfil);		
		if(usuario == null || perfil == null) throw new NullPointerException("Usuário ou Perfil invalido.!");
		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());		
		logger.info("Usuário: {} recebeu novo perfil: {}", usuario, perfil);
		if(usuario.getListPerfil().isEmpty()) {
			usuario.getListPerfil().add(perfil);
		}else {
			if(usuarioRep.findByListPerfilAndDataInativacao(perfil, null).isEmpty()) {
				usuario.getListPerfil().add(perfil);					
			}
		}
		
		return usuarioRep.save(usuario);
	}
	
	public Usuario removerAutorizacao(Long idUsuario, Long idPerfil) {
		Usuario usuario =  usuarioRep.getReferenceById(idUsuario);
		Perfil perfil = perfilRep.getReferenceById(idPerfil);
		if(usuario == null || perfil == null) throw new NullPointerException("Usuário ou Perfil invalido.!");
		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());
		logger.info("Usuário: {} teve o perfil: {} removido", usuario, perfil);
		usuario.getListPerfil().remove(perfil);	
		return usuarioRep.save(usuario);
	}


	public List<Usuario> buscarUsuarioDoPerfil(String nomePerfil) {
		Perfil perfil = perfilRep.findByNome(nomePerfil);		
		List<Usuario> usuario = usuarioRep.findByListPerfilAndDataInativacao(perfil, null);
		if(usuario == null || perfil == null) throw new NullPointerException("Usuário ou Perfil invalido.!");
		return usuario;
	}

	public List<Usuario> buscarUsuariosPorPerfil(Long idPerfil) {
		Perfil perfil = perfilRep.getReferenceById(idPerfil);
		List<Usuario> usuario = usuarioRep.findByListPerfilAndDataInativacao(perfil, null);
		if(usuario == null || perfil == null) throw new NullPointerException("Usuário ou Perfil invalido.!");

		return usuario;
	}

	public Page<Usuario> buscarUsuariosPorPerfil(Perfil perfil, Pageable pageable) {
		return usuarioRep.findByListPerfilAndDataInativacao(perfil, null, pageable);
	}

	public Usuario autorizarLista(Long idUsuario, List<Perfil> listaPerfil) {
		Usuario usuario = usuarioRep.getReferenceById(idUsuario);	
		usuario.getListPerfil().clear();
		
		for(Perfil lista: listaPerfil) {			
			Perfil perfil = perfilRep.getReferenceById(lista.getId());	
			
			usuario.getListPerfil().add(perfil);
			logger.info("Usuário: {} recebeu a autorização: {}", usuario.getNome(), perfil.getNome());		
		}	
		
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());		
		return usuarioRep.save(usuario);
	}

	public List<Usuario> vincularPerfilEmVariosUsuarios(Long idPerfil, List<UsuarioDto> usuariosDto) {
		Perfil perfil = perfilRep.getReferenceById(idPerfil);

		if(perfil == null) throw new NullPointerException("Perfil invalido.!");
		revogarPermissao(perfil, usuariosDto);
		List<Usuario> usuarios = new ArrayList<>();

		for(UsuarioDto usuarioDtoItem : usuariosDto) {
			Usuario usuario = usuarioRep.getReferenceById(usuarioDtoItem.getId());

			if(usuario == null)  continue;

			List<Perfil> perfis = usuario.getListPerfil().stream().filter(item -> {
				return item.getId().equals(perfil.getId());
			}).collect(Collectors.toList());

			if(perfis.isEmpty()) {
				usuario.getListPerfil().add(perfil);
				usuarios.add(usuario);

				logger.info("Usuário: {} recebeu a autorização: {}", usuario.getNome(), perfil.getNome());
			}
		}

		return usuarioRep.saveAll(usuarios);
	}

	public List<Usuario> revogarPermissao(Perfil perfil, List<UsuarioDto> usuariosDto) {
		List<Usuario> usuarios = usuarioRep.findByListPerfilAndDataInativacao(perfil, null);

		for(Usuario usuario : usuarios) {
			UsuarioDto usuarioDto = usuariosDto.stream().filter(usuarioDtoItem -> {
				return usuarioDtoItem.getId().equals(usuario.getId());
			}).findFirst().orElse(null);

			if(usuarioDto == null) {
				usuario.getListPerfil().remove(perfil);
			}
		}

		return usuarioRep.saveAll(usuarios);
	}
	
	public Usuario revogarLista(Long idUsuario, List<Perfil> listaPerfil) {	
		Usuario usuario = usuarioRep.getReferenceById(idUsuario);				
		
		for(Perfil unico: listaPerfil) {			
			Perfil perfil = perfilRep.getReferenceById(unico.getId());			
			if(usuario == null || perfil == null) throw new NullPointerException("Usuário ou Perfil invalido.!");
			logger.info("Usuário: {} perdeu a autorização: {}", usuario, perfil);
			usuario.getListPerfil().remove(perfil);
			usuarioRep.save(usuario);
		}
		historicoAcessoFuncionalidadesService.salvarHistorico(usuario.getId(), Thread.currentThread().getStackTrace());
		return usuario;
	}

	public Page<UsuarioDto> buscarTodos(String filtro, Situacao situacao, Pageable pageable) {
		Page<Usuario> pageUsuario = usuarioRep.buscarUsuariosFiltroGlobal(filtro, situacao, pageable);
		List<UsuarioDto> listUsuarioDto = UsuarioDto.construir(pageUsuario.getContent());
		Page<UsuarioDto> pageUsuarioDto = new PageImpl<>(
				listUsuarioDto,
				pageable,
				pageUsuario.getTotalElements()
		);
		return pageUsuarioDto;
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Page<PerfilDto> buscarPerfisPorUsuario(Long id, Pageable pageable) {
		Page<Perfil> perfilPage = perfilRep.findByPerfisUsuario(id, pageable);
		List<PerfilDto> response = PerfilDto.contruir(perfilPage.getContent());
		return new PageImpl(response, pageable, perfilPage.getTotalElements());
	}

	public Usuario buscarUsuarioPorId(Long id) {
		Usuario usuario = usuarioRep.getReferenceById(id);

		if(usuario == null) {
			throw new ObjectNotFoundException("O usuário não foi encontrado! id: " + id + ", Tipo: " + Usuario.class.getName());
		}

		return usuario;
	}

	public Usuario getUsuarioLogado() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario usuario = usuarioRep.findByUsername(username);

		if(usuario == null) {
			throw new ObjectNotFoundException("O usuário não foi encontrado! Username: " + username + ", Tipo: " + Usuario.class.getName());
		}

		return usuario;
	}

	public Boolean isUsuarioFazParteDoPerfil(Usuario usuario, String nomePerfil) {
		Perfil perfil = perfilRep.findByNome(nomePerfil);
		return usuario.getListPerfil().contains(perfil);
	}

	public Boolean isUsuarioFazParteDoPerfil(String nomePerfil) {
		Usuario usuarioLogado = getUsuarioLogado();
		return isUsuarioFazParteDoPerfil(usuarioLogado, nomePerfil);
	}
	public void resetarSenha(UsuarioResetarSenhaDto objDto) {
		Usuario usuario = buscarUsuarioPorId(objDto.getId());
		usuario.setTrocarSenha(true);
		String senhaCriptografada = passwordEncoder.encode(objDto.getNovaSenha());
		usuario.setPassword(senhaCriptografada);
		usuarioRep.save(usuario);
	}

	public void trocaSenhaUsuario(UsuarioAlterarSenhaDto objDto) {
		Usuario usuario = getUsuarioLogado();
		System.out.println(usuario.getId());
		if(usuario != null && passwordEncoder.matches(objDto.getSenhaAntiga(), usuario.getPassword())) {
			usuario.setTrocarSenha(false);
			usuario.setPassword(passwordEncoder.encode(objDto.getSenhaNova()));
			usuarioRep.save(usuario);
			logger.info("Senha alterada com sucesso.");
		}		
		throw new NullPointerException("Não foi possivel alterar a senha do usuário.");		
	}
	
	public String alterarSenhaUsuario(UsuarioAlterarSenhaDto objDto) {
	
		Usuario usuario = getUsuarioLogado();
		if(usuario != null && passwordEncoder.matches(objDto.getSenhaAntiga(), usuario.getPassword())) {
			usuario.setTrocarSenha(false);
			usuario.setPassword(passwordEncoder.encode(objDto.getSenhaNova()));
			usuarioRep.save(usuario);
			logger.info("Senha alterada com sucesso.");
			return "Salvo com sucesso!";
		}		
		throw new NullPointerException("Não foi possivel alterar a senha do usuário.");		
	}
	
	public void recuperarSenha(RecuperarSenhaDto objDto) {		
		Usuario usuario = usuarioRep.findByUsername(objDto.getUsername());
		
		if(usuario == null) {
			throw new ObjectNotFoundException("O usuário não foi encontrado! Username: " + objDto.getUsername() + ", Tipo: " + Usuario.class.getName());
		}

		String senhaNovaCriptografada = passwordEncoder.encode(objDto.getSenha());
		usuario.setPassword(senhaNovaCriptografada);
		usuario.setTrocarSenha(false);
		usuarioRep.save(usuario);
	}
	
	 public Page<UsuarioSimplesDto> findByFilterDescricao(UsuarioFiltro filter, Pageable pageable) {
		logger.info("Filtrando usuários: pesquisa: {} situacao: {}", filter.getPesquisar(), filter.getSituacao());	
		Page<Usuario> usuarios = usuarioRep.findAll(
				UsuarioSpec.doFilter(filter.getPesquisar())
				.and(UsuarioSpec.daSituacao(filter.getSituacao())), 
				pageable);
		
		List<UsuarioSimplesDto> response = new ArrayList<>();
		for(Usuario item: usuarios.getContent()) {
			UsuarioSimplesDto usuario = new UsuarioSimplesDto();
			BeanUtils.copyProperties(item, usuario);
			response.add(usuario);
		}
		return new PageImpl<UsuarioSimplesDto>(response, pageable, usuarios.getTotalElements());
	}
	 
	 public List<UsuarioSimplesDto> findBySearch(String dados){
		 List<UsuarioSimplesDto> response = new ArrayList<>();
		for(Usuario item: usuarioRep.findAll(UsuarioSpec.daBusca(dados), Sort.by("estabelecimento").ascending())) {
			UsuarioSimplesDto usuario = new UsuarioSimplesDto();
			BeanUtils.copyProperties(item, usuario);
			response.add(usuario);
		}
		return response;		 
	 }
}