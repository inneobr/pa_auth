package br.coop.integrada.auth.exception;

public class SenhaIncorretaException extends RuntimeException {

    public SenhaIncorretaException( String msg ) {
        super( msg );
    }

    public SenhaIncorretaException( String msg, Throwable ceuse ) {
        super( msg, ceuse );
    }
}
