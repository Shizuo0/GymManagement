package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.ExercicioResponseDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomComboBox;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;

/**
 * Panel para gerenciamento de Exercícios
 * COMMIT 8: ExercicioPanel - Catálogo de exercícios com busca e filtro por grupo muscular
 */
public class ExercicioPanel extends JPanel {
    
    private final ApiClient apiClient;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomTextField txtBusca;
    private CustomComboBox<String> cmbFiltroGrupo;
    
    // Painéis
    private JSplitPane splitPane;
    private JPanel formPanel;
    
    // Componentes do formulário
    private CustomTextField txtNome;
    private CustomTextField txtGrupoMuscular;
    
    // Botões
    private CustomButton btnNovo;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnBuscar;
    private CustomButton btnLimparFiltro;
    private CustomButton btnSalvar;
    private CustomButton btnCancelar;
    
    // Estado
    private Long currentExercicioId;
    private boolean isEditMode;
    
    // Grupos musculares comuns
    private static final String[] GRUPOS_MUSCULARES = {
        "Todos",
        "Peito",
        "Costas",
        "Ombros",
        "Bíceps",
        "Tríceps",
        "Pernas",
        "Abdômen",
        "Cardio",
        "Outro"
    };
    
    public ExercicioPanel() {
        this.apiClient = new ApiClient();
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        loadExercicios();
    }
    
    private void initializeUI() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(PADDING_MEDIUM);
        splitPane.setBorder(null);
        splitPane.setBackground(BACKGROUND_COLOR);
        
