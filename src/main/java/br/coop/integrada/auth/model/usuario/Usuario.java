package br.coop.integrada.auth.model.usuario;

import lombok.Setter;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import br.coop.integrada.auth.model.perfil.Perfil;
import br.coop.integrada.auth.model.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter @Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "usuario")
public class Usuario extends AbstractEntity {
	private static final long serialVersionUID = 1L;	

	@Column(unique = true)
	private String matricula;

	@Column(unique = true, nullable = false)
    private String codUsuario;
	
	@Column(length = 20, unique = true, nullable = false)
	private String username;
	
	@Column(unique = true)
	private String cpf;

	private String nome;
	
	@Column(name="id_regional")
	private String regional;
	
	@Column(name="id_estabelecimento")
	private String estabelecimento;
	
	@Column(name="status")
	private String status;
	
	@Column(name="email")
	private String email;
	
	@Column(name="trocar_senha")
	private Boolean trocarSenha;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@JoinTable(
			name = "v_usuario_perfil",
			joinColumns = @JoinColumn(name = "usuario_id"),
			inverseJoinColumns = @JoinColumn(name = "list_perfil_id")
	)
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Perfil> listPerfil = new ArrayList<>(); 	
	 
}
