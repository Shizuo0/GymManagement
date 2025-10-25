package com.example.demo.ui;

import com.example.demo.ui.panels.AlunoPanel;
import com.example.demo.ui.panels.PlanoPanel;
import com.example.demo.ui.panels.MatriculaPanel;
import com.example.demo.ui.panels.PagamentoPanel;
import com.example.demo.ui.panels.FrequenciaPanel;

import javax.swing.*;
import java.awt.*;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Classe principal do frontend JFrame do sistema de gestão de academia.
 * Implementa a interface principal com navegação por abas e design moderno escuro.
 */
public class GymManagementUI extends JFrame {
    
    private static final String TITLE = "🏋️ Sistema de Gestão de Academia";
    private static final int DEFAULT_WIDTH = 1400;
    private static final int DEFAULT_HEIGHT = 800;
    
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
        tabbedPane.setFont(FONT_REGULAR);
        tabbedPane.setBackground(BACKGROUND_SECONDARY);
        tabbedPane.setForeground(TEXT_PRIMARY);
        
        // Adiciona painéis
        addWelcomePanel();
        addAlunoPanel();
        addPlanoPanel();
        addMatriculaPanel();
        addPagamentoPanel();
        addFrequenciaPanel();
    }
    
    /**
     * Adiciona o painel de alunos
     */
    private void addAlunoPanel() {
        AlunoPanel alunoPanel = new AlunoPanel();
        tabbedPane.addTab("👤 Alunos", alunoPanel);
    }
    
    /**
     * Adiciona o painel de planos
     */
    private void addPlanoPanel() {
        PlanoPanel planoPanel = new PlanoPanel();
        tabbedPane.addTab("💳 Planos", planoPanel);
    }
    
    /**
     * Adiciona o painel de matrículas
     */
    private void addMatriculaPanel() {
        MatriculaPanel matriculaPanel = new MatriculaPanel();
        tabbedPane.addTab("📝 Matrículas", matriculaPanel);
    }
    
    /**
     * Adiciona o painel de pagamentos
     */
    private void addPagamentoPanel() {
        PagamentoPanel pagamentoPanel = new PagamentoPanel();
        tabbedPane.addTab("💰 Pagamentos", pagamentoPanel);
    }
    
    /**
     * Adiciona o painel de frequências
     */
    private void addFrequenciaPanel() {
        FrequenciaPanel frequenciaPanel = new FrequenciaPanel();
        tabbedPane.addTab("📊 Frequência", frequenciaPanel);
    }
    
    /**
     * Adiciona o painel de boas-vindas temporário
     */
    private void addWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(PANEL_BACKGROUND);
        
        JLabel welcomeLabel = new JLabel(
            "<html><div style='text-align: center; color: #FFFFFF;'>" +
            "<h1 style='color: #4169E1;'>🏋️ Bem-vindo ao Sistema de Gestão de Academia</h1>" +
            "<p style='color: #AEAEB2; font-size: 14px;'>Sistema moderno com design escuro e azul royal</p>" +
            "<p style='margin-top: 30px; color: #6495ED;'><strong>Funcionalidades em breve:</strong></p>" +
            "<ul style='text-align: left; margin: 20px 100px; color: #FFFFFF; line-height: 2;'>" +
            "<li>📋 Gestão de Alunos</li>" +
            "<li>💳 Gestão de Planos</li>" +
            "<li>✅ Gestão de Matrículas</li>" +
            "<li>💰 Controle de Pagamentos</li>" +
            "<li>📊 Registro de Frequência</li>" +
            "<li>💪 Planos de Treino</li>" +
            "</ul>" +
            "</div></html>",
            SwingConstants.CENTER
        );
        welcomeLabel.setFont(FONT_REGULAR);
        
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        tabbedPane.addTab("🏠 Início", welcomePanel);
    }
    
    /**
     * Configura o layout da janela
     */
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Configura propriedades da janela
     */
    private void configureWindow() {
        setTitle(TITLE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(1200, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Define ícone da aplicação (será adicionado depois)
        try {
            // setIconImage(...)
        } catch (Exception e) {
            System.err.println("Não foi possível carregar o ícone da aplicação");
        }
        
        // Aplica Look and Feel do sistema com tema escuro
        applyDarkTheme();
    }
    
    /**
     * Aplica o tema escuro customizado
     */
    private void applyDarkTheme() {
        try {
            // Define propriedades do UIManager para tema escuro
            UIManager.put("control", CARD_BACKGROUND);
            UIManager.put("info", CARD_BACKGROUND);
            UIManager.put("nimbusBase", BACKGROUND_SECONDARY);
            UIManager.put("nimbusAlertYellow", WARNING_COLOR);
            UIManager.put("nimbusDisabledText", TEXT_TERTIARY);
            UIManager.put("nimbusFocus", PRIMARY_COLOR);
            UIManager.put("nimbusGreen", SUCCESS_COLOR);
            UIManager.put("nimbusInfoBlue", INFO_COLOR);
            UIManager.put("nimbusLightBackground", PANEL_BACKGROUND);
            UIManager.put("nimbusOrange", WARNING_COLOR);
            UIManager.put("nimbusRed", ERROR_COLOR);
            UIManager.put("nimbusSelectedText", TEXT_ON_PRIMARY);
            UIManager.put("nimbusSelectionBackground", PRIMARY_COLOR);
            UIManager.put("text", TEXT_PRIMARY);
            
            // Cores de componentes
            UIManager.put("Panel.background", PANEL_BACKGROUND);
            UIManager.put("OptionPane.background", CARD_BACKGROUND);
            UIManager.put("TabbedPane.background", BACKGROUND_SECONDARY);
            UIManager.put("TabbedPane.selected", PRIMARY_COLOR);
            UIManager.put("TabbedPane.contentAreaColor", PANEL_BACKGROUND);
            UIManager.put("TabbedPane.borderHighlightColor", BORDER_COLOR);
            UIManager.put("TabbedPane.darkShadow", BACKGROUND_COLOR);
            
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.err.println("Não foi possível aplicar o tema escuro: " + e.getMessage());
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
