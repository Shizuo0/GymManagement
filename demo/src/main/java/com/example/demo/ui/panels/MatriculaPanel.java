package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.MatriculaResponseDTO;
import com.example.demo.dto.PlanoResponseDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomDatePicker;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Panel para gerenciamento de Matrículas
 */
public class MatriculaPanel extends JPanel {
    
    private final ApiClient apiClient;
    private final DateTimeFormatter dateFormatter;
    private final ObjectMapper objectMapper;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomTextField txtBusca;
    private JComboBox<String> cmbFiltroStatus;
    
    // Componentes do formulário
    private JComboBox<AlunoItem> cmbAluno;
    private JComboBox<PlanoItem> cmbPlano;
    private CustomDatePicker datePickerInicio;
    private CustomDatePicker datePickerFim;
    private JComboBox<String> cmbStatus;
    private JLabel lblDuracao;
    
    // Botões
    private CustomButton btnNovo;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnAtivar;
    private CustomButton btnInativar;
    private CustomButton btnCancelar;
    private CustomButton btnSalvar;
    private CustomButton btnCancelarForm;
    private CustomButton btnBuscar;
    
    // Controle de estado
    private Long currentMatriculaId;
    private boolean isEditMode;
    
    // Mensagens
    private static final String MSG_SUCCESS_SAVE = "Matrícula cadastrada com sucesso!";
    private static final String MSG_SUCCESS_UPDATE = "Matrícula atualizada com sucesso!";
    private static final String MSG_SUCCESS_DELETE = "Matrícula excluída com sucesso!";
    private static final String MSG_SUCCESS_ATIVAR = "Matrícula ativada com sucesso!";
    private static final String MSG_SUCCESS_INATIVAR = "Matrícula inativada com sucesso!";
    private static final String MSG_SUCCESS_CANCELAR = "Matrícula cancelada com sucesso!";
    
    public MatriculaPanel() {
        this.apiClient = new ApiClient();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.objectMapper = new ObjectMapper();
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        loadMatriculas();
        loadComboBoxData();
    }
    
    private void initializeUI() {
        // Split pane: tabela à esquerda, formulário à direita
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
        
        // Cabeçalho com título e filtros
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel("📝 Matrículas");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        // Painel de busca e filtros
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblFiltro = new JLabel("Status:");
        lblFiltro.setFont(FONT_REGULAR);
        lblFiltro.setForeground(TEXT_PRIMARY);
        
        cmbFiltroStatus = new JComboBox<>(new String[]{"Todos", "ATIVA", "INATIVA", "PENDENTE", "CANCELADA"});
        cmbFiltroStatus.setFont(FONT_REGULAR);
        cmbFiltroStatus.addActionListener(e -> filtrarPorStatus());
        
        txtBusca = new CustomTextField("Buscar por aluno...", 15);
        btnBuscar = new CustomButton("🔍", CustomButton.ButtonType.PRIMARY);
        btnBuscar.addActionListener(e -> buscarMatriculas());
        
        searchPanel.add(lblFiltro);
        searchPanel.add(cmbFiltroStatus);
        searchPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        searchPanel.add(txtBusca);
        searchPanel.add(btnBuscar);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Aluno", "Plano", "Início", "Fim", "Status"};
        table = new CustomTable(colunas);
        table.setPreferredScrollableViewportSize(new Dimension(700, 400));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onMatriculaSelected();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);
        
        btnNovo = new CustomButton("➕ Nova Matrícula", CustomButton.ButtonType.SUCCESS);
        btnEditar = new CustomButton("✏️ Editar", CustomButton.ButtonType.PRIMARY);
        btnExcluir = new CustomButton("🗑️ Excluir", CustomButton.ButtonType.DANGER);
        btnAtivar = new CustomButton("✅ Ativar", CustomButton.ButtonType.SUCCESS);
        btnInativar = new CustomButton("⏸️ Inativar", CustomButton.ButtonType.WARNING);
        btnCancelar = new CustomButton("✖ Cancelar Matrícula", CustomButton.ButtonType.DANGER);
        
