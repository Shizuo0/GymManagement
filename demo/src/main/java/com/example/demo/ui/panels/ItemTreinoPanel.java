package com.example.demo.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.ExercicioResponseDTO;
import com.example.demo.dto.ItemTreinoRequestDTO;
import com.example.demo.dto.ItemTreinoResponseDTO;
import com.example.demo.dto.PlanoTreinoResponseDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomComboBox;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;
import static com.example.demo.ui.utils.UIConstants.BACKGROUND_COLOR;
import static com.example.demo.ui.utils.UIConstants.BORDER_COLOR;
import static com.example.demo.ui.utils.UIConstants.BUTTON_HEIGHT;
import static com.example.demo.ui.utils.UIConstants.CARD_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.FONT_REGULAR;
import static com.example.demo.ui.utils.UIConstants.FONT_SUBTITLE;
import static com.example.demo.ui.utils.UIConstants.FONT_TITLE;
import static com.example.demo.ui.utils.UIConstants.MSG_VALIDATION_ERROR;
import static com.example.demo.ui.utils.UIConstants.PADDING_LARGE;
import static com.example.demo.ui.utils.UIConstants.PADDING_MEDIUM;
import static com.example.demo.ui.utils.UIConstants.PADDING_SMALL;
import static com.example.demo.ui.utils.UIConstants.PANEL_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.PRIMARY_COLOR;
import static com.example.demo.ui.utils.UIConstants.TEXTFIELD_HEIGHT;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;

/**
 * Panel para gerenciamento de Itens de Treino
 * COMMIT 8: ItemTreinoPanel - Gerenciar exercícios dentro dos planos de treino
 */
public class ItemTreinoPanel extends JPanel {
    
    private final ApiClient apiClient;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomComboBox<PlanoItem> cmbFiltroPlano;
    
    // Painéis
    private JSplitPane splitPane;
    private JPanel formPanel;
    
    // Componentes do formulário
    private CustomComboBox<PlanoItem> cmbPlano;
    private CustomComboBox<ExercicioItem> cmbExercicio;
    private CustomTextField txtSeries;
    private CustomTextField txtRepeticoes;
    private CustomTextField txtCarga;
    private JTextArea txtObservacoes;
    
    // Botões
    private CustomButton btnNovo;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnLimparFiltro;
    private CustomButton btnSalvar;
    private CustomButton btnCancelar;
    
    // Estado
    private Long currentItemId;
    private boolean isEditMode;
    
