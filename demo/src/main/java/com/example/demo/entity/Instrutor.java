package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade que representa um Instrutor na academia
 */
@Entity
@Table(name = "Instrutores")
public class Instrutor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_instrutor")
    private Long idInstrutor;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "especialidade", length = 50)
    private String especialidade;
    
    // Construtores
    public Instrutor() {
    }
    
    public Instrutor(String nome, String especialidade) {
        this.nome = nome;
        this.especialidade = especialidade;
    }
    
    // Getters e Setters
    public Long getIdInstrutor() {
        return idInstrutor;
    }
    
    public void setIdInstrutor(Long idInstrutor) {
        this.idInstrutor = idInstrutor;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEspecialidade() {
        return especialidade;
    }
    
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
    
    @Override
    public String toString() {
        return "Instrutor{" +
                "idInstrutor=" + idInstrutor +
                ", nome='" + nome + '\'' +
                ", especialidade='" + especialidade + '\'' +
                '}';
    }
}
