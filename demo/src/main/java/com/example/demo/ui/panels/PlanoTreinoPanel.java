package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.AlunoDTO;
import com.example.demo.dto.InstrutorDTO;
import com.example.demo.dto.PlanoTreinoResponseDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomDatePicker;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;

/**
 * Panel para gerenciamento de Planos de Treino
 * COMMIT 8: PlanoTreinoPanel - Gerenciar planos de treino vinculados a alunos e instrutores
 */
public class PlanoTreinoPanel extends JPanel {
    
    private final ApiClient apiClient;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomTextField txtBusca;
    
    // Componentes do formulário
    private JComboBox<AlunoItem> cmbAluno;
    private JComboBox<InstrutorItem> cmbInstrutor;
    private CustomDatePicker dataCriacao;
    private JTextArea txtDescricao;
    private CustomTextField txtDuracaoSemanas;
    
    // Botões
    private CustomButton btnNovo;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnBuscar;
    private CustomButton btnLimparFiltro;
    private CustomButton btnSalvar;
    private CustomButton btnCancelar;
    private CustomButton btnGerenciarItens;
    
    // Estado
    private Long currentPlanoId;
    private boolean isEditMode;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public PlanoTreinoPanel() {
        this.apiClient = new ApiClient();
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        loadPlanosTreino();
    }
    
    private void initializeUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(PADDING_MEDIUM);
        splitPane.setBorder(null);
        splitPane.setBackground(BACKGROUND_COLOR);
        
