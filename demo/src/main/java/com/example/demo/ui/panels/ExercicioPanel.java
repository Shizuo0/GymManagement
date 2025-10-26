package com.example.demo.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.example.demo.dto.ExercicioRequestDTO;
import com.example.demo.dto.ExercicioResponseDTO;
import com.example.demo.ui.GymManagementUI;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;
import static com.example.demo.ui.utils.UIConstants.*;

public class ExercicioPanel extends JPanel implements RefreshablePanel {
    
    private final ApiClient apiClient;
    private CustomTable table;
    private CustomTextField txtBusca;
    private CustomButton btnNovo, btnEditar, btnExcluir, btnAtualizar;
    
    public ExercicioPanel() {
        this.apiClient = new ApiClient();
        initComponents();
        setupLayout();
        SwingUtilities.invokeLater(this::loadExercicios);
    }
    
    private void initComponents() {
        setBackground(PANEL_BACKGROUND);
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        String[] columns = {"ID", "Nome", "Grupo Muscular"};
        table = new CustomTable(columns);
        table.setColumnWidth(0, 60);
        table.centerColumn(0);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });
        
        txtBusca = new CustomTextField("Buscar exercício...", 25);
        txtBusca.addActionListener(e -> buscarExercicios());
        
        btnNovo = CustomButton.createAddButton("Novo");
        btnEditar = CustomButton.createEditButton("Editar");
        btnExcluir = CustomButton.createDeleteButton("Excluir");
        btnAtualizar = CustomButton.createRefreshButton("Atualizar");
        
        btnNovo.addActionListener(e -> showDialog(null));
        btnEditar.addActionListener(e -> editarExercicio());
        btnExcluir.addActionListener(e -> excluirExercicio());
        btnAtualizar.addActionListener(e -> loadExercicios());
        
        updateButtonStates();
    }
    
    private void setupLayout() {
        JPanel topPanel = new JPanel(new BorderLayout(PADDING_MEDIUM, PADDING_MEDIUM));
        topPanel.setBackground(PANEL_BACKGROUND);
        
        JLabel title = new JLabel("Catálogo de Exercícios");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        topPanel.add(title, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        searchPanel.setBackground(PANEL_BACKGROUND);
        searchPanel.add(txtBusca);
        searchPanel.add(CustomButton.createSearchButton("Buscar"));
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
    
    private void loadExercicios() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando exercícios...",
            () -> {
                String response = apiClient.get("/exercicios");
                List<ExercicioResponseDTO> exercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
                SwingUtilities.invokeLater(() -> updateTable(exercicios));
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar exercícios: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<ExercicioResponseDTO> exercicios) {
        table.clearRows();
        for (ExercicioResponseDTO ex : exercicios) {
            table.addRow(new Object[]{
                ex.getId(),
                ex.getNome(),
                ex.getGrupoMuscular() != null ? ex.getGrupoMuscular() : "-"
            });
        }
        updateButtonStates();
    }
    
    private void editarExercicio() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um exercício para editar.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/exercicios/" + id);
                ExercicioResponseDTO exercicio = apiClient.fromJson(response, ExercicioResponseDTO.class);
                SwingUtilities.invokeLater(() -> showDialog(exercicio));
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
    
    private void excluirExercicio() {
        if (!table.hasSelection()) {
            MessageDialog.showWarning(this, "Selecione um exercício para excluir.");
            return;
        }
        
        Long id = (Long) table.getSelectedRowValue(0);
        String nome = (String) table.getSelectedRowValue(1);
        
        if (!MessageDialog.showConfirmation(this, 
            "Deseja realmente excluir o exercício \"" + nome + "\"?",
            "Confirmar Exclusão")) {
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Excluindo exercício...",
            () -> apiClient.delete("/exercicios/" + id),
            () -> {
                MessageDialog.showSuccess(this, "Exercício excluído com sucesso!");
                loadExercicios();
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
    
    private void showDialog(ExercicioResponseDTO exercicio) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            exercicio == null ? "Novo Exercício" : "Editar Exercício", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        JLabel lblNome = new JLabel("Nome do Exercício *");
        lblNome.setForeground(TEXT_PRIMARY);
        lblNome.setFont(FONT_REGULAR);
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomTextField txtNome = new CustomTextField("", 30);
        txtNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (exercicio != null) txtNome.setText(exercicio.getNome());
        
        content.add(lblNome);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(txtNome);
        content.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        JLabel lblGrupo = new JLabel("Grupo Muscular *");
        lblGrupo.setForeground(TEXT_PRIMARY);
        lblGrupo.setFont(FONT_REGULAR);
        lblGrupo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomTextField txtGrupo = new CustomTextField("", 30);
        txtGrupo.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (exercicio != null && exercicio.getGrupoMuscular() != null) {
            txtGrupo.setText(exercicio.getGrupoMuscular());
        }
        
        content.add(lblGrupo);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(txtGrupo);
        content.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        JLabel lblDesc = new JLabel("Instruções / Descrição");
        lblDesc.setForeground(TEXT_PRIMARY);
        lblDesc.setFont(FONT_REGULAR);
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea txtDescricao = new JTextArea(4, 30);
        txtDescricao.setFont(FONT_REGULAR);
        txtDescricao.setBackground(SURFACE_COLOR);
        txtDescricao.setForeground(TEXT_PRIMARY);
        txtDescricao.setCaretColor(TEXT_PRIMARY);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_SMALL, PADDING_SMALL, PADDING_SMALL)
        ));
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.setBorder(null);
        scrollDescricao.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDescricao.setPreferredSize(new Dimension(400, 100));
        
        content.add(lblDesc);
        content.add(Box.createVerticalStrut(PADDING_SMALL));
        content.add(scrollDescricao);
        content.add(Box.createVerticalStrut(PADDING_LARGE));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        btnPanel.setBackground(CARD_BACKGROUND);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomButton btnCancelar = CustomButton.createCancelButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        CustomButton btnSalvar = CustomButton.createSaveButton("Salvar");
        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String grupoMuscular = txtGrupo.getText().trim();
            
            if (nome.isEmpty() || grupoMuscular.isEmpty()) {
                MessageDialog.showWarning(dialog, "Nome e Grupo Muscular são obrigatórios!");
                return;
            }
            
            ExercicioRequestDTO dto = new ExercicioRequestDTO();
            dto.setNome(nome);
            dto.setGrupoMuscular(grupoMuscular);
            
            salvarExercicio(dto, exercicio);
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
    
    private void salvarExercicio(ExercicioRequestDTO dto, ExercicioResponseDTO existing) {
        boolean isNew = existing == null;
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isNew ? "Cadastrando..." : "Atualizando...",
            () -> {
                if (isNew) {
                    apiClient.post("/exercicios", dto);
                } else {
                    apiClient.put("/exercicios/" + existing.getId(), dto);
                }
            },
            () -> {
                MessageDialog.showSuccess(this, isNew ? MSG_SUCCESS_SAVE : MSG_SUCCESS_UPDATE);
                loadExercicios();
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
    
    private void buscarExercicios() {
        String busca = txtBusca.getText().trim().toLowerCase();
        if (busca.isEmpty()) {
            loadExercicios();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando...",
            () -> {
                String response = apiClient.get("/exercicios");
                List<ExercicioResponseDTO> exercicios = apiClient.fromJsonArray(response, ExercicioResponseDTO.class);
                List<ExercicioResponseDTO> filtered = exercicios.stream()
                    .filter(ex -> ex.getNome().toLowerCase().contains(busca) ||
                                (ex.getGrupoMuscular() != null && ex.getGrupoMuscular().toLowerCase().contains(busca)))
                    .toList();
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtered);
                    if (filtered.isEmpty()) {
                        MessageDialog.showInfo(this, "Nenhum exercício encontrado.");
                    }
                });
            },
            () -> {},
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao buscar: " + error.getMessage());
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
        loadExercicios();
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
}
