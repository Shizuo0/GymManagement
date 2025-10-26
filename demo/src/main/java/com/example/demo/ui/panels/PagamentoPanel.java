package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.MatriculaResponseDTO;
import com.example.demo.dto.PagamentoResponseDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomComboBox;
import com.example.demo.ui.components.CustomDatePicker;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;

/**
 * Panel para gerenciamento de Pagamentos
 */
public class PagamentoPanel extends JPanel {
    
    private final ApiClient apiClient;
    private final DateTimeFormatter dateFormatter;
    private final NumberFormat currencyFormat;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomComboBox<String> cmbFiltroForma;
    private CustomTextField txtBusca;
    
    // Painéis
    private JSplitPane splitPane;
    private JPanel formPanel;
    
    // Componentes do formulário
    private CustomComboBox<MatriculaItem> cmbMatricula;
    private CustomDatePicker datePickerPagamento;
    private CustomTextField txtValor;
    private CustomComboBox<String> cmbFormaPagamento;
    private JLabel lblInfoMatricula;
    private JLabel lblTotalPago;
    
    // Botões
    private CustomButton btnNovo;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnHistorico;
    private CustomButton btnSalvar;
    private CustomButton btnCancelar;
    private CustomButton btnBuscar;
    
    // Controle de estado
    private Long currentPagamentoId;
    private boolean isEditMode;
    
    // Mensagens
    private static final String MSG_SUCCESS_SAVE = "Pagamento registrado com sucesso!";
    private static final String MSG_SUCCESS_UPDATE = "Pagamento atualizado com sucesso!";
    private static final String MSG_SUCCESS_DELETE = "Pagamento excluído com sucesso!";
    
    // Formas de pagamento
    private static final String[] FORMAS_PAGAMENTO = {
        "PIX", "DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "TRANSFERENCIA"
    };
    
    public PagamentoPanel() {
        this.apiClient = new ApiClient();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        loadPagamentos();
        loadMatriculas();
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
        
        // Cabeçalho com título e filtros
        JPanel headerPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel("Pagamentos");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        // Painel de busca e filtros
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblFiltro = new JLabel("Forma:");
        lblFiltro.setFont(FONT_REGULAR);
        lblFiltro.setForeground(TEXT_PRIMARY);
        
        String[] filtrosForma = new String[FORMAS_PAGAMENTO.length + 1];
        filtrosForma[0] = "Todas";
        System.arraycopy(FORMAS_PAGAMENTO, 0, filtrosForma, 1, FORMAS_PAGAMENTO.length);
        
        cmbFiltroForma = new CustomComboBox<>(filtrosForma);
        cmbFiltroForma.setFont(FONT_REGULAR);
        cmbFiltroForma.addActionListener(e -> filtrarPorForma());
        
        txtBusca = new CustomTextField("Buscar por aluno...", 15);
        btnBuscar = new CustomButton("[ ? ]", CustomButton.ButtonType.PRIMARY);
        btnBuscar.addActionListener(e -> buscarPagamentos());
        
        searchPanel.add(lblFiltro);
        searchPanel.add(cmbFiltroForma);
        searchPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        searchPanel.add(txtBusca);
        searchPanel.add(btnBuscar);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Aluno", "Plano", "Data", "Valor", "Forma"};
        table = new CustomTable(colunas);
        table.setPreferredScrollableViewportSize(new Dimension(700, 400));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onPagamentoSelected();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        actionPanel.setBackground(BACKGROUND_COLOR);
        
        btnNovo = new CustomButton("+ Novo Pagamento", CustomButton.ButtonType.SUCCESS);
        btnEditar = new CustomButton("Editar", CustomButton.ButtonType.PRIMARY);
        btnExcluir = new CustomButton("X Excluir", CustomButton.ButtonType.DANGER);
        btnHistorico = new CustomButton("Ver Histórico", CustomButton.ButtonType.DEFAULT);
        
        btnNovo.addActionListener(e -> newPagamento());
        btnEditar.addActionListener(e -> editPagamento());
        btnExcluir.addActionListener(e -> deletePagamento());
        btnHistorico.addActionListener(e -> mostrarHistorico());
        
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);
        btnHistorico.setEnabled(false);
        
