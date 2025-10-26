package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.PlanoResponseDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;

/**
 * Panel para gerenciamento de Planos de Assinatura
 */
public class PlanoPanel extends JPanel {
    
    private final ApiClient apiClient;
    private final NumberFormat currencyFormat;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomTextField txtBusca;
    
    // Painéis
    private JSplitPane splitPane;
    private JPanel formPanel;
    
    // Componentes do formulário
    private CustomTextField txtNome;
    private JTextArea txtDescricao;
    private CustomTextField txtValor;
    private CustomTextField txtDuracaoMeses;
    private JLabel lblStatus;
    
    // Botões
    private CustomButton btnNovo;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnAtivar;
    private CustomButton btnInativar;
    private CustomButton btnSalvar;
    private CustomButton btnCancelar;
    private CustomButton btnBuscar;
    
    // Controle de estado
    private Long currentPlanoId;
    private boolean isEditMode;
    
    // Mensagens
    private static final String MSG_SUCCESS_SAVE = "Plano cadastrado com sucesso!";
    private static final String MSG_SUCCESS_UPDATE = "Plano atualizado com sucesso!";
    private static final String MSG_SUCCESS_DELETE = "Plano excluído com sucesso!";
    private static final String MSG_SUCCESS_ATIVAR = "Plano ativado com sucesso!";
    private static final String MSG_SUCCESS_INATIVAR = "Plano inativado com sucesso!";
    
    public PlanoPanel() {
        this.apiClient = new ApiClient();
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        loadPlanos();
    }
    
