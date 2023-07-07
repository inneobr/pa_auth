package br.coop.integrada.auth.model.recuperarsenha;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recuperar_senha")
public class RecuperarSenha implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "data_cadastro", nullable = false)
	private LocalDateTime dataCadastro;
	
	@Column(name = "data_inativacao")
	private LocalDateTime dataInativacao;
	
	/*@Column(name = "id_usuario")
	private Integer idUsuario;*/

	@Column(name = "username")
	private String username;

	private String token;

	@Column(name = "data_expira")
	private LocalDateTime dataExpira;

	@Column(name = "data_recuperacao")
	private LocalDateTime dataRecuperacao;

	public static RecuperarSenha construir(String username) {
		return RecuperarSenha.builder()
				.username(username)
				.token(UUID.randomUUID().toString())
				.dataCadastro(LocalDateTime.now())
				.dataExpira(LocalDateTime.now().plus(2, ChronoUnit.HOURS))
				.build();
	}

	
}