        splitPane.setLeftComponent(createListPanel());
        splitPane.setRightComponent(createFormPanel());
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, PADDING_MEDIUM));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Cabeçalho com título e busca
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel("📋 Planos de Treino");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        txtBusca = new CustomTextField("Buscar por aluno ou instrutor...", 20);
        btnBuscar = new CustomButton("🔍", CustomButton.ButtonType.PRIMARY);
        btnBuscar.addActionListener(e -> buscarPlanos());
        
        btnLimparFiltro = new CustomButton("🔄", CustomButton.ButtonType.SECONDARY);
        btnLimparFiltro.addActionListener(e -> limparFiltros());
        
        searchPanel.add(txtBusca);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnLimparFiltro);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Aluno", "Instrutor", "Data Criação", "Duração (sem)", "Descrição"};
        table = new CustomTable(colunas);
        table.setPreferredScrollableViewportSize(new Dimension(800, 400));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtons();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões de ação
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(PADDING_MEDIUM, 0, 0, 0));
        
        btnNovo = new CustomButton("➕ Novo", CustomButton.ButtonType.PRIMARY);
        btnEditar = new CustomButton("✏️ Editar", CustomButton.ButtonType.SECONDARY);
        btnExcluir = new CustomButton("🗑️ Excluir", CustomButton.ButtonType.DANGER);
        btnGerenciarItens = new CustomButton("📝 Gerenciar Exercícios", CustomButton.ButtonType.SECONDARY);
        
        btnNovo.addActionListener(e -> novoPlano());
        btnEditar.addActionListener(e -> editarPlano());
        btnExcluir.addActionListener(e -> excluirPlano());
        btnGerenciarItens.addActionListener(e -> gerenciarItens());
        
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(Box.createHorizontalStrut(PADDING_LARGE));
        buttonPanel.add(btnGerenciarItens);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        updateButtons();
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        
        // Título
        JLabel lblTitle = new JLabel("Dados do Plano de Treino");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Campo Aluno
        JLabel lblAluno = new JLabel("Aluno:*");
        lblAluno.setFont(FONT_REGULAR);
        lblAluno.setForeground(TEXT_PRIMARY);
        lblAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblAluno);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        cmbAluno = new JComboBox<>();
        cmbAluno.setFont(FONT_REGULAR);
        cmbAluno.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbAluno);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Instrutor
        JLabel lblInstrutor = new JLabel("Instrutor:*");
        lblInstrutor.setFont(FONT_REGULAR);
        lblInstrutor.setForeground(TEXT_PRIMARY);
        lblInstrutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblInstrutor);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        cmbInstrutor = new JComboBox<>();
        cmbInstrutor.setFont(FONT_REGULAR);
        cmbInstrutor.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbInstrutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbInstrutor);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Data Criação
        JLabel lblData = new JLabel("Data de Criação:*");
        lblData.setFont(FONT_REGULAR);
        lblData.setForeground(TEXT_PRIMARY);
        lblData.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblData);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        dataCriacao = new CustomDatePicker();
        dataCriacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dataCriacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dataCriacao);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Duração em Semanas
        JLabel lblDuracao = new JLabel("Duração (semanas):");
        lblDuracao.setFont(FONT_REGULAR);
        lblDuracao.setForeground(TEXT_PRIMARY);
        lblDuracao.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDuracao);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtDuracaoSemanas = CustomTextField.createNumericField("Ex: 12");
        txtDuracaoSemanas.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        txtDuracaoSemanas.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtDuracaoSemanas);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Descrição
        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setFont(FONT_REGULAR);
        lblDescricao.setForeground(TEXT_PRIMARY);
        lblDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDescricao);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtDescricao = new JTextArea(4, 30);
        txtDescricao.setFont(FONT_REGULAR);
        txtDescricao.setForeground(TEXT_PRIMARY);
        txtDescricao.setBackground(CARD_BACKGROUND);
        txtDescricao.setCaretColor(PRIMARY_COLOR);
        txtDescricao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM)
        ));
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollDescricao);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões do formulário
        JPanel btnFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        btnFormPanel.setBackground(CARD_BACKGROUND);
        btnFormPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT + 10));
        btnFormPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnSalvar = new CustomButton("💾 Salvar", CustomButton.ButtonType.PRIMARY);
        btnCancelar = new CustomButton("❌ Cancelar", CustomButton.ButtonType.SECONDARY);
        
        btnSalvar.addActionListener(e -> salvarPlano());
        btnCancelar.addActionListener(e -> cancelarEdicao());
        
        btnFormPanel.add(btnSalvar);
        btnFormPanel.add(btnCancelar);
        panel.add(btnFormPanel);
        
        panel.add(Box.createVerticalGlue());
        
        // Carregar dados dos ComboBoxes
        loadAlunos();
        loadInstrutores();
        
        // Estado inicial: formulário desabilitado
        setFormEnabled(false);
        
        return panel;
    }
    
    // ========== CARREGAMENTO DE DADOS ==========
    
    private void loadPlanosTreino() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando planos de treino...",
            () -> {
                String response = apiClient.get("/api/planos-treino");
                List<PlanoTreinoResponseDTO> planos = apiClient.fromJsonArray(response, PlanoTreinoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(planos);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar planos de treino: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<PlanoTreinoResponseDTO> planos) {
        table.clearRows();
        for (PlanoTreinoResponseDTO plano : planos) {
            table.addRow(new Object[]{
                plano.getId(),
                plano.getNomeAluno(),
                plano.getNomeInstrutor(),
                plano.getDataCriacao() != null ? plano.getDataCriacao().format(FORMATTER) : "-",
                plano.getDuracaoSemanas() != null ? plano.getDuracaoSemanas() + " sem" : "-",
                plano.getDescricao() != null && !plano.getDescricao().isEmpty() 
                    ? (plano.getDescricao().length() > 30 
                        ? plano.getDescricao().substring(0, 30) + "..." 
                        : plano.getDescricao())
                    : "-"
            });
        }
    }
    
    private void loadAlunos() {
        try {
            String response = apiClient.get("/api/alunos");
            List<AlunoDTO> alunos = apiClient.fromJsonArray(response, AlunoDTO.class);
            
            cmbAluno.removeAllItems();
            cmbAluno.addItem(new AlunoItem(null, "Selecione um aluno..."));
            for (AlunoDTO aluno : alunos) {
                cmbAluno.addItem(new AlunoItem(aluno.getIdAluno(), aluno.getNome()));
            }
        } catch (Exception ex) {
            MessageDialog.showError(this, "Erro ao carregar alunos: " + ex.getMessage());
        }
    }
    
    private void loadInstrutores() {
        try {
            String response = apiClient.get("/api/instrutores");
            List<InstrutorDTO> instrutores = apiClient.fromJsonArray(response, InstrutorDTO.class);
            
            cmbInstrutor.removeAllItems();
            cmbInstrutor.addItem(new InstrutorItem(null, "Selecione um instrutor..."));
            for (InstrutorDTO instrutor : instrutores) {
                cmbInstrutor.addItem(new InstrutorItem(
                    instrutor.getIdInstrutor(), 
                    instrutor.getNome() + " - " + (instrutor.getEspecialidade() != null ? instrutor.getEspecialidade() : "")
                ));
            }
        } catch (Exception ex) {
            MessageDialog.showError(this, "Erro ao carregar instrutores: " + ex.getMessage());
        }
    }
    
    // ========== CRUD OPERATIONS ==========
    
    private void novoPlano() {
        isEditMode = false;
        currentPlanoId = null;
        clearForm();
        setFormEnabled(true);
        updateButtons();
        cmbAluno.requestFocus();
    }
    
    private void editarPlano() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        currentPlanoId = (Long) table.getValueAt(selectedRow, 0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/api/planos-treino/" + currentPlanoId);
                PlanoTreinoResponseDTO plano = apiClient.fromJson(response, PlanoTreinoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    isEditMode = true;
                    populateForm(plano);
                    setFormEnabled(true);
                    updateButtons();
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar plano: " + error.getMessage());
                }
            }
        );
    }
    
    private void excluirPlano() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long id = (Long) table.getValueAt(selectedRow, 0);
        String nomeAluno = (String) table.getValueAt(selectedRow, 1);
        
        boolean confirmed = MessageDialog.showConfirmation(
            this,
            "Deseja realmente excluir o plano de treino do aluno \"" + nomeAluno + "\"?\n" +
            "ATENÇÃO: Todos os itens de treino (exercícios) serão removidos!",
            "Confirmar Exclusão"
        );
        
        if (confirmed) {
            LoadingDialog.executeWithLoading(
                SwingUtilities.getWindowAncestor(this),
                "Excluindo plano...",
                () -> {
                    apiClient.delete("/api/planos-treino/" + id);
                    SwingUtilities.invokeLater(() -> {
                        MessageDialog.showSuccess(this, "Plano de treino excluído com sucesso!");
                        loadPlanosTreino();
                    });
                },
                () -> {
                    // Sucesso
                },
                error -> {
                    if (error instanceof ApiException) {
                        MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                    } else {
                        MessageDialog.showError(this, "Erro ao excluir plano: " + error.getMessage());
                    }
                }
            );
        }
    }
    
    private void salvarPlano() {
        if (!validateForm()) {
            MessageDialog.showWarning(this, MSG_VALIDATION_ERROR);
            return;
        }
        
        AlunoItem selectedAluno = (AlunoItem) cmbAluno.getSelectedItem();
        InstrutorItem selectedInstrutor = (InstrutorItem) cmbInstrutor.getSelectedItem();
        LocalDate data = dataCriacao.getLocalDate();
        String descricao = txtDescricao.getText().trim();
        String duracaoStr = txtDuracaoSemanas.getText().trim();
        
        // Criar JSON manualmente
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"idAluno\":").append(selectedAluno.getId()).append(",");
        json.append("\"idInstrutor\":").append(selectedInstrutor.getId()).append(",");
        json.append("\"dataCriacao\":\"").append(data.toString()).append("\"");
        
        if (!descricao.isEmpty()) {
            json.append(",\"descricao\":\"").append(descricao.replace("\"", "\\\"")).append("\"");
        }
        
        if (!duracaoStr.isEmpty()) {
            try {
                int duracao = Integer.parseInt(duracaoStr);
                json.append(",\"duracaoSemanas\":").append(duracao);
            } catch (NumberFormatException ex) {
                // Ignorar se não for número válido
            }
        }
        
        json.append("}");
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando plano..." : "Criando plano...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/api/planos-treino/" + currentPlanoId, json.toString());
                } else {
                    apiClient.post("/api/planos-treino", json.toString());
                }
                
                SwingUtilities.invokeLater(() -> {
                    MessageDialog.showSuccess(
                        this,
                        isEditMode ? "Plano atualizado com sucesso!" : "Plano criado com sucesso!"
                    );
                    cancelarEdicao();
                    loadPlanosTreino();
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar plano: " + error.getMessage());
                }
            }
        );
    }
    
    private void cancelarEdicao() {
        clearForm();
        setFormEnabled(false);
        isEditMode = false;
        currentPlanoId = null;
        table.clearSelection();
        updateButtons();
    }
    
    private void gerenciarItens() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showInfo(this, "Selecione um plano de treino para gerenciar os exercícios.");
            return;
        }
        
        // Abrir dialog para gerenciar itens
        MessageDialog.showInfo(this, "Funcionalidade de gerenciamento de exercícios será implementada no ItemTreinoPanel.");
        // TODO: Implementar ItemTreinoDialog ou abrir ItemTreinoPanel com filtro
    }
    
    // ========== FILTROS E BUSCA ==========
    
    private void buscarPlanos() {
        String busca = txtBusca.getText().trim();
        
        if (busca.isEmpty()) {
            loadPlanosTreino();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando planos...",
            () -> {
                String response = apiClient.get("/api/planos-treino");
                List<PlanoTreinoResponseDTO> todosPlanos = apiClient.fromJsonArray(response, PlanoTreinoResponseDTO.class);
                
                // Filtrar localmente
                List<PlanoTreinoResponseDTO> filtrados = todosPlanos.stream()
                    .filter(p -> 
                        p.getNomeAluno().toLowerCase().contains(busca.toLowerCase()) ||
                        p.getNomeInstrutor().toLowerCase().contains(busca.toLowerCase())
                    )
                    .toList();
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtrados);
                    
                    if (filtrados.isEmpty()) {
                        MessageDialog.showInfo(this, "Nenhum plano encontrado.");
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
                    MessageDialog.showError(this, "Erro ao buscar planos: " + error.getMessage());
                }
            }
        );
    }
    
    private void limparFiltros() {
        txtBusca.setText("");
        loadPlanosTreino();
    }
    
    // ========== VALIDAÇÃO E FORMULÁRIO ==========
    
    private boolean validateForm() {
        AlunoItem selectedAluno = (AlunoItem) cmbAluno.getSelectedItem();
        if (selectedAluno == null || selectedAluno.getId() == null) {
            MessageDialog.showWarning(this, "Selecione um aluno.");
            return false;
        }
        
        InstrutorItem selectedInstrutor = (InstrutorItem) cmbInstrutor.getSelectedItem();
        if (selectedInstrutor == null || selectedInstrutor.getId() == null) {
            MessageDialog.showWarning(this, "Selecione um instrutor.");
            return false;
        }
        
        LocalDate data = dataCriacao.getLocalDate();
        if (data == null) {
            MessageDialog.showWarning(this, "Informe a data de criação.");
            return false;
        }
        
        String duracaoStr = txtDuracaoSemanas.getText().trim();
        if (!duracaoStr.isEmpty()) {
            try {
                int duracao = Integer.parseInt(duracaoStr);
                if (duracao < 1) {
                    MessageDialog.showWarning(this, "A duração deve ser maior que zero.");
                    return false;
                }
            } catch (NumberFormatException ex) {
                MessageDialog.showWarning(this, "Duração inválida. Informe um número inteiro.");
                return false;
            }
        }
        
        return true;
    }
    
    private void populateForm(PlanoTreinoResponseDTO plano) {
        // Selecionar aluno
        for (int i = 0; i < cmbAluno.getItemCount(); i++) {
            AlunoItem item = cmbAluno.getItemAt(i);
            if (item.getId() != null && item.getId().equals(plano.getIdAluno())) {
                cmbAluno.setSelectedIndex(i);
                break;
            }
        }
        
        // Selecionar instrutor
        for (int i = 0; i < cmbInstrutor.getItemCount(); i++) {
            InstrutorItem item = cmbInstrutor.getItemAt(i);
            if (item.getId() != null && item.getId().equals(plano.getIdInstrutor())) {
                cmbInstrutor.setSelectedIndex(i);
                break;
            }
        }
        
        dataCriacao.setLocalDate(plano.getDataCriacao());
        txtDescricao.setText(plano.getDescricao() != null ? plano.getDescricao() : "");
        txtDuracaoSemanas.setText(plano.getDuracaoSemanas() != null ? plano.getDuracaoSemanas().toString() : "");
    }
    
    private void clearForm() {
        cmbAluno.setSelectedIndex(0);
        cmbInstrutor.setSelectedIndex(0);
        dataCriacao.setLocalDate(LocalDate.now());
        txtDescricao.setText("");
        txtDuracaoSemanas.setText("");
    }
    
    private void setFormEnabled(boolean enabled) {
        cmbAluno.setEnabled(enabled);
        cmbInstrutor.setEnabled(enabled);
        dataCriacao.setEnabled(enabled);
        txtDescricao.setEnabled(enabled);
        txtDuracaoSemanas.setEnabled(enabled);
        btnSalvar.setEnabled(enabled);
        btnCancelar.setEnabled(enabled);
    }
    
    private void updateButtons() {
        boolean hasSelection = table.getSelectedRow() != -1;
        boolean formEnabled = btnSalvar.isEnabled();
        
        btnNovo.setEnabled(!formEnabled);
        btnEditar.setEnabled(hasSelection && !formEnabled);
        btnExcluir.setEnabled(hasSelection && !formEnabled);
        btnGerenciarItens.setEnabled(hasSelection && !formEnabled);
    }
    
    // ========== CLASSES AUXILIARES ==========
    
    /**
     * Item do ComboBox de Aluno
     */
    private static class AlunoItem {
        private final Long id;
        private final String nome;
        
        public AlunoItem(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return nome;
        }
    }
    
    /**
     * Item do ComboBox de Instrutor
     */
    private static class InstrutorItem {
        private final Long id;
        private final String nome;
        
        public InstrutorItem(Long id, String nome) {
            this.id = id;
            this.nome = nome;
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return nome;
        }
    }
}
