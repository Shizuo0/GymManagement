package com.example.demo.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.example.demo.ui.panels.AlunoPanel;
import com.example.demo.ui.panels.DashboardPanel;
import com.example.demo.ui.panels.ExercicioPanel;
import com.example.demo.ui.panels.FrequenciaPanel;
import com.example.demo.ui.panels.InstrutorPanel;
import com.example.demo.ui.panels.ItemTreinoPanel;
import com.example.demo.ui.panels.MatriculaPanel;
import com.example.demo.ui.panels.PagamentoPanel;
import com.example.demo.ui.panels.PlanoPanel;
import com.example.demo.ui.panels.PlanoTreinoPanel;
import static com.example.demo.ui.utils.UIConstants.BACKGROUND_COLOR;
import static com.example.demo.ui.utils.UIConstants.BACKGROUND_SECONDARY;
import static com.example.demo.ui.utils.UIConstants.BORDER_COLOR;
import static com.example.demo.ui.utils.UIConstants.CARD_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.ERROR_COLOR;
import static com.example.demo.ui.utils.UIConstants.FONT_REGULAR;
import static com.example.demo.ui.utils.UIConstants.INFO_COLOR;
import static com.example.demo.ui.utils.UIConstants.PANEL_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.PRIMARY_COLOR;
import static com.example.demo.ui.utils.UIConstants.SUCCESS_COLOR;
import static com.example.demo.ui.utils.UIConstants.TEXT_ON_PRIMARY;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;
import static com.example.demo.ui.utils.UIConstants.TEXT_TERTIARY;
import static com.example.demo.ui.utils.UIConstants.WARNING_COLOR;

/**
 * Classe principal do frontend JFrame do sistema de gestão de academia.
 * Implementa a interface principal com navegação por abas e design moderno escuro.
 */
public class GymManagementUI extends JFrame {
    
    private static final String TITLE = "Sistema de Gestão de Academia";
    private static final int DEFAULT_WIDTH = 1400;
    private static final int DEFAULT_HEIGHT = 900;
    
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
        
        // Adiciona painéis organizados por categoria
        // Visão Geral
        addDashboardPanel();
        
        // Cadastros Principais
        addAlunoPanel();
        addInstrutorPanel();
        addExercicioPanel();
        
        // Planos e Treinos
        addPlanoPanel();
        addPlanoTreinoPanel();
        addItemTreinoPanel();
        
        // Gestão Financeira
        addMatriculaPanel();
        addPagamentoPanel();
        
        // Acompanhamento
        addFrequenciaPanel();
    }
    
    /**
     * Adiciona o painel de dashboard
     */
    private void addDashboardPanel() {
        DashboardPanel dashboardPanel = new DashboardPanel();
        tabbedPane.addTab("Dashboard", dashboardPanel);
    }
    
    /**
     * Adiciona o painel de alunos
     */
    private void addAlunoPanel() {
        AlunoPanel alunoPanel = new AlunoPanel();
        tabbedPane.addTab("Alunos", alunoPanel);
    }
    
    /**
     * Adiciona o painel de instrutores
     */
    private void addInstrutorPanel() {
        InstrutorPanel instrutorPanel = new InstrutorPanel();
        tabbedPane.addTab("Instrutores", instrutorPanel);
    }
    
    /**
     * Adiciona o painel de planos
     */
    private void addPlanoPanel() {
        PlanoPanel planoPanel = new PlanoPanel();
        tabbedPane.addTab("Planos", planoPanel);
    }
    
    /**
     * Adiciona o painel de matrículas
     */
    private void addMatriculaPanel() {
        MatriculaPanel matriculaPanel = new MatriculaPanel();
        tabbedPane.addTab("Matrículas", matriculaPanel);
    }
    
    /**
     * Adiciona o painel de pagamentos
     */
    private void addPagamentoPanel() {
        PagamentoPanel pagamentoPanel = new PagamentoPanel();
        tabbedPane.addTab("Pagamentos", pagamentoPanel);
    }
    
    /**
     * Adiciona o painel de frequências
     */
    private void addFrequenciaPanel() {
        FrequenciaPanel frequenciaPanel = new FrequenciaPanel();
        tabbedPane.addTab("Frequência", frequenciaPanel);
    }
    
    /**
     * Adiciona o painel de exercícios
     */
    private void addExercicioPanel() {
        ExercicioPanel exercicioPanel = new ExercicioPanel();
        tabbedPane.addTab("Exercícios", exercicioPanel);
    }
    
    /**
     * Adiciona o painel de planos de treino
     */
    private void addPlanoTreinoPanel() {
        PlanoTreinoPanel planoTreinoPanel = new PlanoTreinoPanel();
        tabbedPane.addTab("Planos de Treino", planoTreinoPanel);
    }
    
    /**
     * Adiciona o painel de itens de treino
     */
    private void addItemTreinoPanel() {
        ItemTreinoPanel itemTreinoPanel = new ItemTreinoPanel();
        tabbedPane.addTab("Exercícios do Plano", itemTreinoPanel);
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
            
            // REMOVIDO: SwingUtilities.updateComponentTreeUI(this) causava loop infinito
            
            // Força repaint
            // A UI já aplica as propriedades do UIManager automaticamente
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
     * Método main para executar a aplicação
     */
    public static void main(String[] args) {
        // Define propriedades do sistema para melhor renderização
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> {
            GymManagementUI mainWindow = new GymManagementUI();
            mainWindow.setVisible(true);
        });
    }
}
