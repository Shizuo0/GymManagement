package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.*;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;

/**
 * Panel de Busca Global cross-entity
 * COMMIT 9: BuscaGlobalPanel - Busca em todas as entidades do sistema
 */
public class BuscaGlobalPanel extends JPanel {
    
    private final ApiClient apiClient;
    
    // Componentes
    private CustomTextField txtBusca;
    private CustomButton btnBuscar;
    private CustomButton btnLimpar;
    private JTabbedPane tabbedResultados;
    
    // Tabelas de resultados
    private CustomTable tableAlunos;
    private CustomTable tablePlanos;
    private CustomTable tableMatriculas;
    private CustomTable tablePagamentos;
    private CustomTable tableExercicios;
    private CustomTable tablePlanosTreino;
    
    // Labels de contadores
    private JLabel lblTotalResultados;
    
    public BuscaGlobalPanel() {
        this.apiClient = new ApiClient();
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
    }
    
    private void initializeUI() {
        // Painel superior com busca
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Painel central com resultados em abas
        JPanel resultsPanel = createResultsPanel();
        add(resultsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        
        // Título
        JLabel lblTitle = new JLabel("🔍 Busca Global");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        JLabel lblSubtitle = new JLabel("Pesquise em todas as entidades do sistema simultaneamente");
        lblSubtitle.setFont(FONT_REGULAR);
        lblSubtitle.setForeground(TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblSubtitle);
        
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Campo de busca
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        inputPanel.setBackground(CARD_BACKGROUND);
        inputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtBusca = new CustomTextField("Digite sua busca (nome, CPF, descrição, etc)...", 40);
        txtBusca.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarGlobal();
                }
            }
        });
        
        btnBuscar = new CustomButton("🔍 Buscar", CustomButton.ButtonType.PRIMARY);
        btnBuscar.addActionListener(e -> buscarGlobal());
        
        btnLimpar = new CustomButton("🔄 Limpar", CustomButton.ButtonType.SECONDARY);
        btnLimpar.addActionListener(e -> limparResultados());
        
        inputPanel.add(txtBusca);
        inputPanel.add(btnBuscar);
        inputPanel.add(btnLimpar);
        
        panel.add(inputPanel);
        
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Label de total de resultados
        lblTotalResultados = new JLabel(" ");
        lblTotalResultados.setFont(FONT_REGULAR);
        lblTotalResultados.setForeground(PRIMARY_COLOR);
        lblTotalResultados.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTotalResultados);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        tabbedResultados = new JTabbedPane(JTabbedPane.TOP);
        tabbedResultados.setFont(FONT_REGULAR);
        tabbedResultados.setBackground(BACKGROUND_SECONDARY);
        tabbedResultados.setForeground(TEXT_PRIMARY);
        
        // Criar abas para cada entidade
        tabbedResultados.addTab("👤 Alunos", createAlunosTab());
        tabbedResultados.addTab("💳 Planos", createPlanosTab());
        tabbedResultados.addTab("📝 Matrículas", createMatriculasTab());
        tabbedResultados.addTab("💰 Pagamentos", createPagamentosTab());
        tabbedResultados.addTab("💪 Exercícios", createExerciciosTab());
        tabbedResultados.addTab("📋 Planos Treino", createPlanosTreinoTab());
        
        panel.add(tabbedResultados, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JScrollPane createAlunosTab() {
        String[] colunas = {"ID", "Nome", "CPF", "Data Ingresso"};
        tableAlunos = new CustomTable(colunas);
        tableAlunos.setPreferredScrollableViewportSize(new Dimension(900, 400));
        
        JScrollPane scrollPane = new JScrollPane(tableAlunos);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        return scrollPane;
    }
    
    private JScrollPane createPlanosTab() {
        String[] colunas = {"ID", "Nome", "Descrição", "Valor", "Duração (meses)", "Status"};
        tablePlanos = new CustomTable(colunas);
        tablePlanos.setPreferredScrollableViewportSize(new Dimension(900, 400));
        
        JScrollPane scrollPane = new JScrollPane(tablePlanos);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        return scrollPane;
    }
    
    private JScrollPane createMatriculasTab() {
        String[] colunas = {"ID", "Aluno", "Plano", "Data Início", "Data Fim", "Status"};
        tableMatriculas = new CustomTable(colunas);
        tableMatriculas.setPreferredScrollableViewportSize(new Dimension(900, 400));
        
        JScrollPane scrollPane = new JScrollPane(tableMatriculas);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        return scrollPane;
    }
    
    private JScrollPane createPagamentosTab() {
        String[] colunas = {"ID", "Aluno", "Plano", "Data Pagamento", "Valor", "Forma Pagamento"};
        tablePagamentos = new CustomTable(colunas);
        tablePagamentos.setPreferredScrollableViewportSize(new Dimension(900, 400));
        
        JScrollPane scrollPane = new JScrollPane(tablePagamentos);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        return scrollPane;
    }
    
    private JScrollPane createExerciciosTab() {
        String[] colunas = {"ID", "Nome", "Grupo Muscular"};
        tableExercicios = new CustomTable(colunas);
        tableExercicios.setPreferredScrollableViewportSize(new Dimension(900, 400));
        
        JScrollPane scrollPane = new JScrollPane(tableExercicios);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        return scrollPane;
    }
    
    private JScrollPane createPlanosTreinoTab() {
        String[] colunas = {"ID", "Aluno", "Instrutor", "Data Criação", "Duração (sem)", "Descrição"};
        tablePlanosTreino = new CustomTable(colunas);
        tablePlanosTreino.setPreferredScrollableViewportSize(new Dimension(900, 400));
        
        JScrollPane scrollPane = new JScrollPane(tablePlanosTreino);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        return scrollPane;
    }
    
    // ========== BUSCA GLOBAL ==========
    
    private void buscarGlobal() {
        String termo = txtBusca.getText().trim();
        
        if (termo.isEmpty()) {
            MessageDialog.showWarning(this, "Digite um termo para buscar.");
            return;
        }
        
        if (termo.length() < 2) {
            MessageDialog.showWarning(this, "Digite pelo menos 2 caracteres para buscar.");
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando em todas as entidades...",
            () -> {
                int totalResultados = 0;
                
                // Buscar em todas as entidades
                totalResultados += buscarAlunos(termo);
                totalResultados += buscarPlanos(termo);
                totalResultados += buscarMatriculas(termo);
                totalResultados += buscarPagamentos(termo);
                totalResultados += buscarExercicios(termo);
                totalResultados += buscarPlanosTreino(termo);
                
                final int total = totalResultados;
                
                SwingUtilities.invokeLater(() -> {
                    if (total == 0) {
                        lblTotalResultados.setText("❌ Nenhum resultado encontrado para: \"" + termo + "\"");
                        MessageDialog.showInfo(BuscaGlobalPanel.this, 
                            "Nenhum resultado encontrado para: \"" + termo + "\"");
                    } else {
                        lblTotalResultados.setText(String.format(
                            "✅ %d resultado(s) encontrado(s) para: \"%s\"", total, termo));
                    }
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro na busca: " + error.getMessage());
                }
            }
        );
    }
    
    private int buscarAlunos(String termo) {
        try {
            String response = apiClient.get("/api/alunos");
            List<AlunoDTO> alunos = apiClient.fromJsonArray(response, AlunoDTO.class);
            
            List<AlunoDTO> filtrados = alunos.stream()
                .filter(a -> 
                    a.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                    (a.getCpf() != null && a.getCpf().contains(termo))
                )
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                tableAlunos.clearRows();
                for (AlunoDTO aluno : filtrados) {
                    tableAlunos.addRow(new Object[]{
                        aluno.getIdAluno(),
                        aluno.getNome(),
                        aluno.getCpf(),
                        aluno.getDataIngresso() != null ? aluno.getDataIngresso().toString() : "-"
                    });
                }
                
                // Atualizar contador da aba
                int index = 0;
                tabbedResultados.setTitleAt(index, String.format("👤 Alunos (%d)", filtrados.size()));
            });
            
            return filtrados.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private int buscarPlanos(String termo) {
        try {
            String response = apiClient.get("/api/planos");
            List<PlanoResponseDTO> planos = apiClient.fromJsonArray(response, PlanoResponseDTO.class);
            
            List<PlanoResponseDTO> filtrados = planos.stream()
                .filter(p -> 
                    p.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                    (p.getDescricao() != null && p.getDescricao().toLowerCase().contains(termo.toLowerCase()))
                )
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                tablePlanos.clearRows();
                for (PlanoResponseDTO plano : filtrados) {
                    tablePlanos.addRow(new Object[]{
                        plano.getId(),
                        plano.getNome(),
                        plano.getDescricao() != null ? plano.getDescricao() : "-",
                        String.format("R$ %.2f", plano.getValor()),
                        plano.getDuracaoMeses() != null ? plano.getDuracaoMeses() + " meses" : "-",
                        plano.getStatus()
                    });
                }
                
                // Atualizar contador da aba
                int index = 1;
                tabbedResultados.setTitleAt(index, String.format("💳 Planos (%d)", filtrados.size()));
            });
            
            return filtrados.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private int buscarMatriculas(String termo) {
        try {
            String response = apiClient.get("/api/matriculas");
            List<MatriculaResponseDTO> matriculas = apiClient.fromJsonArray(response, MatriculaResponseDTO.class);
            
            List<MatriculaResponseDTO> filtrados = matriculas.stream()
                .filter(m -> 
                    m.getNomeAluno().toLowerCase().contains(termo.toLowerCase()) ||
                    m.getNomePlano().toLowerCase().contains(termo.toLowerCase())
                )
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                tableMatriculas.clearRows();
                for (MatriculaResponseDTO matricula : filtrados) {
                    tableMatriculas.addRow(new Object[]{
                        matricula.getId(),
                        matricula.getNomeAluno(),
                        matricula.getNomePlano(),
                        matricula.getDataInicio().toString(),
                        matricula.getDataFim() != null ? matricula.getDataFim().toString() : "-",
                        matricula.getStatus()
                    });
                }
                
                // Atualizar contador da aba
                int index = 2;
                tabbedResultados.setTitleAt(index, String.format("📝 Matrículas (%d)", filtrados.size()));
            });
            
            return filtrados.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private int buscarPagamentos(String termo) {
        try {
            String response = apiClient.get("/api/pagamentos");
            List<PagamentoResponseDTO> pagamentos = apiClient.fromJsonArray(response, PagamentoResponseDTO.class);
            
            List<PagamentoResponseDTO> filtrados = pagamentos.stream()
                .filter(p -> 
                    p.getNomeAluno().toLowerCase().contains(termo.toLowerCase()) ||
                    p.getNomePlano().toLowerCase().contains(termo.toLowerCase()) ||
                    (p.getFormaPagamento() != null && p.getFormaPagamento().toLowerCase().contains(termo.toLowerCase()))
                )
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                tablePagamentos.clearRows();
                for (PagamentoResponseDTO pagamento : filtrados) {
                    tablePagamentos.addRow(new Object[]{
                        pagamento.getIdPagamento(),
                        pagamento.getNomeAluno(),
                        pagamento.getNomePlano(),
                        pagamento.getDataPagamento() != null ? pagamento.getDataPagamento().toString() : "-",
                        String.format("R$ %.2f", pagamento.getValorPago()),
                        pagamento.getFormaPagamento() != null ? pagamento.getFormaPagamento() : "-"
                    });
                }
                
                // Atualizar contador da aba
                int index = 3;
                tabbedResultados.setTitleAt(index, String.format("💰 Pagamentos (%d)", filtrados.size()));
            });
            
            return filtrados.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private int buscarExercicios(String termo) {
        try {
            String response = apiClient.get("/api/exercicios");
            List<ExercicioResponseDTO> exercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
            
            List<ExercicioResponseDTO> filtrados = exercicios.stream()
                .filter(e -> 
                    e.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                    (e.getGrupoMuscular() != null && e.getGrupoMuscular().toLowerCase().contains(termo.toLowerCase()))
                )
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                tableExercicios.clearRows();
                for (ExercicioResponseDTO exercicio : filtrados) {
                    tableExercicios.addRow(new Object[]{
                        exercicio.getId(),
                        exercicio.getNome(),
                        exercicio.getGrupoMuscular() != null ? exercicio.getGrupoMuscular() : "-"
                    });
                }
                
                // Atualizar contador da aba
                int index = 4;
                tabbedResultados.setTitleAt(index, String.format("💪 Exercícios (%d)", filtrados.size()));
            });
            
            return filtrados.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private int buscarPlanosTreino(String termo) {
        try {
            String response = apiClient.get("/api/planos-treino");
            List<PlanoTreinoResponseDTO> planos = apiClient.fromJsonArray(response, PlanoTreinoResponseDTO.class);
            
            List<PlanoTreinoResponseDTO> filtrados = planos.stream()
                .filter(p -> 
                    p.getNomeAluno().toLowerCase().contains(termo.toLowerCase()) ||
                    p.getNomeInstrutor().toLowerCase().contains(termo.toLowerCase()) ||
                    (p.getDescricao() != null && p.getDescricao().toLowerCase().contains(termo.toLowerCase()))
                )
                .toList();
            
            SwingUtilities.invokeLater(() -> {
                tablePlanosTreino.clearRows();
                for (PlanoTreinoResponseDTO plano : filtrados) {
                    tablePlanosTreino.addRow(new Object[]{
                        plano.getId(),
                        plano.getNomeAluno(),
                        plano.getNomeInstrutor(),
                        plano.getDataCriacao().toString(),
                        plano.getDuracaoSemanas() != null ? plano.getDuracaoSemanas() + " sem" : "-",
                        plano.getDescricao() != null && !plano.getDescricao().isEmpty()
                            ? (plano.getDescricao().length() > 30 
                                ? plano.getDescricao().substring(0, 30) + "..."
                                : plano.getDescricao())
                            : "-"
                    });
                }
                
                // Atualizar contador da aba
                int index = 5;
                tabbedResultados.setTitleAt(index, String.format("📋 Planos Treino (%d)", filtrados.size()));
            });
            
            return filtrados.size();
        } catch (Exception ex) {
            return 0;
        }
    }
    
    private void limparResultados() {
        txtBusca.setText("");
        lblTotalResultados.setText(" ");
        
        tableAlunos.clearRows();
        tablePlanos.clearRows();
        tableMatriculas.clearRows();
        tablePagamentos.clearRows();
        tableExercicios.clearRows();
        tablePlanosTreino.clearRows();
        
        // Resetar títulos das abas
        tabbedResultados.setTitleAt(0, "👤 Alunos");
        tabbedResultados.setTitleAt(1, "💳 Planos");
        tabbedResultados.setTitleAt(2, "📝 Matrículas");
        tabbedResultados.setTitleAt(3, "💰 Pagamentos");
        tabbedResultados.setTitleAt(4, "💪 Exercícios");
        tabbedResultados.setTitleAt(5, "📋 Planos Treino");
        
        tabbedResultados.setSelectedIndex(0);
    }
}
