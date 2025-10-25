package com.example.demo.ui.utils;

import javax.swing.*;
import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Utilitários para exibir diálogos padronizados na aplicação.
 * Fornece métodos para mensagens de sucesso, erro, confirmação, etc.
 */
public class DialogUtils {
    
    /**
     * Exibe mensagem de sucesso
     * 
     * @param message Mensagem a ser exibida
     */
    public static void showSuccess(String message) {
        showSuccess(null, message);
    }
    
    /**
     * Exibe mensagem de sucesso
     * 
     * @param parent Componente pai
     * @param message Mensagem a ser exibida
     */
    public static void showSuccess(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Sucesso",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Exibe mensagem de erro
     * 
     * @param message Mensagem a ser exibida
     */
    public static void showError(String message) {
        showError(null, message);
    }
    
    /**
     * Exibe mensagem de erro
     * 
     * @param parent Componente pai
     * @param message Mensagem a ser exibida
     */
    public static void showError(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Exibe mensagem de erro de API com tratamento especial
     * 
     * @param parent Componente pai
     * @param exception Exceção da API
     */
    public static void showApiError(java.awt.Component parent, ApiException exception) {
        showError(parent, exception.getUserFriendlyMessage());
    }
    
    /**
     * Exibe mensagem de aviso
     * 
     * @param message Mensagem a ser exibida
     */
    public static void showWarning(String message) {
        showWarning(null, message);
    }
    
    /**
     * Exibe mensagem de aviso
     * 
     * @param parent Componente pai
     * @param message Mensagem a ser exibida
     */
    public static void showWarning(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Aviso",
            JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Exibe mensagem de informação
     * 
     * @param message Mensagem a ser exibida
     */
    public static void showInfo(String message) {
        showInfo(null, message);
    }
    
    /**
     * Exibe mensagem de informação
     * 
     * @param parent Componente pai
     * @param message Mensagem a ser exibida
     */
    public static void showInfo(java.awt.Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Informação",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Exibe diálogo de confirmação
     * 
     * @param message Mensagem a ser exibida
     * @return true se confirmado, false caso contrário
     */
    public static boolean showConfirmation(String message) {
        return showConfirmation(null, message);
    }
    
    /**
     * Exibe diálogo de confirmação
     * 
     * @param parent Componente pai
     * @param message Mensagem a ser exibida
     * @return true se confirmado, false caso contrário
     */
    public static boolean showConfirmation(java.awt.Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "Confirmação",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Exibe diálogo de confirmação de exclusão
     * 
     * @param parent Componente pai
     * @return true se confirmado, false caso contrário
     */
    public static boolean showDeleteConfirmation(java.awt.Component parent) {
        return showConfirmation(parent, MSG_CONFIRM_DELETE);
    }
    
    /**
     * Exibe diálogo de entrada de texto
     * 
     * @param message Mensagem a ser exibida
     * @param initialValue Valor inicial
     * @return Texto digitado ou null se cancelado
     */
    public static String showInputDialog(String message, String initialValue) {
        return showInputDialog(null, message, initialValue);
    }
    
    /**
     * Exibe diálogo de entrada de texto
     * 
     * @param parent Componente pai
     * @param message Mensagem a ser exibida
     * @param initialValue Valor inicial
     * @return Texto digitado ou null se cancelado
     */
    public static String showInputDialog(java.awt.Component parent, String message, String initialValue) {
        return (String) JOptionPane.showInputDialog(
            parent,
            message,
            "Entrada",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            initialValue
        );
    }
    
    /**
     * Exibe diálogo de loading em uma thread separada
     * 
     * @param parent Componente pai
     * @param message Mensagem de loading
     * @return Dialog que pode ser fechado posteriormente
     */
    public static JDialog showLoadingDialog(java.awt.Component parent, String message) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Aguarde");
        dialog.setModal(false);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JLabel label = new JLabel(message);
        label.setFont(FONT_REGULAR);
        label.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        panel.add(progressBar);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        return dialog;
    }
    
    /**
     * Fecha um diálogo de loading
     * 
     * @param dialog Dialog a ser fechado
     */
    public static void closeLoadingDialog(JDialog dialog) {
        if (dialog != null) {
            SwingUtilities.invokeLater(() -> {
                dialog.setVisible(false);
                dialog.dispose();
            });
        }
    }
    
    /**
     * Exibe exceção com detalhes técnicos (para debug)
     * 
     * @param parent Componente pai
     * @param exception Exceção a ser exibida
     */
    public static void showException(java.awt.Component parent, Exception exception) {
        StringBuilder message = new StringBuilder();
        message.append("Erro: ").append(exception.getMessage()).append("\n\n");
        message.append("Detalhes técnicos:\n");
        message.append(exception.getClass().getSimpleName()).append("\n");
        
        if (exception.getCause() != null) {
            message.append("Causa: ").append(exception.getCause().getMessage());
        }
        
        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        textArea.setRows(10);
        textArea.setColumns(50);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(
            parent,
            scrollPane,
            "Erro Detalhado",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    // Construtor privado para evitar instanciação
    private DialogUtils() {
        throw new AssertionError("Classe utilitária não deve ser instanciada");
    }
}
