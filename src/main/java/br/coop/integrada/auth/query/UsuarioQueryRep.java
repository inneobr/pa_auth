package br.coop.integrada.auth.query;

import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.query.enums.Situacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioQueryRep {
    Page<Usuario> buscarUsuariosFiltroGlobal(String filtro, Situacao situacao, Pageable pageable);
}
