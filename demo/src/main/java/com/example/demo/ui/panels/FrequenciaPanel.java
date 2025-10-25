package com.example.demo.ui.panels;

import static com.example.demo.ui.utils.UIConstants.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.example.demo.dto.AlunoDTO;
import com.example.demo.dto.FrequenciaResponseDTO;
import com.example.demo.ui.components.CustomButton;
import com.example.demo.ui.components.CustomDatePicker;
import com.example.demo.ui.components.CustomTable;
import com.example.demo.ui.components.CustomTextField;
import com.example.demo.ui.components.LoadingDialog;
import com.example.demo.ui.components.MessageDialog;
import com.example.demo.ui.utils.ApiClient;
import com.example.demo.ui.utils.ApiException;

/**
 * Panel para gerenciamento de Frequências (registro de presença dos alunos)
 * COMMIT 7: FrequenciaPanel com registro de presença, filtros por data/aluno/período, e estatísticas
 */
public class FrequenciaPanel extends JPanel {
    
    private final ApiClient apiClient;
    private final DateTimeFormatter dateFormatter;
    
    // Componentes da tabela
    private CustomTable table;
    private CustomTextField txtBuscaAluno;
    private CustomDatePicker dataBuscaInicio;
    private CustomDatePicker dataBuscaFim;
    
    // Componentes do formulário
    private JComboBox<AlunoItem> cmbAluno;
    private CustomDatePicker dataFrequencia;
    private JRadioButton rbPresente;
    private JRadioButton rbAusente;
    private ButtonGroup bgPresenca;
    
    // Botões
    private CustomButton btnNovo;
    private CustomButton btnEditar;
    private CustomButton btnExcluir;
    private CustomButton btnFiltrar;
    private CustomButton btnLimpar;
    private CustomButton btnEstatisticas;
    private CustomButton btnSalvar;
    private CustomButton btnCancelar;
    
    // Estado
    private Long currentFrequenciaId;
    private boolean isEditMode;
    private List<AlunoDTO> alunosDisponiveis;
    
    public FrequenciaPanel() {
        this.apiClient = new ApiClient();
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.alunosDisponiveis = new ArrayList<>();
        
        setLayout(new BorderLayout(PADDING_LARGE, PADDING_LARGE));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        initializeUI();
        loadAlunos();
        loadFrequencias();
    }
    
