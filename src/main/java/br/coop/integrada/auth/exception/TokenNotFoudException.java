package br.coop.integrada.auth.exception;

public class TokenNotFoudException extends Exception {
	private static final long serialVersionUID = 1L;

	public TokenNotFoudException(String mensagem) {
		super(mensagem);
	}
}
