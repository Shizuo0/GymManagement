package com.example.demo.ui.components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Campo de texto customizado com estilo consistente e validação visual.
 * Suporta placeholder, validação em tempo real e feedback visual de erros.
 */
public class CustomTextField extends JTextField {
    
    private String placeholder;
    private boolean isValid = true;
    private Color normalBorderColor = BORDER_COLOR;
    private Color focusBorderColor = BORDER_FOCUS_COLOR;
    private Color errorBorderColor = ERROR_COLOR;
    
    /**
     * Construtor padrão
     */
    public CustomTextField() {
        this(null, 20);
    }
    
    /**
     * Construtor com placeholder
     * 
     * @param placeholder Texto placeholder
     */
    public CustomTextField(String placeholder) {
        this(placeholder, 20);
    }
    
    /**
     * Construtor com placeholder e colunas
     * 
     * @param placeholder Texto placeholder
     * @param columns Número de colunas
     */
    public CustomTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        setupTextField();
    }
    
    /**
     * Configura as propriedades visuais do campo
     */
    private void setupTextField() {
        // Configurações básicas
        setFont(FONT_REGULAR);
        setForeground(TEXT_PRIMARY);
        setBackground(CARD_BACKGROUND);
        setCaretColor(PRIMARY_COLOR);
        
        // Dimensões
        setPreferredSize(new Dimension(200, TEXTFIELD_HEIGHT));
        setMinimumSize(new Dimension(100, TEXTFIELD_HEIGHT));
        
        // Borda
        updateBorder(normalBorderColor);
        
        // Margens internas
        setMargin(new Insets(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM));
        
        // Adiciona listeners para efeitos de foco
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isValid) {
                    updateBorder(focusBorderColor);
                    setBackground(SURFACE_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                setBackground(CARD_BACKGROUND);
                updateBorder(isValid ? normalBorderColor : errorBorderColor);
            }
        });
    }
    
    /**
     * Atualiza a borda do campo
     * 
     * @param color Cor da borda
     */
    private void updateBorder(Color color) {
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_MEDIUM, PADDING_SMALL, PADDING_MEDIUM)
        ));
    }
    
    /**
     * Define o placeholder
     * 
     * @param placeholder Texto placeholder
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    /**
     * Retorna o placeholder
     * 
     * @return Texto placeholder
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Define o estado de validação do campo
     * 
     * @param valid true se válido, false caso contrário
     */
    public void setValid(boolean valid) {
        this.isValid = valid;
        updateBorder(valid ? (hasFocus() ? focusBorderColor : normalBorderColor) : errorBorderColor);
    }
    
    /**
     * Verifica se o campo está válido
     * 
     * @return true se válido, false caso contrário
     */
    public boolean isValid() {
        return isValid;
    }
    
    /**
     * Marca o campo como inválido e exibe borda vermelha
     */
    public void markAsInvalid() {
        setValid(false);
    }
    
    /**
     * Marca o campo como válido e exibe borda normal
     */
    public void markAsValid() {
        setValid(true);
    }
    
    /**
     * Limpa o campo e marca como válido
     */
    public void clear() {
        setText("");
        markAsValid();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Desenha placeholder se o campo estiver vazio e sem foco
        if (placeholder != null && getText().isEmpty() && !hasFocus()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(TEXT_SECONDARY);
            g2d.setFont(getFont());
            
            FontMetrics fm = g2d.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            
            g2d.drawString(placeholder, x, y);
            g2d.dispose();
        }
    }
    
    /**
     * Cria um campo de texto para CPF com formatação automática
     * 
     * @param placeholder Texto placeholder
     * @return Campo de texto configurado
     */
    public static CustomTextField createCPFField(String placeholder) {
        CustomTextField field = new CustomTextField(placeholder);
        
        // Adiciona listener para formatar CPF enquanto digita
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = field.getText().replaceAll("[^0-9]", "");
                if (text.length() > 11) {
                    text = text.substring(0, 11);
                }
                
                if (text.length() > 0) {
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < text.length(); i++) {
                        if (i == 3 || i == 6) {
                            formatted.append(".");
                        } else if (i == 9) {
                            formatted.append("-");
                        }
                        formatted.append(text.charAt(i));
                    }
                    
                    if (!field.getText().equals(formatted.toString())) {
                        field.setText(formatted.toString());
                    }
                }
            }
        });
        
        return field;
    }
    
    /**
     * Cria um campo de texto numérico (apenas números)
     * 
     * @param placeholder Texto placeholder
     * @return Campo de texto configurado
     */
    public static CustomTextField createNumericField(String placeholder) {
        CustomTextField field = new CustomTextField(placeholder);
        
        // Adiciona listener para permitir apenas números
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != ',' && c != '\b') {
                    e.consume();
                }
            }
        });
        
        return field;
    }
    
    /**
     * Cria um campo de texto para email
     * 
     * @param placeholder Texto placeholder
     * @return Campo de texto configurado
     */
    public static CustomTextField createEmailField(String placeholder) {
        CustomTextField field = new CustomTextField(placeholder);
        field.setToolTipText("Digite um email válido (exemplo@dominio.com)");
        return field;
    }
}
