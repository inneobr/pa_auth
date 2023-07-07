package br.coop.integrada.auth.model.theme;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.Table;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

import br.coop.integrada.auth.model.AbstractEntity;
import br.coop.integrada.auth.model.usuario.Usuario;

@Getter @Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "options")
public class Options extends AbstractEntity {
	private static final long serialVersionUID = 1L;	
	
	private boolean rtl;	
	private String tema;
	private String menu;
	private Integer fonte;
	private String corMenu;
	private String menuPosition;
	private String estiloCampos;
	private String rippleEffect;	
	private String corCabecalho;
	private String corComponentes;
	private boolean administrador;
	
	@OneToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;	
}
