package com.example.demo.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Classe principal do frontend JFrame do sistema de gestão de academia.
 * Implementa a interface principal com navegação por abas e design moderno.
 */
public class GymManagementUI extends JFrame {
    
    private static final String TITLE = "Sistema de Gestão de Academia";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 700;
    
    private JTabbedPane tabbedPane;
    
    public GymManagementUI() {
        initializeUI();
        setupLayout();
        configureWindow();
    }
    
    /**
     * Inicializa os componentes da interface
     */
    private void initializeUI() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Painéis serão adicionados nos próximos commits
        addWelcomePanel();
    }
    
    /**
     * Adiciona o painel de boas-vindas temporário
     */
    private void addWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<h1>Bem-vindo ao Sistema de Gestão de Academia</h1>" +
            "<p>Sistema em desenvolvimento - Frontend JFrame</p>" +
            "<p style='margin-top: 20px;'>Funcionalidades em breve:</p>" +
            "<ul style='text-align: left; margin: 20px;'>" +
            "<li>Gestão de Alunos</li>" +
            "<li>Gestão de Planos</li>" +
            "<li>Gestão de Matrículas</li>" +
            "<li>Controle de Pagamentos</li>" +
            "<li>Registro de Frequência</li>" +
            "<li>Planos de Treino</li>" +
            "</ul>" +
            "</div></html>",
            SwingConstants.CENTER
        );
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        tabbedPane.addTab("🏠 Início", welcomePanel);
    }
    
    /**
     * Configura o layout da janela
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Configura propriedades da janela
     */
    private void configureWindow() {
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Define ícone da aplicação (será adicionado depois)
        try {
            // setIconImage(...)
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone da aplicação");
        }
        
        // Aplica Look and Feel do sistema
        applySystemLookAndFeel();
    }
    
    /**
     * Aplica o Look and Feel do sistema operacional
     */
    private void applySystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o Look and Feel do sistema: " + e.getMessage());
        }
    }
    
    /**
     * Adiciona um painel como nova aba
     * 
     * @param title Título da aba
     * @param icon Ícone da aba (opcional)
     * @param panel Painel a ser adicionado
     */
    public void addTab(String title, Icon icon, JPanel panel) {
        if (icon != null) {
            tabbedPane.addTab(title, icon, panel);
        } else {
            tabbedPane.addTab(title, panel);
        }
    }
    
    /**
     * Exibe a interface
     */
    public void show() {
        setVisible(true);
    }
    
    /**
     * Método main para executar a aplicação
     */
    public static void main(String[] args) {
        // Define propriedades do sistema para melhor renderização
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> {
            GymManagementUI mainWindow = new GymManagementUI();
            mainWindow.show();
        });
    }
}