        actionPanel.add(btnNovo);
        actionPanel.add(btnEditar);
        actionPanel.add(btnExcluir);
        actionPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        actionPanel.add(btnHistorico);
        
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
        JLabel lblFormTitle = new JLabel("Registro de Pagamento");
        lblFormTitle.setFont(FONT_SUBTITLE);
        lblFormTitle.setForeground(TEXT_PRIMARY);
        panel.add(lblFormTitle, BorderLayout.NORTH);
        
        // Campos do formulário
        JPanel formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setBackground(CARD_BACKGROUND);
        
        // Matrícula
        formFields.add(createLabel("Matrícula *"));
        cmbMatricula = new CustomComboBox<>();
        cmbMatricula.setFont(FONT_REGULAR);
        cmbMatricula.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbMatricula.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbMatricula.addActionListener(e -> atualizarInfoMatricula());
        formFields.add(cmbMatricula);
        formFields.add(Box.createVerticalStrut(PADDING_SMALL));
        
        // Info da matrícula
        lblInfoMatricula = new JLabel(" ");
        lblInfoMatricula.setFont(FONT_SMALL);
        lblInfoMatricula.setForeground(TEXT_SECONDARY);
        lblInfoMatricula.setAlignmentX(Component.LEFT_ALIGNMENT);
        formFields.add(lblInfoMatricula);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Data do Pagamento
        formFields.add(createLabel("Data do Pagamento *"));
        datePickerPagamento = new CustomDatePicker();
        datePickerPagamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePickerPagamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        formFields.add(datePickerPagamento);
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Valor Pago
        formFields.add(createLabel("Valor Pago (R$) *"));
        txtValor = new CustomTextField("Ex: 150.00", 15);
        formFields.add(createFieldComponent(txtValor));
        formFields.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Forma de Pagamento
        formFields.add(createLabel("Forma de Pagamento *"));
        cmbFormaPagamento = new CustomComboBox<>(FORMAS_PAGAMENTO);
        cmbFormaPagamento.setFont(FONT_REGULAR);
        cmbFormaPagamento.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbFormaPagamento.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        formFields.add(cmbFormaPagamento);
        formFields.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Total Pago (informativo)
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        totalPanel.setBackground(CARD_BACKGROUND);
        totalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblTotalLabel = new JLabel("Total Pago nesta Matrícula:");
        lblTotalLabel.setFont(FONT_REGULAR);
        lblTotalLabel.setForeground(TEXT_SECONDARY);
        
        lblTotalPago = new JLabel("R$ 0,00");
        lblTotalPago.setFont(FONT_SUBTITLE);
        lblTotalPago.setForeground(SUCCESS_COLOR);
        
        totalPanel.add(lblTotalLabel);
        totalPanel.add(lblTotalPago);
        totalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        formFields.add(totalPanel);
        
        panel.add(formFields, BorderLayout.CENTER);
        
        // Botões do formulário
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        btnSalvar = new CustomButton("Salvar", CustomButton.ButtonType.SUCCESS);
        btnCancelar = new CustomButton("Cancelar", CustomButton.ButtonType.DEFAULT);
        
        btnSalvar.addActionListener(e -> savePagamento());
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
    
