package br.com.lagoinha.utils;

public class Validador {

    // Valida se o CPF é válido
    public static boolean isCpfValid(String cpf) {
        // Aqui você pode adicionar uma lógica real de validação de CPF
        return cpf != null && cpf.matches("\\d{11}");
    }
}
