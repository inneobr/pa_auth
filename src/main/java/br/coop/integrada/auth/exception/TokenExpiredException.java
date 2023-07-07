package br.coop.integrada.auth.exception;

public class TokenExpiredException extends Exception {
	private static final long serialVersionUID = 1L;

	public TokenExpiredException(String mensagem) {
		super(mensagem);
	}
}
