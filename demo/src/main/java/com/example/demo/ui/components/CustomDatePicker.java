package com.example.demo.ui.components;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Calendar;

import static com.example.demo.ui.utils.UIConstants.*;

/**
 * Seletor de data customizado usando JSpinner.
 * Fornece interface amigável para seleção de datas.
 */
public class CustomDatePicker extends JPanel {
    
    private JSpinner dateSpinner;
    private SimpleDateFormat dateFormat;
    
    /**
     * Construtor padrão - data atual
     */
    public CustomDatePicker() {
        this(new Date());
    }
    
    /**
     * Construtor com data inicial
     * 
     * @param initialDate Data inicial
     */
    public CustomDatePicker(Date initialDate) {
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        setupDatePicker(initialDate);
    }
    
    /**
     * Construtor com LocalDate
     * 
     * @param localDate Data inicial
     */
    public CustomDatePicker(LocalDate localDate) {
        this(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
    
    /**
     * Configura o seletor de data
     * 
     * @param initialDate Data inicial
     */
    private void setupDatePicker(Date initialDate) {
        setLayout(new BorderLayout());
        setBackground(CARD_BACKGROUND);
        
        // Cria o modelo do spinner
        SpinnerDateModel dateModel = new SpinnerDateModel(
            initialDate,
            null, // sem data mínima
            null, // sem data máxima
            Calendar.DAY_OF_MONTH
        );
        
        // Cria o spinner
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setFont(FONT_REGULAR);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setPreferredSize(new Dimension(150, TEXTFIELD_HEIGHT));
        
        // Estiliza o spinner
        JComponent editor = dateSpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
            spinnerEditor.getTextField().setFont(FONT_REGULAR);
            spinnerEditor.getTextField().setBackground(CARD_BACKGROUND);
            spinnerEditor.getTextField().setForeground(TEXT_PRIMARY);
            spinnerEditor.getTextField().setCaretColor(PRIMARY_COLOR);
        }
        
        // Adiciona label com ícone
        JLabel iconLabel = new JLabel("📅");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setForeground(PRIMARY_COLOR);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, PADDING_MEDIUM, 0, PADDING_SMALL));
        
        add(iconLabel, BorderLayout.WEST);
        add(dateSpinner, BorderLayout.CENTER);
        
        // Borda com destaque
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true),
            BorderFactory.createEmptyBorder(PADDING_SMALL, PADDING_SMALL, PADDING_SMALL, PADDING_MEDIUM)
        ));
    }
    
    /**
     * Retorna a data selecionada como Date
     * 
     * @return Data selecionada
     */
    public Date getDate() {
        return (Date) dateSpinner.getValue();
    }
    
    /**
     * Retorna a data selecionada como LocalDate
     * 
     * @return Data selecionada
     */
    public LocalDate getLocalDate() {
        Date date = getDate();
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }
    
    /**
     * Define a data
     * 
     * @param date Data a ser definida
     */
    public void setDate(Date date) {
        dateSpinner.setValue(date);
    }
    
    /**
     * Define a data usando LocalDate
     * 
     * @param localDate Data a ser definida
     */
    public void setLocalDate(LocalDate localDate) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        setDate(date);
    }
    
    /**
     * Define a data para hoje
     */
    public void setToday() {
        setDate(new Date());
    }
    
    /**
     * Retorna a data formatada como String
     * 
     * @return Data formatada (dd/MM/yyyy)
     */
    public String getFormattedDate() {
        return dateFormat.format(getDate());
    }
    
    /**
     * Habilita ou desabilita o seletor
     * 
     * @param enabled true para habilitar, false para desabilitar
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateSpinner.setEnabled(enabled);
    }
    
    /**
     * Adiciona listener para mudanças de data
     * 
     * @param listener ChangeListener
     */
    public void addDateChangeListener(javax.swing.event.ChangeListener listener) {
        dateSpinner.addChangeListener(listener);
    }
    
    /**
     * Valida se a data está no futuro
     * 
     * @return true se a data é futura, false caso contrário
     */
    public boolean isFutureDate() {
        return getDate().after(new Date());
    }
    
    /**
     * Valida se a data está no passado
     * 
     * @return true se a data é passada, false caso contrário
     */
    public boolean isPastDate() {
        return getDate().before(new Date());
    }
    
    /**
     * Valida se a data é hoje
     * 
     * @return true se a data é hoje, false caso contrário
     */
    public boolean isToday() {
        LocalDate today = LocalDate.now();
        LocalDate selected = getLocalDate();
        return today.equals(selected);
    }
    
    /**
     * Cria um date picker com label
     * 
     * @param labelText Texto do label
     * @return Panel contendo label e date picker
     */
    public static JPanel createWithLabel(String labelText) {
        return createWithLabel(labelText, new Date());
    }
    
    /**
     * Cria um date picker com label e data inicial
     * 
     * @param labelText Texto do label
     * @param initialDate Data inicial
     * @return Panel contendo label e date picker
     */
    public static JPanel createWithLabel(String labelText, Date initialDate) {
        JPanel panel = new JPanel(new BorderLayout(PADDING_MEDIUM, 0));
        panel.setBackground(PANEL_BACKGROUND);
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        
        CustomDatePicker datePicker = new CustomDatePicker(initialDate);
        
        panel.add(label, BorderLayout.WEST);
        panel.add(datePicker, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Cria um date picker com label e LocalDate inicial
     * 
     * @param labelText Texto do label
     * @param localDate Data inicial
     * @return Panel contendo label e date picker
     */
    public static JPanel createWithLabel(String labelText, LocalDate localDate) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return createWithLabel(labelText, date);
    }
}
