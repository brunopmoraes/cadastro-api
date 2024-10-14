package br.com.lagoinha.utils;

import java.util.function.IntFunction;

public class ValidadorCPF {

    public static boolean validarCPF(String cpfInput) {
        String cpf = cpfInput.replaceAll("\\D", ""); // Remove tudo que não for dígito

        // Verifica se tem 11 dígitos ou se todos os números são iguais
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;

        // Função para calcular cada dígito verificador
        IntFunction<Integer> calcularDigito = (int pesoInicial) -> {
            int soma = 0;
            for (int i = 0; i < pesoInicial - 1; i++)
                soma += (cpf.charAt(i) - '0') * (pesoInicial - i);
            int digito = 11 - (soma % 11);
            return (digito > 9) ? 0 : digito;
        };

        // Verifica os dois dígitos verificadores
        return calcularDigito.apply(10) == cpf.charAt(9) - '0' && calcularDigito.apply(11) == cpf.charAt(10) - '0';
    }
}

