package br.coop.integrada.auth.query;

import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.query.enums.Situacao;
import br.coop.integrada.auth.repository.UsuarioRep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioQueryRepImpl implements UsuarioQueryRep {

    @Autowired @Lazy
    private UsuarioRep todosUsuario;

    public Page<Usuario> buscarUsuariosFiltroGlobal(String filtro, Situacao situacao, Pageable pageable) {
        return todosUsuario.findAll(
                UsuarioSpec.doEstabelecimento(filtro)
                        .or(UsuarioSpec.doRegional(filtro))
                        .or(UsuarioSpec.doMatricula(filtro))
                        .or(UsuarioSpec.doNome(filtro))
                        .and(UsuarioSpec.daSituacao(situacao)),
                pageable);
    }
}
