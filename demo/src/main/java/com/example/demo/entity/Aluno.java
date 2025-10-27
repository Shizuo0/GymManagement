package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade que representa um Aluno no sistema de gestão da academia
 */
@Entity
@Table(name = "Alunos")
public class Aluno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aluno")
    private Long idAluno;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "cpf", nullable = false, unique = true, length = 14)
    private String cpf;
    
    @Column(name = "data_ingresso")
    private LocalDate dataIngresso;
    
    // Construtores
    public Aluno() {
    }
    
    public Aluno(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }
    
    public Aluno(String nome, String cpf, LocalDate dataIngresso) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataIngresso = dataIngresso;
    }
    
    // Getters e Setters
    public Long getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public LocalDate getDataIngresso() {
        return dataIngresso;
    }
    
    public void setDataIngresso(LocalDate dataIngresso) {
        this.dataIngresso = dataIngresso;
    }
    
    @Override
    public String toString() {
        return "Aluno{" +
                "idAluno=" + idAluno +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", dataIngresso=" + dataIngresso +
                '}';
    }
}
