package com.example.demo.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidade que representa um Exercício no catálogo da academia
 */
@Entity
@Table(name = "Exercicios")
public class Exercicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercicio")
    private Long idExercicio;
    
    @NotBlank(message = "O nome do exercício é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Size(max = 50, message = "O grupo muscular deve ter no máximo 50 caracteres")
    @Column(name = "grupo_muscular", length = 50)
    private String grupoMuscular;
    
    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao", length = 500)
    private String descricao;
    
    @OneToMany(mappedBy = "exercicio", cascade = CascadeType.ALL)
    private Set<ItemTreino> itensTreino = new HashSet<>();
    
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
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
