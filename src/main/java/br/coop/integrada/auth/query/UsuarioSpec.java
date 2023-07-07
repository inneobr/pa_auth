package br.coop.integrada.auth.query;

import br.coop.integrada.auth.model.usuario.Usuario;
import br.coop.integrada.auth.query.enums.Situacao;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;

public class UsuarioSpec {

    public static Specification<Usuario> doMatricula(String matricula){
        return (root, query, builder) -> {
            if(StringUtils.hasText(matricula)) {
                return builder.like(builder.upper(root.get("matricula")), "%"+ matricula.toUpperCase() +"%");
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Usuario> doEstabelecimento(String estabelecimento){
        return (root, query, builder) -> {
            if(StringUtils.hasText(estabelecimento)) {
                return builder.like(builder.upper(root.get("estabelecimento")), "%"+ estabelecimento.toUpperCase() +"%");
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Usuario> doRegional(String regional){
        return (root, query, builder) -> {
            if(StringUtils.hasText(regional)) {
                return builder.like(builder.upper(root.get("regional")), "%"+ regional.toUpperCase() +"%");
            }
            return builder.and(new Predicate[0]);
        };
    }

    public static Specification<Usuario> doNome(String nome){
        return (root, query, builder) -> {
            if(StringUtils.hasText(nome)) {
                return builder.like(builder.upper(root.get("nome")), "%"+ nome.toUpperCase() +"%");
            }
            return builder.and(new Predicate[0]);
        };
    }
    
    public static Specification<Usuario> doFilter(String filtro){
		return (root, query, builder) -> {
			if(filtro != null) {		
				Predicate predicateFieldNome = builder.like(builder.upper(root.get("nome")), "%"+ filtro.toUpperCase() +"%");
				Predicate predicateFieldUsername = builder.like(builder.upper(root.get("username")), "%"+ filtro.toUpperCase() +"%");
				Predicate predicateFieldMatricula = builder.like(builder.upper(root.get("matricula")), "%"+ filtro.toUpperCase() +"%");				
				return builder.or(predicateFieldNome, predicateFieldUsername,  predicateFieldMatricula);
			}
			return builder.and(new Predicate[0]);			
		};
	}

    public static Specification<Usuario> daSituacao(Situacao situacao){
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
    
    public static Specification<Usuario> daBusca(String filtro){
		return (root, query, builder) -> {
			if(filtro != null) {
				Predicate predicateFieldCodUsuarioCodUsuario = builder.like(builder.upper(root.get("codUsuario")), "%"+ filtro.toUpperCase() +"%");		
				return builder.or(predicateFieldCodUsuarioCodUsuario);
			}
			return builder.and(new Predicate[0]);			
		};
	}
}
