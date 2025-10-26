package com.example.demo.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import com.example.demo.dto.AlunoDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomDatePicker;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;
import static com.example.demo.ui.utils.UIConstants.BORDER_COLOR;
import static com.example.demo.ui.utils.UIConstants.CARD_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.FONT_LABEL;
import static com.example.demo.ui.utils.UIConstants.FONT_SUBTITLE;
import static com.example.demo.ui.utils.UIConstants.FONT_TITLE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_DELETE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_SAVE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_UPDATE;
import static com.example.demo.ui.utils.UIConstants.PADDING_LARGE;
import static com.example.demo.ui.utils.UIConstants.PADDING_MEDIUM;
import static com.example.demo.ui.utils.UIConstants.PADDING_SMALL;
import static com.example.demo.ui.utils.UIConstants.PANEL_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.PRIMARY_COLOR;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;
import com.example.demo.ui.utils.ValidationUtils;

/**
 * Panel para gerenciamento completo de alunos (CRUD).
 * Implementa listagem, cadastro, edição e exclusão com validações.
 */
public class AlunoPanel extends JPanel {
    
    // Componentes de UI
    private CustomTable table;
    private CustomTextField txtNome;
    private CustomTextField txtCPF;
    private CustomDatePicker datePicker;
    private CustomTextField txtBusca;
    
    private CustomButton btnAdicionar;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnSalvar;
    private CustomButton btnCancelar;
    private CustomButton btnBuscar;
    private CustomButton btnLimparBusca;
    
    private JPanel formPanel;
    private JPanel listPanel;
    private JSplitPane splitPane;
    
    // Estado
    private ApiClient apiClient;
    private boolean isEditMode = false;
    private Long currentAlunoId = null;
    
    /**
     * Construtor
     */
    public AlunoPanel() {
        this.apiClient = new ApiClient();
        initializeComponents();
        setupLayout();
        
        // Defer loading para evitar problemas com LoadingDialog
        SwingUtilities.invokeLater(this::loadAlunos);
    }
    
    /**
     * Inicializa os componentes
     */
    private void initializeComponents() {
        setBackground(PANEL_BACKGROUND);
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        // Inicializa tabela
        String[] columns = {"ID", "Nome", "CPF", "Data de Ingresso"};
        table = new CustomTable(columns);
        table.setColumnWidth(0, 60);
        table.setColumnWidth(2, 150);
        table.setColumnWidth(3, 150);
        table.centerColumn(0);
        table.centerColumn(2);
        table.centerColumn(3);
        
        // Listener para seleção na tabela
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // Inicializa campos do formulário
        txtNome = new CustomTextField("Nome completo do aluno", 30);
        txtCPF = CustomTextField.createCPFField("000.000.000-00");
        datePicker = new CustomDatePicker(LocalDate.now());
        txtBusca = new CustomTextField("Buscar por nome ou CPF", 25);
        
        // Inicializa botões
        btnAdicionar = CustomButton.createAddButton("Novo");
        btnEditar = CustomButton.createEditButton("Editar");
        btnExcluir = CustomButton.createDeleteButton("Excluir");
        btnSalvar = CustomButton.createSaveButton("Salvar");
        btnCancelar = CustomButton.createCancelButton("Cancelar");
        btnBuscar = CustomButton.createSearchButton("Buscar");
        btnLimparBusca = CustomButton.createRefreshButton("Limpar");
        
        // Adiciona actions aos botões
        btnAdicionar.addActionListener(e -> showFormForNew());
        btnEditar.addActionListener(e -> showFormForEdit());
        btnExcluir.addActionListener(e -> deleteAluno());
        btnSalvar.addActionListener(e -> saveAluno());
        btnCancelar.addActionListener(e -> cancelForm());
        btnBuscar.addActionListener(e -> buscarAlunos());
        btnLimparBusca.addActionListener(e -> limparBusca());
        
        // Enter key nos campos de busca
        txtBusca.addActionListener(e -> buscarAlunos());
    }
    
