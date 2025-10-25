package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.*;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;

/**
 * Panel do Dashboard com estatísticas e indicadores
 * COMMIT 9: DashboardPanel - Visão geral do sistema com métricas e gráficos
 */
public class DashboardPanel extends JPanel {
    
    private final ApiClient apiClient;
    
    // Labels de estatísticas
    private JLabel lblTotalAlunos;
    private JLabel lblMatriculasAtivas;
    private JLabel lblReceitaMensal;
    private JLabel lblFrequenciaMedia;
    private JLabel lblTotalExercicios;
    private JLabel lblPlanosTreino;
    
    // Labels de atividades recentes
    private JTextArea txtAtividadesRecentes;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private boolean dataLoaded = false;
    
    public DashboardPanel() {
        this.apiClient = new ApiClient();
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        
        // Carregar dados apenas quando o painel for exibido pela primeira vez
        addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                if (!dataLoaded) {
                    dataLoaded = true;
                    // Aguardar um pouco para garantir que a janela está totalmente visível
                    SwingUtilities.invokeLater(() -> loadDashboardData());
                }
            }
            
            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {}
            
            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });
    }
    
    private void initializeUI() {
        // Painel principal com scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Título
        JLabel lblTitle = new JLabel("📊 Dashboard - Visão Geral do Sistema");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Painel de estatísticas principais (cards)
        JPanel statsPanel = createStatsPanel();
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(statsPanel);
        mainPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Painel de atividades recentes
        JPanel activityPanel = createActivityPanel();
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(activityPanel);
        mainPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Painel de ações rápidas
        JPanel quickActionsPanel = createQuickActionsPanel();
        quickActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(quickActionsPanel);
        
        mainPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, PADDING_LARGE, PADDING_LARGE));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        // Card 1: Total de Alunos
        lblTotalAlunos = new JLabel("0");
        JPanel cardAlunos = createStatCard(
            "👤 Total de Alunos",
            lblTotalAlunos,
            "Alunos cadastrados no sistema",
            PRIMARY_COLOR
        );
        panel.add(cardAlunos);
        
        // Card 2: Matrículas Ativas
        lblMatriculasAtivas = new JLabel("0");
        JPanel cardMatriculas = createStatCard(
            "📝 Matrículas Ativas",
            lblMatriculasAtivas,
            "Alunos com matrícula ativa",
            SUCCESS_COLOR
        );
        panel.add(cardMatriculas);
        
        // Card 3: Receita Mensal
        lblReceitaMensal = new JLabel("R$ 0,00");
        JPanel cardReceita = createStatCard(
            "💰 Receita do Mês",
            lblReceitaMensal,
            "Pagamentos recebidos este mês",
            new Color(255, 193, 7) // Amarelo/Dourado
        );
        panel.add(cardReceita);
        
        // Card 4: Frequência Média
        lblFrequenciaMedia = new JLabel("0%");
        JPanel cardFrequencia = createStatCard(
            "📊 Frequência Média",
            lblFrequenciaMedia,
            "Taxa de presença dos alunos",
            new Color(103, 58, 183) // Roxo
        );
        panel.add(cardFrequencia);
        
        // Card 5: Total de Exercícios
        lblTotalExercicios = new JLabel("0");
        JPanel cardExercicios = createStatCard(
            "💪 Exercícios",
            lblTotalExercicios,
            "Exercícios cadastrados",
            new Color(233, 30, 99) // Rosa
        );
        panel.add(cardExercicios);
        
        // Card 6: Planos de Treino
        lblPlanosTreino = new JLabel("0");
        JPanel cardPlanos = createStatCard(
            "📋 Planos de Treino",
            lblPlanosTreino,
            "Planos ativos no sistema",
            new Color(0, 150, 136) // Teal
        );
        panel.add(cardPlanos);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, JLabel lblValue, String subtitle, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accentColor, 2),
            new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        
        // Título
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_REGULAR);
        lblTitle.setForeground(TEXT_SECONDARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblTitle);
        
        card.add(Box.createVerticalStrut(PADDING_SMALL));
        
        // Valor principal
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setBackground(CARD_BACKGROUND);
        valuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(accentColor);
        valuePanel.add(lblValue);
        
        card.add(valuePanel);
        card.add(Box.createVerticalStrut(PADDING_SMALL));
        
        // Subtítulo
        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblSubtitle);
        
        return card;
    }
    
    private JPanel createActivityPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel lblTitle = new JLabel("📋 Atividades Recentes");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        txtAtividadesRecentes = new JTextArea(10, 50);
        txtAtividadesRecentes.setEditable(false);
        txtAtividadesRecentes.setFont(FONT_REGULAR);
        txtAtividadesRecentes.setForeground(TEXT_PRIMARY);
        txtAtividadesRecentes.setBackground(PANEL_BACKGROUND);
        txtAtividadesRecentes.setCaretColor(PRIMARY_COLOR);
        txtAtividadesRecentes.setBorder(new EmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM));
        txtAtividadesRecentes.setLineWrap(true);
        txtAtividadesRecentes.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtAtividadesRecentes);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollPane);
        
        return panel;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel lblTitle = new JLabel("⚡ Ações Rápidas");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_MEDIUM, PADDING_SMALL));
        buttonsPanel.setBackground(CARD_BACKGROUND);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton btnRefresh = createQuickActionButton("🔄 Atualizar", "Recarregar estatísticas");
        btnRefresh.addActionListener(e -> loadDashboardData());
        
        JButton btnRelatorio = createQuickActionButton("📊 Relatórios", "Ver relatórios detalhados");
        btnRelatorio.addActionListener(e -> MessageDialog.showInfo(this, 
            "Funcionalidade de relatórios será implementada em breve."));
        
        JButton btnExportar = createQuickActionButton("💾 Exportar", "Exportar dados");
        btnExportar.addActionListener(e -> MessageDialog.showInfo(this, 
            "Funcionalidade de exportação será implementada em breve."));
        
        buttonsPanel.add(btnRefresh);
        buttonsPanel.add(btnRelatorio);
        buttonsPanel.add(btnExportar);
        
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    private JButton createQuickActionButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(FONT_REGULAR);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    // ========== CARREGAMENTO DE DADOS ==========
    
    private void loadDashboardData() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando estatísticas...",
            () -> {
                try {
                    // Carregar todas as estatísticas em paralelo
                    int totalAlunos = loadTotalAlunos();
                    int matriculasAtivas = loadMatriculasAtivas();
                    BigDecimal receitaMensal = loadReceitaMensal();
                    double frequenciaMedia = loadFrequenciaMedia();
                    int totalExercicios = loadTotalExercicios();
                    int planosTreino = loadPlanosTreino();
                    String atividades = loadAtividadesRecentes();
                    
                    // Atualizar UI na thread principal
                    SwingUtilities.invokeLater(() -> {
                        lblTotalAlunos.setText(String.valueOf(totalAlunos));
                        lblMatriculasAtivas.setText(String.valueOf(matriculasAtivas));
                        lblReceitaMensal.setText(String.format("R$ %.2f", receitaMensal));
                        lblFrequenciaMedia.setText(String.format("%.1f%%", frequenciaMedia));
                        lblTotalExercicios.setText(String.valueOf(totalExercicios));
                        lblPlanosTreino.setText(String.valueOf(planosTreino));
                        txtAtividadesRecentes.setText(atividades);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        MessageDialog.showError(DashboardPanel.this, 
                            "Erro ao carregar estatísticas: " + ex.getMessage());
                    });
                }
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar dashboard: " + error.getMessage());
                }
            }
        );
    }
    
    private int loadTotalAlunos() {
        try {
            String response = apiClient.get("/alunos");
            List<AlunoDTO> alunos = apiClient.fromJsonArray(response, AlunoDTO.class);
            return alunos.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private int loadMatriculasAtivas() {
        try {
            String response = apiClient.get("/matriculas");
            List<MatriculaResponseDTO> matriculas = apiClient.fromJsonArray(response, MatriculaResponseDTO.class);
            return (int) matriculas.stream()
                .filter(m -> m.getStatus() != null && m.getStatus().name().equals("ATIVA"))
                .count();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private BigDecimal loadReceitaMensal() {
        try {
            String response = apiClient.get("/pagamentos");
            List<PagamentoResponseDTO> pagamentos = apiClient.fromJsonArray(response, PagamentoResponseDTO.class);
            
            LocalDate hoje = LocalDate.now();
            int mesAtual = hoje.getMonthValue();
            int anoAtual = hoje.getYear();
            
            return pagamentos.stream()
                .filter(p -> p.getDataPagamento() != null)
                .filter(p -> p.getDataPagamento().getMonthValue() == mesAtual && 
                           p.getDataPagamento().getYear() == anoAtual)
                .map(PagamentoResponseDTO::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }
    
    private double loadFrequenciaMedia() {
        try {
            String response = apiClient.get("/frequencias");
            List<FrequenciaResponseDTO> frequencias = apiClient.fromJsonArray(response, FrequenciaResponseDTO.class);
            
            if (frequencias.isEmpty()) {
                return 0.0;
            }
            
            long presentes = frequencias.stream()
                .filter(f -> Boolean.TRUE.equals(f.getPresenca()))
                .count();
            
            return (presentes * 100.0) / frequencias.size();
        } catch (Exception ex) {
            return 0.0;
        }
    }
    
    private int loadTotalExercicios() {
        try {
            String response = apiClient.get("/exercicios");
            List<ExercicioResponseDTO> exercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
            return exercicios.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private int loadPlanosTreino() {
        try {
            String response = apiClient.get("/planos-treino");
            List<PlanoTreinoResponseDTO> planos = apiClient.fromJsonArray(response, PlanoTreinoResponseDTO.class);
            return planos.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private String loadAtividadesRecentes() {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Atividades Recentes do Sistema:\n\n");
        
        try {
            // Últimas matrículas
            String responseMatriculas = apiClient.get("/matriculas");
            List<MatriculaResponseDTO> matriculas = apiClient.fromJsonArray(responseMatriculas, MatriculaResponseDTO.class);
            
            sb.append("📝 Últimas Matrículas:\n");
            matriculas.stream()
                .sorted((m1, m2) -> m2.getDataInicio().compareTo(m1.getDataInicio()))
                .limit(3)
                .forEach(m -> {
                    sb.append(String.format("   • %s - %s (%s)\n", 
                        m.getDataInicio().format(FORMATTER),
                        m.getNomeAluno(),
                        m.getStatus()));
                });
            
            sb.append("\n");
            
            // Últimos pagamentos
            String responsePagamentos = apiClient.get("/pagamentos");
            List<PagamentoResponseDTO> pagamentos = apiClient.fromJsonArray(responsePagamentos, PagamentoResponseDTO.class);
            
            sb.append("💰 Últimos Pagamentos:\n");
            pagamentos.stream()
                .filter(p -> p.getDataPagamento() != null)
                .sorted((p1, p2) -> p2.getDataPagamento().compareTo(p1.getDataPagamento()))
                .limit(3)
                .forEach(p -> {
                    sb.append(String.format("   • %s - %s: R$ %.2f\n",
                        p.getDataPagamento().format(FORMATTER),
                        p.getNomeAluno(),
                        p.getValorPago()));
                });
            
            sb.append("\n");
            
            // Últimos planos de treino
            String responsePlanos = apiClient.get("/planos-treino");
            List<PlanoTreinoResponseDTO> planos = apiClient.fromJsonArray(responsePlanos, PlanoTreinoResponseDTO.class);
            
            sb.append("📋 Últimos Planos de Treino:\n");
            planos.stream()
                .sorted((p1, p2) -> p2.getDataCriacao().compareTo(p1.getDataCriacao()))
                .limit(3)
                .forEach(p -> {
                    sb.append(String.format("   • %s - %s (Instrutor: %s)\n",
                        p.getDataCriacao().format(FORMATTER),
                        p.getNomeAluno(),
                        p.getNomeInstrutor()));
                });
            
        } catch (Exception ex) {
            sb.append("Erro ao carregar atividades recentes: ").append(ex.getMessage());
        }
        
        return sb.toString();
    }
}
