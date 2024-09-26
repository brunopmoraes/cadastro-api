package br.com.lagoinha.exception;

public class CadastroNotFoundException extends RuntimeException {

    public CadastroNotFoundException(String message) {
        super(message);
    }

    public CadastroNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
