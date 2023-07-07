package br.coop.integrada.auth.query;

import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.query.enums.Situacao;
import br.coop.integrada.auth.repository.PerfilRep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class PerfilQueryRepImpl implements PerfilQueryRep {

    @Autowired
    @Lazy
    private PerfilRep todosPerfil;

    public Page<Perfil> buscarPerfisFiltroGlobal(String filtro, Situacao situacao, Pageable pageable) {
        return todosPerfil.findAll(
                PerfilSpec.doNome(filtro)
                        .or(PerfilSpec.doDescricao(filtro))
                        .and(PerfilSpec.doSituacao(situacao)),
                pageable);
    }

    public Page<Perfil> buscarPerfisPorUsuario(String filtro, Situacao situacao, Pageable pageable) {
        return todosPerfil.findAll(
                PerfilSpec.doUsuarioNome(filtro)
                        .or(PerfilSpec.doUsuarioUsername(filtro))
                        .or(PerfilSpec.doUsuarioMatricula(filtro))
                        .and(PerfilSpec.doSituacao(situacao)),
                pageable);
    }

    public Page<Perfil> buscarPerfisPorMenuItem(String filtro, Situacao situacao, Pageable pageable) {
        return todosPerfil.findAll(
                PerfilSpec.doMenuItemLabel(filtro)
                        .and(PerfilSpec.doSituacao(situacao)),
                pageable);
    }
}
