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
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.AlunoDTO;
import com.example.demo.ui.GymManagementUI;
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
import static com.example.demo.ui.utils.UIConstants.FONT_TITLE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_DELETE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_SAVE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_UPDATE;
import static com.example.demo.ui.utils.UIConstants.PADDING_LARGE;
import static com.example.demo.ui.utils.UIConstants.PADDING_MEDIUM;
import static com.example.demo.ui.utils.UIConstants.PADDING_SMALL;
import static com.example.demo.ui.utils.UIConstants.PANEL_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;
import com.example.demo.ui.utils.ValidationUtils;

public class AlunoPanel extends JPanel implements RefreshablePanel {
    
    private final ApiClient apiClient;
    private CustomTable table;
    private CustomTextField txtBusca;
    private CustomButton btnNovo, btnEditar, btnExcluir, btnAtualizar;
    
    public AlunoPanel() {
        this.apiClient = new ApiClient();
        initComponents();
        setupLayout();
        loadAlunos();
    }
    
    private void initComponents() {
        setBackground(PANEL_BACKGROUND);
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        String[] columns = {"ID", "Nome", "CPF", "Data de Ingresso"};
        table = new CustomTable(columns);
        table.setColumnWidth(0, 60);
        table.setColumnWidth(2, 150);
        table.setColumnWidth(3, 150);
        table.centerColumn(0);
        table.centerColumn(2);
        table.centerColumn(3);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        txtBusca = new CustomTextField("Buscar aluno por nome", 25);
        txtBusca.addActionListener(e -> buscarAlunos());
        
        btnNovo = CustomButton.createAddButton("Novo");
        btnEditar = CustomButton.createEditButton("Editar");
        btnExcluir = CustomButton.createDeleteButton("Excluir");
        btnAtualizar = CustomButton.createRefreshButton("Atualizar");
        
        btnNovo.addActionListener(e -> showDialog(null));
        btnEditar.addActionListener(e -> editarAluno());
        btnExcluir.addActionListener(e -> excluirAluno());
        btnAtualizar.addActionListener(e -> loadAlunos());
        
        updateButtonStates();
    }
    
    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        topPanel.setBackground(PANEL_BACKGROUND);
        
        JLabel title = new JLabel("Gerenciamento de Alunos");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        topPanel.add(title, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(PANEL_BACKGROUND);
        searchPanel.add(txtBusca);
        CustomButton btnBuscar = CustomButton.createSearchButton("Buscar");
        btnBuscar.addActionListener(e -> buscarAlunos());
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
        buttonPanel.add(btnAtualizar);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
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
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar alunos: " + error.getMessage());
                }
            }
        );
    }
    
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
    
    private void editarAluno() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um aluno para editar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados do aluno...",
            () -> {
                String response = apiClient.get("/alunos/" + id);
                AlunoDTO aluno = apiClient.fromJson(response, AlunoDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    showDialog(aluno);
                });
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar aluno: " + error.getMessage());
                }
            }
        );
    }
    
    private void excluirAluno() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um aluno para excluir.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        if (!MessageDialog.showDeleteConfirmation(this)) {
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo aluno...",
            () -> {
                apiClient.delete("/alunos/" + id);
            },
            () -> {
                MessageDialog.showSuccess(this, MSG_SUCCESS_DELETE);
                loadAlunos();
                notifyParentToRefresh();
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
    
    private void showDialog(AlunoDTO aluno) {
        boolean isNew = (aluno == null);
        
        JDialog dialog = new JDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            isNew ? "Novo Aluno" : "Editar Aluno",
            true
        );
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        CustomTextField txtNome = new CustomTextField("Nome completo do aluno", 30);
        CustomTextField txtCPF = CustomTextField.createCPFField("000.000.000-00");
        CustomDatePicker datePicker = new CustomDatePicker(LocalDate.now());
        
        if (!isNew) {
            txtNome.setText(aluno.getNome());
            txtCPF.setText(aluno.getCpf());
            if (aluno.getDataIngresso() != null) {
                datePicker.setLocalDate(aluno.getDataIngresso());
            }
        }
        
        formPanel.add(createFieldPanel("Nome *", txtNome));
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        formPanel.add(createFieldPanel("CPF *", txtCPF));
        formPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        formPanel.add(createFieldPanel("Data de Ingresso *", datePicker));
        formPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_MEDIUM, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        
        CustomButton btnCancelar = CustomButton.createCancelButton("Cancelar");
        CustomButton btnSalvar = CustomButton.createSaveButton("Salvar");
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnSalvar.addActionListener(e -> {
            txtNome.markAsValid();
            txtCPF.markAsValid();
            
            if (txtNome.getText().trim().isEmpty()) {
                txtNome.markAsInvalid();
                MessageDialog.showError(dialog, "O nome é obrigatório.");
                txtNome.requestFocus();
                return;
            }
            
            if (!ValidationUtils.validateCPFField(txtCPF)) {
                txtCPF.markAsInvalid();
                txtCPF.requestFocus();
                return;
            }
            
            AlunoDTO dto = new AlunoDTO();
            dto.setNome(txtNome.getText().trim());
            dto.setCpf(ValidationUtils.unformatCPF(txtCPF.getText()));
            dto.setDataIngresso(datePicker.getLocalDate());
            
            if (!isNew) {
                dto.setIdAluno(aluno.getIdAluno());
            }
            
            dialog.dispose();
            salvarAluno(dto, isNew);
        });
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        
        formPanel.add(buttonPanel);
        
        dialog.add(formPanel);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    private JPanel createFieldPanel(String labelText, Component field) {
        JPanel panel = new JPanel(new BorderLayout(PADDING_SMALL, PADDING_SMALL));
        panel.setBackground(CARD_BACKGROUND);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void salvarAluno(AlunoDTO dto, boolean isNew) {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isNew ? "Cadastrando aluno..." : "Atualizando aluno...",
            () -> {
                if (isNew) {
                    apiClient.post("/alunos", dto);
                } else {
                    apiClient.put("/alunos/" + dto.getIdAluno(), dto);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isNew ? MSG_SUCCESS_SAVE : MSG_SUCCESS_UPDATE);
                loadAlunos();
                notifyParentToRefresh();
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
                
                List<AlunoDTO> filtered = alunos.stream()
                    .filter(a -> a.getNome().toLowerCase().contains(termo.toLowerCase()))
                    .toList();
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtered);
                    if (filtered.isEmpty()) {
                        MessageDialog.showInfo(this, "Nenhum aluno encontrado.");
                    }
                });
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao buscar alunos: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateButtonStates() {
        boolean hasSelection = table.hasSelection();
        btnEditar.setEnabled(hasSelection);
        btnExcluir.setEnabled(hasSelection);
    }
    
    @Override
    public void refreshData() {
        loadAlunos();
    }
    
    private void notifyParentToRefresh() {
        Component parent = SwingUtilities.getWindowAncestor(this);
        if (parent instanceof GymManagementUI) {
            ((GymManagementUI) parent).notifyDataChanged();
        }
    }
}
