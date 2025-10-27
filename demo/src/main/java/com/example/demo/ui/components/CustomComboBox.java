package com.example.demo.ui.components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * ComboBox customizado com tema escuro
 */
public class CustomComboBox<E> extends JComboBox<E> {
    
    public CustomComboBox() {
        super();
        setupStyle();
    }
    
    public CustomComboBox(E[] items) {
        super(items);
        setupStyle();
    }
    
    public CustomComboBox(ComboBoxModel<E> model) {
        super(model);
        setupStyle();
    }
    
    private void setupStyle() {
        setFont(FONT_REGULAR);
        setBackground(CARD_BACKGROUND);
        setForeground(TEXT_PRIMARY);
        
        // Remove borda padrão
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        
        // Aplica UI customizada
        setUI(new DarkComboBoxUI());
        
        // Customiza o renderer para itens do dropdown
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                
                label.setFont(FONT_REGULAR);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                
                if (isSelected) {
                    label.setBackground(PRIMARY_COLOR);
                    label.setForeground(TEXT_ON_PRIMARY);
                } else {
                    label.setBackground(CARD_BACKGROUND);
                    label.setForeground(TEXT_PRIMARY);
                }
                
                return label;
            }
        });
    }
    
    /**
     * UI customizada para o ComboBox com tema escuro
     */
    private static class DarkComboBoxUI extends BasicComboBoxUI {
        
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼") {
                @Override
                public void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fundo do botão
                    g2.setColor(SURFACE_COLOR);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // Desenha a seta
                    g2.setColor(TEXT_PRIMARY);
                    g2.setFont(new Font("Arial", Font.PLAIN, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth("▼")) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    g2.drawString("▼", x, y);
                    
                    g2.dispose();
                }
            };
            
            button.setPreferredSize(new Dimension(20, 20));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setOpaque(true);
            button.setBackground(SURFACE_COLOR);
            
            return button;
        }
        
        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                @Override
                protected JScrollPane createScroller() {
                    JScrollPane scroller = super.createScroller();
                    scroller.getViewport().setBackground(CARD_BACKGROUND);
                    scroller.setBorder(null);
                    scroller.getVerticalScrollBar().setBackground(CARD_BACKGROUND);
                    scroller.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
                        @Override
                        protected void configureScrollBarColors() {
                            this.thumbColor = SURFACE_COLOR;
                            this.trackColor = CARD_BACKGROUND;
                        }
                        
                        @Override
                        protected JButton createDecreaseButton(int orientation) {
                            return createZeroButton();
                        }
                        
                        @Override
                        protected JButton createIncreaseButton(int orientation) {
                            return createZeroButton();
                        }
                        
                        private JButton createZeroButton() {
                            JButton button = new JButton();
                            button.setPreferredSize(new Dimension(0, 0));
                            button.setMinimumSize(new Dimension(0, 0));
                            button.setMaximumSize(new Dimension(0, 0));
                            return button;
                        }
                    });
                    return scroller;
                }
                
                @Override
                protected void configurePopup() {
                    super.configurePopup();
                    setOpaque(false);
                    setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2));
                    setBackground(CARD_BACKGROUND);
                }
                
                @Override
                protected void configureList() {
                    super.configureList();
                    list.setBackground(CARD_BACKGROUND);
                    list.setForeground(TEXT_PRIMARY);
                    list.setSelectionBackground(PRIMARY_COLOR);
                    list.setSelectionForeground(TEXT_ON_PRIMARY);
                    list.setFont(FONT_REGULAR);
                }
            };
            
            return popup;
        }
        
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Pinta o fundo do valor atual
            g.setColor(CARD_BACKGROUND);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Customiza a renderização do valor atual
            ListCellRenderer<Object> renderer = (ListCellRenderer<Object>) comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(
                listBox, comboBox.getSelectedItem(), -1, false, false);
            
            c.setFont(comboBox.getFont());
            
            if (hasFocus && !isPopupVisible(comboBox)) {
                c.setForeground(listBox.getForeground());
                c.setBackground(listBox.getBackground());
            } else {
                c.setForeground(TEXT_PRIMARY);
                c.setBackground(CARD_BACKGROUND);
            }
            
            // Renderiza o componente
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y,
                bounds.width, bounds.height, false);
        }
    }
    
    /**
     * Cria um ComboBox com array de items
     */
    public static <T> CustomComboBox<T> create(T[] items) {
        return new CustomComboBox<>(items);
    }
    
    /**
     * Cria um ComboBox vazio
     */
    public static <T> CustomComboBox<T> create() {
        return new CustomComboBox<>();
    }
}