        splitPane.setLeftComponent(createListPanel());
        formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Oculta o formulário e atualiza os botões
        hideFormPanel();
        updateButtons();
    }
    
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, PADDING_MEDIUM));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Cabeçalho com título e filtros
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel("Catálogo de Exercícios");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        // Painel de busca e filtros
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblFiltro = new JLabel("Grupo:");
        lblFiltro.setFont(FONT_REGULAR);
        lblFiltro.setForeground(TEXT_PRIMARY);
        
        cmbFiltroGrupo = new CustomComboBox<>(GRUPOS_MUSCULARES);
        cmbFiltroGrupo.setFont(FONT_REGULAR);
        cmbFiltroGrupo.addActionListener(e -> filtrarPorGrupo());
        
        txtBusca = new CustomTextField("Buscar exercício...", 15);
        btnBuscar = new CustomButton("[ ? ]", CustomButton.ButtonType.PRIMARY);
        btnBuscar.addActionListener(e -> buscarExercicios());
        
        btnLimparFiltro = new CustomButton("Limpar", CustomButton.ButtonType.SECONDARY);
        btnLimparFiltro.addActionListener(e -> limparFiltros());
        
        searchPanel.add(lblFiltro);
        searchPanel.add(cmbFiltroGrupo);
        searchPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        searchPanel.add(txtBusca);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnLimparFiltro);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Nome", "Grupo Muscular"};
        table = new CustomTable(colunas);
        table.setPreferredScrollableViewportSize(new Dimension(700, 400));
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
        
        btnNovo = new CustomButton("+ Novo", CustomButton.ButtonType.PRIMARY);
        btnEditar = new CustomButton("Editar", CustomButton.ButtonType.SECONDARY);
        btnExcluir = new CustomButton("X Excluir", CustomButton.ButtonType.DANGER);
        
        btnNovo.addActionListener(e -> novoExercicio());
        btnEditar.addActionListener(e -> editarExercicio());
        btnExcluir.addActionListener(e -> excluirExercicio());
        
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
        JLabel lblTitle = new JLabel("Dados do Exercício");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Campo Nome
        JLabel lblNome = new JLabel("Nome do Exercício:*");
        lblNome.setFont(FONT_REGULAR);
        lblNome.setForeground(TEXT_PRIMARY);
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblNome);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtNome = new CustomTextField("Ex: Supino Reto", 30);
        txtNome.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        txtNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtNome);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Grupo Muscular
        JLabel lblGrupo = new JLabel("Grupo Muscular:");
        lblGrupo.setFont(FONT_REGULAR);
        lblGrupo.setForeground(TEXT_PRIMARY);
        lblGrupo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblGrupo);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtGrupoMuscular = new CustomTextField("Ex: Peito", 30);
        txtGrupoMuscular.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        txtGrupoMuscular.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtGrupoMuscular);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões do formulário
        JPanel btnFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        btnFormPanel.setBackground(CARD_BACKGROUND);
        btnFormPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT + 10));
        btnFormPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnSalvar = new CustomButton("Salvar", CustomButton.ButtonType.PRIMARY);
        btnCancelar = new CustomButton("Cancelar", CustomButton.ButtonType.SECONDARY);
        
        btnSalvar.addActionListener(e -> salvarExercicio());
        btnCancelar.addActionListener(e -> cancelarEdicao());
        
        btnFormPanel.add(btnSalvar);
        btnFormPanel.add(btnCancelar);
        panel.add(btnFormPanel);
        
        panel.add(Box.createVerticalGlue());
        
        // Estado inicial: formulário desabilitado
        setFormEnabled(false);
        
        return panel;
    }
    
    // ========== CARREGAMENTO DE DADOS ==========
    
    private void loadExercicios() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando exercícios...",
            () -> {
                String response = apiClient.get("/exercicios");
                List<ExercicioResponseDTO> exercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(exercicios);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar exercícios: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<ExercicioResponseDTO> exercicios) {
        table.clearRows();
        for (ExercicioResponseDTO ex : exercicios) {
            table.addRow(new Object[]{
                ex.getId(),
                ex.getNome(),
                ex.getGrupoMuscular() != null ? ex.getGrupoMuscular() : "-"
            });
        }
    }
    
    // ========== CRUD OPERATIONS ==========
    
    private void novoExercicio() {
        isEditMode = false;
        currentExercicioId = null;
        clearForm();
        setFormEnabled(true);
        showFormPanel();
        updateButtons();
        txtNome.requestFocus();
    }
    
    private void editarExercicio() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        currentExercicioId = (Long) table.getValueAt(selectedRow, 0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/exercicios/" + currentExercicioId);
                ExercicioResponseDTO exercicio = apiClient.fromJson(response, ExercicioResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    isEditMode = true;
                    populateForm(exercicio);
                    setFormEnabled(true);
                    showFormPanel();
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
                    MessageDialog.showError(this, "Erro ao carregar exercício: " + error.getMessage());
                }
            }
        );
    }
    
    private void excluirExercicio() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long id = (Long) table.getValueAt(selectedRow, 0);
        String nomeExercicio = (String) table.getValueAt(selectedRow, 1);
        
        boolean confirmed = MessageDialog.showConfirmation(
            this,
            "Deseja realmente excluir o exercício \"" + nomeExercicio + "\"?",
            "Confirmar Exclusão"
        );
        
        if (confirmed) {
            LoadingDialog.executeWithLoading(
                SwingUtilities.getWindowAncestor(this),
                "Excluindo exercício...",
                () -> {
                    apiClient.delete("/exercicios/" + id);
                    SwingUtilities.invokeLater(() -> {
                        MessageDialog.showSuccess(this, "Exercício excluído com sucesso!");
                        loadExercicios();
                    });
                },
                () -> {
                    // Sucesso
                },
                error -> {
                    if (error instanceof ApiException) {
                        MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                    } else {
                        MessageDialog.showError(this, "Erro ao excluir exercício: " + error.getMessage());
                    }
                }
            );
        }
    }
    
    private void salvarExercicio() {
        if (!validateForm()) {
            MessageDialog.showWarning(this, MSG_VALIDATION_ERROR);
            return;
        }
        
        String nome = txtNome.getText().trim();
        String grupoMuscular = txtGrupoMuscular.getText().trim();
        
        // Criar JSON manualmente
        String jsonData = String.format(
            "{\"nome\":\"%s\",\"grupoMuscular\":\"%s\"}",
            nome.replace("\"", "\\\""),
            grupoMuscular.isEmpty() ? "" : grupoMuscular.replace("\"", "\\\"")
        );
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando exercício..." : "Cadastrando exercício...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/exercicios/" + currentExercicioId, jsonData);
                } else {
                    apiClient.post("/exercicios", jsonData);
                }
                
                SwingUtilities.invokeLater(() -> {
                    MessageDialog.showSuccess(
                        this,
                        isEditMode ? "Exercício atualizado com sucesso!" : "Exercício cadastrado com sucesso!"
                    );
                    cancelarEdicao();
                    loadExercicios();
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar exercício: " + error.getMessage());
                }
            }
        );
    }
    
    private void cancelarEdicao() {
        clearForm();
        setFormEnabled(false);
        isEditMode = false;
        currentExercicioId = null;
        table.clearSelection();
        hideFormPanel();
        updateButtons();
    }
    
    // ========== FILTROS E BUSCA ==========
    
    private void buscarExercicios() {
        String busca = txtBusca.getText().trim();
        
        if (busca.isEmpty()) {
            loadExercicios();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando exercícios...",
            () -> {
                String response = apiClient.get("/exercicios");
                List<ExercicioResponseDTO> todosExercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
                
                // Filtrar localmente pelo nome
                List<ExercicioResponseDTO> filtrados = todosExercicios.stream()
                    .filter(ex -> ex.getNome().toLowerCase().contains(busca.toLowerCase()))
                    .toList();
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtrados);
                    
                    if (filtrados.isEmpty()) {
                        MessageDialog.showInfo(this, 
                            "Nenhum exercício encontrado com o nome: " + busca);
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
                    MessageDialog.showError(this, "Erro ao buscar exercícios: " + error.getMessage());
                }
            }
        );
    }
    
    private void filtrarPorGrupo() {
        String grupoSelecionado = (String) cmbFiltroGrupo.getSelectedItem();
        
        if ("Todos".equals(grupoSelecionado)) {
            loadExercicios();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Filtrando exercícios...",
            () -> {
                String response = apiClient.get("/exercicios/grupo/" + grupoSelecionado);
                List<ExercicioResponseDTO> exercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(exercicios);
                    
                    if (exercicios.isEmpty()) {
                        MessageDialog.showInfo(this, 
                            "Nenhum exercício encontrado para o grupo: " + grupoSelecionado);
                    }
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                // Se o endpoint não existir, filtrar localmente
                try {
                    String response = apiClient.get("/exercicios");
                    List<ExercicioResponseDTO> todosExercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
                    
                    List<ExercicioResponseDTO> filtrados = todosExercicios.stream()
                        .filter(ex -> grupoSelecionado.equalsIgnoreCase(ex.getGrupoMuscular()))
                        .toList();
                    
                    SwingUtilities.invokeLater(() -> {
                        updateTable(filtrados);
                        
                        if (filtrados.isEmpty()) {
                            MessageDialog.showInfo(ExercicioPanel.this, 
                                "Nenhum exercício encontrado para o grupo: " + grupoSelecionado);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        MessageDialog.showError(ExercicioPanel.this, "Erro ao filtrar exercícios: " + ex.getMessage());
                    });
                }
            }
        );
    }
    
    private void limparFiltros() {
        txtBusca.setText("");
        cmbFiltroGrupo.setSelectedIndex(0);
        loadExercicios();
    }
    
    // ========== VALIDAÇÃO E FORMULÁRIO ==========
    
    private boolean validateForm() {
        String nome = txtNome.getText().trim();
        
        if (nome.isEmpty()) {
            txtNome.markAsInvalid();
            return false;
        }
        
        if (nome.length() < 3) {
            txtNome.markAsInvalid();
            MessageDialog.showWarning(this, "O nome do exercício deve ter pelo menos 3 caracteres.");
            return false;
        }
        
        txtNome.markAsValid();
        return true;
    }
    
    private void populateForm(ExercicioResponseDTO exercicio) {
        txtNome.setText(exercicio.getNome());
        txtGrupoMuscular.setText(exercicio.getGrupoMuscular() != null ? exercicio.getGrupoMuscular() : "");
    }
    
    private void clearForm() {
        txtNome.setText("");
        txtGrupoMuscular.setText("");
        txtNome.markAsValid();
    }
    
    private void setFormEnabled(boolean enabled) {
        txtNome.setEnabled(enabled);
        txtGrupoMuscular.setEnabled(enabled);
        btnSalvar.setEnabled(enabled);
        btnCancelar.setEnabled(enabled);
    }
    
    private void updateButtons() {
        boolean hasSelection = table.getSelectedRow() != -1;
        boolean formEnabled = btnSalvar.isEnabled();
        
        btnNovo.setEnabled(!formEnabled);
        btnEditar.setEnabled(hasSelection && !formEnabled);
        btnExcluir.setEnabled(hasSelection && !formEnabled);
    }
    
    private void showFormPanel() {
        splitPane.setRightComponent(formPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(0.6);
        formPanel.setVisible(true);
    }
    
    private void hideFormPanel() {
        splitPane.remove(formPanel);
        formPanel.setVisible(false);
    }
}
