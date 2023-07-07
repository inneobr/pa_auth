package br.coop.integrada.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.coop.integrada.auth.model.theme.Options;

public interface OptionRep extends JpaRepository<Options, Long>{

}
