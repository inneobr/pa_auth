package br.coop.integrada.auth.query;

import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.query.enums.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerfilQueryRep {
    Page<Perfil> buscarPerfisFiltroGlobal(String filtro, Situacao situacao, Pageable pageable);

    public Page<Perfil> buscarPerfisPorUsuario(String filtro, Situacao situacao, Pageable pageable);

    public Page<Perfil> buscarPerfisPorMenuItem(String filtro, Situacao situacao, Pageable pageable);
}
