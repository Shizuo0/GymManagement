package com.example.demo.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.PlanoResponseDTO;
import com.example.demo.ui.GymManagementUI;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;
import static com.example.demo.ui.utils.UIConstants.BORDER_COLOR;
import static com.example.demo.ui.utils.UIConstants.CARD_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.FONT_LABEL;
import static com.example.demo.ui.utils.UIConstants.FONT_REGULAR;
import static com.example.demo.ui.utils.UIConstants.FONT_TITLE;
import static com.example.demo.ui.utils.UIConstants.PADDING_LARGE;
import static com.example.demo.ui.utils.UIConstants.PADDING_MEDIUM;
import static com.example.demo.ui.utils.UIConstants.PADDING_SMALL;
import static com.example.demo.ui.utils.UIConstants.PANEL_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;

public class PlanoPanel extends JPanel implements RefreshablePanel {
    
    private final ApiClient apiClient;
    private final NumberFormat currencyFormat;
    private CustomTable table;
    private CustomTextField txtBusca;
    private CustomButton btnNovo, btnEditar, btnExcluir, btnAtivar, btnInativar, btnAtualizar;
    
    public PlanoPanel() {
        this.apiClient = new ApiClient();
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        initComponents();
        setupLayout();
        loadPlanos();
    }
    
    private void initComponents() {
        setBackground(PANEL_BACKGROUND);
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        String[] columns = {"ID", "Nome", "Descrição", "Valor", "Duração", "Status"};
        table = new CustomTable(columns);
        table.setColumnWidth(0, 60);
        table.setColumnWidth(3, 120);
        table.setColumnWidth(4, 100);
        table.setColumnWidth(5, 100);
        table.centerColumn(0);
        table.centerColumn(3);
        table.centerColumn(4);
        table.centerColumn(5);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        txtBusca = new CustomTextField("Buscar plano por nome", 25);
        txtBusca.addActionListener(e -> buscarPlanos());
        
        btnNovo = CustomButton.createAddButton("Novo");
        btnEditar = CustomButton.createEditButton("Editar");
        btnExcluir = CustomButton.createDeleteButton("Excluir");
        btnAtivar = new CustomButton("✓ Ativar", CustomButton.ButtonType.SUCCESS);
        btnInativar = new CustomButton("✗ Inativar", CustomButton.ButtonType.WARNING);
        btnAtualizar = CustomButton.createRefreshButton("Atualizar");
        
        btnNovo.addActionListener(e -> showDialog(null));
        btnEditar.addActionListener(e -> editarPlano());
        btnExcluir.addActionListener(e -> excluirPlano());
        btnAtivar.addActionListener(e -> ativarPlano());
        btnInativar.addActionListener(e -> inativarPlano());
        btnAtualizar.addActionListener(e -> loadPlanos());
        
        updateButtonStates();
    }
    
    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        topPanel.setBackground(PANEL_BACKGROUND);
        
