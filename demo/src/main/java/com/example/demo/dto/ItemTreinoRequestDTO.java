package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemTreinoRequestDTO {
    
    @NotNull(message = "O ID do exercício é obrigatório")
    private Long exercicioId;
    
    @NotNull(message = "O ID do plano de treino é obrigatório")
    private Long planoTreinoId;
    
    @NotNull(message = "O número de séries é obrigatório")
    @Min(value = 1, message = "O número de séries deve ser maior que zero")
    private Integer series;
    
    @NotNull(message = "O número de repetições é obrigatório")
    @Min(value = 1, message = "O número de repetições deve ser maior que zero")
    private Integer repeticoes;
    
    @Min(value = 0, message = "A carga não pode ser negativa")
    private BigDecimal carga;
    
    private String observacoes;
    
    // Getters e Setters
    public Long getExercicioId() {
        return exercicioId;
    }
    
    public void setExercicioId(Long exercicioId) {
        this.exercicioId = exercicioId;
    }
    
    public Long getPlanoTreinoId() {
        return planoTreinoId;
    }
    
    public void setPlanoTreinoId(Long planoTreinoId) {
        this.planoTreinoId = planoTreinoId;
    }
    
    public Integer getSeries() {
        return series;
    }
    
    public void setSeries(Integer series) {
        this.series = series;
    }
    
    public Integer getRepeticoes() {
        return repeticoes;
    }
    
    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }
    
    public BigDecimal getCarga() {
        return carga;
    }
    
    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}