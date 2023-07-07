package br.coop.integrada.auth.repository;

import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.query.PerfilQueryRep;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface PerfilRep extends JpaRepository<Perfil, Long>, PerfilQueryRep, JpaSpecificationExecutor<Perfil> {
	Perfil findByNome(String username);
	List<Perfil> findByAndDataInativacao(Date dataInativacao);

	@Query(value="SELECT P.* FROM PERFIL P "
			+ "INNER JOIN V_USUARIO_PERFIL UP "
			+ "ON P.ID = UP.LIST_PERFIL_ID "
			+ "AND UP.USUARIO_ID = :id "
			+ "ORDER BY P.DESCRICAO", nativeQuery=true)
	Page<Perfil> findByPerfisUsuario(Long id, Pageable pageable);
}
