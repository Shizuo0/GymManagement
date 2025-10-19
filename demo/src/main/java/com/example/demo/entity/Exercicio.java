package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * Entidade que representa um Exercício no catálogo da academia
 */
@Entity
@Table(name = "exercicios")
public class Exercicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercicio")
    private Long idExercicio;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "grupo_muscular", length = 50)
    private String grupoMuscular;
    
    // Construtores
    public Exercicio() {
    }
    
    public Exercicio(String nome) {
        this.nome = nome;
    }
    
    public Exercicio(String nome, String grupoMuscular) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
    }
    
    // Getters e Setters
    public Long getIdExercicio() {
        return idExercicio;
    }
    
    public void setIdExercicio(Long idExercicio) {
        this.idExercicio = idExercicio;
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
    
    @Override
    public String toString() {
        return "Exercicio{" +
                "idExercicio=" + idExercicio +
                ", nome='" + nome + '\'' +
                ", grupoMuscular='" + grupoMuscular + '\'' +
                '}';
    }
}
