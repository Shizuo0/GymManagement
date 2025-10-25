package com.example.demo.ui.utils;

import javax.swing.*;

/**
 * Utilitários para validação de campos de formulário.
 * Fornece métodos para validar entrada do usuário antes de enviar ao backend.
 */
public class ValidationUtils {
    
    /**
     * Valida se um campo de texto não está vazio
     * 
     * @param field Campo a ser validado
     * @param fieldName Nome do campo para mensagem de erro
     * @return true se válido, false caso contrário
     */
    public static boolean validateNotEmpty(JTextField field, String fieldName) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            showValidationError(fieldName + " não pode estar vazio.");
            field.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Valida se um campo de texto não está vazio
     * 
     * @param field Campo a ser validado
     * @param fieldName Nome do campo para mensagem de erro
     * @return true se válido, false caso contrário
     */
    public static boolean validateNotEmpty(JTextArea field, String fieldName) {
        String value = field.getText().trim();
        if (value.isEmpty()) {
            showValidationError(fieldName + " não pode estar vazio.");
            field.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Valida CPF
     * 
     * @param cpf CPF a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean validateCPF(String cpf) {
        if (cpf == null) {
            return false;
        }
        
        // Remove formatação
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica tamanho
        if (cpf.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Validação do primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit > 9) firstDigit = 0;
        if (firstDigit != (cpf.charAt(9) - '0')) {
            return false;
        }
        
        // Validação do segundo dígito verificador
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
    
    /**
     * Valida campo de CPF
     * 
     * @param field Campo contendo o CPF
     * @return true se válido, false caso contrário
     */
    public static boolean validateCPFField(JTextField field) {
        String cpf = field.getText().trim();
        
        if (cpf.isEmpty()) {
            showValidationError("CPF não pode estar vazio.");
            field.requestFocus();
            return false;
        }
        
        if (!validateCPF(cpf)) {
            showValidationError("CPF inválido.");
            field.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida se um valor numérico é positivo
     * 
     * @param field Campo contendo o número
     * @param fieldName Nome do campo para mensagem de erro
     * @return true se válido, false caso contrário
     */
    public static boolean validatePositiveNumber(JTextField field, String fieldName) {
        String value = field.getText().trim();
        
        if (value.isEmpty()) {
            showValidationError(fieldName + " não pode estar vazio.");
            field.requestFocus();
            return false;
        }
        
        try {
            double number = Double.parseDouble(value);
            if (number <= 0) {
                showValidationError(fieldName + " deve ser maior que zero.");
                field.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError(fieldName + " deve ser um número válido.");
            field.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida se um valor é um inteiro positivo
     * 
     * @param field Campo contendo o número
     * @param fieldName Nome do campo para mensagem de erro
     * @return true se válido, false caso contrário
     */
    public static boolean validatePositiveInteger(JTextField field, String fieldName) {
        String value = field.getText().trim();
        
        if (value.isEmpty()) {
            showValidationError(fieldName + " não pode estar vazio.");
            field.requestFocus();
            return false;
        }
        
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
                showValidationError(fieldName + " deve ser maior que zero.");
                field.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError(fieldName + " deve ser um número inteiro válido.");
            field.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida email (validação básica)
     * 
     * @param email Email a ser validado
     * @return true se válido, false caso contrário
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // Regex simples para email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Valida campo de email
     * 
     * @param field Campo contendo o email
     * @return true se válido, false caso contrário
     */
    public static boolean validateEmailField(JTextField field) {
        String email = field.getText().trim();
        
        if (email.isEmpty()) {
            showValidationError("Email não pode estar vazio.");
            field.requestFocus();
            return false;
        }
        
        if (!validateEmail(email)) {
            showValidationError("Email inválido.");
            field.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Valida se um combo box tem uma seleção válida
     * 
     * @param comboBox ComboBox a ser validado
     * @param fieldName Nome do campo para mensagem de erro
     * @return true se válido, false caso contrário
     */
    public static boolean validateComboBoxSelection(JComboBox<?> comboBox, String fieldName) {
        if (comboBox.getSelectedIndex() == -1 || comboBox.getSelectedItem() == null) {
            showValidationError("Por favor, selecione um " + fieldName + ".");
            comboBox.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Formata CPF (adiciona pontos e traço)
     * 
     * @param cpf CPF sem formatação
     * @return CPF formatado (000.000.000-00)
     */
    public static String formatCPF(String cpf) {
        if (cpf == null) return null;
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
    
    /**
     * Remove formatação de CPF
     * 
     * @param cpf CPF formatado
     * @return CPF apenas com números
     */
    public static String unformatCPF(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("[^0-9]", "");
    }
    
    /**
     * Exibe mensagem de erro de validação
     * 
     * @param message Mensagem a ser exibida
     */
    private static void showValidationError(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Erro de Validação",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    // Construtor privado para evitar instanciação
    private ValidationUtils() {
        throw new AssertionError("Classe utilitária não deve ser instanciada");
    }
}
