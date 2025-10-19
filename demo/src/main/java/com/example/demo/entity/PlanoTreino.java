package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidade que representa um Plano de Treino criado por um instrutor para um aluno
 */
@Entity
@Table(name = "planostreino")
public class PlanoTreino {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plano_treino")
    private Long idPlanoTreino;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instrutor", nullable = false)
    private Instrutor instrutor;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;
    
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;
    
    @Column(name = "duracao_semanas")
    private Integer duracaoSemanas;
    
    // Construtores
    public PlanoTreino() {
    }
    
    public PlanoTreino(Aluno aluno, Instrutor instrutor, LocalDate dataCriacao) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataCriacao = dataCriacao;
    }
    
    public PlanoTreino(Aluno aluno, Instrutor instrutor, LocalDate dataCriacao, String descricao, Integer duracaoSemanas) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataCriacao = dataCriacao;
        this.descricao = descricao;
        this.duracaoSemanas = duracaoSemanas;
    }
    
    // Getters e Setters
    public Long getIdPlanoTreino() {
        return idPlanoTreino;
    }
    
    public void setIdPlanoTreino(Long idPlanoTreino) {
        this.idPlanoTreino = idPlanoTreino;
    }
    
    public Aluno getAluno() {
        return aluno;
    }
    
    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
    
    public Instrutor getInstrutor() {
        return instrutor;
    }
    
    public void setInstrutor(Instrutor instrutor) {
        this.instrutor = instrutor;
    }
    
    public LocalDate getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Integer getDuracaoSemanas() {
        return duracaoSemanas;
    }
    
    public void setDuracaoSemanas(Integer duracaoSemanas) {
        this.duracaoSemanas = duracaoSemanas;
    }
    
    @Override
    public String toString() {
        return "PlanoTreino{" +
                "idPlanoTreino=" + idPlanoTreino +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", instrutor=" + (instrutor != null ? instrutor.getNome() : "null") +
                ", dataCriacao=" + dataCriacao +
                ", duracaoSemanas=" + duracaoSemanas +
                '}';
    }
}
