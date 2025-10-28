package com.example.demo.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.AlunoDTO;
import com.example.demo.dto.InstrutorDTO;
import com.example.demo.dto.PlanoTreinoRequestDTO;
import com.example.demo.dto.PlanoTreinoResponseDTO;
import com.example.demo.ui.GymManagementUI;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomComboBox;
import com.example.demo.ui.components.CustomDatePicker;
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
import static com.example.demo.ui.utils.UIConstants.SURFACE_COLOR;
import static com.example.demo.ui.utils.UIConstants.TEXTFIELD_HEIGHT;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;

/**
 * Panel para gerenciamento de Planos de Treino
 * COMMIT 8: PlanoTreinoPanel - Gerenciar planos de treino vinculados a alunos e instrutores
 */
public class PlanoTreinoPanel extends JPanel implements RefreshablePanel {
    
    private final ApiClient apiClient;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomTextField txtBusca;
    
    // Painéis
    private JSplitPane splitPane;
    private JPanel formPanel;
    
    // Componentes do formulário
    private CustomComboBox<AlunoItem> cmbAluno;
    private CustomComboBox<InstrutorItem> cmbInstrutor;
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
    private CustomButton btnAtualizar;
    
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
        
        // Carregar dados após a UI estar visível
        SwingUtilities.invokeLater(this::loadPlanosTreino);
    }
    
    private void initializeUI() {
        // Criar o painel do formulário primeiro
        formPanel = createFormPanel();
        
        // Criar o splitPane
        JPanel listPanel = createListPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, null);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1.0);
        splitPane.setBorder(null);
        
        add(splitPane, BorderLayout.CENTER);
        updateButtons();
    }
    
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, PADDING_MEDIUM));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Cabeçalho com título e busca
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel("Planos de Treino");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        // Painel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        txtBusca = new CustomTextField("Buscar plano de treino por aluno ou instrutor", 20);
        btnBuscar = new CustomButton("[ ? ]", CustomButton.ButtonType.PRIMARY);
        btnBuscar.addActionListener(e -> buscarPlanos());
        
        btnLimparFiltro = new CustomButton("Limpar", CustomButton.ButtonType.SECONDARY);
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
        
        btnNovo = new CustomButton("+ Novo", CustomButton.ButtonType.PRIMARY);
        btnEditar = new CustomButton("Editar", CustomButton.ButtonType.SECONDARY);
        btnExcluir = new CustomButton("X Excluir", CustomButton.ButtonType.DANGER);
        btnAtualizar = new CustomButton("↻ Atualizar", CustomButton.ButtonType.PRIMARY);
        
        btnNovo.addActionListener(e -> novoPlano());
        btnEditar.addActionListener(e -> editarPlano());
        btnExcluir.addActionListener(e -> excluirPlano());
        btnAtualizar.addActionListener(e -> loadPlanosTreino());
        
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(Box.createHorizontalStrut(PADDING_LARGE));
        buttonPanel.add(btnAtualizar);
        
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
        JLabel lblTitle = new JLabel("Dados do Plano de Treino");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Campo Aluno
        panel.add(createFormField("Aluno:*", cmbAluno = new CustomComboBox<>()));
        
        // Campo Instrutor
        panel.add(createFormField("Instrutor:*", cmbInstrutor = new CustomComboBox<>()));
        
        // Campo Data Criação
        panel.add(createFormField("Data de Criação:*", dataCriacao = new CustomDatePicker()));
        
        // Campo Duração em Semanas
        panel.add(createFormField("Duração (semanas):", txtDuracaoSemanas = CustomTextField.createNumericField("Ex: 12")));
        
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
        txtDescricao.setBackground(SURFACE_COLOR);
        txtDescricao.setCaretColor(TEXT_PRIMARY);
        txtDescricao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_SMALL, PADDING_SMALL, PADDING_SMALL)
        ));
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDescricao.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollDescricao);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões do formulário
        JPanel btnFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        btnFormPanel.setBackground(CARD_BACKGROUND);
        btnFormPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT + 10));
        btnFormPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnSalvar = new CustomButton("Salvar", CustomButton.ButtonType.PRIMARY);
        btnCancelar = new CustomButton("Cancelar", CustomButton.ButtonType.SECONDARY);
        
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
            inputComponent instanceof CustomTextField || 
            inputComponent instanceof CustomDatePicker) {
            inputComponent.setFont(FONT_REGULAR);
            ((javax.swing.JComponent) inputComponent).setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
            ((javax.swing.JComponent) inputComponent).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        fieldPanel.add(inputComponent);
        fieldPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        return fieldPanel;
    }
    
    // ========== CARREGAMENTO DE DADOS ==========
    
    private void loadPlanosTreino() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando planos de treino...",
            () -> {
                String response = apiClient.get("/planos-treino");
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
        loadAlunos(false);
    }
    
    private void loadAlunos(boolean showErrorDialog) {
        try {
            String response = apiClient.get("/alunos");
            List<AlunoDTO> alunos = apiClient.fromJsonArray(response, AlunoDTO.class);
            
            cmbAluno.removeAllItems();
            cmbAluno.addItem(new AlunoItem(null, "Selecione um aluno..."));
            for (AlunoDTO aluno : alunos) {
                cmbAluno.addItem(new AlunoItem(aluno.getIdAluno(), aluno.getNome()));
            }
        } catch (Exception ex) {
            // Adiciona item padrão mesmo em caso de erro
            cmbAluno.removeAllItems();
            cmbAluno.addItem(new AlunoItem(null, "Selecione um aluno..."));
            
            if (showErrorDialog) {
                MessageDialog.showError(this, "Erro ao carregar alunos: " + ex.getMessage());
            } else {
                System.err.println("[AVISO] Não foi possível carregar alunos. Verifique se o backend está rodando.");
            }
        }
    }
    
    private void loadInstrutores() {
        loadInstrutores(false);
    }
    
    private void loadInstrutores(boolean showErrorDialog) {
        try {
            String response = apiClient.get("/instrutores");
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
            // Adiciona item padrão mesmo em caso de erro
            cmbInstrutor.removeAllItems();
            cmbInstrutor.addItem(new InstrutorItem(null, "Selecione um instrutor..."));
            
            if (showErrorDialog) {
                MessageDialog.showError(this, "Erro ao carregar instrutores: " + ex.getMessage());
            } else {
                System.err.println("[AVISO] Não foi possível carregar instrutores. Verifique se o backend está rodando.");
            }
        }
    }
    
    // ========== CRUD OPERATIONS ==========
    
    private void novoPlano() {
        showPlanoTreinoDialog(null);
    }
    
    private void editarPlano() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long id = (Long) table.getValueAt(selectedRow, 0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/planos-treino/" + id);
                PlanoTreinoResponseDTO plano = apiClient.fromJson(response, PlanoTreinoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    showPlanoTreinoDialog(plano);
                });
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar plano: " + error.getMessage());
                }
            }
        );
    }
    
    private void showPlanoTreinoDialog(PlanoTreinoResponseDTO plano) {
        boolean isNew = (plano == null);
        
        JDialog dialog = new JDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            isNew ? "Novo Plano de Treino" : "Editar Plano de Treino",
            true
        );
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Carregar dados para os combos
        List<AlunoDTO> alunos = new java.util.ArrayList<>();
        List<InstrutorDTO> instrutores = new java.util.ArrayList<>();
        
        try {
            String alunosJson = apiClient.get("/alunos");
            alunos = apiClient.fromJsonArray(alunosJson, AlunoDTO.class);
            
            String instrutoresJson = apiClient.get("/instrutores");
            instrutores = apiClient.fromJsonArray(instrutoresJson, InstrutorDTO.class);
        } catch (Exception ex) {
            MessageDialog.showError(dialog, "Erro ao carregar dados: " + ex.getMessage());
            dialog.dispose();
            return;
        }
        
        // Campo Aluno
        CustomComboBox<AlunoItem> dialogCmbAluno = new CustomComboBox<>();
        dialogCmbAluno.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogCmbAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (AlunoDTO aluno : alunos) {
            dialogCmbAluno.addItem(new AlunoItem(aluno.getIdAluno(), aluno.getNome()));
        }
        
        // Campo Instrutor
        CustomComboBox<InstrutorItem> dialogCmbInstrutor = new CustomComboBox<>();
        dialogCmbInstrutor.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogCmbInstrutor.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (InstrutorDTO instrutor : instrutores) {
            dialogCmbInstrutor.addItem(new InstrutorItem(instrutor.getIdInstrutor(), 
                instrutor.getNome() + " - " + (instrutor.getEspecialidade() != null ? instrutor.getEspecialidade() : "")));
        }
        
        // Campo Data
        CustomDatePicker dialogDataCriacao = new CustomDatePicker();
        dialogDataCriacao.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogDataCriacao.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo Duração
        CustomTextField dialogTxtDuracao = CustomTextField.createNumericField("Ex: 12");
        dialogTxtDuracao.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dialogTxtDuracao.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Campo Descrição
        JTextArea dialogTxtDescricao = new JTextArea(4, 30);
        dialogTxtDescricao.setFont(FONT_REGULAR);
        dialogTxtDescricao.setLineWrap(true);
        dialogTxtDescricao.setWrapStyleWord(true);
        dialogTxtDescricao.setBackground(SURFACE_COLOR);
        dialogTxtDescricao.setForeground(TEXT_PRIMARY);
        dialogTxtDescricao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_SMALL, PADDING_SMALL, PADDING_SMALL)
        ));
        JScrollPane scrollDescricao = new JScrollPane(dialogTxtDescricao);
        scrollDescricao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Preencher dados se for edição
        if (!isNew && plano != null) {
            for (int i = 0; i < dialogCmbAluno.getItemCount(); i++) {
                if (dialogCmbAluno.getItemAt(i).getId().equals(plano.getIdAluno())) {
                    dialogCmbAluno.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < dialogCmbInstrutor.getItemCount(); i++) {
                if (dialogCmbInstrutor.getItemAt(i).getId().equals(plano.getIdInstrutor())) {
                    dialogCmbInstrutor.setSelectedIndex(i);
                    break;
                }
            }
            dialogDataCriacao.setLocalDate(plano.getDataCriacao());
            if (plano.getDuracaoSemanas() != null) {
                dialogTxtDuracao.setText(plano.getDuracaoSemanas().toString());
            }
            if (plano.getDescricao() != null) {
                dialogTxtDescricao.setText(plano.getDescricao());
            }
        } else {
            dialogDataCriacao.setToday();
        }
        
        // Adicionar campos ao painel
        formPanel.add(createFormLabel("Aluno *"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogCmbAluno);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createFormLabel("Instrutor *"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogCmbInstrutor);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createFormLabel("Data de Criação *"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogDataCriacao);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createFormLabel("Duração (semanas)"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(dialogTxtDuracao);
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        formPanel.add(createFormLabel("Descrição"));
        formPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        formPanel.add(scrollDescricao);
        formPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        CustomButton btnCancelar = new CustomButton("Cancelar", CustomButton.ButtonType.SECONDARY);
        CustomButton btnSalvar = new CustomButton("Salvar", CustomButton.ButtonType.PRIMARY);
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnSalvar.addActionListener(e -> {
            AlunoItem selectedAluno = (AlunoItem) dialogCmbAluno.getSelectedItem();
            InstrutorItem selectedInstrutor = (InstrutorItem) dialogCmbInstrutor.getSelectedItem();
            
            if (selectedAluno == null || selectedInstrutor == null) {
                MessageDialog.showWarning(dialog, "Preencha todos os campos obrigatórios.");
                return;
            }
            
            LocalDate data = dialogDataCriacao.getLocalDate();
            if (data == null) {
                MessageDialog.showWarning(dialog, "Informe a data de criação.");
                return;
            }
            
            PlanoTreinoRequestDTO dto = new PlanoTreinoRequestDTO();
            dto.setIdAluno(selectedAluno.getId());
            dto.setIdInstrutor(selectedInstrutor.getId());
            dto.setDataCriacao(data);
            dto.setDescricao(dialogTxtDescricao.getText().trim());
            
            String duracaoStr = dialogTxtDuracao.getText().trim();
            if (!duracaoStr.isEmpty()) {
                try {
                    dto.setDuracaoSemanas(Integer.parseInt(duracaoStr));
                } catch (NumberFormatException ex) {
                    MessageDialog.showWarning(dialog, "Duração deve ser um número válido.");
                    return;
                }
            }
            
            dialog.dispose();
            
            LoadingDialog.executeWithLoading(
                SwingUtilities.getWindowAncestor(this),
                isNew ? "Cadastrando plano..." : "Atualizando plano...",
                () -> {
                    if (isNew) {
                        apiClient.post("/planos-treino", dto);
                    } else {
                        apiClient.put("/planos-treino/" + plano.getId(), dto);
                    }
                },
                () -> {
                    MessageDialog.showSuccess(this, isNew ? "Plano cadastrado com sucesso!" : "Plano atualizado com sucesso!");
                    loadPlanosTreino();
                    notifyParentToRefresh();
                },
                error -> {
                    if (error instanceof ApiException) {
                        MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                    } else {
                        MessageDialog.showError(this, "Erro ao salvar plano: " + error.getMessage());
                    }
                }
            );
        });
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        formPanel.add(buttonPanel);
        
        dialog.add(formPanel, BorderLayout.NORTH);
        dialog.setSize(550, 550);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_REGULAR);
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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
                    apiClient.delete("/planos-treino/" + id);
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
        
        // Criar DTO
        PlanoTreinoRequestDTO dto = new PlanoTreinoRequestDTO();
        dto.setIdAluno(selectedAluno.getId());
        dto.setIdInstrutor(selectedInstrutor.getId());
        dto.setDataCriacao(data);
        dto.setDescricao(descricao.isEmpty() ? null : descricao);
        
        if (!duracaoStr.isEmpty()) {
            try {
                int duracao = Integer.parseInt(duracaoStr);
                dto.setDuracaoSemanas(duracao);
            } catch (NumberFormatException ex) {
                MessageDialog.showWarning(this, "Duração em semanas deve ser um número válido.");
                return;
            }
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando plano..." : "Criando plano...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/planos-treino/" + currentPlanoId, dto);
                } else {
                    apiClient.post("/planos-treino", dto);
                }
            },
            () -> {
                // Ação de sucesso (executada no EDT)
                MessageDialog.showSuccess(
                    this,
                    isEditMode ? "Plano atualizado com sucesso!" : "Plano criado com sucesso!"
                );
                cancelarEdicao();
                refreshData();
                notifyParentToRefresh();
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
        hideFormPanel();
        updateButtons();
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
                String response = apiClient.get("/planos-treino");
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
    
    // ========== REFRESH E NOTIFICAÇÕES ==========
    
    /**
     * Implementação de RefreshablePanel - atualiza os dados do painel
     */
    @Override
    public void refreshData() {
        loadAlunos();
        loadInstrutores();
        loadPlanosTreino();
    }
    
    /**
     * Notifica o GymManagementUI para atualizar outros painéis
     */
    private void notifyParentToRefresh() {
        // Busca o GymManagementUI na hierarquia de componentes
        java.awt.Container parent = getParent();
        while (parent != null && !(parent instanceof GymManagementUI)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof GymManagementUI) {
            ((GymManagementUI) parent).notifyDataChanged();
        }
    }
}
