package br.coop.integrada.auth.model.item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.coop.integrada.auth.model.AbstractEntity;
import br.coop.integrada.auth.model.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	
	private String label;	
	private String icon;	
	private String path;
	
	@Column(name="sequencia")
	private Integer sequencia;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@ManyToOne
	@JoinColumn(name = "id_menu")	
	private Menu menu;
}
