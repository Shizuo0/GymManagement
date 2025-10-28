package com.example.demo.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.example.demo.dto.InstrutorDTO;
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
import static com.example.demo.ui.utils.UIConstants.FONT_REGULAR;
import static com.example.demo.ui.utils.UIConstants.FONT_TITLE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_SAVE;
import static com.example.demo.ui.utils.UIConstants.MSG_SUCCESS_UPDATE;
import static com.example.demo.ui.utils.UIConstants.PADDING_LARGE;
import static com.example.demo.ui.utils.UIConstants.PADDING_MEDIUM;
import static com.example.demo.ui.utils.UIConstants.PADDING_SMALL;
import static com.example.demo.ui.utils.UIConstants.PANEL_BACKGROUND;
import static com.example.demo.ui.utils.UIConstants.TEXT_PRIMARY;

public class InstrutorPanel extends JPanel implements RefreshablePanel {
    
    private final ApiClient apiClient;
    private CustomTable table;
    private CustomTextField txtBusca;
    private CustomButton btnNovo, btnEditar, btnExcluir, btnAtualizar;
    
    public InstrutorPanel() {
        this.apiClient = new ApiClient();
        initComponents();
        setupLayout();
        loadInstrutores();
    }
    
    private void initComponents() {
        setBackground(PANEL_BACKGROUND);
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        String[] columns = {"ID", "Nome", "Especialidade"};
        table = new CustomTable(columns);
        table.setColumnWidth(0, 60);
        table.centerColumn(0);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });
        
        txtBusca = new CustomTextField("Buscar instrutor por nome ou especialidade", 25);
        txtBusca.addActionListener(e -> buscarInstrutores());
        
        btnNovo = CustomButton.createAddButton("Novo");
        btnEditar = CustomButton.createEditButton("Editar");
        btnExcluir = CustomButton.createDeleteButton("Excluir");
        btnAtualizar = CustomButton.createRefreshButton("Atualizar");
        
        btnNovo.addActionListener(e -> showDialog(null));
        btnEditar.addActionListener(e -> editarInstrutor());
        btnExcluir.addActionListener(e -> excluirInstrutor());
        btnAtualizar.addActionListener(e -> loadInstrutores());
        
        updateButtonStates();
    }
    
    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        topPanel.setBackground(PANEL_BACKGROUND);
        
        JLabel title = new JLabel("Gerenciamento de Instrutores");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        topPanel.add(title, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(PANEL_BACKGROUND);
        searchPanel.add(txtBusca);
        
        CustomButton btnBuscar = CustomButton.createSearchButton("Buscar");
        btnBuscar.addActionListener(e -> buscarInstrutores());
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
    
    private void loadInstrutores() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando instrutores...",
            () -> {
                String response = apiClient.get("/instrutores");
                List<InstrutorDTO> instrutores = apiClient.fromJsonArray(response, InstrutorDTO.class);
                SwingUtilities.invokeLater(() -> updateTable(instrutores));
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
    
    private void updateTable(List<InstrutorDTO> instrutores) {
        table.clearRows();
        for (InstrutorDTO inst : instrutores) {
            table.addRow(new Object[]{
                inst.getIdInstrutor(),
                inst.getNome(),
                inst.getEspecialidade() != null ? inst.getEspecialidade() : ""
            });
        }
        updateButtonStates();
    }
    
    private void editarInstrutor() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um instrutor para editar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/instrutores/" + id);
                InstrutorDTO inst = apiClient.fromJson(response, InstrutorDTO.class);
                SwingUtilities.invokeLater(() -> showDialog(inst));
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
    
    private void excluirInstrutor() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um instrutor para excluir.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        String nome = (String) table.getSelectedRowValue(1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir o instrutor " + nome + "?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo instrutor...",
            () -> apiClient.delete("/instrutores/" + id),
            () -> {
                MessageDialog.showSuccess(this, "Instrutor excluído com sucesso!");
                loadInstrutores();
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
    
    private void showDialog(InstrutorDTO instrutor) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            instrutor == null ? "Novo Instrutor" : "Editar Instrutor", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        JLabel lblNome = new JLabel("Nome *");
        lblNome.setForeground(TEXT_PRIMARY);
        lblNome.setFont(FONT_REGULAR);
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomTextField txtNome = new CustomTextField("", 30);
        txtNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (instrutor != null) txtNome.setText(instrutor.getNome());
        
        content.add(lblNome);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(txtNome);
        content.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        JLabel lblEsp = new JLabel("Especialidade");
        lblEsp.setForeground(TEXT_PRIMARY);
        lblEsp.setFont(FONT_REGULAR);
        lblEsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomTextField txtEsp = new CustomTextField("", 30);
        txtEsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (instrutor != null && instrutor.getEspecialidade() != null) {
            txtEsp.setText(instrutor.getEspecialidade());
        }
        
        content.add(lblEsp);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(txtEsp);
        content.add(Box.createVerticalStrut(PADDING_LARGE));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        btnPanel.setBackground(CARD_BACKGROUND);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomButton btnCancelar = CustomButton.createCancelButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        CustomButton btnSalvar = CustomButton.createSaveButton("Salvar");
        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                MessageDialog.showWarning(dialog, "O nome é obrigatório!");
                return;
            }
            
            InstrutorDTO dto = new InstrutorDTO();
            dto.setNome(nome);
            String esp = txtEsp.getText().trim();
            dto.setEspecialidade(esp.isEmpty() ? null : esp);
            if (instrutor != null) dto.setIdInstrutor(instrutor.getIdInstrutor());
            
            salvarInstrutor(dto, instrutor == null);
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
    
    private void salvarInstrutor(InstrutorDTO dto, boolean isNew) {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isNew ? "Cadastrando..." : "Atualizando...",
            () -> {
                if (isNew) {
                    apiClient.post("/instrutores", dto);
                } else {
                    apiClient.put("/instrutores/" + dto.getIdInstrutor(), dto);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isNew ? MSG_SUCCESS_SAVE : MSG_SUCCESS_UPDATE);
                loadInstrutores();
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
    
    private void buscarInstrutores() {
        String busca = txtBusca.getText().trim().toLowerCase();
        if (busca.isEmpty()) {
            loadInstrutores();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando...",
            () -> {
                String response = apiClient.get("/instrutores");
                List<InstrutorDTO> instrutores = apiClient.fromJsonArray(response, InstrutorDTO.class);
                List<InstrutorDTO> filtered = instrutores.stream()
                    .filter(i -> i.getNome().toLowerCase().contains(busca) ||
                               (i.getEspecialidade() != null && i.getEspecialidade().toLowerCase().contains(busca)))
                    .toList();
                SwingUtilities.invokeLater(() -> updateTable(filtered));
            },
            () -> {},
            error -> MessageDialog.showError(this, "Erro ao buscar: " + error.getMessage())
        );
    }
    
    private void updateButtonStates() {
        boolean hasSelection = table.hasSelection();
        btnEditar.setEnabled(hasSelection);
        btnExcluir.setEnabled(hasSelection);
    }
    
    // ========== REFRESH E NOTIFICAÇÕES ==========
    
    /**
     * Implementação de RefreshablePanel - atualiza os dados do painel
     */
    @Override
    public void refreshData() {
        loadInstrutores();
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
