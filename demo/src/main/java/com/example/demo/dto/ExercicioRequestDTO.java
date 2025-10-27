package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ExercicioRequestDTO {
    
    @NotBlank(message = "O nome do exercício é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;
    
    @Size(max = 50, message = "O grupo muscular deve ter no máximo 50 caracteres")
    private String grupoMuscular;
    
    @Size(max = 50, message = "A descrição deve ter no máximo 50 caracteres")
    private String descricao;
    
    // Getters e Setters
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
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}