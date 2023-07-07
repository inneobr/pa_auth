package br.coop.integrada.auth.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.query.UsuarioQueryRep;

@Repository
public interface UsuarioRep extends JpaRepository<Usuario, Long>, UsuarioQueryRep, JpaSpecificationExecutor<Usuario> {
	Usuario findByUsername(String username);
	Usuario findByUsernameIgnoreCase(String username);
	Usuario findByMatricula(String matricula);
	List<Usuario> findByAndDataInativacao(Date dataInativacao);	
	List<Usuario> findByListPerfilAndDataInativacao(Perfil perfil, Date dataInativacao);	
	List<Usuario> findByNomeContainingIgnoreCaseOrCpfContainingIgnoreCase(String nome, String cpf);
	Page<Usuario> findByListPerfilAndDataInativacao(Perfil perfil, Date dataInativacao, Pageable pageable);
	Page<Usuario> findByCodUsuarioContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrNomeContainingIgnoreCaseOrderByNomeAsc(String codigo, String username, String nome, Pageable pageable);
}