    private void loadPagamentos() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando pagamentos...",
            () -> {
                String response = apiClient.get("/pagamentos");
                List<PagamentoResponseDTO> pagamentos = apiClient.fromJsonArray(response, PagamentoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(pagamentos);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar pagamentos: " + error.getMessage());
                }
            }
        );
    }
    
    private void loadMatriculas() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando matrículas...",
            () -> {
                String response = apiClient.get("/matriculas");
                List<MatriculaResponseDTO> matriculas = apiClient.fromJsonArray(response, MatriculaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    cmbMatricula.removeAllItems();
                    for (MatriculaResponseDTO matricula : matriculas) {
                        // Adicionar apenas matrículas ATIVAS
                        if ("ATIVA".equals(matricula.getStatus().toString())) {
                            cmbMatricula.addItem(new MatriculaItem(
                                matricula.getId(),
                                matricula.getNomeAluno(),
                                matricula.getNomePlano(),
                                matricula.getIdPlano()
                            ));
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
                    MessageDialog.showError(this, "Erro ao carregar matrículas: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<PagamentoResponseDTO> pagamentos) {
        table.clearRows();
        for (PagamentoResponseDTO pagamento : pagamentos) {
            table.addRow(new Object[]{
                pagamento.getIdPagamento(),
                pagamento.getNomeAluno(),
                pagamento.getNomePlano(),
                pagamento.getDataPagamento().format(dateFormatter),
                currencyFormat.format(pagamento.getValorPago()),
                formatarFormaPagamento(pagamento.getFormaPagamento())
            });
        }
    }
    
    private void onPagamentoSelected() {
        if (!table.hasSelection()) {
            clearSelection();
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/pagamentos/" + id);
                PagamentoResponseDTO pagamento = apiClient.fromJson(response, PagamentoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    populateForm(pagamento);
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
                    MessageDialog.showError(this, "Erro ao carregar pagamento: " + error.getMessage());
                }
            }
        );
    }
    
    private void populateForm(PagamentoResponseDTO pagamento) {
        currentPagamentoId = pagamento.getIdPagamento();
        
        // Selecionar matrícula
        for (int i = 0; i < cmbMatricula.getItemCount(); i++) {
            if (cmbMatricula.getItemAt(i).getId().equals(pagamento.getIdMatricula())) {
                cmbMatricula.setSelectedIndex(i);
                break;
            }
        }
        
        datePickerPagamento.setLocalDate(pagamento.getDataPagamento());
        txtValor.setText(pagamento.getValorPago().toString());
        cmbFormaPagamento.setSelectedItem(pagamento.getFormaPagamento());
        
        carregarTotalPago(pagamento.getIdMatricula());
        
        setFormEnabled(false);
        isEditMode = false;
    }
    
    private void newPagamento() {
        clearForm();
        setFormEnabled(true);
        isEditMode = false;
        currentPagamentoId = null;
        table.clearSelection();
        
        // Definir data como hoje
        datePickerPagamento.setLocalDate(LocalDate.now());
        
        showFormPanel();
        updateButtons();
    }
    
    private void editPagamento() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um pagamento para editar.");
            return;
        }
        
        setFormEnabled(true);
        isEditMode = true;
        showFormPanel();
        updateButtons();
    }
    
    private void savePagamento() {
        if (!validateForm()) {
            return;
        }
        
        MatriculaItem matricula = (MatriculaItem) cmbMatricula.getSelectedItem();
        
        Map<String, Object> pagamentoData = new HashMap<>();
        pagamentoData.put("idMatricula", matricula.getId());
        pagamentoData.put("dataPagamento", datePickerPagamento.getLocalDate().toString());
        pagamentoData.put("valorPago", new BigDecimal(txtValor.getText().trim().replace(",", ".")));
        pagamentoData.put("formaPagamento", cmbFormaPagamento.getSelectedItem().toString());
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando pagamento..." : "Registrando pagamento...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/pagamentos/" + currentPagamentoId, pagamentoData);
                } else {
                    apiClient.post("/pagamentos", pagamentoData);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isEditMode ? MSG_SUCCESS_UPDATE : MSG_SUCCESS_SAVE);
                cancelForm();
                loadPagamentos();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar pagamento: " + error.getMessage());
                }
            }
        );
    }
    
    private boolean validateForm() {
        // Validar seleção de matrícula
        if (cmbMatricula.getSelectedItem() == null) {
            MessageDialog.showError(this, "Selecione uma matrícula.");
            cmbMatricula.requestFocus();
            return false;
        }
        
        // Validar data
        LocalDate dataPagamento = datePickerPagamento.getLocalDate();
        if (dataPagamento == null) {
            MessageDialog.showError(this, "Informe a data do pagamento.");
            datePickerPagamento.requestFocus();
            return false;
        }
        
        // Validar que data não é futura
        if (dataPagamento.isAfter(LocalDate.now())) {
            MessageDialog.showError(this, "A data do pagamento não pode ser futura.");
            datePickerPagamento.requestFocus();
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
            txtValor.markAsValid();
        } catch (NumberFormatException e) {
            txtValor.markAsInvalid();
            MessageDialog.showError(this, "Valor inválido. Use o formato: 99.99");
            txtValor.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void deletePagamento() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um pagamento para excluir.");
            return;
        }
        
        if (!MessageDialog.showDeleteConfirmation(this)) {
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo pagamento...",
            () -> {
                apiClient.delete("/pagamentos/" + id);
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_DELETE);
                clearSelection();
                loadPagamentos();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao excluir pagamento: " + error.getMessage());
                }
            }
        );
    }
    
    private void mostrarHistorico() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um pagamento para ver o histórico da matrícula.");
            return;
        }
        
        Long idMatricula = getIdMatriculaFromSelection();
        if (idMatricula == null) return;
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando histórico...",
            () -> {
                String response = apiClient.get("/pagamentos/matricula/" + idMatricula);
                List<PagamentoResponseDTO> historico = apiClient.fromJsonArray(response, PagamentoResponseDTO.class);
                
                String totalResponse = apiClient.get("/pagamentos/matricula/" + idMatricula + "/total");
                BigDecimal total = new BigDecimal(totalResponse.replace("\"", ""));
                
                SwingUtilities.invokeLater(() -> {
                    exibirDialogoHistorico(historico, total);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar histórico: " + error.getMessage());
                }
            }
        );
    }
    
    private Long getIdMatriculaFromSelection() {
        try {
            Long id = (Long) table.getSelectedRowValue(0);
            String response = apiClient.get("/pagamentos/" + id);
            PagamentoResponseDTO pagamento = apiClient.fromJson(response, PagamentoResponseDTO.class);
            return pagamento.getIdMatricula();
        } catch (Exception e) {
            MessageDialog.showError(this, "Erro ao obter ID da matrícula.");
            return null;
        }
    }
    
    private void exibirDialogoHistorico(List<PagamentoResponseDTO> historico, BigDecimal total) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Histórico de Pagamentos", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        mainPanel.setBackground(CARD_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        // Informações da matrícula
        if (!historico.isEmpty()) {
            PagamentoResponseDTO primeiro = historico.get(0);
            JLabel lblInfo = new JLabel(
                "<html><b>Aluno:</b> " + primeiro.getNomeAluno() + 
                " | <b>Plano:</b> " + primeiro.getNomePlano() + "</html>"
            );
            lblInfo.setFont(FONT_SUBTITLE);
            lblInfo.setForeground(TEXT_PRIMARY);
            mainPanel.add(lblInfo, BorderLayout.NORTH);
        }
        
        // Tabela de histórico
        String[] colunas = {"Data", "Valor", "Forma de Pagamento"};
        CustomTable tableHistorico = new CustomTable(colunas);
        
        for (PagamentoResponseDTO pag : historico) {
            tableHistorico.addRow(new Object[]{
                pag.getDataPagamento().format(dateFormatter),
                currencyFormat.format(pag.getValorPago()),
                formatarFormaPagamento(pag.getFormaPagamento())
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(tableHistorico);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Total pago
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        totalPanel.setBackground(CARD_BACKGROUND);
        
        JLabel lblTotal = new JLabel("Total Pago: " + currencyFormat.format(total));
        lblTotal.setFont(FONT_TITLE);
        lblTotal.setForeground(SUCCESS_COLOR);
        totalPanel.add(lblTotal);
        
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void buscarPagamentos() {
        String termo = txtBusca.getText().trim().toLowerCase();
        if (termo.isEmpty()) {
            loadPagamentos();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando pagamentos...",
            () -> {
                String response = apiClient.get("/pagamentos");
                List<PagamentoResponseDTO> todosPagamentos = apiClient.fromJsonArray(response, PagamentoResponseDTO.class);
                List<PagamentoResponseDTO> filtrados = todosPagamentos.stream()
                    .filter(p -> p.getNomeAluno().toLowerCase().contains(termo))
                    .toList();
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtrados);
                    if (filtrados.isEmpty()) {
                        MessageDialog.showInfo(this, "Nenhum pagamento encontrado.");
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
                    MessageDialog.showError(this, "Erro ao buscar pagamentos: " + error.getMessage());
                }
            }
        );
    }
    
    private void filtrarPorForma() {
        String formaSelecionada = (String) cmbFiltroForma.getSelectedItem();
        
        if ("Todas".equals(formaSelecionada)) {
            loadPagamentos();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Filtrando pagamentos...",
            () -> {
                String response = apiClient.get("/pagamentos/forma-pagamento/" + formaSelecionada);
                List<PagamentoResponseDTO> pagamentos = apiClient.fromJsonArray(response, PagamentoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(pagamentos);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao filtrar pagamentos: " + error.getMessage());
                }
            }
        );
    }
    
    private void atualizarInfoMatricula() {
        MatriculaItem item = (MatriculaItem) cmbMatricula.getSelectedItem();
        if (item != null) {
            lblInfoMatricula.setText("Plano: " + item.getNomePlano());
            carregarTotalPago(item.getId());
        }
    }
    
    private void carregarTotalPago(Long idMatricula) {
        try {
            String response = apiClient.get("/pagamentos/matricula/" + idMatricula + "/total");
            BigDecimal total = new BigDecimal(response.replace("\"", ""));
            lblTotalPago.setText(currencyFormat.format(total));
        } catch (Exception e) {
            lblTotalPago.setText("R$ 0,00");
        }
    }
    
    private void cancelForm() {
        clearForm();
        setFormEnabled(false);
        isEditMode = false;
        currentPagamentoId = null;
        hideFormPanel();
        if (table.hasSelection()) {
            onPagamentoSelected();
        }
        updateButtons();
    }
    
    private void clearForm() {
        if (cmbMatricula.getItemCount() > 0) {
            cmbMatricula.setSelectedIndex(0);
        }
        datePickerPagamento.setLocalDate(LocalDate.now());
        txtValor.setText("0.00");
        cmbFormaPagamento.setSelectedIndex(0);
        lblInfoMatricula.setText(" ");
        lblTotalPago.setText("R$ 0,00");
        txtValor.markAsValid();
    }
    
    private void clearSelection() {
        table.clearSelection();
        currentPagamentoId = null;
        isEditMode = false;
        clearForm();
        setFormEnabled(false);
        updateButtons();
    }
    
    private void setFormEnabled(boolean enabled) {
        cmbMatricula.setEnabled(enabled);
        datePickerPagamento.setEnabled(enabled);
        txtValor.setEditable(enabled);
        cmbFormaPagamento.setEnabled(enabled);
        btnSalvar.setEnabled(enabled);
        btnCancelar.setEnabled(enabled);
    }
    
    private void updateButtons() {
        boolean hasSelection = table.hasSelection();
        boolean formEnabled = cmbMatricula.isEnabled();
        
        btnNovo.setEnabled(!formEnabled);
        btnEditar.setEnabled(hasSelection && !formEnabled);
        btnExcluir.setEnabled(hasSelection && !formEnabled);
        btnHistorico.setEnabled(hasSelection && !formEnabled);
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
    
    private String formatarFormaPagamento(String forma) {
        if (forma == null) return "";
        return forma.replace("_", " ");
    }
    
    // Classe auxiliar para ComboBox de matrículas
    private static class MatriculaItem {
        private final Long id;
        private final String nomeAluno;
        private final String nomePlano;
        
        public MatriculaItem(Long id, String nomeAluno, String nomePlano, Long idPlano) {
            this.id = id;
            this.nomeAluno = nomeAluno;
            this.nomePlano = nomePlano;
        }
        
        public Long getId() {
            return id;
        }
        
        public String getNomePlano() {
            return nomePlano;
        }
        
        @Override
        public String toString() {
            return nomeAluno + " - " + nomePlano;
        }
    }
}
