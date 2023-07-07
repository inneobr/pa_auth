package br.coop.integrada.auth.model.historico.historicoAcesso;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity @NoArgsConstructor @AllArgsConstructor
@Table(name = "historico_acesso")
public class HistoricoAcesso implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name="id_usuario")
	private Long idUsuario;
	
	@Column(name = "ip_address", nullable = false)
	private String ipAddress;
	
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date data;
    
    

}
