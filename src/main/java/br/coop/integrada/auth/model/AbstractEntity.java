package br.coop.integrada.auth.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import br.coop.integrada.auth.enums.StatusIntegracao;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    @Column(name = "data_cadastro")
    private Date dataCadastro;  
    
    @UpdateTimestamp
    @JsonProperty(access = Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_atualizacao")
    private Date dataAtualizacao; 
    
    @JsonProperty(access = Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_inativacao")
    private Date dataInativacao;
    
    @JsonProperty(access = Access.READ_ONLY)
    @Temporal(TemporalType.TIMESTAMP )
    @Column(name="data_integracao")
	private Date dataIntegracao;
	
    @JsonProperty(access = Access.READ_ONLY)
	@Enumerated(EnumType.STRING)
	@Column(name="status_integracao")
	private StatusIntegracao statusIntegracao;

    @Transient
    public Boolean getAtivo() {
        if(dataInativacao == null) {
            return true;
        }
        return false;
    }
    
    @Transient
	public String getDescricaoStatusIntegracao() {
		if(this.getStatusIntegracao() != null)
			return this.getStatusIntegracao().getDescricao();
		
		return null;
	}
}
