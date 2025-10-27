package com.example.demo.ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Tabela customizada com estilo consistente e funcionalidades úteis.
 * Tabela não editável por padrão, com cabeçalho estilizado e linhas alternadas.
 */
public class CustomTable extends JTable {
    
    private DefaultTableModel tableModel;
    
    /**
     * Construtor com colunas
     * 
     * @param columnNames Nomes das colunas
     */
    public CustomTable(String[] columnNames) {
        super();
        initializeTable(columnNames);
        setupTable();
    }
    
    /**
     * Construtor com dados e colunas
     * 
     * @param data Dados da tabela
     * @param columnNames Nomes das colunas
     */
    public CustomTable(Object[][] data, String[] columnNames) {
        super(data, columnNames);
        this.tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável por padrão
            }
        };
        setModel(tableModel);
        setupTable();
    }
    
    /**
     * Inicializa o modelo da tabela
     * 
     * @param columnNames Nomes das colunas
     */
    private void initializeTable(String[] columnNames) {
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável por padrão
            }
        };
        setModel(tableModel);
    }
    
    /**
     * Configura as propriedades visuais da tabela
     */
    private void setupTable() {
        // Configurações básicas
        setFont(FONT_REGULAR);
        setForeground(TEXT_PRIMARY);
        setBackground(PANEL_BACKGROUND);
        setGridColor(TABLE_GRID_COLOR);
        setRowHeight(TABLE_ROW_HEIGHT);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setShowGrid(true);
        setIntercellSpacing(new Dimension(1, 1));
        
        // Configurações de seleção
        setSelectionBackground(TABLE_SELECTION_BACKGROUND);
        setSelectionForeground(TABLE_SELECTION_FOREGROUND);
        
        // Configuração do cabeçalho
        JTableHeader header = getTableHeader();
        header.setFont(FONT_BUTTON);
        header.setBackground(TABLE_HEADER_BACKGROUND);
        header.setForeground(TABLE_HEADER_FOREGROUND);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
        
        // Renderizador customizado para cabeçalho
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setFont(FONT_BUTTON);
                label.setBackground(TABLE_HEADER_BACKGROUND);
                label.setForeground(TABLE_HEADER_FOREGROUND);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL)
                ));
                label.setOpaque(true);
                return label;
            }
        });
        
        // Renderizador customizado para células
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    // Linhas alternadas
                    if (row % 2 == 0) {
                        c.setBackground(PANEL_BACKGROUND);
                    } else {
                        c.setBackground(TABLE_ALTERNATE_ROW);
                    }
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(TABLE_SELECTION_BACKGROUND);
                    c.setForeground(TABLE_SELECTION_FOREGROUND);
                }
                
                // Adiciona padding nas células
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(
                        PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM
                    ));
                }
                
                return c;
            }
        });
        
        // Auto-resize mode
        setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    /**
     * Adiciona uma linha à tabela
     * 
     * @param rowData Dados da linha
     */
    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }
    
    /**
     * Remove a linha selecionada
     */
    public void removeSelectedRow() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
        }
    }
    
    /**
     * Remove uma linha específica
     * 
     * @param row Índice da linha
     */
    public void removeRow(int row) {
        if (row >= 0 && row < tableModel.getRowCount()) {
            tableModel.removeRow(row);
        }
    }
    
    /**
     * Limpa todas as linhas da tabela
     */
    public void clearRows() {
        tableModel.setRowCount(0);
    }
    
    /**
     * Atualiza uma linha específica
     * 
     * @param row Índice da linha
     * @param rowData Novos dados da linha
     */
    public void updateRow(int row, Object[] rowData) {
        if (row >= 0 && row < tableModel.getRowCount()) {
            for (int col = 0; col < rowData.length && col < tableModel.getColumnCount(); col++) {
                tableModel.setValueAt(rowData[col], row, col);
            }
        }
    }
    
    /**
     * Retorna os dados da linha selecionada
     * 
     * @return Array com os dados da linha ou null se nenhuma linha selecionada
     */
    public Object[] getSelectedRowData() {
        int selectedRow = getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        
        Object[] rowData = new Object[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++) {
            rowData[i] = getValueAt(selectedRow, i);
        }
        
        return rowData;
    }
    
    /**
     * Retorna o valor de uma célula específica da linha selecionada
     * 
     * @param columnIndex Índice da coluna
     * @return Valor da célula ou null se nenhuma linha selecionada
     */
    public Object getSelectedRowValue(int columnIndex) {
        int selectedRow = getSelectedRow();
        if (selectedRow == -1 || columnIndex < 0 || columnIndex >= getColumnCount()) {
            return null;
        }
        
        return getValueAt(selectedRow, columnIndex);
    }
    
    /**
     * Define a largura de uma coluna específica
     * 
     * @param columnIndex Índice da coluna
     * @param width Largura em pixels
     */
    public void setColumnWidth(int columnIndex, int width) {
        if (columnIndex >= 0 && columnIndex < getColumnCount()) {
            TableColumn column = getColumnModel().getColumn(columnIndex);
            column.setPreferredWidth(width);
            column.setMinWidth(width / 2);
            column.setMaxWidth(width * 2);
        }
    }
    
    /**
     * Centraliza o conteúdo de uma coluna específica
     * 
     * @param columnIndex Índice da coluna
     */
    public void centerColumn(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < getColumnCount()) {
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
            getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
        }
    }
    
    /**
     * Alinha à direita o conteúdo de uma coluna específica
     * 
     * @param columnIndex Índice da coluna
     */
    public void rightAlignColumn(int columnIndex) {
        if (columnIndex >= 0 && columnIndex < getColumnCount()) {
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }
    
    /**
     * Retorna o modelo da tabela
     * 
     * @return DefaultTableModel
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }
    
    /**
     * Verifica se há uma linha selecionada
     * 
     * @return true se há linha selecionada, false caso contrário
     */
    public boolean hasSelection() {
        return getSelectedRow() != -1;
    }
    
    /**
     * Desseleciona todas as linhas
     */
    public void clearSelection() {
        super.clearSelection();
    }
    
    /**
     * Retorna o número de linhas na tabela
     * 
     * @return Número de linhas
     */
    public int getRowCountData() {
        return tableModel.getRowCount();
    }
}