    public ItemTreinoPanel() {
        this.apiClient = new ApiClient();
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        
        // Carregar dados após a UI estar visível
        SwingUtilities.invokeLater(this::loadItensTreino);
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
        
        // Cabeçalho com título e filtro
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel("Exercícios nos Treinos");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        // Painel de filtro
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        filterPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblFiltro = new JLabel("Filtrar por Plano:");
        lblFiltro.setFont(FONT_REGULAR);
        lblFiltro.setForeground(TEXT_PRIMARY);
        
        cmbFiltroPlano = new CustomComboBox<>();
        cmbFiltroPlano.setFont(FONT_REGULAR);
        cmbFiltroPlano.setPreferredSize(new Dimension(250, TEXTFIELD_HEIGHT));
        cmbFiltroPlano.addActionListener(e -> filtrarPorPlano());
        
        btnLimparFiltro = new CustomButton("Limpar", CustomButton.ButtonType.SECONDARY);
        btnLimparFiltro.addActionListener(e -> limparFiltro());
        
        filterPanel.add(lblFiltro);
        filterPanel.add(cmbFiltroPlano);
        filterPanel.add(btnLimparFiltro);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Plano (Aluno)", "Exercício", "Grupo", "Séries", "Repetições", "Carga (kg)", "Observações"};
        table = new CustomTable(colunas);
        table.setPreferredScrollableViewportSize(new Dimension(900, 400));
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
        
        btnNovo.addActionListener(e -> novoItem());
        btnEditar.addActionListener(e -> editarItem());
        btnExcluir.addActionListener(e -> excluirItem());
        
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Carregar planos para o filtro
        loadPlanosForFilter();
        
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
        JLabel lblTitle = new JLabel("Adicionar Exercício ao Plano");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Campo Plano de Treino
        JLabel lblPlano = new JLabel("Plano de Treino:*");
        lblPlano.setFont(FONT_REGULAR);
        lblPlano.setForeground(TEXT_PRIMARY);
        lblPlano.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblPlano);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        cmbPlano = new CustomComboBox<>();
        cmbPlano.setFont(FONT_REGULAR);
        cmbPlano.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbPlano.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbPlano);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Exercício
        JLabel lblExercicio = new JLabel("Exercício:*");
        lblExercicio.setFont(FONT_REGULAR);
        lblExercicio.setForeground(TEXT_PRIMARY);
        lblExercicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblExercicio);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        cmbExercicio = new CustomComboBox<>();
        cmbExercicio.setFont(FONT_REGULAR);
        cmbExercicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbExercicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbExercicio);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Séries
        JLabel lblSeries = new JLabel("Séries:*");
        lblSeries.setFont(FONT_REGULAR);
        lblSeries.setForeground(TEXT_PRIMARY);
        lblSeries.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblSeries);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtSeries = CustomTextField.createNumericField("Ex: 3");
        txtSeries.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        txtSeries.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtSeries);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Repetições
        JLabel lblRepeticoes = new JLabel("Repetições:*");
        lblRepeticoes.setFont(FONT_REGULAR);
        lblRepeticoes.setForeground(TEXT_PRIMARY);
        lblRepeticoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblRepeticoes);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtRepeticoes = CustomTextField.createNumericField("Ex: 12");
        txtRepeticoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        txtRepeticoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtRepeticoes);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Carga
        JLabel lblCarga = new JLabel("Carga (kg):");
        lblCarga.setFont(FONT_REGULAR);
        lblCarga.setForeground(TEXT_PRIMARY);
        lblCarga.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblCarga);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtCarga = CustomTextField.createNumericField("Ex: 50");
        txtCarga.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        txtCarga.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtCarga);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Observações
        JLabel lblObservacoes = new JLabel("Observações:");
        lblObservacoes.setFont(FONT_REGULAR);
        lblObservacoes.setForeground(TEXT_PRIMARY);
        lblObservacoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblObservacoes);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        txtObservacoes = new JTextArea(3, 30);
        txtObservacoes.setFont(FONT_REGULAR);
        txtObservacoes.setForeground(TEXT_PRIMARY);
        txtObservacoes.setBackground(CARD_BACKGROUND);
        txtObservacoes.setCaretColor(PRIMARY_COLOR);
        txtObservacoes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM)
        ));
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        
        JScrollPane scrollObservacoes = new JScrollPane(txtObservacoes);
        scrollObservacoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        scrollObservacoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(scrollObservacoes);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões do formulário
        JPanel btnFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        btnFormPanel.setBackground(CARD_BACKGROUND);
        btnFormPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT + 10));
        btnFormPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnSalvar = new CustomButton("Salvar", CustomButton.ButtonType.PRIMARY);
        btnCancelar = new CustomButton("Cancelar", CustomButton.ButtonType.SECONDARY);
        
        btnSalvar.addActionListener(e -> salvarItem());
        btnCancelar.addActionListener(e -> cancelarEdicao());
        
        btnFormPanel.add(btnSalvar);
        btnFormPanel.add(btnCancelar);
        panel.add(btnFormPanel);
        
        panel.add(Box.createVerticalGlue());
        
        // Carregar dados dos ComboBoxes
        loadPlanosForForm();
        loadExercicios();
        
        // Estado inicial: formulário desabilitado
        setFormEnabled(false);
        
        return panel;
    }
    
    // ========== CARREGAMENTO DE DADOS ==========
    
    private void loadItensTreino() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando itens de treino...",
            () -> {
                String response = apiClient.get("/itens-treino");
                List<ItemTreinoResponseDTO> itens = apiClient.fromJsonArray(response, ItemTreinoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(itens);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar itens: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<ItemTreinoResponseDTO> itens) {
        table.clearRows();
        for (ItemTreinoResponseDTO item : itens) {
            table.addRow(new Object[]{
                item.getId(),
                item.getPlanoTreinoNome() != null ? item.getPlanoTreinoNome() : "Plano #" + item.getPlanoTreinoId(),
                item.getExercicioNome(),
                item.getGrupoMuscular() != null ? item.getGrupoMuscular() : "-",
                item.getSeries(),
                item.getRepeticoes(),
                item.getCarga() != null ? item.getCarga() + " kg" : "-",
                item.getObservacoes() != null && !item.getObservacoes().isEmpty()
                    ? (item.getObservacoes().length() > 20 
                        ? item.getObservacoes().substring(0, 20) + "..." 
                        : item.getObservacoes())
                    : "-"
            });
        }
    }
    
    private void loadPlanosForFilter() {
        try {
            String response = apiClient.get("/planos-treino");
            List<PlanoTreinoResponseDTO> planos = apiClient.fromJsonArray(response, PlanoTreinoResponseDTO.class);
            
            cmbFiltroPlano.removeAllItems();
            cmbFiltroPlano.addItem(new PlanoItem(null, "Todos os planos"));
            for (PlanoTreinoResponseDTO plano : planos) {
                String descricao = plano.getNomeAluno() + " (" + plano.getNomeInstrutor() + ")";
                cmbFiltroPlano.addItem(new PlanoItem(plano.getId(), descricao));
            }
        } catch (Exception ex) {
            MessageDialog.showError(this, "Erro ao carregar planos: " + ex.getMessage());
        }
    }
    
    private void loadPlanosForForm() {
        try {
            String response = apiClient.get("/planos-treino");
            List<PlanoTreinoResponseDTO> planos = apiClient.fromJsonArray(response, PlanoTreinoResponseDTO.class);
            
            cmbPlano.removeAllItems();
            cmbPlano.addItem(new PlanoItem(null, "Selecione um plano..."));
            for (PlanoTreinoResponseDTO plano : planos) {
                String descricao = plano.getNomeAluno() + " (" + plano.getNomeInstrutor() + ")";
                cmbPlano.addItem(new PlanoItem(plano.getId(), descricao));
            }
        } catch (Exception ex) {
            MessageDialog.showError(this, "Erro ao carregar planos: " + ex.getMessage());
        }
    }
    
    private void loadExercicios() {
        try {
            String response = apiClient.get("/exercicios");
            List<ExercicioResponseDTO> exercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
            
            cmbExercicio.removeAllItems();
            cmbExercicio.addItem(new ExercicioItem(null, "Selecione um exercício..."));
            for (ExercicioResponseDTO ex : exercicios) {
                String descricao = ex.getNome();
                if (ex.getGrupoMuscular() != null) {
                    descricao += " [" + ex.getGrupoMuscular() + "]";
                }
                cmbExercicio.addItem(new ExercicioItem(ex.getId(), descricao));
            }
        } catch (Exception ex) {
            MessageDialog.showError(this, "Erro ao carregar exercícios: " + ex.getMessage());
        }
    }
    
    // ========== CRUD OPERATIONS ==========
    
    private void novoItem() {
        isEditMode = false;
        currentItemId = null;
        clearForm();
        setFormEnabled(true);
        showFormPanel();
        updateButtons();
        cmbPlano.requestFocus();
    }
    
    private void editarItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        currentItemId = (Long) table.getValueAt(selectedRow, 0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/itens-treino/" + currentItemId);
                ItemTreinoResponseDTO item = apiClient.fromJson(response, ItemTreinoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    isEditMode = true;
                    populateForm(item);
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
                    MessageDialog.showError(this, "Erro ao carregar item: " + error.getMessage());
                }
            }
        );
    }
    
    private void excluirItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long id = (Long) table.getValueAt(selectedRow, 0);
        String exercicio = (String) table.getValueAt(selectedRow, 2);
        
        boolean confirmed = MessageDialog.showConfirmation(
            this,
            "Deseja realmente excluir o exercício \"" + exercicio + "\" do plano?",
            "Confirmar Exclusão"
        );
        
        if (confirmed) {
            LoadingDialog.executeWithLoading(
                SwingUtilities.getWindowAncestor(this),
                "Excluindo item...",
                () -> {
                    apiClient.delete("/itens-treino/" + id);
                    SwingUtilities.invokeLater(() -> {
                        MessageDialog.showSuccess(this, "Item excluído com sucesso!");
                        loadItensTreino();
                    });
                },
                () -> {
                    // Sucesso
                },
                error -> {
                    if (error instanceof ApiException) {
                        MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                    } else {
                        MessageDialog.showError(this, "Erro ao excluir item: " + error.getMessage());
                    }
                }
            );
        }
    }
    
    private void salvarItem() {
        if (!validateForm()) {
            MessageDialog.showWarning(this, MSG_VALIDATION_ERROR);
            return;
        }
        
        PlanoItem selectedPlano = (PlanoItem) cmbPlano.getSelectedItem();
        ExercicioItem selectedExercicio = (ExercicioItem) cmbExercicio.getSelectedItem();
        int series = Integer.parseInt(txtSeries.getText().trim());
        int repeticoes = Integer.parseInt(txtRepeticoes.getText().trim());
        String cargaStr = txtCarga.getText().trim();
        String observacoes = txtObservacoes.getText().trim();
        
        // Criar DTO em vez de JSON manual
        ItemTreinoRequestDTO dto = new ItemTreinoRequestDTO();
        dto.setPlanoTreinoId(selectedPlano.getId());
        dto.setExercicioId(selectedExercicio.getId());
        dto.setSeries(series);
        dto.setRepeticoes(repeticoes);
        
        if (!cargaStr.isEmpty()) {
            try {
                BigDecimal carga = new BigDecimal(cargaStr.replace(",", "."));
                dto.setCarga(carga);
            } catch (NumberFormatException ex) {
                // Ignorar se não for número válido
            }
        }
        
        if (!observacoes.isEmpty()) {
            dto.setObservacoes(observacoes);
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando item..." : "Adicionando exercício...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/itens-treino/" + currentItemId, dto);
                } else {
                    apiClient.post("/itens-treino", dto);
                }
                
                SwingUtilities.invokeLater(() -> {
                    MessageDialog.showSuccess(
                        this,
                        isEditMode ? "Item atualizado com sucesso!" : "Exercício adicionado ao plano com sucesso!"
                    );
                    cancelarEdicao();
                    loadItensTreino();
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar item: " + error.getMessage());
                }
            }
        );
    }
    
    private void cancelarEdicao() {
        clearForm();
        setFormEnabled(false);
        isEditMode = false;
        currentItemId = null;
        table.clearSelection();
        hideFormPanel();
        updateButtons();
    }
    
    // ========== FILTROS ==========
    
    private void filtrarPorPlano() {
        PlanoItem selectedPlano = (PlanoItem) cmbFiltroPlano.getSelectedItem();
        
        if (selectedPlano == null || selectedPlano.getId() == null) {
            loadItensTreino();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Filtrando itens...",
            () -> {
                String response = apiClient.get("/itens-treino/plano/" + selectedPlano.getId());
                List<ItemTreinoResponseDTO> itens = apiClient.fromJsonArray(response, ItemTreinoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(itens);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                // Se o endpoint não existir, filtrar localmente
                try {
                    String response = apiClient.get("/itens-treino");
                    List<ItemTreinoResponseDTO> todosItens = apiClient.fromJsonArray(response, ItemTreinoResponseDTO.class);
                    
                    List<ItemTreinoResponseDTO> filtrados = todosItens.stream()
                        .filter(item -> item.getPlanoTreinoId().equals(selectedPlano.getId()))
                        .toList();
                    
                    SwingUtilities.invokeLater(() -> {
                        updateTable(filtrados);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        MessageDialog.showError(ItemTreinoPanel.this, "Erro ao filtrar itens: " + ex.getMessage());
                    });
                }
            }
        );
    }
    
    private void limparFiltro() {
        cmbFiltroPlano.setSelectedIndex(0);
        loadItensTreino();
    }
    
    // ========== VALIDAÇÃO E FORMULÁRIO ==========
    
    private boolean validateForm() {
        PlanoItem selectedPlano = (PlanoItem) cmbPlano.getSelectedItem();
        if (selectedPlano == null || selectedPlano.getId() == null) {
            MessageDialog.showWarning(this, "Selecione um plano de treino.");
            return false;
        }
        
        ExercicioItem selectedExercicio = (ExercicioItem) cmbExercicio.getSelectedItem();
        if (selectedExercicio == null || selectedExercicio.getId() == null) {
            MessageDialog.showWarning(this, "Selecione um exercício.");
            return false;
        }
        
        String seriesStr = txtSeries.getText().trim();
        if (seriesStr.isEmpty()) {
            txtSeries.markAsInvalid();
            MessageDialog.showWarning(this, "Informe o número de séries.");
            return false;
        }
        
        try {
            int series = Integer.parseInt(seriesStr);
            if (series < 1) {
                txtSeries.markAsInvalid();
                MessageDialog.showWarning(this, "O número de séries deve ser maior que zero.");
                return false;
            }
        } catch (NumberFormatException ex) {
            txtSeries.markAsInvalid();
            MessageDialog.showWarning(this, "Número de séries inválido.");
            return false;
        }
        txtSeries.markAsValid();
        
        String repeticoesStr = txtRepeticoes.getText().trim();
        if (repeticoesStr.isEmpty()) {
            txtRepeticoes.markAsInvalid();
            MessageDialog.showWarning(this, "Informe o número de repetições.");
            return false;
        }
        
        try {
            int repeticoes = Integer.parseInt(repeticoesStr);
            if (repeticoes < 1) {
                txtRepeticoes.markAsInvalid();
                MessageDialog.showWarning(this, "O número de repetições deve ser maior que zero.");
                return false;
            }
        } catch (NumberFormatException ex) {
            txtRepeticoes.markAsInvalid();
            MessageDialog.showWarning(this, "Número de repetições inválido.");
            return false;
        }
        txtRepeticoes.markAsValid();
        
        String cargaStr = txtCarga.getText().trim();
        if (!cargaStr.isEmpty()) {
            try {
                BigDecimal carga = new BigDecimal(cargaStr.replace(",", "."));
                if (carga.compareTo(BigDecimal.ZERO) < 0) {
                    txtCarga.markAsInvalid();
                    MessageDialog.showWarning(this, "A carga não pode ser negativa.");
                    return false;
                }
            } catch (NumberFormatException ex) {
                txtCarga.markAsInvalid();
                MessageDialog.showWarning(this, "Carga inválida.");
                return false;
            }
        }
        txtCarga.markAsValid();
        
        return true;
    }
    
    private void populateForm(ItemTreinoResponseDTO item) {
        // Selecionar plano
        for (int i = 0; i < cmbPlano.getItemCount(); i++) {
            PlanoItem planoItem = cmbPlano.getItemAt(i);
            if (planoItem.getId() != null && planoItem.getId().equals(item.getPlanoTreinoId())) {
                cmbPlano.setSelectedIndex(i);
                break;
            }
        }
        
        // Selecionar exercício
        for (int i = 0; i < cmbExercicio.getItemCount(); i++) {
            ExercicioItem exItem = cmbExercicio.getItemAt(i);
            if (exItem.getId() != null && exItem.getId().equals(item.getExercicioId())) {
                cmbExercicio.setSelectedIndex(i);
                break;
            }
        }
        
        txtSeries.setText(item.getSeries().toString());
        txtRepeticoes.setText(item.getRepeticoes().toString());
        txtCarga.setText(item.getCarga() != null ? item.getCarga().toString() : "");
        txtObservacoes.setText(item.getObservacoes() != null ? item.getObservacoes() : "");
    }
    
    private void clearForm() {
        cmbPlano.setSelectedIndex(0);
        cmbExercicio.setSelectedIndex(0);
        txtSeries.setText("");
        txtRepeticoes.setText("");
        txtCarga.setText("");
        txtObservacoes.setText("");
        txtSeries.markAsValid();
        txtRepeticoes.markAsValid();
        txtCarga.markAsValid();
    }
    
    private void setFormEnabled(boolean enabled) {
        cmbPlano.setEnabled(enabled);
        cmbExercicio.setEnabled(enabled);
        txtSeries.setEnabled(enabled);
        txtRepeticoes.setEnabled(enabled);
        txtCarga.setEnabled(enabled);
        txtObservacoes.setEnabled(enabled);
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
    
    // ========== CLASSES AUXILIARES ==========
    
    /**
     * Item do ComboBox de Plano
     */
    private static class PlanoItem {
        private final Long id;
        private final String descricao;
        
        public PlanoItem(Long id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return descricao;
        }
    }
    
    /**
     * Item do ComboBox de Exercício
     */
    private static class ExercicioItem {
        private final Long id;
        private final String descricao;
        
        public ExercicioItem(Long id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return descricao;
        }
    }
}