    private void initializeUI() {
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
        
        // Cabeçalho
        JLabel lblTitle = new JLabel("📊 Registro de Frequência");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Painel central: filtros e tabela
        JPanel centerPanel = new JPanel(new BorderLayout(0, PADDING_MEDIUM));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(createFilterPanel(), BorderLayout.NORTH);
        
        // Tabela
        String[] colunas = {"ID", "Aluno", "CPF", "Data", "Status"};
        table = new CustomTable(colunas);
        table.setPreferredScrollableViewportSize(new Dimension(700, 400));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtons();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Bot panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(new EmptyBorder(PADDING_MEDIUM, 0, 0, 0));
        
        btnNovo = new CustomButton("➕ Novo", CustomButton.ButtonType.PRIMARY);
        btnEditar = new CustomButton("✏️ Editar", CustomButton.ButtonType.SECONDARY);
        btnExcluir = new CustomButton("🗑️ Excluir", CustomButton.ButtonType.DANGER);
        
        btnNovo.addActionListener(e -> novaFrequencia());
        btnEditar.addActionListener(e -> editarFrequencia());
        btnExcluir.addActionListener(e -> excluirFrequencia());
        
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnExcluir);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        updateButtons();
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            new EmptyBorder(PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM, PADDING_MEDIUM)
        ));
        
        // Busca por aluno
        JPanel buscaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        buscaPanel.setBackground(CARD_BACKGROUND);
        
        JLabel lblBusca = new JLabel("Buscar Aluno:");
        lblBusca.setFont(FONT_REGULAR);
        lblBusca.setForeground(TEXT_PRIMARY);
        
        txtBuscaAluno = new CustomTextField("Digite o nome do aluno...", 25);
        txtBuscaAluno.addActionListener(e -> filtrarPorAluno());
        
        buscaPanel.add(lblBusca);
        buscaPanel.add(txtBuscaAluno);
        panel.add(buscaPanel);
        
        // Filtro por período
        JPanel periodoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        periodoPanel.setBackground(CARD_BACKGROUND);
        
        JLabel lblPeriodo = new JLabel("Período:");
        lblPeriodo.setFont(FONT_REGULAR);
        lblPeriodo.setForeground(TEXT_PRIMARY);
        
        JLabel lblDataInicio = new JLabel("De:");
        lblDataInicio.setFont(FONT_SMALL);
        lblDataInicio.setForeground(TEXT_SECONDARY);
        
        dataBuscaInicio = new CustomDatePicker();
        dataBuscaInicio.setPreferredSize(new Dimension(150, TEXTFIELD_HEIGHT));
        
        JLabel lblDataFim = new JLabel("Até:");
        lblDataFim.setFont(FONT_SMALL);
        lblDataFim.setForeground(TEXT_SECONDARY);
        
        dataBuscaFim = new CustomDatePicker();
        dataBuscaFim.setPreferredSize(new Dimension(150, TEXTFIELD_HEIGHT));
        
        periodoPanel.add(lblPeriodo);
        periodoPanel.add(lblDataInicio);
        periodoPanel.add(dataBuscaInicio);
        periodoPanel.add(Box.createHorizontalStrut(PADDING_MEDIUM));
        periodoPanel.add(lblDataFim);
        periodoPanel.add(dataBuscaFim);
        panel.add(periodoPanel);
        
        // Botões de filtro
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, PADDING_SMALL, 0));
        btnPanel.setBackground(CARD_BACKGROUND);
        
        btnFiltrar = new CustomButton("🔍 Filtrar", CustomButton.ButtonType.PRIMARY);
        btnLimpar = new CustomButton("🔄 Limpar", CustomButton.ButtonType.SECONDARY);
        btnEstatisticas = new CustomButton("📈 Estatísticas", CustomButton.ButtonType.SECONDARY);
        
        btnFiltrar.addActionListener(e -> filtrarPorPeriodo());
        btnLimpar.addActionListener(e -> limparFiltros());
        btnEstatisticas.addActionListener(e -> mostrarEstatisticas());
        
        btnPanel.add(btnFiltrar);
        btnPanel.add(btnLimpar);
        btnPanel.add(btnEstatisticas);
        panel.add(btnPanel);
        
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
        JLabel lblTitle = new JLabel("Dados do Registro");
        lblTitle.setFont(FONT_SUBTITLE);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Campo Aluno
        JLabel lblAluno = new JLabel("Aluno:*");
        lblAluno.setFont(FONT_REGULAR);
        lblAluno.setForeground(TEXT_PRIMARY);
        lblAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblAluno);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        cmbAluno = new JComboBox<>();
        cmbAluno.setFont(FONT_REGULAR);
        cmbAluno.setBackground(SURFACE_COLOR);
        cmbAluno.setForeground(TEXT_PRIMARY);
        cmbAluno.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cmbAluno);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Data
        JLabel lblData = new JLabel("Data:*");
        lblData.setFont(FONT_REGULAR);
        lblData.setForeground(TEXT_PRIMARY);
        lblData.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblData);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        dataFrequencia = new CustomDatePicker();
        dataFrequencia.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dataFrequencia.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(dataFrequencia);
        panel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Campo Status de Presença
        JLabel lblPresenca = new JLabel("Status:*");
        lblPresenca.setFont(FONT_REGULAR);
        lblPresenca.setForeground(TEXT_PRIMARY);
        lblPresenca.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblPresenca);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        JPanel presencaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_MEDIUM, 0));
        presencaPanel.setBackground(CARD_BACKGROUND);
        presencaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        presencaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        rbPresente = new JRadioButton("✅ Presente");
        rbPresente.setFont(FONT_REGULAR);
        rbPresente.setForeground(SUCCESS_COLOR);
        rbPresente.setBackground(CARD_BACKGROUND);
        rbPresente.setSelected(true);
        
        rbAusente = new JRadioButton("❌ Ausente");
        rbAusente.setFont(FONT_REGULAR);
        rbAusente.setForeground(ERROR_COLOR);
        rbAusente.setBackground(CARD_BACKGROUND);
        
        bgPresenca = new ButtonGroup();
        bgPresenca.add(rbPresente);
        bgPresenca.add(rbAusente);
        
        presencaPanel.add(rbPresente);
        presencaPanel.add(rbAusente);
        panel.add(presencaPanel);
        panel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões do formulário
        JPanel btnFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, PADDING_SMALL, 0));
        btnFormPanel.setBackground(CARD_BACKGROUND);
        btnFormPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT + 10));
        btnFormPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnSalvar = new CustomButton("💾 Salvar", CustomButton.ButtonType.PRIMARY);
        btnCancelar = new CustomButton("❌ Cancelar", CustomButton.ButtonType.SECONDARY);
        
        btnSalvar.addActionListener(e -> salvarFrequencia());
        btnCancelar.addActionListener(e -> cancelarEdicao());
        
        btnFormPanel.add(btnSalvar);
        btnFormPanel.add(btnCancelar);
        panel.add(btnFormPanel);
        
        panel.add(Box.createVerticalGlue());
        
        // Estado inicial: formulário desabilitado
        setFormEnabled(false);
        
        return panel;
    }
    
    // ========== CARREGAMENTO DE DADOS ==========
    
    private void loadAlunos() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando alunos...",
            () -> {
                String response = apiClient.get("/api/alunos");
                List<AlunoDTO> alunos = apiClient.fromJsonArray(response, AlunoDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    alunosDisponiveis.clear();
                    alunosDisponiveis.addAll(alunos);
                    
                    cmbAluno.removeAllItems();
                    for (AlunoDTO aluno : alunos) {
                        cmbAluno.addItem(new AlunoItem(aluno));
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
                    MessageDialog.showError(this, "Erro ao carregar alunos: " + error.getMessage());
                }
            }
        );
    }
    
    private void loadFrequencias() {
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando frequências...",
            () -> {
                String response = apiClient.get("/api/frequencias");
                List<FrequenciaResponseDTO> frequencias = apiClient.fromJsonArray(response, FrequenciaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(frequencias);
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao carregar frequências: " + error.getMessage());
                }
            }
        );
    }
    
    private void updateTable(List<FrequenciaResponseDTO> frequencias) {
        table.clearRows();
        for (FrequenciaResponseDTO freq : frequencias) {
            table.addRow(new Object[]{
                freq.getIdFrequencia(),
                freq.getNomeAluno(),
                freq.getCpfAluno(),
                freq.getData().format(dateFormatter),
                freq.getStatusPresenca()
            });
        }
    }
    
    // ========== CRUD OPERATIONS ==========
    
    private void novaFrequencia() {
        isEditMode = false;
        currentFrequenciaId = null;
        clearForm();
        setFormEnabled(true);
        dataFrequencia.setToday();
        rbPresente.setSelected(true);
        updateButtons();
    }
    
    private void editarFrequencia() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        currentFrequenciaId = (Long) table.getValueAt(selectedRow, 0);
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Carregando dados...",
            () -> {
                String response = apiClient.get("/api/frequencias/" + currentFrequenciaId);
                FrequenciaResponseDTO frequencia = apiClient.fromJson(response, FrequenciaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    isEditMode = true;
                    populateForm(frequencia);
                    setFormEnabled(true);
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
                    MessageDialog.showError(this, "Erro ao carregar frequência: " + error.getMessage());
                }
            }
        );
    }
    
    private void excluirFrequencia() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        
        Long id = (Long) table.getValueAt(selectedRow, 0);
        String nomeAluno = (String) table.getValueAt(selectedRow, 1);
        String data = (String) table.getValueAt(selectedRow, 3);
        
        boolean confirmed = MessageDialog.showConfirmation(
            this,
            "Deseja realmente excluir o registro de frequência de " + nomeAluno + " em " + data + "?",
            "Confirmar Exclusão"
        );
        
        if (confirmed) {
            LoadingDialog.executeWithLoading(
                SwingUtilities.getWindowAncestor(this),
                "Excluindo registro...",
                () -> {
                    apiClient.delete("/api/frequencias/" + id);
                    SwingUtilities.invokeLater(() -> {
                        MessageDialog.showSuccess(this, "Frequência excluída com sucesso!");
                        loadFrequencias();
                    });
                },
                () -> {
                    // Sucesso
                },
                error -> {
                    if (error instanceof ApiException) {
                        MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                    } else {
                        MessageDialog.showError(this, "Erro ao excluir frequência: " + error.getMessage());
                    }
                }
            );
        }
    }
    
    private void salvarFrequencia() {
        if (!validateForm()) {
            MessageDialog.showWarning(this, MSG_VALIDATION_ERROR);
            return;
        }
        
        AlunoItem alunoSelecionado = (AlunoItem) cmbAluno.getSelectedItem();
        LocalDate dataFreq = dataFrequencia.getLocalDate();
        Boolean presenca = rbPresente.isSelected();
        
        // Criar JSON manualmente
        String jsonData = String.format(
            "{\"idAluno\":%d,\"data\":\"%s\",\"presenca\":%b}",
            alunoSelecionado.getId(),
            dataFreq,
            presenca
        );
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            isEditMode ? "Atualizando registro..." : "Registrando presença...",
            () -> {
                if (isEditMode) {
                    apiClient.put("/api/frequencias/" + currentFrequenciaId, jsonData);
                } else {
                    apiClient.post("/api/frequencias", jsonData);
                }
                
                SwingUtilities.invokeLater(() -> {
                    MessageDialog.showSuccess(
                        this,
                        isEditMode ? "Frequência atualizada com sucesso!" : "Presença registrada com sucesso!"
                    );
                    cancelarEdicao();
                    loadFrequencias();
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(this, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(this, "Erro ao salvar frequência: " + error.getMessage());
                }
            }
        );
    }
    
    private void cancelarEdicao() {
        clearForm();
        setFormEnabled(false);
        isEditMode = false;
        currentFrequenciaId = null;
        table.clearSelection();
        updateButtons();
    }
    
    // ========== FILTROS ==========
    
    private void filtrarPorAluno() {
        String busca = txtBuscaAluno.getText().trim();
        
        if (busca.isEmpty()) {
            loadFrequencias();
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Buscando frequências...",
            () -> {
                String response = apiClient.get("/api/frequencias");
                List<FrequenciaResponseDTO> todasFrequencias = apiClient.fromJsonArray(response, FrequenciaResponseDTO.class);
                
                // Filtrar localmente pelo nome do aluno
                List<FrequenciaResponseDTO> filtradas = todasFrequencias.stream()
                    .filter(f -> f.getNomeAluno().toLowerCase().contains(busca.toLowerCase()))
                    .toList();
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(filtradas);
                    
                    if (filtradas.isEmpty()) {
                        MessageDialog.showInfo(this, 
                            "Nenhuma frequência encontrada para o aluno: " + busca);
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
                    MessageDialog.showError(this, "Erro ao buscar frequências: " + error.getMessage());
                }
            }
        );
    }
    
    private void filtrarPorPeriodo() {
        LocalDate dataInicio = dataBuscaInicio.getLocalDate();
        LocalDate dataFim = dataBuscaFim.getLocalDate();
        
        if (dataInicio == null || dataFim == null) {
            MessageDialog.showWarning(this, "Por favor, selecione a data de início e fim.");
            return;
        }
        
        if (dataInicio.isAfter(dataFim)) {
            MessageDialog.showWarning(this, "A data de início não pode ser posterior à data de fim.");
            return;
        }
        
        LoadingDialog.executeWithLoading(
            SwingUtilities.getWindowAncestor(this),
            "Filtrando por período...",
            () -> {
                String response = apiClient.get(
                    "/api/frequencias/periodo?dataInicio=" + dataInicio + "&dataFim=" + dataFim
                );
                List<FrequenciaResponseDTO> frequencias = apiClient.fromJsonArray(response, FrequenciaResponseDTO.class);
                
                SwingUtilities.invokeLater(() -> {
                    updateTable(frequencias);
                    
                    if (frequencias.isEmpty()) {
                        MessageDialog.showInfo(
                            this,
                            "Nenhuma frequência encontrada no período de " + 
                            dataInicio.format(dateFormatter) + " a " + dataFim.format(dateFormatter)
                        );
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
                    MessageDialog.showError(this, "Erro ao filtrar por período: " + error.getMessage());
                }
            }
        );
    }
    
    private void limparFiltros() {
        txtBuscaAluno.setText("");
        dataBuscaInicio.setDate(null);
        dataBuscaFim.setDate(null);
        loadFrequencias();
    }
    
    // ========== ESTATÍSTICAS ==========
    
    private void mostrarEstatisticas() {
        // Dialog de seleção de aluno e período
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "📈 Estatísticas de Frequência", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        // Título
        JLabel lblTitulo = new JLabel("Selecione o Aluno e o Período");
        lblTitulo.setFont(FONT_SUBTITLE);
        lblTitulo.setForeground(TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblTitulo);
        contentPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // ComboBox de alunos
        JLabel lblAluno = new JLabel("Aluno:*");
        lblAluno.setFont(FONT_REGULAR);
        lblAluno.setForeground(TEXT_PRIMARY);
        lblAluno.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblAluno);
        contentPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        JComboBox<AlunoItem> cmbAlunoStat = new JComboBox<>();
        cmbAlunoStat.setFont(FONT_REGULAR);
        cmbAlunoStat.setBackground(SURFACE_COLOR);
        cmbAlunoStat.setForeground(TEXT_PRIMARY);
        cmbAlunoStat.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        cmbAlunoStat.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (AlunoDTO aluno : alunosDisponiveis) {
            cmbAlunoStat.addItem(new AlunoItem(aluno));
        }
        contentPanel.add(cmbAlunoStat);
        contentPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Data início
        JLabel lblDataIni = new JLabel("Data Início:*");
        lblDataIni.setFont(FONT_REGULAR);
        lblDataIni.setForeground(TEXT_PRIMARY);
        lblDataIni.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblDataIni);
        contentPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        CustomDatePicker dataIniStat = new CustomDatePicker();
        dataIniStat.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dataIniStat.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(dataIniStat);
        contentPanel.add(Box.createVerticalStrut(PADDING_MEDIUM));
        
        // Data fim
        JLabel lblDataFim = new JLabel("Data Fim:*");
        lblDataFim.setFont(FONT_REGULAR);
        lblDataFim.setForeground(TEXT_PRIMARY);
        lblDataFim.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblDataFim);
        contentPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        CustomDatePicker dataFimStat = new CustomDatePicker();
        dataFimStat.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        dataFimStat.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(dataFimStat);
        contentPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, PADDING_SMALL, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT + 10));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        CustomButton btnConsultar = new CustomButton("📊 Consultar", CustomButton.ButtonType.PRIMARY);
        CustomButton btnFechar = new CustomButton("❌ Fechar", CustomButton.ButtonType.SECONDARY);
        
        btnConsultar.addActionListener(e -> {
            consultarEstatisticas(dialog, cmbAlunoStat, dataIniStat, dataFimStat);
        });
        
        btnFechar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnConsultar);
        buttonPanel.add(btnFechar);
        contentPanel.add(buttonPanel);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, 400));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void consultarEstatisticas(JDialog parentDialog, JComboBox<AlunoItem> cmbAlunoStat, 
                                       CustomDatePicker dataIniStat, CustomDatePicker dataFimStat) {
        AlunoItem alunoSel = (AlunoItem) cmbAlunoStat.getSelectedItem();
        LocalDate dataIni = dataIniStat.getLocalDate();
        LocalDate dataFim = dataFimStat.getLocalDate();
        
        if (alunoSel == null || dataIni == null || dataFim == null) {
            MessageDialog.showWarning(parentDialog, MSG_VALIDATION_ERROR);
            return;
        }
        
        if (dataIni.isAfter(dataFim)) {
            MessageDialog.showWarning(parentDialog, "A data de início não pode ser posterior à data de fim.");
            return;
        }
        
        LoadingDialog.executeWithLoading(
            parentDialog,
            "Calculando estatísticas...",
            () -> {
                // Buscar taxa de presença
                String responseTaxa = apiClient.get(
                    "/api/frequencias/aluno/" + alunoSel.getId() + "/taxa-presenca?dataInicio=" + 
                    dataIni + "&dataFim=" + dataFim
                );
                @SuppressWarnings("unchecked")
                Map<String, Object> taxaData = apiClient.fromJson(responseTaxa, Map.class);
                
                // Buscar total de presenças
                String responseTotal = apiClient.get("/api/frequencias/aluno/" + alunoSel.getId() + "/total-presencas");
                Long totalPresencas = Long.parseLong(responseTotal);
                
                // Buscar frequências do período
                String responseFreq = apiClient.get(
                    "/api/frequencias/aluno/" + alunoSel.getId() + "/periodo?dataInicio=" + 
                    dataIni + "&dataFim=" + dataFim
                );
                List<FrequenciaResponseDTO> frequencias = apiClient.fromJsonArray(responseFreq, FrequenciaResponseDTO.class);
                
                long presencasNoPeriodo = frequencias.stream()
                    .filter(FrequenciaResponseDTO::getPresenca)
                    .count();
                
                SwingUtilities.invokeLater(() -> {
                    exibirResultados(
                        alunoSel.toString(),
                        dataIni,
                        dataFim,
                        (String) taxaData.get("taxaFormatada"),
                        totalPresencas,
                        frequencias.size(),
                        presencasNoPeriodo,
                        frequencias.size() - presencasNoPeriodo
                    );
                });
            },
            () -> {
                // Sucesso
            },
            error -> {
                if (error instanceof ApiException) {
                    MessageDialog.showError(parentDialog, ((ApiException) error).getUserFriendlyMessage());
                } else {
                    MessageDialog.showError(parentDialog, "Erro ao calcular estatísticas: " + error.getMessage());
                }
            }
        );
    }
    
    private void exibirResultados(String nomeAluno, LocalDate dataIni, LocalDate dataFim,
                                  String taxaFormatada, Long totalPresencas, int registrosPeriodo,
                                  long presencasNoPeriodo, long ausenciasNoPeriodo) {
        JDialog resultDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Resultados", true);
        resultDialog.setLayout(new BorderLayout());
        resultDialog.getContentPane().setBackground(BACKGROUND_COLOR);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE));
        
        // Título
        JLabel lblTitulo = new JLabel("Estatísticas de " + nomeAluno);
        lblTitulo.setFont(FONT_SUBTITLE);
        lblTitulo.setForeground(TEXT_PRIMARY);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblTitulo);
        contentPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        
        // Período
        JLabel lblPeriodo = new JLabel("Período: " + dataIni.format(dateFormatter) + " a " + dataFim.format(dateFormatter));
        lblPeriodo.setFont(FONT_REGULAR);
        lblPeriodo.setForeground(TEXT_SECONDARY);
        lblPeriodo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(lblPeriodo);
        contentPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Estatísticas
        addStatLabel(contentPanel, "Taxa de Presença:", taxaFormatada, PRIMARY_COLOR);
        addStatLabel(contentPanel, "Total de Presenças (geral):", String.valueOf(totalPresencas), SUCCESS_COLOR);
        addStatLabel(contentPanel, "Registros no Período:", String.valueOf(registrosPeriodo), INFO_COLOR);
        addStatLabel(contentPanel, "Presenças no Período:", String.valueOf(presencasNoPeriodo), SUCCESS_COLOR);
        addStatLabel(contentPanel, "Ausências no Período:", String.valueOf(ausenciasNoPeriodo), ERROR_COLOR);
        
        contentPanel.add(Box.createVerticalStrut(PADDING_LARGE));
        
        // Botão fechar
        CustomButton btnFechar = new CustomButton("Fechar", CustomButton.ButtonType.PRIMARY);
        btnFechar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnFechar.addActionListener(e -> resultDialog.dispose());
        contentPanel.add(btnFechar);
        
        resultDialog.add(contentPanel, BorderLayout.CENTER);
        resultDialog.pack();
        resultDialog.setMinimumSize(new Dimension(400, 350));
        resultDialog.setLocationRelativeTo(this);
        resultDialog.setVisible(true);
    }
    
    private void addStatLabel(JPanel panel, String label, String value, Color valueColor) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setBackground(CARD_BACKGROUND);
        statPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, TEXTFIELD_HEIGHT));
        
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(FONT_REGULAR);
        lblLabel.setForeground(TEXT_PRIMARY);
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_SUBTITLE);
        lblValue.setForeground(valueColor);
        
        statPanel.add(lblLabel, BorderLayout.WEST);
        statPanel.add(lblValue, BorderLayout.EAST);
        
        panel.add(statPanel);
        panel.add(Box.createVerticalStrut(PADDING_SMALL));
    }
    
    // ========== VALIDAÇÃO E FORMULÁRIO ==========
    
    private boolean validateForm() {
        if (cmbAluno.getSelectedItem() == null) {
            return false;
        }
        
        if (dataFrequencia.getDate() == null) {
            return false;
        } else if (dataFrequencia.isFutureDate()) {
            MessageDialog.showWarning(this, "A data não pode ser futura!");
            return false;
        }
        
        return true;
    }
    
    private void populateForm(FrequenciaResponseDTO frequencia) {
        // Selecionar aluno
        for (int i = 0; i < cmbAluno.getItemCount(); i++) {
            AlunoItem item = cmbAluno.getItemAt(i);
            if (item.getId().equals(frequencia.getIdAluno())) {
                cmbAluno.setSelectedIndex(i);
                break;
            }
        }
        
        dataFrequencia.setLocalDate(frequencia.getData());
        
        if (frequencia.getPresenca()) {
            rbPresente.setSelected(true);
        } else {
            rbAusente.setSelected(true);
        }
    }
    
    private void clearForm() {
        if (cmbAluno.getItemCount() > 0) {
            cmbAluno.setSelectedIndex(0);
        }
        dataFrequencia.setDate(null);
        rbPresente.setSelected(true);
    }
    
    private void setFormEnabled(boolean enabled) {
        cmbAluno.setEnabled(enabled);
        dataFrequencia.setEnabled(enabled);
        rbPresente.setEnabled(enabled);
        rbAusente.setEnabled(enabled);
        btnSalvar.setEnabled(enabled);
        btnCancelar.setEnabled(enabled);
    }
    
    private void updateButtons() {
        boolean hasSelection = table.getSelectedRow() != -1;
        boolean formEnabled = btnSalvar.isEnabled();
        
        btnNovo.setEnabled(!formEnabled);
        btnEditar.setEnabled(hasSelection && !formEnabled);
        btnExcluir.setEnabled(hasSelection && !formEnabled);
    }
    
    // ========== CLASSE AUXILIAR ==========
    
    private static class AlunoItem {
        private final Long id;
        private final String nome;
        private final String cpf;
        
        public AlunoItem(AlunoDTO aluno) {
            this.id = aluno.getIdAluno();
            this.nome = aluno.getNome();
            this.cpf = aluno.getCpf();
        }
        
        public Long getId() {
            return id;
        }
        
        @Override
        public String toString() {
            return nome + " (CPF: " + cpf + ")";
        }
    }
}
