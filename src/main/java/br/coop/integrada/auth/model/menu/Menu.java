package br.coop.integrada.auth.model.menu;

import lombok.Setter;
import lombok.Getter;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.AllArgsConstructor;
import br.coop.integrada.auth.model.AbstractEntity;
import br.coop.integrada.auth.model.item.Item;

@Getter @Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "menu")
public class Menu extends AbstractEntity {
	private static final long serialVersionUID = 1L;	
	
	private String label;	
	private String icon;
	
	@Column(name="sequencia")
	private Integer sequencia;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "menu" )
    @OrderBy("sequencia ASC")
    private Collection<Item> itens = new ArrayList<>();
}