    private void initializeUI() {
        // Split pane: tabela à esquerda, formulário à direita
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(PADDING_MEDIUM);
        splitPane.setBorder(null);
        splitPane.setBackground(BACKGROUND_COLOR);
        
        splitPane.setLeftComponent(createListPanel());
        formPanel = createFormPanel();
        splitPane.setRightComponent(formPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Oculta o formulário na inicialização
        hideFormPanel();
    }
    
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, PADDING_MEDIUM));
        panel.setBackground(BACKGROUND_COLOR);
        
        // Cabeçalho com título e busca
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel("Planos de Assinatura");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        txtBusca = new CustomTextField("Buscar por nome...", 20);
        btnBuscar = new CustomButton("[ ? ]", CustomButton.ButtonType.PRIMARY);
        btnBuscar.addActionListener(e -> buscarPlanos());
        searchPanel.add(txtBusca);
        searchPanel.add(btnBuscar);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Nome", "Valor", "Duração", "Status"};
        table = new CustomTable(colunas);
        table.setPreferredScrollableViewportSize(new Dimension(600, 400));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onPlanoSelected();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);
        
        btnNovo = new CustomButton("+ Novo", CustomButton.ButtonType.SUCCESS);
        btnEditar = new CustomButton("Editar", CustomButton.ButtonType.PRIMARY);
        btnExcluir = new CustomButton("X Excluir", CustomButton.ButtonType.DANGER);
        btnAtivar = new CustomButton("Ativar", CustomButton.ButtonType.SUCCESS);
        btnInativar = new CustomButton("Inativar", CustomButton.ButtonType.WARNING);
        
        btnNovo.addActionListener(e -> newPlano());
        btnEditar.addActionListener(e -> editPlano());
        btnExcluir.addActionListener(e -> deletePlano());
        btnAtivar.addActionListener(e -> ativarPlano());
        btnInativar.addActionListener(e -> inativarPlano());
        
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnAtivar.setEnabled(false);
        btnInativar.setEnabled(false);
        
        actionPanel.add(btnNovo);
        actionPanel.add(btnEditar);
        actionPanel.add(btnExcluir);
        actionPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        actionPanel.add(btnAtivar);
        actionPanel.add(btnInativar);
        
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
        JLabel lblFormTitle = new JLabel("Detalhes do Plano");
        lblFormTitle.setFont(FONT_SUBTITLE);
        lblFormTitle.setForeground(TEXT_PRIMARY);
        panel.add(lblFormTitle, BorderLayout.NORTH);
        
        // Campos do formulário
        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setBackground(CARD_BACKGROUND);
        
        // Nome
        formFields.add(createLabel("Nome do Plano *"));
        txtNome = new CustomTextField("Ex: Plano Premium", 30);
        formFields.add(createFieldComponent(txtNome));
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Descrição
        formFields.add(createLabel("Descrição"));
        txtDescricao = createTextArea();
        JScrollPane scrollDesc = new JScrollPane(txtDescricao);
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollDesc.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        formFields.add(scrollDesc);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Valor
        formFields.add(createLabel("Valor (R$) *"));
        txtValor = new CustomTextField("Ex: 150.00", 15);
        formFields.add(createFieldComponent(txtValor));
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Duração
        formFields.add(createLabel("Duração (meses) *"));
        txtDuracaoMeses = new CustomTextField("Ex: 12", 10);
        formFields.add(createFieldComponent(txtDuracaoMeses));
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Status
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        statusPanel.setBackground(CARD_BACKGROUND);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblStatusLabel = new JLabel("Status:");
        lblStatusLabel.setFont(FONT_REGULAR);
        lblStatusLabel.setForeground(TEXT_SECONDARY);
        
        lblStatus = new JLabel("ATIVO");
        lblStatus.setFont(FONT_SUBTITLE);
        lblStatus.setForeground(SUCCESS_COLOR);
        
        statusPanel.add(lblStatusLabel);
        statusPanel.add(lblStatus);
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formFields.add(statusPanel);
        
        panel.add(formFields, BorderLayout.CENTER);
        
        // Botões do formulário
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        btnSalvar = new CustomButton("Salvar", CustomButton.ButtonType.SUCCESS);
        btnCancelar = new CustomButton("Cancelar", CustomButton.ButtonType.DEFAULT);
        
        btnSalvar.addActionListener(e -> savePlano());
        btnCancelar.addActionListener(e -> cancelForm());
        
        buttonPanel.add(btnCancelar);
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
    
    private JComponent createFieldComponent(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        return component;
    }
    
    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea(4, 30);
        textArea.setFont(FONT_REGULAR);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBackground(PANEL_BACKGROUND);
        textArea.setCaretColor(TEXT_PRIMARY);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(PADDING_SMALL, PADDING_SMALL, PADDING_SMALL, PADDING_SMALL)
        ));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }
    
    private void loadPlanos() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando planos...",
            () -> {
                String response = apiClient.get("/planos");
                List<PlanoResponseDTO> planos = apiClient.fromJsonArray(response, PlanoResponseDTO.class);
                
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
                    MessageDialog.showError(this, "Erro ao carregar planos: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<PlanoResponseDTO> planos) {
        table.clearRows();
        for (PlanoResponseDTO plano : planos) {
            table.addRow(new Object[]{
                plano.getId(),
                plano.getNome(),
                currencyFormat.format(plano.getValor()),
                plano.getDuracaoMeses() + " meses",
                plano.getStatus()
            });
        }
    }
    
    private void onPlanoSelected() {
        if (!table.hasSelection()) {
            clearSelection();
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/planos/" + id);
                PlanoResponseDTO plano = apiClient.fromJson(response, PlanoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(plano);
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
    
    private void populateForm(PlanoResponseDTO plano) {
        currentPlanoId = plano.getId();
        txtNome.setText(plano.getNome());
        txtDescricao.setText(plano.getDescricao() != null ? plano.getDescricao() : "");
        txtValor.setText(plano.getValor().toString());
        txtDuracaoMeses.setText(plano.getDuracaoMeses().toString());
        
        lblStatus.setText(plano.getStatus());
        if ("ATIVO".equals(plano.getStatus())) {
            lblStatus.setForeground(SUCCESS_COLOR);
        } else {
            lblStatus.setForeground(ERROR_COLOR);
        }
        
        setFormEnabled(false);
        isEditMode = false;
    }
    
    private void newPlano() {
        clearForm();
        setFormEnabled(true);
        isEditMode = false;
        currentPlanoId = null;
        table.clearSelection();
        lblStatus.setText("ATIVO");
        lblStatus.setForeground(SUCCESS_COLOR);
        showFormPanel();
        txtNome.requestFocus();
        updateButtons();
    }
    
    private void editPlano() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um plano para editar.");
            return;
        }
        
        setFormEnabled(true);
        isEditMode = true;
        showFormPanel();
        txtNome.requestFocus();
        updateButtons();
    }
    
    private void savePlano() {
        if (!validateForm()) {
            return;
        }
        
        PlanoResponseDTO plano = new PlanoResponseDTO();
        plano.setNome(txtNome.getText().trim());
        plano.setDescricao(txtDescricao.getText().trim());
        plano.setValor(new BigDecimal(txtValor.getText().trim().replace(",", ".")));
        plano.setDuracaoMeses(Integer.parseInt(txtDuracaoMeses.getText().trim()));
        
        if (isEditMode) {
            plano.setId(currentPlanoId);
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando plano..." : "Cadastrando plano...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/planos/" + currentPlanoId, plano);
                } else {
                    apiClient.post("/planos", plano);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isEditMode ? MSG_SUCCESS_UPDATE : MSG_SUCCESS_SAVE);
                cancelForm();
                loadPlanos();
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
    
    private boolean validateForm() {
        txtNome.markAsValid();
        txtValor.markAsValid();
        txtDuracaoMeses.markAsValid();
        
        // Validar nome
        if (txtNome.getText().trim().isEmpty()) {
            txtNome.markAsInvalid();
            MessageDialog.showError(this, "O nome é obrigatório.");
            txtNome.requestFocus();
            return false;
        }
        
        // Validar valor
        String valorText = txtValor.getText().trim().replace(",", ".");
        try {
            BigDecimal valor = new BigDecimal(valorText);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                txtValor.markAsInvalid();
                MessageDialog.showError(this, "O valor deve ser maior que zero.");
                txtValor.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            txtValor.markAsInvalid();
            MessageDialog.showError(this, "Valor inválido. Use o formato: 99.99");
            txtValor.requestFocus();
            return false;
        }
        
        // Validar duração
        try {
            int duracao = Integer.parseInt(txtDuracaoMeses.getText().trim());
            if (duracao <= 0) {
                txtDuracaoMeses.markAsInvalid();
                MessageDialog.showError(this, "A duração deve ser maior que zero.");
                txtDuracaoMeses.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            txtDuracaoMeses.markAsInvalid();
            MessageDialog.showError(this, "Duração inválida. Informe um número inteiro.");
            txtDuracaoMeses.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void deletePlano() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um plano para excluir.");
            return;
        }
        
        if (!MessageDialog.showDeleteConfirmation(this)) {
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo plano...",
            () -> {
                apiClient.delete("/planos/" + id);
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_DELETE);
                clearSelection();
                loadPlanos();
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
    
    private void ativarPlano() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um plano para ativar.");
            return;
        }
        
        if ("ATIVO".equals(lblStatus.getText())) {
            MessageDialog.showInfo(this, "Este plano já está ativo.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Ativando plano...",
            () -> {
                apiClient.put("/planos/" + id + "/ativar", null);
                String response = apiClient.get("/planos/" + id);
                PlanoResponseDTO plano = apiClient.fromJson(response, PlanoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(plano);
                    updateButtons();
                });
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_ATIVAR);
                loadPlanos();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao ativar plano: " + error.getMessage());
                }
            }
        );
    }
    
    private void inativarPlano() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um plano para inativar.");
            return;
        }
        
        if ("INATIVO".equals(lblStatus.getText())) {
            MessageDialog.showInfo(this, "Este plano já está inativo.");
            return;
        }
        
        boolean confirm = MessageDialog.showConfirmation(this,
            "Tem certeza que deseja inativar este plano?\n" +
            "Planos inativos não estarão disponíveis para novas matrículas.",
            "Confirmar Inativação");
        
        if (!confirm) {
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Inativando plano...",
            () -> {
                apiClient.put("/planos/" + id + "/inativar", null);
                String response = apiClient.get("/planos/" + id);
                PlanoResponseDTO plano = apiClient.fromJson(response, PlanoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(plano);
                    updateButtons();
                });
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_INATIVAR);
                loadPlanos();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao inativar plano: " + error.getMessage());
                }
            }
        );
    }
    
    private void buscarPlanos() {
        String termo = txtBusca.getText().trim().toLowerCase();
        if (termo.isEmpty()) {
            loadPlanos();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando planos...",
            () -> {
                String response = apiClient.get("/planos");
                List<PlanoResponseDTO> todosPlanos = apiClient.fromJsonArray(response, PlanoResponseDTO.class);
                List<PlanoResponseDTO> filtrados = todosPlanos.stream()
                    .filter(p -> p.getNome().toLowerCase().contains(termo))
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
    
    private void cancelForm() {
        clearForm();
        setFormEnabled(false);
        isEditMode = false;
        currentPlanoId = null;
        hideFormPanel();
        if (table.hasSelection()) {
            onPlanoSelected();
        }
        updateButtons();
    }
    
    private void clearForm() {
        txtNome.setText("");
        txtDescricao.setText("");
        txtValor.setText("0.00");
        txtDuracaoMeses.setText("");
        lblStatus.setText("ATIVO");
        lblStatus.setForeground(SUCCESS_COLOR);
        
        txtNome.markAsValid();
        txtValor.markAsValid();
        txtDuracaoMeses.markAsValid();
    }
    
    private void clearSelection() {
        table.clearSelection();
        currentPlanoId = null;
        isEditMode = false;
        clearForm();
        setFormEnabled(false);
        updateButtons();
    }
    
    private void setFormEnabled(boolean enabled) {
        txtNome.setEditable(enabled);
        txtDescricao.setEditable(enabled);
        txtValor.setEditable(enabled);
        txtDuracaoMeses.setEditable(enabled);
        btnSalvar.setEnabled(enabled);
        btnCancelar.setEnabled(enabled);
    }
    
    private void updateButtons() {
        boolean hasSelection = table.hasSelection();
        boolean formEnabled = txtNome.isEditable();
        boolean isAtivo = "ATIVO".equals(lblStatus.getText());
        
        btnNovo.setEnabled(!formEnabled);
        btnEditar.setEnabled(hasSelection && !formEnabled);
        btnExcluir.setEnabled(hasSelection && !formEnabled);
        btnAtivar.setEnabled(hasSelection && !isAtivo && !formEnabled);
        btnInativar.setEnabled(hasSelection && isAtivo && !formEnabled);
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
