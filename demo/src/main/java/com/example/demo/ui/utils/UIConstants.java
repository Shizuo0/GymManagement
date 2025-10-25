package com.example.demo.ui.utils;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes de UI para manter consistência visual em toda a aplicação.
 * Define cores, fontes, espaçamentos e outros elementos de design.
 * TEMA: Moderno Escuro com Azul Royal
 */
public class UIConstants {
    
    // === CORES PRINCIPAIS - AZUL ROYAL ===
    public static final Color PRIMARY_COLOR = new Color(65, 105, 225);      // Royal Blue (#4169E1)
    public static final Color PRIMARY_DARK = new Color(25, 50, 150);        // Royal Blue Escuro
    public static final Color PRIMARY_LIGHT = new Color(100, 149, 237);     // Cornflower Blue (#6495ED)
    public static final Color PRIMARY_HOVER = new Color(75, 115, 235);      // Royal Blue Hover
    
    public static final Color SECONDARY_COLOR = new Color(30, 144, 255);    // Dodger Blue (#1E90FF)
    public static final Color ACCENT_COLOR = new Color(255, 69, 58);        // Vermelho Neon
    public static final Color WARNING_COLOR = new Color(255, 159, 10);      // Laranja Neon
    public static final Color SUCCESS_GLOW = new Color(48, 209, 88);        // Verde Neon
    
    // === CORES DE FUNDO - TEMA ESCURO ===
    public static final Color BACKGROUND_COLOR = new Color(18, 18, 18);     // Preto suave (#121212)
    public static final Color BACKGROUND_SECONDARY = new Color(28, 28, 30); // Cinza muito escuro (#1C1C1E)
    public static final Color PANEL_BACKGROUND = new Color(30, 30, 32);     // Cinza escuro (#1E1E20)
    public static final Color CARD_BACKGROUND = new Color(38, 38, 42);      // Cinza card (#26262A)
    public static final Color HOVER_COLOR = new Color(44, 44, 46);          // Cinza hover (#2C2C2E)
    public static final Color SURFACE_COLOR = new Color(35, 35, 37);        // Superfície (#232325)
    
    // === CORES DE TEXTO - ALTO CONTRASTE ===
    public static final Color TEXT_PRIMARY = new Color(255, 255, 255);      // Branco puro
    public static final Color TEXT_SECONDARY = new Color(174, 174, 178);    // Cinza claro (#AEAEB2)
    public static final Color TEXT_TERTIARY = new Color(142, 142, 147);     // Cinza médio (#8E8E93)
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;
    
    // === CORES DE STATUS ===
    public static final Color SUCCESS_COLOR = new Color(48, 209, 88);       // Verde Neon (#30D158)
    public static final Color ERROR_COLOR = new Color(255, 69, 58);         // Vermelho Neon (#FF453A)
    public static final Color INFO_COLOR = new Color(100, 149, 237);        // Azul info (#6495ED)
    public static final Color WARNING_TEXT_COLOR = new Color(255, 159, 10); // Laranja Neon (#FF9F0A)
    
    // === CORES DE BORDA ===
    public static final Color BORDER_COLOR = new Color(58, 58, 60);         // Cinza escuro borda (#3A3A3C)
    public static final Color BORDER_FOCUS_COLOR = PRIMARY_COLOR;
    public static final Color BORDER_LIGHT = new Color(72, 72, 74);         // Borda clara (#48484A)
    
    // === CORES DE GRADIENTE ===
    public static final Color GRADIENT_START = PRIMARY_COLOR;
    public static final Color GRADIENT_END = new Color(30, 144, 255);
    
    // === EFEITOS E SOMBRAS ===
    public static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);        // Sombra suave
    public static final Color GLOW_COLOR = new Color(65, 105, 225, 80);     // Brilho azul royal
    public static final Color OVERLAY_COLOR = new Color(0, 0, 0, 150);      // Overlay escuro
    
    // === FONTES ===
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    
    // === DIMENSÕES ===
    public static final int BUTTON_HEIGHT = 40;
    public static final int BUTTON_WIDTH = 120;
    public static final int TEXTFIELD_HEIGHT = 38;
    public static final int TEXTAREA_HEIGHT = 100;
    
    // === ESPAÇAMENTOS ===
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 12;
    public static final int PADDING_LARGE = 20;
    public static final int PADDING_XLARGE = 32;
    
    public static final int MARGIN_SMALL = 8;
    public static final int MARGIN_MEDIUM = 12;
    public static final int MARGIN_LARGE = 20;
    
    // === BORDAS E EFEITOS ===
    public static final int BORDER_RADIUS = 8;
    public static final int BORDER_RADIUS_LARGE = 12;
    public static final int BORDER_THICKNESS = 1;
    public static final int SHADOW_SIZE = 4;
    
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
    public static final int TABLE_ROW_HEIGHT = 36;
    public static final Color TABLE_HEADER_BACKGROUND = CARD_BACKGROUND;
    public static final Color TABLE_HEADER_FOREGROUND = TEXT_PRIMARY;
    public static final Color TABLE_SELECTION_BACKGROUND = PRIMARY_COLOR;
    public static final Color TABLE_SELECTION_FOREGROUND = TEXT_ON_PRIMARY;
    public static final Color TABLE_GRID_COLOR = BORDER_COLOR;
    public static final Color TABLE_ALTERNATE_ROW = new Color(42, 42, 46);  // Linha alternada (#2A2A2E)
    
    // Construtor privado para evitar instanciação
    private UIConstants() {
        throw new AssertionError("Classe de constantes não deve ser instanciada");
    }
}
