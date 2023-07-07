package br.coop.integrada.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.coop.integrada.auth.model.recuperarsenha.RecuperarSenha;



@Repository
public interface RecuperarSenhaRep extends JpaRepository<RecuperarSenha, String>{
	Optional<RecuperarSenha> findByToken(String token);
	
	@Query(value = "SELECT * "
			+ " FROM recuperar_senha "
			+ " WHERE token = :token "
			+ " AND username = :username "
			+ " AND data_recuperacao IS NULL", 
			nativeQuery = true)
	Optional<RecuperarSenha> findByTokenAtivo(@Param("token") String token, @Param("username") String username);
}
