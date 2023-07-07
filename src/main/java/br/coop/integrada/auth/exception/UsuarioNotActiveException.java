package br.coop.integrada.auth.exception;

public class UsuarioNotActiveException extends Exception {
	private static final long serialVersionUID = 1L;

	public UsuarioNotActiveException() {
		super("Usuário encontra-se inativo!");
	}
}