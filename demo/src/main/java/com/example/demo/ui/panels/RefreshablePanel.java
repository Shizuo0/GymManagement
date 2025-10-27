package com.example.demo.ui.panels;

/**
 * Interface para painéis que podem atualizar seus dados.
 * Implementar esta interface permite que o GymManagementUI notifique
 * os painéis quando dados relevantes foram alterados.
 */
public interface RefreshablePanel {
    
    /**
     * Atualiza os dados exibidos no painel.
     * Este método será chamado quando dados no sistema forem alterados
     * e o painel precisa refletir essas alterações.
     */
    void refreshData();
}
