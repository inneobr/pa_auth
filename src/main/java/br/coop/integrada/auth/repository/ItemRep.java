package br.coop.integrada.auth.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.coop.integrada.auth.model.item.Item;

public interface ItemRep extends JpaRepository<Item, Long>{
	List<Item> findByAndDataInativacao(Date dataInativacao);

	@Query(value="select distinct i.* from item i "
			+ "inner join v_perfil_item vpi "
			+ "  on vpi.id_item = i.id "
			+ "  and vpi.id_perfil = ?1 "
			+ "where i.data_inativacao is null "
			+ "order by i.label ", nativeQuery=true)
	Page<Item> buscarItensPorPerfil(Long idPerfil, Pageable pageable);

	@Query(value="select distinct i.* from item i "
			+ "inner join v_perfil_item pxm on pxm.id_item = i.id "
			+ "inner join v_usuario_perfil uxp on uxp.list_perfil_id = pxm.id_perfil "
			+ "and uxp.usuario_id = ?1 "
			+ "where i.data_inativacao is null "
			+ "order by i.sequencia asc", nativeQuery=true)
	List<Item> buscarMenuItensPorIdUsuario(Long idUsuario);
}