        btnNovo.addActionListener(e -> newMatricula());
        btnEditar.addActionListener(e -> editMatricula());
        btnExcluir.addActionListener(e -> deleteMatricula());
        btnAtivar.addActionListener(e -> ativarMatricula());
        btnInativar.addActionListener(e -> inativarMatricula());
        btnCancelar.addActionListener(e -> cancelarMatricula());
        
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnAtivar.setEnabled(false);
        btnInativar.setEnabled(false);
        btnCancelar.setEnabled(false);
        
        actionPanel.add(btnNovo);
        actionPanel.add(btnEditar);
        actionPanel.add(btnExcluir);
        actionPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        actionPanel.add(btnAtivar);
        actionPanel.add(btnInativar);
        actionPanel.add(btnCancelar);
        
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, PADDING_MEDIUM));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        
        // Título
        JLabel lblFormTitle = new JLabel("Detalhes da Matrícula");
        lblFormTitle.setFont(FONT_SUBTITLE);
        lblFormTitle.setForeground(TEXT_PRIMARY);
        panel.add(lblFormTitle, BorderLayout.NORTH);
        
        // Campos do formulário
        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setBackground(CARD_BACKGROUND);
        
        // Aluno
        formFields.add(createLabel("Aluno *"));
        cmbAluno = new JComboBox<>();
        cmbAluno.setFont(FONT_REGULAR);
        cmbAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbAluno.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        formFields.add(cmbAluno);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Plano
        formFields.add(createLabel("Plano *"));
        cmbPlano = new JComboBox<>();
        cmbPlano.setFont(FONT_REGULAR);
        cmbPlano.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbPlano.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbPlano.addActionListener(e -> calcularDataFim());
        formFields.add(cmbPlano);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Data de Início
        formFields.add(createLabel("Data de Início *"));
        datePickerInicio = new CustomDatePicker();
        datePickerInicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePickerInicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        datePickerInicio.addPropertyChangeListener("date", evt -> calcularDataFim());
        formFields.add(datePickerInicio);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Data de Fim
        formFields.add(createLabel("Data de Fim *"));
        datePickerFim = new CustomDatePicker();
        datePickerFim.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePickerFim.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        formFields.add(datePickerFim);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Duração (calculada)
        JPanel duracaoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        duracaoPanel.setBackground(CARD_BACKGROUND);
        duracaoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblDuracaoLabel = new JLabel("Duração:");
        lblDuracaoLabel.setFont(FONT_REGULAR);
        lblDuracaoLabel.setForeground(TEXT_SECONDARY);
        
        lblDuracao = new JLabel("--");
        lblDuracao.setFont(FONT_SUBTITLE);
        lblDuracao.setForeground(INFO_COLOR);
        
        duracaoPanel.add(lblDuracaoLabel);
        duracaoPanel.add(lblDuracao);
        duracaoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formFields.add(duracaoPanel);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Status
        formFields.add(createLabel("Status *"));
        cmbStatus = new JComboBox<>(new String[]{"ATIVA", "INATIVA", "PENDENTE", "CANCELADA"});
        cmbStatus.setFont(FONT_REGULAR);
        cmbStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbStatus.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        formFields.add(cmbStatus);
        
        panel.add(formFields, BorderLayout.CENTER);
        
        // Botões do formulário
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        btnSalvar = new CustomButton("💾 Salvar", CustomButton.ButtonType.SUCCESS);
        btnCancelarForm = new CustomButton("✖ Cancelar", CustomButton.ButtonType.DEFAULT);
        
        btnSalvar.addActionListener(e -> saveMatricula());
        btnCancelarForm.addActionListener(e -> cancelForm());
        
        buttonPanel.add(btnCancelarForm);
        buttonPanel.add(btnSalvar);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        setFormEnabled(false);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void loadMatriculas() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando matrículas...",
            () -> {
                String response = apiClient.get("/api/matriculas");
                List<MatriculaResponseDTO> matriculas = apiClient.fromJsonArray(response, MatriculaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(matriculas);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar matrículas: " + error.getMessage());
                }
            }
        );
    }
    
    private void loadComboBoxData() {
        // Carregar alunos
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                // Carregar alunos
                String alunosJson = apiClient.get("/alunos");
                JsonNode alunosArray = objectMapper.readTree(alunosJson);
                
                SwingUtilities.invokeLater(() -> {
                    cmbAluno.removeAllItems();
                    for (JsonNode alunoNode : alunosArray) {
                        Long id = alunoNode.get("idAluno").asLong();
                        String nome = alunoNode.get("nome").asText();
                        cmbAluno.addItem(new AlunoItem(id, nome));
                    }
                });
                
                // Carregar planos ativos
                String planosJson = apiClient.get("/api/planos");
                List<PlanoResponseDTO> planos = apiClient.fromJsonArray(planosJson, PlanoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    cmbPlano.removeAllItems();
                    for (PlanoResponseDTO plano : planos) {
                        if ("ATIVO".equals(plano.getStatus())) {
                            cmbPlano.addItem(new PlanoItem(plano.getId(), plano.getNome(), plano.getDuracaoMeses()));
                        }
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
                    MessageDialog.showError(this, "Erro ao carregar dados: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<MatriculaResponseDTO> matriculas) {
        table.clearRows();
        for (MatriculaResponseDTO matricula : matriculas) {
            table.addRow(new Object[]{
                matricula.getId(),
                matricula.getNomeAluno(),
                matricula.getNomePlano(),
                matricula.getDataInicio().format(dateFormatter),
                matricula.getDataFim().format(dateFormatter),
                matricula.getStatus()
            });
        }
    }
    
    private void onMatriculaSelected() {
        if (!table.hasSelection()) {
            clearSelection();
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/api/matriculas/" + id);
                MatriculaResponseDTO matricula = apiClient.fromJson(response, MatriculaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(matricula);
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
                    MessageDialog.showError(this, "Erro ao carregar matrícula: " + error.getMessage());
                }
            }
        );
    }
    
    private void populateForm(MatriculaResponseDTO matricula) {
        currentMatriculaId = matricula.getId();
        
        // Selecionar aluno
        for (int i = 0; i < cmbAluno.getItemCount(); i++) {
            if (cmbAluno.getItemAt(i).getId().equals(matricula.getIdAluno())) {
                cmbAluno.setSelectedIndex(i);
                break;
            }
        }
        
        // Selecionar plano
        for (int i = 0; i < cmbPlano.getItemCount(); i++) {
            if (cmbPlano.getItemAt(i).getId().equals(matricula.getIdPlano())) {
                cmbPlano.setSelectedIndex(i);
                break;
            }
        }
        
        datePickerInicio.setLocalDate(matricula.getDataInicio());
        datePickerFim.setLocalDate(matricula.getDataFim());
        cmbStatus.setSelectedItem(matricula.getStatus().toString());
        
        calcularDuracao(matricula.getDataInicio(), matricula.getDataFim());
        
        setFormEnabled(false);
        isEditMode = false;
    }
    
    private void newMatricula() {
        clearForm();
        setFormEnabled(true);
        isEditMode = false;
        currentMatriculaId = null;
        table.clearSelection();
        cmbStatus.setSelectedItem("ATIVA");
        
        // Definir data de início como hoje
        datePickerInicio.setLocalDate(LocalDate.now());
        
        updateButtons();
    }
    
    private void editMatricula() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione uma matrícula para editar.");
            return;
        }
        
        setFormEnabled(true);
        isEditMode = true;
        updateButtons();
    }
    
    private void saveMatricula() {
        if (!validateForm()) {
            return;
        }
        
        AlunoItem aluno = (AlunoItem) cmbAluno.getSelectedItem();
        PlanoItem plano = (PlanoItem) cmbPlano.getSelectedItem();
        
        Map<String, Object> matriculaData = new HashMap<>();
        matriculaData.put("idAluno", aluno.getId());
        matriculaData.put("idPlano", plano.getId());
        matriculaData.put("dataInicio", datePickerInicio.getLocalDate().toString());
        matriculaData.put("dataFim", datePickerFim.getLocalDate().toString());
        matriculaData.put("status", cmbStatus.getSelectedItem().toString());
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando matrícula..." : "Cadastrando matrícula...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/api/matriculas/" + currentMatriculaId, matriculaData);
                } else {
                    apiClient.post("/api/matriculas", matriculaData);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isEditMode ? MSG_SUCCESS_UPDATE : MSG_SUCCESS_SAVE);
                cancelForm();
                loadMatriculas();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar matrícula: " + error.getMessage());
                }
            }
        );
    }
    
    private boolean validateForm() {
        // Validar seleção de aluno
        if (cmbAluno.getSelectedItem() == null) {
            MessageDialog.showError(this, "Selecione um aluno.");
            cmbAluno.requestFocus();
            return false;
        }
        
        // Validar seleção de plano
        if (cmbPlano.getSelectedItem() == null) {
            MessageDialog.showError(this, "Selecione um plano.");
            cmbPlano.requestFocus();
            return false;
        }
        
        // Validar data de início
        LocalDate dataInicio = datePickerInicio.getLocalDate();
        if (dataInicio == null) {
            MessageDialog.showError(this, "Informe a data de início.");
            datePickerInicio.requestFocus();
            return false;
        }
        
        // Validar data de fim
        LocalDate dataFim = datePickerFim.getLocalDate();
        if (dataFim == null) {
            MessageDialog.showError(this, "Informe a data de fim.");
            datePickerFim.requestFocus();
            return false;
        }
        
        // Validar que data de fim é posterior à data de início
        if (!dataFim.isAfter(dataInicio)) {
            MessageDialog.showError(this, "A data de fim deve ser posterior à data de início.");
            datePickerFim.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void deleteMatricula() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione uma matrícula para excluir.");
            return;
        }
        
        if (!MessageDialog.showDeleteConfirmation(this)) {
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo matrícula...",
            () -> {
                apiClient.delete("/api/matriculas/" + id);
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_DELETE);
                clearSelection();
                loadMatriculas();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao excluir matrícula: " + error.getMessage());
                }
            }
        );
    }
    
    private void ativarMatricula() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione uma matrícula para ativar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Ativando matrícula...",
            () -> {
                apiClient.put("/api/matriculas/" + id + "/ativar", null);
                String response = apiClient.get("/api/matriculas/" + id);
                MatriculaResponseDTO matricula = apiClient.fromJson(response, MatriculaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(matricula);
                    updateButtons();
                });
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_ATIVAR);
                loadMatriculas();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao ativar matrícula: " + error.getMessage());
                }
            }
        );
    }
    
    private void inativarMatricula() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione uma matrícula para inativar.");
            return;
        }
        
        boolean confirm = MessageDialog.showConfirmation(this,
            "Tem certeza que deseja inativar esta matrícula?",
            "Confirmar Inativação");
        
        if (!confirm) {
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Inativando matrícula...",
            () -> {
                apiClient.put("/api/matriculas/" + id + "/inativar", null);
                String response = apiClient.get("/api/matriculas/" + id);
                MatriculaResponseDTO matricula = apiClient.fromJson(response, MatriculaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(matricula);
                    updateButtons();
                });
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_INATIVAR);
                loadMatriculas();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao inativar matrícula: " + error.getMessage());
                }
            }
        );
    }
    
    private void cancelarMatricula() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione uma matrícula para cancelar.");
            return;
        }
        
        boolean confirm = MessageDialog.showConfirmation(this,
            "Tem certeza que deseja CANCELAR esta matrícula?\n" +
            "Esta ação não pode ser desfeita.",
            "Confirmar Cancelamento");
        
        if (!confirm) {
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Cancelando matrícula...",
            () -> {
                apiClient.put("/api/matriculas/" + id + "/cancelar", null);
                String response = apiClient.get("/api/matriculas/" + id);
                MatriculaResponseDTO matricula = apiClient.fromJson(response, MatriculaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(matricula);
                    updateButtons();
                });
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_CANCELAR);
                loadMatriculas();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao cancelar matrícula: " + error.getMessage());
                }
            }
        );
    }
    
    private void buscarMatriculas() {
        String termo = txtBusca.getText().trim().toLowerCase();
        if (termo.isEmpty()) {
            loadMatriculas();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando matrículas...",
            () -> {
                String response = apiClient.get("/api/matriculas");
                List<MatriculaResponseDTO> todasMatriculas = apiClient.fromJsonArray(response, MatriculaResponseDTO.class);
                List<MatriculaResponseDTO> filtradas = todasMatriculas.stream()
                    .filter(m -> m.getNomeAluno().toLowerCase().contains(termo))
                    .toList();
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtradas);
                    if (filtradas.isEmpty()) {
                        MessageDialog.showInfo(this, "Nenhuma matrícula encontrada.");
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
                    MessageDialog.showError(this, "Erro ao buscar matrículas: " + error.getMessage());
                }
            }
        );
    }
    
    private void filtrarPorStatus() {
        String statusSelecionado = (String) cmbFiltroStatus.getSelectedItem();
        
        if ("Todos".equals(statusSelecionado)) {
            loadMatriculas();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Filtrando matrículas...",
            () -> {
                String response = apiClient.get("/api/matriculas/status/" + statusSelecionado);
                List<MatriculaResponseDTO> matriculas = apiClient.fromJsonArray(response, MatriculaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(matriculas);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao filtrar matrículas: " + error.getMessage());
                }
            }
        );
    }
    
    private void calcularDataFim() {
        PlanoItem plano = (PlanoItem) cmbPlano.getSelectedItem();
        LocalDate dataInicio = datePickerInicio.getLocalDate();
        
        if (plano != null && dataInicio != null) {
            LocalDate dataFim = dataInicio.plusMonths(plano.getDuracaoMeses());
            datePickerFim.setLocalDate(dataFim);
            calcularDuracao(dataInicio, dataFim);
        }
    }
    
    private void calcularDuracao(LocalDate inicio, LocalDate fim) {
        if (inicio != null && fim != null) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(inicio, fim);
            long meses = java.time.temporal.ChronoUnit.MONTHS.between(inicio, fim);
            lblDuracao.setText(meses + " meses (" + dias + " dias)");
        } else {
            lblDuracao.setText("--");
        }
    }
    
    private void cancelForm() {
        clearForm();
        setFormEnabled(false);
        isEditMode = false;
        currentMatriculaId = null;
        if (table.hasSelection()) {
            onMatriculaSelected();
        }
        updateButtons();
    }
    
    private void clearForm() {
        if (cmbAluno.getItemCount() > 0) {
            cmbAluno.setSelectedIndex(0);
        }
        if (cmbPlano.getItemCount() > 0) {
            cmbPlano.setSelectedIndex(0);
        }
        datePickerInicio.setLocalDate(LocalDate.now());
        datePickerFim.setLocalDate(null);
        cmbStatus.setSelectedItem("ATIVA");
        lblDuracao.setText("--");
    }
    
    private void clearSelection() {
        table.clearSelection();
        currentMatriculaId = null;
        isEditMode = false;
        clearForm();
        setFormEnabled(false);
        updateButtons();
    }
    
    private void setFormEnabled(boolean enabled) {
        cmbAluno.setEnabled(enabled);
        cmbPlano.setEnabled(enabled);
        datePickerInicio.setEnabled(enabled);
        datePickerFim.setEnabled(enabled);
        cmbStatus.setEnabled(enabled);
        btnSalvar.setEnabled(enabled);
        btnCancelarForm.setEnabled(enabled);
    }
    
    private void updateButtons() {
        boolean hasSelection = table.hasSelection();
        boolean formEnabled = cmbAluno.isEnabled();
        String status = hasSelection ? (String) table.getSelectedRowValue(5) : "";
        
        btnNovo.setEnabled(!formEnabled);
        btnEditar.setEnabled(hasSelection && !formEnabled);
        btnExcluir.setEnabled(hasSelection && !formEnabled);
        btnAtivar.setEnabled(hasSelection && !"ATIVA".equals(status) && !formEnabled);
        btnInativar.setEnabled(hasSelection && "ATIVA".equals(status) && !formEnabled);
        btnCancelar.setEnabled(hasSelection && !"CANCELADA".equals(status) && !formEnabled);
    }
    
    // Classes auxiliares para ComboBox
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
    
    private static class PlanoItem {
        private final Long id;
        private final String nome;
        private final Integer duracaoMeses;
        
        public PlanoItem(Long id, String nome, Integer duracaoMeses) {
            this.id = id;
            this.nome = nome;
            this.duracaoMeses = duracaoMeses;
        }
        
        public Long getId() {
            return id;
        }
        
        public Integer getDuracaoMeses() {
            return duracaoMeses;
        }
        
        @Override
        public String toString() {
            return nome + " (" + duracaoMeses + " meses)";
        }
    }
}
