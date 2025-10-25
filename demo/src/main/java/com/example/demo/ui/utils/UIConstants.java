package com.example.demo.ui.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes de UI para manter consistência visual em toda a aplicação.
 * Define cores, fontes, espaçamentos e outros elementos de design.
 */
public class UIConstants {
    
    // === CORES PRINCIPAIS ===
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Azul principal
    public static final Color PRIMARY_DARK = new Color(28, 94, 145);        // Azul escuro
    public static final Color PRIMARY_LIGHT = new Color(52, 152, 219);      // Azul claro
    
    public static final Color SECONDARY_COLOR = new Color(46, 204, 113);    // Verde
    public static final Color ACCENT_COLOR = new Color(231, 76, 60);        // Vermelho
    public static final Color WARNING_COLOR = new Color(243, 156, 18);      // Laranja
    
    // === CORES DE FUNDO ===
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);  // Cinza claro
    public static final Color PANEL_BACKGROUND = Color.WHITE;
    public static final Color HOVER_COLOR = new Color(189, 195, 199);       // Cinza hover
    
    // === CORES DE TEXTO ===
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);         // Cinza escuro
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);    // Cinza médio
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;
    
    // === CORES DE STATUS ===
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Verde sucesso
    public static final Color ERROR_COLOR = new Color(231, 76, 60);         // Vermelho erro
    public static final Color INFO_COLOR = new Color(52, 152, 219);         // Azul informação
    public static final Color WARNING_TEXT_COLOR = new Color(243, 156, 18); // Laranja aviso
    
    // === CORES DE BORDA ===
    public static final Color BORDER_COLOR = new Color(189, 195, 199);
    public static final Color BORDER_FOCUS_COLOR = PRIMARY_COLOR;
    
    // === FONTES ===
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    
    // === DIMENSÕES ===
    public static final int BUTTON_HEIGHT = 35;
    public static final int BUTTON_WIDTH = 100;
    public static final int TEXTFIELD_HEIGHT = 30;
    public static final int TEXTAREA_HEIGHT = 100;
    
    // === ESPAÇAMENTOS ===
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    public static final int PADDING_XLARGE = 30;
    
    public static final int MARGIN_SMALL = 5;
    public static final int MARGIN_MEDIUM = 10;
    public static final int MARGIN_LARGE = 15;
    
    // === BORDAS ===
    public static final int BORDER_RADIUS = 5;
    public static final int BORDER_THICKNESS = 1;
    
    // === ÍCONES (emojis temporários até adicionar ícones reais) ===
    public static final String ICON_ADD = "➕";
    public static final String ICON_EDIT = "✏️";
    public static final String ICON_DELETE = "🗑️";
    public static final String ICON_SAVE = "💾";
    public static final String ICON_CANCEL = "❌";
    public static final String ICON_SEARCH = "🔍";
    public static final String ICON_REFRESH = "🔄";
    public static final String ICON_SUCCESS = "✅";
    public static final String ICON_ERROR = "❌";
    public static final String ICON_WARNING = "⚠️";
    public static final String ICON_INFO = "ℹ️";
    
    // === MENSAGENS PADRÃO ===
    public static final String MSG_CONFIRM_DELETE = "Tem certeza que deseja excluir este registro?";
    public static final String MSG_SUCCESS_SAVE = "Registro salvo com sucesso!";
    public static final String MSG_SUCCESS_UPDATE = "Registro atualizado com sucesso!";
    public static final String MSG_SUCCESS_DELETE = "Registro excluído com sucesso!";
    public static final String MSG_ERROR_SAVE = "Erro ao salvar registro.";
    public static final String MSG_ERROR_LOAD = "Erro ao carregar dados.";
    public static final String MSG_ERROR_DELETE = "Erro ao excluir registro.";
    public static final String MSG_VALIDATION_ERROR = "Por favor, preencha todos os campos obrigatórios.";
    public static final String MSG_CONNECTION_ERROR = "Erro de conexão com o servidor.";
    
    // === CONFIGURAÇÕES DE TABELA ===
    public static final int TABLE_ROW_HEIGHT = 30;
    public static final Color TABLE_HEADER_BACKGROUND = PRIMARY_COLOR;
    public static final Color TABLE_HEADER_FOREGROUND = Color.WHITE;
    public static final Color TABLE_SELECTION_BACKGROUND = PRIMARY_LIGHT;
    public static final Color TABLE_SELECTION_FOREGROUND = Color.BLACK;
    public static final Color TABLE_GRID_COLOR = BORDER_COLOR;
    
    // Construtor privado para evitar instanciação
    private UIConstants() {
        throw new AssertionError("Classe de constantes não deve ser instanciada");
    }
}
