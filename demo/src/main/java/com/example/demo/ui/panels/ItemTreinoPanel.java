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
import static com.example.demo.ui.utils.UIConstants.SURFACE_COLOR;
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
        add(createListPanel(), BorderLayout.CENTER);
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
        CustomButton btnAtualizar = new CustomButton("↻ Atualizar", CustomButton.ButtonType.SECONDARY);
        
        btnNovo.addActionListener(e -> novoItem());
        btnEditar.addActionListener(e -> editarItem());
        btnExcluir.addActionListener(e -> excluirItem());
        btnAtualizar.addActionListener(e -> atualizarPagina());
        
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnAtualizar);
        
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
        panel.add(createFormField("Plano de Treino:*", cmbPlano = new CustomComboBox<>()));
        
        // Campo Exercício
        panel.add(createFormField("Exercício:*", cmbExercicio = new CustomComboBox<>()));
        
        // Campo Séries
        panel.add(createFormField("Séries:*", txtSeries = CustomTextField.createNumericField("Ex: 3")));
        
        // Campo Repetições
        panel.add(createFormField("Repetições:*", txtRepeticoes = CustomTextField.createNumericField("Ex: 12")));
        
        // Campo Carga
        panel.add(createFormField("Carga (kg):", txtCarga = CustomTextField.createNumericField("Ex: 50")));
        
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
        scrollObservacoes.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
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
    
    /**
     * Método auxiliar para criar campos de formulário com label acima do input
     */
    private JPanel createFormField(String labelText, Component inputComponent) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(CARD_BACKGROUND);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT + 30));
        
        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        // Input
        if (inputComponent instanceof CustomComboBox || 
            inputComponent instanceof CustomTextField) {
            inputComponent.setFont(FONT_REGULAR);
            ((javax.swing.JComponent) inputComponent).setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
            ((javax.swing.JComponent) inputComponent).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        fieldPanel.add(inputComponent);
        fieldPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        return fieldPanel;
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
    
    private void atualizarPagina() {
        loadItensTreino();
        loadPlanosForFilter();
        loadPlanosForForm();
        loadExercicios();
        MessageDialog.showSuccess(this, "Página atualizada com sucesso!");
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
            // Adiciona item padrão em caso de erro
            cmbFiltroPlano.removeAllItems();
            cmbFiltroPlano.addItem(new PlanoItem(null, "Todos os planos"));
            System.err.println("[AVISO] Não foi possível carregar planos. Verifique se o backend está rodando.");
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
            // Adiciona item padrão em caso de erro
            cmbPlano.removeAllItems();
            cmbPlano.addItem(new PlanoItem(null, "Selecione um plano..."));
            System.err.println("[AVISO] Não foi possível carregar planos. Verifique se o backend está rodando.");
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
            // Adiciona item padrão em caso de erro
            cmbExercicio.removeAllItems();
            cmbExercicio.addItem(new ExercicioItem(null, "Selecione um exercício..."));
            System.err.println("[AVISO] Não foi possível carregar exercícios. Verifique se o backend está rodando.");
        }
    }
    
    // ========== CRUD OPERATIONS ==========
    
    private void novoItem() {
        showItemTreinoDialog(null);
    }
    
    private void editarItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long id = (Long) table.getValueAt(selectedRow, 0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/itens-treino/" + id);
                ItemTreinoResponseDTO item = apiClient.fromJson(response, ItemTreinoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    showItemTreinoDialog(item);
                });
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar item: " + error.getMessage());
                }
            }
        );
    }
    
    private void showItemTreinoDialog(ItemTreinoResponseDTO item) {
        boolean isNew = (item == null);
        
        javax.swing.JDialog dialog = new javax.swing.JDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            isNew ? "Novo Exercício no Plano" : "Editar Exercício no Plano",
            true
        );
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Carregar dados para os combos
        List<PlanoTreinoResponseDTO> planos = new java.util.ArrayList<>();
        List<ExercicioResponseDTO> exercicios = new java.util.ArrayList<>();
        
        try {
            String planosJson = apiClient.get("/planos-treino");
            planos = apiClient.fromJsonArray(planosJson, PlanoTreinoResponseDTO.class);
            
            String exerciciosJson = apiClient.get("/exercicios");
            exercicios = apiClient.fromJsonArray(exerciciosJson, ExercicioResponseDTO.class);
        } catch (Exception ex) {
            MessageDialog.showError(dialog, "Erro ao carregar dados: " + ex.getMessage());
            dialog.dispose();
            return;
        }
        
        // Campo Plano de Treino
        CustomComboBox<PlanoItem> dialogCmbPlano = new CustomComboBox<>();
        dialogCmbPlano.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogCmbPlano.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (PlanoTreinoResponseDTO plano : planos) {
            String display = "Plano #" + plano.getId() + " - " + plano.getNomeAluno();
            dialogCmbPlano.addItem(new PlanoItem(plano.getId(), display));
        }
        
        // Campo Exercício
        CustomComboBox<ExercicioItem> dialogCmbExercicio = new CustomComboBox<>();
        dialogCmbExercicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogCmbExercicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (ExercicioResponseDTO exercicio : exercicios) {
            String display = exercicio.getNome() + " (" + exercicio.getGrupoMuscular() + ")";
            dialogCmbExercicio.addItem(new ExercicioItem(exercicio.getId(), display));
        }
        
        // Campo Séries
        CustomTextField dialogTxtSeries = CustomTextField.createNumericField("Ex: 3");
        dialogTxtSeries.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogTxtSeries.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo Repetições
        CustomTextField dialogTxtRepeticoes = CustomTextField.createNumericField("Ex: 12");
        dialogTxtRepeticoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogTxtRepeticoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo Carga
        CustomTextField dialogTxtCarga = CustomTextField.createNumericField("Ex: 50");
        dialogTxtCarga.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogTxtCarga.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo Observações
        JTextArea dialogTxtObs = new JTextArea(3, 30);
        dialogTxtObs.setFont(FONT_REGULAR);
        dialogTxtObs.setLineWrap(true);
        dialogTxtObs.setWrapStyleWord(true);
        dialogTxtObs.setBackground(SURFACE_COLOR);
        dialogTxtObs.setForeground(TEXT_PRIMARY);
        dialogTxtObs.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM)
        ));
        JScrollPane scrollObs = new JScrollPane(dialogTxtObs);
        scrollObs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        scrollObs.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Preencher dados se for edição
        if (!isNew && item != null) {
            for (int i = 0; i < dialogCmbPlano.getItemCount(); i++) {
                if (dialogCmbPlano.getItemAt(i).getId().equals(item.getPlanoTreinoId())) {
                    dialogCmbPlano.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < dialogCmbExercicio.getItemCount(); i++) {
                if (dialogCmbExercicio.getItemAt(i).getId().equals(item.getExercicioId())) {
                    dialogCmbExercicio.setSelectedIndex(i);
                    break;
                }
            }
            dialogTxtSeries.setText(item.getSeries().toString());
            dialogTxtRepeticoes.setText(item.getRepeticoes().toString());
            if (item.getCarga() != null) {
                dialogTxtCarga.setText(item.getCarga().toString());
            }
            if (item.getObservacoes() != null) {
                dialogTxtObs.setText(item.getObservacoes());
            }
        }
        
        // Adicionar campos ao painel
        formPanel.add(createItemLabel("Plano de Treino *"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogCmbPlano);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createItemLabel("Exercício *"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogCmbExercicio);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createItemLabel("Séries *"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogTxtSeries);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createItemLabel("Repetições *"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogTxtRepeticoes);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createItemLabel("Carga (kg)"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogTxtCarga);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createItemLabel("Observações"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(scrollObs);
        formPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        CustomButton btnCancelar = new CustomButton("Cancelar", CustomButton.ButtonType.SECONDARY);
        CustomButton btnSalvar = new CustomButton("Salvar", CustomButton.ButtonType.PRIMARY);
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnSalvar.addActionListener(e -> {
            PlanoItem selectedPlano = (PlanoItem) dialogCmbPlano.getSelectedItem();
            ExercicioItem selectedExercicio = (ExercicioItem) dialogCmbExercicio.getSelectedItem();
            
            if (selectedPlano == null || selectedExercicio == null) {
                MessageDialog.showWarning(dialog, "Selecione o plano e o exercício.");
                return;
            }
            
            String seriesStr = dialogTxtSeries.getText().trim();
            String repeticoesStr = dialogTxtRepeticoes.getText().trim();
            
            if (seriesStr.isEmpty() || repeticoesStr.isEmpty()) {
                MessageDialog.showWarning(dialog, "Preencha séries e repetições.");
                return;
            }
            
            try {
                ItemTreinoRequestDTO dto = new ItemTreinoRequestDTO();
                dto.setPlanoTreinoId(selectedPlano.getId());
                dto.setExercicioId(selectedExercicio.getId());
                dto.setSeries(Integer.parseInt(seriesStr));
                dto.setRepeticoes(Integer.parseInt(repeticoesStr));
                
                String cargaStr = dialogTxtCarga.getText().trim();
                if (!cargaStr.isEmpty()) {
                    dto.setCarga(new BigDecimal(cargaStr));
                }
                
                dto.setObservacoes(dialogTxtObs.getText().trim());
                
                dialog.dispose();
                
                LoadingDialog.executeWithLoading(
                    SwingUtilities.getWindowAncestor(this),
                    isNew ? "Adicionando exercício..." : "Atualizando exercício...",
                    () -> {
                        if (isNew) {
                            apiClient.post("/itens-treino", dto);
                        } else {
                            apiClient.put("/itens-treino/" + item.getId(), dto);
                        }
                    },
                    () -> {
                        MessageDialog.showSuccess(this, isNew ? "Exercício adicionado com sucesso!" : "Exercício atualizado com sucesso!");
                        loadItensTreino();
                    },
                    error -> {
                        if (error instanceof ApiException) {
                            MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                        } else {
                            MessageDialog.showError(this, "Erro ao salvar exercício: " + error.getMessage());
                        }
                    }
                );
            } catch (NumberFormatException ex) {
                MessageDialog.showWarning(dialog, "Séries e repetições devem ser números válidos.");
            }
        });
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        formPanel.add(buttonPanel);
        
        dialog.add(formPanel, BorderLayout.NORTH);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    private JLabel createItemLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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
        if (table == null) return; // Prevenir NullPointerException durante inicialização
        boolean hasSelection = table.getSelectedRow() != -1;
        
        btnEditar.setEnabled(hasSelection);
        btnExcluir.setEnabled(hasSelection);
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
