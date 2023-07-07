package br.coop.integrada.auth.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.coop.integrada.auth.model.menu.Menu;

public interface MenuRep extends JpaRepository<Menu, Long>{
	List<Menu> findByAndDataInativacao(Date dataInativacao);
}
