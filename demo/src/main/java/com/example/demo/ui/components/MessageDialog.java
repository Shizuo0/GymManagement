package com.example.demo.ui.components;

import javax.swing.*;
import java.awt.*;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Diálogo de mensagem customizado com visual consistente.
 * Fornece métodos estáticos para diferentes tipos de mensagens.
 */
public class MessageDialog {
    
    /**
     * Tipo de mensagem
     */
    public enum MessageType {
        SUCCESS(SUCCESS_COLOR, ICON_SUCCESS, "Sucesso"),
        ERROR(ERROR_COLOR, ICON_ERROR, "Erro"),
        WARNING(WARNING_COLOR, ICON_WARNING, "Aviso"),
        INFO(INFO_COLOR, ICON_INFO, "Informação");
        
        final Color color;
        final String icon;
        final String title;
        
        MessageType(Color color, String icon, String title) {
            this.color = color;
            this.icon = icon;
            this.title = title;
        }
    }
    
    /**
     * Exibe um diálogo customizado
     * 
     * @param parent Componente pai
     * @param message Mensagem a ser exibida
     * @param type Tipo da mensagem
     */
    public static void show(Component parent, String message, MessageType type) {
        JDialog dialog = createDialog(parent, message, type);
        dialog.setVisible(true);
    }
    
    /**
     * Exibe mensagem de sucesso
     * 
     * @param parent Componente pai
     * @param message Mensagem
     */
    public static void showSuccess(Component parent, String message) {
        show(parent, message, MessageType.SUCCESS);
    }
    
    /**
     * Exibe mensagem de erro
     * 
     * @param parent Componente pai
     * @param message Mensagem
     */
    public static void showError(Component parent, String message) {
        show(parent, message, MessageType.ERROR);
    }
    
    /**
     * Exibe mensagem de aviso
     * 
     * @param parent Componente pai
     * @param message Mensagem
     */
    public static void showWarning(Component parent, String message) {
        show(parent, message, MessageType.WARNING);
    }
    
    /**
     * Exibe mensagem de informação
     * 
     * @param parent Componente pai
     * @param message Mensagem
     */
    public static void showInfo(Component parent, String message) {
        show(parent, message, MessageType.INFO);
    }
    
    /**
     * Exibe diálogo de confirmação
     * 
     * @param parent Componente pai
     * @param message Mensagem
     * @param title Título
     * @return true se confirmado, false caso contrário
     */
    public static boolean showConfirmation(Component parent, String message, String title) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(true);
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        mainPanel.setBackground(CARD_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE)
        ));
        
        // Painel de conteúdo
        JPanel contentPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        contentPanel.setBackground(CARD_BACKGROUND);
        
        // Ícone
        JLabel iconLabel = new JLabel(ICON_WARNING);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Mensagem
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(FONT_REGULAR);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBackground(CARD_BACKGROUND);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM));
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(textArea, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        final boolean[] result = {false};
        
        CustomButton yesButton = new CustomButton("Sim", CustomButton.ButtonType.SUCCESS);
        yesButton.addActionListener(e -> {
            result[0] = true;
            dialog.dispose();
        });
        
        CustomButton noButton = new CustomButton("Não", CustomButton.ButtonType.DEFAULT);
        noButton.addActionListener(e -> {
            result[0] = false;
            dialog.dispose();
        });
        
        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, 220));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    /**
     * Exibe diálogo de confirmação padrão de exclusão
     * 
     * @param parent Componente pai
     * @return true se confirmado, false caso contrário
     */
    public static boolean showDeleteConfirmation(Component parent) {
        return showConfirmation(parent, MSG_CONFIRM_DELETE, "Confirmar Exclusão");
    }
    
    /**
     * Cria um diálogo customizado
     * 
     * @param parent Componente pai
     * @param message Mensagem
     * @param type Tipo da mensagem
     * @return JDialog configurado
     */
    private static JDialog createDialog(Component parent, String message, MessageType type) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), type.title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setUndecorated(true);
        
        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        mainPanel.setBackground(CARD_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(type.color, 2),
            BorderFactory.createEmptyBorder(PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE)
        ));
        
        // Painel de conteúdo
        JPanel contentPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        contentPanel.setBackground(CARD_BACKGROUND);
        
        // Ícone
        JLabel iconLabel = new JLabel(type.icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Mensagem
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(FONT_REGULAR);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBackground(CARD_BACKGROUND);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM));
        
        contentPanel.add(iconLabel, BorderLayout.WEST);
        contentPanel.add(textArea, BorderLayout.CENTER);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        CustomButton okButton = new CustomButton("OK", getButtonType(type));
        okButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, 200));
        dialog.setLocationRelativeTo(parent);
        
        return dialog;
    }
    
    /**
     * Retorna o tipo de botão apropriado para o tipo de mensagem
     * 
     * @param messageType Tipo da mensagem
     * @return Tipo do botão
     */
    private static CustomButton.ButtonType getButtonType(MessageType messageType) {
        switch (messageType) {
            case SUCCESS:
                return CustomButton.ButtonType.SUCCESS;
            case ERROR:
                return CustomButton.ButtonType.DANGER;
            case WARNING:
                return CustomButton.ButtonType.WARNING;
            case INFO:
            default:
                return CustomButton.ButtonType.PRIMARY;
        }
    }
    
    // Construtor privado para evitar instanciação
    private MessageDialog() {
        throw new AssertionError("Classe utilitária não deve ser instanciada");
    }
}
