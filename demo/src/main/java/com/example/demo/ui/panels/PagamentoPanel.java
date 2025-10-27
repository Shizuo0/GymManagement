package com.example.demo.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.example.demo.dto.MatriculaResponseDTO;
import com.example.demo.dto.PagamentoRequestDTO;
import com.example.demo.dto.PagamentoResponseDTO;
import com.example.demo.enums.MatriculaStatus;
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
import static com.example.demo.ui.utils.UIConstants.BORDER_COLOR;
import static com.example.demo.ui.utils.UIConstants.CARD_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.FONT_REGULAR;
import static com.example.demo.ui.utils.UIConstants.FONT_TITLE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_SAVE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_UPDATE;
import static com.example.demo.ui.utils.UIConstants.PADDING_LARGE;
import static com.example.demo.ui.utils.UIConstants.PADDING_MEDIUM;
import static com.example.demo.ui.utils.UIConstants.PADDING_SMALL;
import static com.example.demo.ui.utils.UIConstants.PANEL_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;

public class PagamentoPanel extends JPanel implements RefreshablePanel {
    
    private final ApiClient apiClient;
    private CustomTable table;
    private CustomButton btnNovo, btnEditar, btnExcluir, btnAtualizar;
    
    public PagamentoPanel() {
        this.apiClient = new ApiClient();
        initComponents();
        setupLayout();
        loadPagamentos();
    }
    
