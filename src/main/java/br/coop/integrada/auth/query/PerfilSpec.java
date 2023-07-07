package br.coop.integrada.auth.query;

import br.coop.integrada.auth.model.item.Item;
import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.query.enums.Situacao;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

public class PerfilSpec {
    public static Specification<Perfil> doNome(String nome){
        return (root, query, builder) -> {
            if(StringUtils.hasText(nome)) {
                return builder.like(builder.upper(root.get("nome")), "%"+ nome.toUpperCase() +"%");
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Perfil> doDescricao(String descricao){
        return (root, query, builder) -> {
            if(StringUtils.hasText(descricao)) {
                return builder.like(builder.upper(root.get("descricao")), "%"+ descricao.toUpperCase() +"%");
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Perfil> doUsuarioNome(String usuarioNome){
        return (root, query, builder) -> {
            if(StringUtils.hasText(usuarioNome)) {
                query.distinct(true);
                Join<Usuario, Perfil> usuario = root.join("usuarios");
                return builder.like(
                        builder.upper(usuario.get("nome")),
                        "%"+ usuarioNome.toUpperCase() +"%"
                );
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Perfil> doUsuarioUsername(String username){
        return (root, query, builder) -> {
            if(StringUtils.hasText(username)) {
                query.distinct(true);
                Join<Usuario, Perfil> usuario = root.join("usuarios");
                return builder.like(
                        builder.upper(usuario.get("username")),
                        "%"+ username.toUpperCase() +"%"
                );
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Perfil> doUsuarioMatricula(String matricula){
        return (root, query, builder) -> {
            if(StringUtils.hasText(matricula)) {
                query.distinct(true);
                Join<Usuario, Perfil> usuario = root.join("usuarios");
                return builder.like(
                        builder.upper(usuario.get("matricula")),
                        "%"+ matricula.toUpperCase() +"%"
                );
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Perfil> doMenuItemLabel(String itemLabel){
        return (root, query, builder) -> {
            if(StringUtils.hasText(itemLabel)) {
                query.distinct(true);
                Join<Item, Perfil> menuItens = root.join("menuItens");
                return builder.like(
                        builder.upper(menuItens.get("label")),
                        "%"+ itemLabel.toUpperCase() +"%"
                );
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Perfil> doSituacao(Situacao situacao){
        return (root, query, builder) -> {
            if(situacao != null) {
                if(situacao.equals(Situacao.ATIVO)) {
                    return builder.isNull(root.get("dataInativacao"));
                }
                else if(situacao.equals(Situacao.INATIVO)) {
                    return builder.isNotNull(root.get("dataInativacao"));
                }
            }
            return builder.and(new Predicate[0]);
        };
    }
}
