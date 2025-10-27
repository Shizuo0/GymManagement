package com.example.demo.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Botão customizado com estilo consistente e efeitos hover.
 * Suporta diferentes tipos: primário, secundário, perigo, sucesso.
 */
public class CustomButton extends JButton {
    
    public enum ButtonType {
        PRIMARY(PRIMARY_COLOR, PRIMARY_HOVER, TEXT_ON_PRIMARY),
        SECONDARY(SECONDARY_COLOR, new Color(50, 154, 255), TEXT_ON_PRIMARY),
        DANGER(ERROR_COLOR, new Color(255, 89, 78), TEXT_ON_PRIMARY),
        SUCCESS(SUCCESS_COLOR, new Color(68, 219, 108), TEXT_ON_PRIMARY),
        WARNING(WARNING_COLOR, new Color(255, 179, 30), TEXT_ON_PRIMARY),
        DEFAULT(CARD_BACKGROUND, HOVER_COLOR, TEXT_PRIMARY),
        OUTLINE(PANEL_BACKGROUND, HOVER_COLOR, PRIMARY_COLOR);
        
        final Color baseColor;
        final Color hoverColor;
        final Color textColor;
        
        ButtonType(Color baseColor, Color hoverColor, Color textColor) {
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.textColor = textColor;
        }
    }
    
    private ButtonType type;
    
    /**
     * Construtor padrão - cria botão primário
     * 
     * @param text Texto do botão
     */
    public CustomButton(String text) {
        this(text, ButtonType.PRIMARY);
    }
    
    /**
     * Construtor com tipo
     * 
     * @param text Texto do botão
     * @param type Tipo do botão
     */
    public CustomButton(String text, ButtonType type) {
        super(text);
        this.type = type;
        setupButton();
    }
    
    /**
     * Construtor com ícone
     * 
     * @param text Texto do botão
     * @param icon Ícone do botão
     * @param type Tipo do botão
     */
    public CustomButton(String text, Icon icon, ButtonType type) {
        super(text, icon);
        this.type = type;
        setupButton();
    }
    
    /**
     * Configura as propriedades visuais do botão
     */
    private void setupButton() {
        // Configurações básicas
        setFont(FONT_BUTTON);
        setForeground(type.textColor);
        setBackground(type.baseColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Dimensões
        setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        setMinimumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        
        // Margens internas
        setMargin(new Insets(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM));
        
        // Adiciona efeito hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(type.hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(type.baseColor);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(type.hoverColor.darker());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled()) {
                    setBackground(type.hoverColor);
                }
            }
        });
    }
    
    /**
     * Altera o tipo do botão
     * 
     * @param type Novo tipo
     */
    public void setButtonType(ButtonType type) {
        this.type = type;
        setForeground(type.textColor);
        setBackground(type.baseColor);
    }
    
    /**
     * Retorna o tipo atual do botão
     * 
     * @return Tipo do botão
     */
    public ButtonType getButtonType() {
        return type;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setBackground(type.baseColor);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setBackground(SURFACE_COLOR);
            setForeground(TEXT_TERTIARY);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Adiciona sombra sutil
        if (isEnabled()) {
            g2d.setColor(SHADOW_COLOR);
            g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, BORDER_RADIUS, BORDER_RADIUS);
        }
        
        // Pinta o fundo
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, BORDER_RADIUS, BORDER_RADIUS);
        
        // Adiciona borda para tipo OUTLINE
        if (type == ButtonType.OUTLINE) {
            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 4, BORDER_RADIUS, BORDER_RADIUS);
        }
        
        g2d.dispose();
        super.paintComponent(g);
    }
    
    /**
     * Cria um botão de adicionar
     * 
     * @param text Texto do botão
     * @return Botão configurado
     */
    public static CustomButton createAddButton(String text) {
        return new CustomButton(ICON_ADD + " " + text, ButtonType.SUCCESS);
    }
    
    /**
     * Cria um botão de editar
     * 
     * @param text Texto do botão
     * @return Botão configurado
     */
    public static CustomButton createEditButton(String text) {
        return new CustomButton(ICON_EDIT + " " + text, ButtonType.PRIMARY);
    }
    
    /**
     * Cria um botão de excluir
     * 
     * @param text Texto do botão
     * @return Botão configurado
     */
    public static CustomButton createDeleteButton(String text) {
        return new CustomButton(ICON_DELETE + " " + text, ButtonType.DANGER);
    }
    
    /**
     * Cria um botão de salvar
     * 
     * @param text Texto do botão
     * @return Botão configurado
     */
    public static CustomButton createSaveButton(String text) {
        return new CustomButton(ICON_SAVE + " " + text, ButtonType.SUCCESS);
    }
    
    /**
     * Cria um botão de cancelar
     * 
     * @param text Texto do botão
     * @return Botão configurado
     */
    public static CustomButton createCancelButton(String text) {
        return new CustomButton(ICON_CANCEL + " " + text, ButtonType.DEFAULT);
    }
    
    /**
     * Cria um botão de buscar
     * 
     * @param text Texto do botão
     * @return Botão configurado
     */
    public static CustomButton createSearchButton(String text) {
        return new CustomButton(ICON_SEARCH + " " + text, ButtonType.PRIMARY);
    }
    
    /**
     * Cria um botão de atualizar
     * 
     * @param text Texto do botão
     * @return Botão configurado
     */
    public static CustomButton createRefreshButton(String text) {
        return new CustomButton(ICON_REFRESH + " " + text, ButtonType.PRIMARY);
    }
}
