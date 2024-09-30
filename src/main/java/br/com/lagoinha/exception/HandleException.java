package br.com.lagoinha.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HandleException {

    public static ResponseEntity<?> handleException(Exception e) {
        if (e instanceof CadastroNotFoundException) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Retorna 500
    }
}