    /**
     * Configura o layout do panel
     */
    private void setupLayout() {
        // Panel de lista (esquerda)
        listPanel = createListPanel();
        
        // Panel de formulário (direita)
        formPanel = createFormPanel();
        formPanel.setVisible(false);
        
        // Split pane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, formPanel);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.6);
        splitPane.setBackground(PANEL_BACKGROUND);
        splitPane.setBorder(null);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Cria o panel de listagem
     */
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        panel.setBackground(PANEL_BACKGROUND);
        
        // Título
        JLabel title = new JLabel("Gerenciamento de Alunos");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        
        // Panel de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_MEDIUM, 0));
        searchPanel.setBackground(PANEL_BACKGROUND);
        searchPanel.add(txtBusca);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnLimparBusca);
        
        // Panel superior (título + busca)
        JPanel topPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        topPanel.setBackground(PANEL_BACKGROUND);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Tabela com scroll
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        
        // Panel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_MEDIUM, PADDING_MEDIUM));
        buttonPanel.setBackground(PANEL_BACKGROUND);
        buttonPanel.add(btnAdicionar);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        
        // Monta o panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Cria o panel de formulário
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        
        // Título
        JLabel title = new JLabel("Cadastro de Aluno");
        title.setFont(FONT_SUBTITLE);
        title.setForeground(PRIMARY_COLOR);
        
        // Panel de campos
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBackground(CARD_BACKGROUND);
        
