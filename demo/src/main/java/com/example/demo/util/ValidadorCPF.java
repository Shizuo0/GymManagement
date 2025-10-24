package com.example.demo.util;

public class ValidadorCPF {
    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove todos os caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Check for known invalid CPFs (todos dígitos iguais)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // First digit validation
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;
        if (firstDigit != (cpf.charAt(9) - '0')) {
            return false;
        }

        // Second digit validation
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit > 9) secondDigit = 0;
        if (secondDigit != (cpf.charAt(10) - '0')) {
            return false;
        }

        return true;
    }

    public static String format(String cpf) {
        if (cpf == null) return null;
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}