        JLabel title = new JLabel("Planos de Assinatura");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        topPanel.add(title, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(PANEL_BACKGROUND);
        searchPanel.add(txtBusca);
        CustomButton btnBuscar = CustomButton.createSearchButton("Buscar");
        btnBuscar.addActionListener(e -> buscarPlanos());
        searchPanel.add(btnBuscar);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_MEDIUM, PADDING_MEDIUM));
        buttonPanel.setBackground(PANEL_BACKGROUND);
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        buttonPanel.add(btnAtivar);
        buttonPanel.add(btnInativar);
        buttonPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        buttonPanel.add(btnAtualizar);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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
            () -> {},
            error -> {
                if (error instanceof ApiException apiException) {
                    MessageDialog.showError(this, apiException.getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar planos: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<PlanoResponseDTO> planos) {
        table.clearRows();
        for (PlanoResponseDTO plano : planos) {
            String descricaoExibida = plano.getDescricao() != null && !plano.getDescricao().isEmpty() 
                ? (plano.getDescricao().length() > 50 
                    ? plano.getDescricao().substring(0, 50) + "..." 
                    : plano.getDescricao())
                : "-";
            
            table.addRow(new Object[]{
                plano.getId(),
                plano.getNome(),
                descricaoExibida,
                currencyFormat.format(plano.getValor()),
                plano.getDuracaoMeses() + " meses",
                plano.getStatus()
            });
        }
        updateButtonStates();
    }
    
    private void editarPlano() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um plano para editar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados do plano...",
            () -> {
                String response = apiClient.get("/planos/" + id);
                PlanoResponseDTO plano = apiClient.fromJson(response, PlanoResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    showDialog(plano);
                });
            },
            () -> {},
            error -> {
                if (error instanceof ApiException apiException) {
                    MessageDialog.showError(this, apiException.getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar plano: " + (error != null ? error.getMessage() : "Erro desconhecido"));
                }
            }
        );
    }
    
    private void excluirPlano() {
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
                MessageDialog.showSuccess(this, "Plano excluído com sucesso!");
                loadPlanos();
                notifyParentToRefresh();
            },
            error -> {
                if (error instanceof ApiException apiException) {
                    MessageDialog.showError(this, apiException.getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao excluir plano: " + (error != null ? error.getMessage() : "Erro desconhecido"));
                }
            }
        );
    }
    
    private void ativarPlano() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um plano para ativar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        String status = (String) table.getSelectedRowValue(4);
        
        if ("ATIVO".equals(status)) {
            MessageDialog.showInfo(this, "Este plano já está ativo.");
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Ativando plano...",
            () -> {
                apiClient.put("/planos/" + id + "/ativar", null);
            },
            () -> {
                MessageDialog.showSuccess(this, "Plano ativado com sucesso!");
                loadPlanos();
                notifyParentToRefresh();
            },
            error -> {
                if (error instanceof ApiException apiException) {
                    MessageDialog.showError(this, apiException.getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao ativar plano: " + (error != null ? error.getMessage() : "Erro desconhecido"));
                }
            }
        );
    }
    
    private void inativarPlano() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um plano para inativar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        String status = (String) table.getSelectedRowValue(4);
        
        if ("INATIVO".equals(status)) {
            MessageDialog.showInfo(this, "Este plano já está inativo.");
            return;
        }
        
        boolean confirm = MessageDialog.showConfirmation(this,
            "Tem certeza que deseja inativar este plano?\nPlanos inativos não estarão disponíveis para novas matrículas.",
            "Confirmar Inativação");
        
        if (!confirm) {
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Inativando plano...",
            () -> {
                apiClient.put("/planos/" + id + "/inativar", null);
            },
            () -> {
                MessageDialog.showSuccess(this, "Plano inativado com sucesso!");
                loadPlanos();
                notifyParentToRefresh();
            },
            error -> {
                if (error instanceof ApiException apiException) {
                    MessageDialog.showError(this, apiException.getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao inativar plano: " + (error != null ? error.getMessage() : "Erro desconhecido"));
                }
            }
        );
    }
    
    private void showDialog(PlanoResponseDTO plano) {
        boolean isNew = (plano == null);
        
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            isNew ? "Novo Plano" : "Editar Plano",
            true
        );
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        CustomTextField txtNome = new CustomTextField("Nome do plano", 30);
        JTextArea txtDescricao = new JTextArea(4, 30);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setFont(FONT_REGULAR);
        txtDescricao.setBackground(CARD_BACKGROUND);
        txtDescricao.setForeground(TEXT_PRIMARY);
        txtDescricao.setCaretColor(TEXT_PRIMARY);
        txtDescricao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_SMALL, PADDING_SMALL, PADDING_SMALL)
        ));
        JScrollPane scrollDesc = new JScrollPane(txtDescricao);
        scrollDesc.setBorder(null);
        
        CustomTextField txtValor = new CustomTextField("Ex: 150.00", 15);
        CustomTextField txtDuracao = new CustomTextField("Ex: 12", 10);
        
        if (!isNew) {
            txtNome.setText(plano.getNome());
            txtDescricao.setText(plano.getDescricao() != null ? plano.getDescricao() : "");
            txtValor.setText(plano.getValor().toString());
            txtDuracao.setText(plano.getDuracaoMeses().toString());
        }
        
        formPanel.add(createFieldPanel("Nome *", txtNome));
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        formPanel.add(createFieldPanel("Descrição", scrollDesc));
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        formPanel.add(createFieldPanel("Valor (R$) *", txtValor));
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        formPanel.add(createFieldPanel("Duração (meses) *", txtDuracao));
        formPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        CustomButton btnCancelar = CustomButton.createCancelButton("Cancelar");
        CustomButton btnSalvar = CustomButton.createSaveButton("Salvar");
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnSalvar.addActionListener(e -> {
            txtNome.markAsValid();
            txtValor.markAsValid();
            txtDuracao.markAsValid();
            
            if (txtNome.getText().trim().isEmpty()) {
                txtNome.markAsInvalid();
                MessageDialog.showError(dialog, "O nome é obrigatório.");
                txtNome.requestFocus();
                return;
            }
            
            String valorText = txtValor.getText().trim().replace(",", ".");
            BigDecimal valor;
            try {
                valor = new BigDecimal(valorText);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    txtValor.markAsInvalid();
                    MessageDialog.showError(dialog, "O valor deve ser maior que zero.");
                    txtValor.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                txtValor.markAsInvalid();
                MessageDialog.showError(dialog, "Valor inválido. Use o formato: 99.99");
                txtValor.requestFocus();
                return;
            }
            
            int duracao;
            try {
                duracao = Integer.parseInt(txtDuracao.getText().trim());
                if (duracao <= 0) {
                    txtDuracao.markAsInvalid();
                    MessageDialog.showError(dialog, "A duração deve ser maior que zero.");
                    txtDuracao.requestFocus();
                    return;
                }
            } catch (NumberFormatException ex) {
                txtDuracao.markAsInvalid();
                MessageDialog.showError(dialog, "Duração inválida. Informe um número inteiro.");
                txtDuracao.requestFocus();
                return;
            }
            
            PlanoResponseDTO dto = new PlanoResponseDTO();
            dto.setNome(txtNome.getText().trim());
            dto.setDescricao(txtDescricao.getText().trim());
            dto.setValor(valor);
            dto.setDuracaoMeses(duracao);
            
            if (!isNew) {
                dto.setId(plano.getId());
            }
            
            dialog.dispose();
            salvarPlano(dto, isNew);
        });
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        
        formPanel.add(buttonPanel);
        
        dialog.add(formPanel);
        dialog.setSize(550, 500);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    private JPanel createFieldPanel(String labelText, Component field) {
        JPanel panel = new JPanel(new BorderLayout(PADDING_SMALL, PADDING_SMALL));
        panel.setBackground(CARD_BACKGROUND);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, field instanceof JScrollPane ? 120 : 80));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void salvarPlano(PlanoResponseDTO dto, boolean isNew) {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isNew ? "Cadastrando plano..." : "Atualizando plano...",
            () -> {
                if (isNew) {
                    apiClient.post("/planos", dto);
                } else {
                    apiClient.put("/planos/" + dto.getId(), dto);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isNew ? "Plano cadastrado com sucesso!" : "Plano atualizado com sucesso!");
                loadPlanos();
                notifyParentToRefresh();
            },
            error -> {
                if (error instanceof ApiException apiException) {
                    MessageDialog.showError(this, apiException.getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar plano: " + (error != null ? error.getMessage() : "Erro desconhecido"));
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
            () -> {},
            error -> {
                if (error instanceof ApiException apiException) {
                    MessageDialog.showError(this, apiException.getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao buscar planos: " + (error != null ? error.getMessage() : "Erro desconhecido"));
                }
            }
        );
    }
    
    private void updateButtonStates() {
        boolean hasSelection = table.hasSelection();
        btnEditar.setEnabled(hasSelection);
        btnExcluir.setEnabled(hasSelection);
        btnAtivar.setEnabled(hasSelection);
        btnInativar.setEnabled(hasSelection);
    }
    
    @Override
    public void refreshData() {
        loadPlanos();
    }
    
    private void notifyParentToRefresh() {
        Component parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof GymManagementUI gym) {
            gym.notifyDataChanged();
        }
    }
}