    private void initComponents() {
        setBackground(PANEL_BACKGROUND);
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        String[] columns = {"ID", "Aluno", "Plano", "Data Pag.", "Valor", "Forma Pag."};
        table = new CustomTable(columns);
        table.setColumnWidth(0, 60);
        table.setColumnWidth(3, 100);
        table.setColumnWidth(4, 100);
        table.setColumnWidth(5, 130);
        table.centerColumn(0);
        table.centerColumn(3);
        table.centerColumn(4);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });
        
        btnNovo = CustomButton.createAddButton("Novo");
        btnEditar = CustomButton.createEditButton("Editar");
        btnExcluir = CustomButton.createDeleteButton("Excluir");
        btnAtualizar = CustomButton.createRefreshButton("Atualizar");
        
        btnNovo.addActionListener(e -> showDialog(null));
        btnEditar.addActionListener(e -> editarPagamento());
        btnExcluir.addActionListener(e -> excluirPagamento());
        btnAtualizar.addActionListener(e -> loadPagamentos());
        
        updateButtonStates();
    }
    
    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        topPanel.setBackground(PANEL_BACKGROUND);
        
        JLabel title = new JLabel("Gerenciamento de Pagamentos");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        topPanel.add(title, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_MEDIUM, PADDING_MEDIUM));
        buttonPanel.setBackground(PANEL_BACKGROUND);
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        buttonPanel.add(btnAtualizar);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadPagamentos() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando pagamentos...",
            () -> {
                String response = apiClient.get("/pagamentos");
                List<PagamentoResponseDTO> pagamentos = apiClient.fromJsonArray(response, PagamentoResponseDTO.class);
                SwingUtilities.invokeLater(() -> updateTable(pagamentos));
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar pagamentos: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<PagamentoResponseDTO> pagamentos) {
        table.clearRows();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (PagamentoResponseDTO pag : pagamentos) {
            Object[] row = {
                pag.getIdPagamento(),
                pag.getNomeAluno(),
                pag.getNomePlano(),
                pag.getDataPagamento() != null ? pag.getDataPagamento().format(formatter) : "",
                pag.getValorPago(),
                formatFormaPagamento(pag.getFormaPagamento())
            };
            table.addRow(row);
        }
        updateButtonStates();
    }
    
    private String formatFormaPagamento(String forma) {
        if (forma == null) return "";
        return switch (forma) {
            case "CARTAO_CREDITO" -> "Cartão Crédito";
            case "CARTAO_DEBITO" -> "Cartão Débito";
            case "DINHEIRO" -> "Dinheiro";
            case "PIX" -> "PIX";
            default -> forma;
        };
    }
    
    private void editarPagamento() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um pagamento para editar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/pagamentos/" + id);
                PagamentoResponseDTO pag = apiClient.fromJson(response, PagamentoResponseDTO.class);
                SwingUtilities.invokeLater(() -> showDialog(pag));
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar: " + error.getMessage());
                }
            }
        );
    }
    
    private void excluirPagamento() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um pagamento para excluir.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        String aluno = (String) table.getSelectedRowValue(1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir o pagamento do aluno " + aluno + "?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo pagamento...",
            () -> apiClient.delete("/pagamentos/" + id),
            () -> {
                MessageDialog.showSuccess(this, "Pagamento excluído com sucesso!");
                loadPagamentos();
                notifyParentToRefresh();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao excluir: " + error.getMessage());
                }
            }
        );
    }
    
    private void showDialog(PagamentoResponseDTO pagamento) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            pagamento == null ? "Novo Pagamento" : "Editar Pagamento", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        // Carregar matrículas
        List<MatriculaItem> matriculas = carregarMatriculas();
        
        // Matrícula
        JLabel lblMatricula = new JLabel("Matrícula *");
        lblMatricula.setForeground(TEXT_PRIMARY);
        lblMatricula.setFont(FONT_REGULAR);
        lblMatricula.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomComboBox<MatriculaItem> cmbMatricula = new CustomComboBox<>(matriculas.toArray(new MatriculaItem[0]));
        cmbMatricula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbMatricula.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (pagamento != null) {
            for (int i = 0; i < cmbMatricula.getItemCount(); i++) {
                if (cmbMatricula.getItemAt(i).id.equals(pagamento.getIdMatricula())) {
                    cmbMatricula.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        content.add(lblMatricula);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(cmbMatricula);
        content.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Data Pagamento
        JLabel lblData = new JLabel("Data Pagamento *");
        lblData.setForeground(TEXT_PRIMARY);
        lblData.setFont(FONT_REGULAR);
        lblData.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomDatePicker dpData = new CustomDatePicker();
        dpData.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (pagamento != null && pagamento.getDataPagamento() != null) {
            dpData.setLocalDate(pagamento.getDataPagamento());
        } else {
            dpData.setLocalDate(LocalDate.now());
        }
        
        content.add(lblData);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(dpData);
        content.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Valor Pago
        JLabel lblValor = new JLabel("Valor Pago *");
        lblValor.setForeground(TEXT_PRIMARY);
        lblValor.setFont(FONT_REGULAR);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomTextField txtValor = new CustomTextField("", 30);
        txtValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (pagamento != null && pagamento.getValorPago() != null) {
            txtValor.setText(pagamento.getValorPago().toString());
        }
        
        content.add(lblValor);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(txtValor);
        content.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Forma de Pagamento
        JLabel lblForma = new JLabel("Forma de Pagamento *");
        lblForma.setForeground(TEXT_PRIMARY);
        lblForma.setFont(FONT_REGULAR);
        lblForma.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] formasPagamento = {"DINHEIRO", "CARTAO_CREDITO", "CARTAO_DEBITO", "PIX"};
        CustomComboBox<String> cmbForma = new CustomComboBox<>(formasPagamento);
        cmbForma.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbForma.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (pagamento != null && pagamento.getFormaPagamento() != null) {
            cmbForma.setSelectedItem(pagamento.getFormaPagamento());
        }
        
        content.add(lblForma);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(cmbForma);
        content.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        btnPanel.setBackground(CARD_BACKGROUND);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomButton btnCancelar = CustomButton.createCancelButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        CustomButton btnSalvar = CustomButton.createSaveButton("Salvar");
        btnSalvar.addActionListener(e -> {
            MatriculaItem selectedMatricula = (MatriculaItem) cmbMatricula.getSelectedItem();
            if (selectedMatricula == null) {
                MessageDialog.showWarning(dialog, "Selecione uma matrícula!");
                return;
            }
            
            LocalDate dataPagamento = dpData.getLocalDate();
            if (dataPagamento == null) {
                MessageDialog.showWarning(dialog, "Selecione a data de pagamento!");
                return;
            }
            
            String valorStr = txtValor.getText().trim();
            if (valorStr.isEmpty()) {
                MessageDialog.showWarning(dialog, "Informe o valor pago!");
                return;
            }
            
            BigDecimal valorPago;
            try {
                valorPago = new BigDecimal(valorStr);
                if (valorPago.compareTo(BigDecimal.ZERO) <= 0) {
                    MessageDialog.showWarning(dialog, "Valor deve ser maior que zero!");
                    return;
                }
            } catch (NumberFormatException ex) {
                MessageDialog.showWarning(dialog, "Valor inválido!");
                return;
            }
            
            String formaPagamento = (String) cmbForma.getSelectedItem();
            
            // Criar request
            PagamentoRequestDTO request = new PagamentoRequestDTO();
            request.setIdMatricula(selectedMatricula.id);
            request.setDataPagamento(dataPagamento);
            request.setValorPago(valorPago);
            request.setFormaPagamento(formaPagamento);
            
            salvarPagamento(request, pagamento);
            dialog.dispose();
        });
        
        btnPanel.add(btnCancelar);
        btnPanel.add(btnSalvar);
        content.add(btnPanel);
        
        dialog.add(content, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private List<MatriculaItem> carregarMatriculas() {
        try {
            String response = apiClient.get("/matriculas");
            List<MatriculaResponseDTO> matriculas = apiClient.fromJsonArray(response, MatriculaResponseDTO.class);
            return matriculas.stream()
                .filter(m -> m.getStatus() == MatriculaStatus.ATIVA)
                .map(MatriculaItem::new)
                .collect(Collectors.toList());
        } catch (Exception e) {
            MessageDialog.showError(this, "Erro ao carregar matrículas: " + e.getMessage());
            return List.of();
        }
    }
    
    private void salvarPagamento(PagamentoRequestDTO request, PagamentoResponseDTO existing) {
        boolean isNew = existing == null;
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isNew ? "Cadastrando..." : "Atualizando...",
            () -> {
                if (isNew) {
                    apiClient.post("/pagamentos", request);
                } else {
                    apiClient.put("/pagamentos/" + existing.getIdPagamento(), request);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isNew ? MSG_SUCCESS_SAVE : MSG_SUCCESS_UPDATE);
                loadPagamentos();
                notifyParentToRefresh();
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateButtonStates() {
        boolean hasSelection = table.hasSelection();
        btnEditar.setEnabled(hasSelection);
        btnExcluir.setEnabled(hasSelection);
    }
    
    // ========== REFRESH E NOTIFICAÇÕES ==========
    
    @Override
    public void refreshData() {
        loadPagamentos();
    }
    
    private void notifyParentToRefresh() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof GymManagementUI)) {
            parent = parent.getParent();
        }
        
        if (parent instanceof GymManagementUI) {
            ((GymManagementUI) parent).notifyDataChanged();
        }
    }
    
    // ========== INNER CLASS ==========
    
    private static class MatriculaItem {
        Long id;
        String display;
        
        MatriculaItem(MatriculaResponseDTO dto) {
            this.id = dto.getId();
            this.display = dto.getNomeAluno() + " - " + dto.getNomePlano();
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
}
