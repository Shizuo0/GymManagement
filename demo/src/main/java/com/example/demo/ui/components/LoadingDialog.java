package com.example.demo.ui.components;

import javax.swing.*;
import java.awt.*;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Diálogo de carregamento customizado.
 * Exibe uma barra de progresso indeterminada com mensagem.
 */
public class LoadingDialog extends JDialog {
    
    private JLabel messageLabel;
    private JProgressBar progressBar;
    
    /**
     * Construtor com mensagem padrão
     * 
     * @param parent Frame pai
     */
    public LoadingDialog(Frame parent) {
        this(parent, "Carregando, aguarde...");
    }
    
    /**
     * Construtor com mensagem customizada
     * 
     * @param parent Frame pai
     * @param message Mensagem de carregamento
     */
    public LoadingDialog(Frame parent, String message) {
        super(parent, "Processando", false);  // Non-modal
        setupDialog(message);
    }
    
    /**
     * Construtor para Dialog pai
     * 
     * @param parent Dialog pai
     * @param message Mensagem de carregamento
     */
    public LoadingDialog(Dialog parent, String message) {
        super(parent, "Processando", false);  // Non-modal
        setupDialog(message);
    }
    
    /**
     * Configura o diálogo
     * 
     * @param message Mensagem de carregamento
     */
    private void setupDialog(String message) {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        
        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(CARD_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE, PADDING_XLARGE)
        ));
        
        // Ícone de loading (animação seria melhor, mas usaremos emoji por enquanto)
        JLabel iconLabel = new JLabel("⏳");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Mensagem
        messageLabel = new JLabel(message);
        messageLabel.setFont(FONT_REGULAR);
        messageLabel.setForeground(TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Barra de progresso
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 24));
        progressBar.setMaximumSize(new Dimension(300, 24));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setBackground(SURFACE_COLOR);
        progressBar.setForeground(PRIMARY_COLOR);
        progressBar.setBorderPainted(false);
        
        // Adiciona componentes
        mainPanel.add(iconLabel);
        mainPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        mainPanel.add(progressBar);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(getParent());
    }
    
    /**
     * Atualiza a mensagem de carregamento
     * 
     * @param message Nova mensagem
     */
    public void setMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            messageLabel.setText(message);
            pack();
        });
    }
    
    /**
     * Exibe o diálogo em uma thread separada
     */
    public void showDialog() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }
    
    /**
     * Fecha o diálogo
     */
    public void closeDialog() {
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            dispose();
        });
    }
    
    /**
     * Executa uma tarefa em background com diálogo de loading
     * 
     * @param parent Window pai
     * @param message Mensagem de loading
     * @param task Tarefa a ser executada
     * @param onComplete Ação a ser executada após conclusão (opcional)
     * @param onError Ação a ser executada em caso de erro (opcional)
     */
    public static void executeWithLoading(
            Window parent,
            String message,
            BackgroundTask task,
            Runnable onComplete,
            java.util.function.Consumer<Exception> onError) {
        
        Frame frameParent = parent instanceof Frame ? (Frame) parent : null;
        LoadingDialog dialog = new LoadingDialog(frameParent, message);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private Exception exception = null;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    task.execute();
                } catch (Exception e) {
                    exception = e;
                }
                return null;
            }
            
            @Override
            protected void done() {
                dialog.closeDialog();
                
                if (exception != null) {
                    if (onError != null) {
                        onError.accept(exception);
                    }
                } else {
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        };
        
        worker.execute();
        dialog.showDialog();
    }
    
    /**
     * Interface funcional para tarefas em background
     */
    @FunctionalInterface
    public interface BackgroundTask {
        void execute() throws Exception;
    }
}
