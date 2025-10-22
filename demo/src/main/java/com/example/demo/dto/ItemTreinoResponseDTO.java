package com.example.demo.dto;

import java.math.BigDecimal;

public class ItemTreinoResponseDTO {
    
    private Long id;
    private Long planoTreinoId;
    private String planoTreinoNome;
    private Long exercicioId;
    private String exercicioNome;
    private Integer series;
    private Integer repeticoes;
    private BigDecimal peso;
    
    public ItemTreinoResponseDTO(Long id, Long planoTreinoId, String planoTreinoNome,
                                Long exercicioId, String exercicioNome,
                                Integer series, Integer repeticoes, BigDecimal peso) {
        this.id = id;
        this.planoTreinoId = planoTreinoId;
        this.planoTreinoNome = planoTreinoNome;
        this.exercicioId = exercicioId;
        this.exercicioNome = exercicioNome;
        this.series = series;
        this.repeticoes = repeticoes;
        this.peso = peso;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getPlanoTreinoId() {
        return planoTreinoId;
    }
    
    public void setPlanoTreinoId(Long planoTreinoId) {
        this.planoTreinoId = planoTreinoId;
    }
    
    public String getPlanoTreinoNome() {
        return planoTreinoNome;
    }
    
    public void setPlanoTreinoNome(String planoTreinoNome) {
        this.planoTreinoNome = planoTreinoNome;
    }
    
    public Long getExercicioId() {
        return exercicioId;
    }
    
    public void setExercicioId(Long exercicioId) {
        this.exercicioId = exercicioId;
    }
    
    public String getExercicioNome() {
        return exercicioNome;
    }
    
    public void setExercicioNome(String exercicioNome) {
        this.exercicioNome = exercicioNome;
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
    
    public BigDecimal getPeso() {
        return peso;
    }
    
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }
}