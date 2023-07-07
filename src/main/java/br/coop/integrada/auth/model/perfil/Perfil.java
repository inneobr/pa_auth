package br.coop.integrada.auth.model.perfil;

import br.coop.integrada.auth.model.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotBlank;
import br.coop.integrada.auth.model.item.Item;
import br.coop.integrada.auth.model.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter @Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
@Table(name = "perfil")
public class Perfil extends AbstractEntity {
	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Campo obrigatório!")
	@Column(unique = true)
	private String nome;
	
	@NotBlank(message = "Campo obrigatório!")
	private String descricao;		

	@JoinTable(name = "v_perfil_item", 
	        joinColumns = {@JoinColumn(name = "id_perfil") }, 
	        inverseJoinColumns = { @JoinColumn(name = "id_item") })
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Item> menuItens = new ArrayList<>();

	@JsonIgnore
	@ManyToMany(mappedBy = "listPerfil")
	private Collection<Usuario> usuarios = new ArrayList<>();
}	