        // Campo Nome
        fieldsPanel.add(createFieldPanel("Nome *", txtNome));
        fieldsPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo CPF
        fieldsPanel.add(createFieldPanel("CPF *", txtCPF));
        fieldsPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Data de Ingresso
        fieldsPanel.add(createFieldPanel("Data de Ingresso *", datePicker));
        fieldsPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Panel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        
        // Monta o panel
        panel.add(title, BorderLayout.NORTH);
        panel.add(fieldsPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Cria um panel para um campo do formulário
     */
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(PADDING_SMALL, PADDING_SMALL));
        panel.setBackground(CARD_BACKGROUND);
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Carrega todos os alunos
     */
    private void loadAlunos() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando alunos...",
            () -> {
                String response = apiClient.get("/alunos");
                List<AlunoDTO> alunos = apiClient.fromJsonArray(response, AlunoDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(alunos);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar alunos: " + error.getMessage());
                }
            }
        );
    }
    
    /**
     * Atualiza a tabela com os alunos
     */
    private void updateTable(List<AlunoDTO> alunos) {
        table.clearRows();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (AlunoDTO aluno : alunos) {
            Object[] row = {
                aluno.getIdAluno(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getDataIngresso() != null ? aluno.getDataIngresso().format(formatter) : ""
            };
            table.addRow(row);
        }
        
        updateButtonStates();
    }
    
    /**
     * Mostra o formulário para novo aluno
     */
    private void showFormForNew() {
        isEditMode = false;
        currentAlunoId = null;
        clearForm();
        
        // Garante que o formPanel está no splitPane
        splitPane.setRightComponent(formPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerLocation(0.6);
        
        formPanel.setVisible(true);
        txtNome.requestFocus();
    }
    
    /**
     * Mostra o formulário para edição
     */
    private void showFormForEdit() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um aluno para editar.");
            return;
        }
        
        isEditMode = true;
        currentAlunoId = (Long) table.getSelectedRowValue(0);
        
        // Carrega os dados do aluno
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados do aluno...",
            () -> {
                String response = apiClient.get("/alunos/" + currentAlunoId);
                AlunoDTO aluno = apiClient.fromJson(response, AlunoDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(aluno);
                    
                    // Garante que o formPanel está no splitPane
                    splitPane.setRightComponent(formPanel);
                    splitPane.setResizeWeight(0.6);
                    splitPane.setDividerLocation(0.6);
                    
                    formPanel.setVisible(true);
                    txtNome.requestFocus();
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar aluno: " + error.getMessage());
                }
            }
        );
    }
    
    /**
     * Popula o formulário com dados do aluno
     */
    private void populateForm(AlunoDTO aluno) {
        txtNome.setText(aluno.getNome());
        txtCPF.setText(aluno.getCpf());
        if (aluno.getDataIngresso() != null) {
            datePicker.setLocalDate(aluno.getDataIngresso());
        }
    }
    
    /**
     * Salva o aluno (novo ou editado)
     */
    private void saveAluno() {
        // Validações
        if (!validateForm()) {
            return;
        }
        
        // Cria o DTO
        AlunoDTO aluno = new AlunoDTO();
        aluno.setNome(txtNome.getText().trim());
        aluno.setCpf(ValidationUtils.unformatCPF(txtCPF.getText()));
        aluno.setDataIngresso(datePicker.getLocalDate());
        
        if (isEditMode) {
            aluno.setIdAluno(currentAlunoId);
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando aluno..." : "Cadastrando aluno...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/alunos/" + currentAlunoId, aluno);
                } else {
                    apiClient.post("/alunos", aluno);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, 
                    isEditMode ? MSG_SUCCESS_UPDATE : MSG_SUCCESS_SAVE);
                cancelForm();
                loadAlunos();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar aluno: " + error.getMessage());
                }
            }
        );
    }
    
    /**
     * Valida o formulário
     */
    private boolean validateForm() {
        // Limpa marcações anteriores
        txtNome.markAsValid();
        txtCPF.markAsValid();
        
        boolean isValid = true;
        
        // Valida nome
        if (txtNome.getText().trim().isEmpty()) {
            txtNome.markAsInvalid();
            MessageDialog.showError(this, "O nome é obrigatório.");
            txtNome.requestFocus();
            return false;
        }
        
        // Valida CPF
        if (!ValidationUtils.validateCPFField(txtCPF)) {
            txtCPF.markAsInvalid();
            txtCPF.requestFocus();
            return false;
        }
        
        return isValid;
    }
    
    /**
     * Exclui o aluno selecionado
     */
    private void deleteAluno() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um aluno para excluir.");
            return;
        }
        
        if (!MessageDialog.showDeleteConfirmation(this)) {
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo aluno...",
            () -> {
                apiClient.delete("/alunos/" + id);
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_DELETE);
                loadAlunos();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao excluir aluno: " + error.getMessage());
                }
            }
        );
    }
    
    /**
     * Busca alunos por nome ou CPF
     */
    private void buscarAlunos() {
        String termo = txtBusca.getText().trim();
        
        if (termo.isEmpty()) {
            loadAlunos();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando alunos...",
            () -> {
                String response = apiClient.get("/alunos");
                List<AlunoDTO> alunos = apiClient.fromJsonArray(response, AlunoDTO.class);
                
                // Filtra localmente
                List<AlunoDTO> filtered = alunos.stream()
                    .filter(a -> 
                        a.getNome().toLowerCase().contains(termo.toLowerCase()) ||
                        a.getCpf().contains(termo.replaceAll("[^0-9]", ""))
                    )
                    .toList();
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtered);
                    if (filtered.isEmpty()) {
                        MessageDialog.showInfo(this, "Nenhum aluno encontrado.");
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
                    MessageDialog.showError(this, "Erro ao buscar alunos: " + error.getMessage());
                }
            }
        );
    }
    
    /**
     * Limpa a busca e recarrega todos os alunos
     */
    private void limparBusca() {
        txtBusca.clear();
        loadAlunos();
    }
    
    /**
     * Cancela a edição e fecha o formulário
     */
    private void cancelForm() {
        clearForm();
        
        // Remove o formPanel do splitPane
        splitPane.remove(formPanel);
        splitPane.setDividerLocation(1.0);
        
        formPanel.setVisible(false);
        isEditMode = false;
        currentAlunoId = null;
    }
    
    /**
     * Limpa o formulário
     */
    private void clearForm() {
        txtNome.clear();
        txtCPF.clear();
        datePicker.setToday();
    }
    
    /**
     * Atualiza o estado dos botões
     */
    private void updateButtonStates() {
        boolean hasSelection = table.hasSelection();
        btnEditar.setEnabled(hasSelection);
        btnExcluir.setEnabled(hasSelection);
    }
}
