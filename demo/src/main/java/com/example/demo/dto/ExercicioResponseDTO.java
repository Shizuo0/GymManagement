package com.example.demo.dto;

public class ExercicioResponseDTO {
    
    private Long id;
    private String nome;
    private String grupoMuscular;
    
    public ExercicioResponseDTO(Long id, String nome, String grupoMuscular) {
        this.id = id;
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getGrupoMuscular() {
        return grupoMuscular;
    }
    
    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }
}