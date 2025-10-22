package com.example.demo.dto;

import java.time.LocalDate;

/**
 * DTO para resumo de Frequência no histórico do aluno
 */
public class FrequenciaSummaryDTO {
    
    private LocalDate mes; // Primeiro dia do mês
    private Long totalDias;
    private Long totalPresencas;
    private Long totalAusencias;
    private Double taxaPresenca; // Percentual de presença
    
    // Construtores
    public FrequenciaSummaryDTO() {
    }
    
    public FrequenciaSummaryDTO(LocalDate mes, Long totalDias, Long totalPresencas, Long totalAusencias) {
        this.mes = mes;
        this.totalDias = totalDias;
        this.totalPresencas = totalPresencas;
        this.totalAusencias = totalAusencias;
        this.taxaPresenca = calcularTaxaPresenca(totalPresencas, totalDias);
    }
    
    // Método auxiliar para calcular taxa de presença
    private Double calcularTaxaPresenca(Long presencas, Long total) {
        if (total == null || total == 0) {
            return 0.0;
        }
        return (presencas.doubleValue() / total.doubleValue()) * 100;
    }
    
    // Getters e Setters
    public LocalDate getMes() {
        return mes;
    }
    
    public void setMes(LocalDate mes) {
        this.mes = mes;
    }
    
    public Long getTotalDias() {
        return totalDias;
    }
    
    public void setTotalDias(Long totalDias) {
        this.totalDias = totalDias;
        this.taxaPresenca = calcularTaxaPresenca(this.totalPresencas, this.totalDias);
    }
    
    public Long getTotalPresencas() {
        return totalPresencas;
    }
    
    public void setTotalPresencas(Long totalPresencas) {
        this.totalPresencas = totalPresencas;
        this.taxaPresenca = calcularTaxaPresenca(this.totalPresencas, this.totalDias);
    }
    
    public Long getTotalAusencias() {
        return totalAusencias;
    }
    
    public void setTotalAusencias(Long totalAusencias) {
        this.totalAusencias = totalAusencias;
    }
    
    public Double getTaxaPresenca() {
        return taxaPresenca;
    }
    
    public void setTaxaPresenca(Double taxaPresenca) {
        this.taxaPresenca = taxaPresenca;
    }
    
    @Override
    public String toString() {
        return "FrequenciaSummaryDTO{" +
                "mes=" + mes +
                ", totalDias=" + totalDias +
                ", totalPresencas=" + totalPresencas +
                ", totalAusencias=" + totalAusencias +
                ", taxaPresenca=" + String.format("%.2f%%", taxaPresenca) +
                '}';
    }
}
