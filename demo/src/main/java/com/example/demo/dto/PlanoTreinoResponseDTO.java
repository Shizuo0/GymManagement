package com.example.demo.dto;

import java.time.LocalDate;

/**
 * DTO para resposta com dados de PlanoTreino
 */
public class PlanoTreinoResponseDTO {
    
    private Long id;
    private Long idAluno;
    private String nomeAluno;
    private Long idInstrutor;
    private String nomeInstrutor;
    private LocalDate dataCriacao;
    private String descricao;
    private Integer duracaoSemanas;
    
    // Construtores
    public PlanoTreinoResponseDTO() {
    }
    
    public PlanoTreinoResponseDTO(Long id, Long idAluno, String nomeAluno, Long idInstrutor, 
                                  String nomeInstrutor, LocalDate dataCriacao, String descricao, 
                                  Integer duracaoSemanas) {
        this.id = id;
        this.idAluno = idAluno;
        this.nomeAluno = nomeAluno;
        this.idInstrutor = idInstrutor;
        this.nomeInstrutor = nomeInstrutor;
        this.dataCriacao = dataCriacao;
        this.descricao = descricao;
        this.duracaoSemanas = duracaoSemanas;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }
    
    public String getNomeAluno() {
        return nomeAluno;
    }
    
    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }
    
    public Long getIdInstrutor() {
        return idInstrutor;
    }
    
    public void setIdInstrutor(Long idInstrutor) {
        this.idInstrutor = idInstrutor;
    }
    
    public String getNomeInstrutor() {
        return nomeInstrutor;
    }
    
    public void setNomeInstrutor(String nomeInstrutor) {
        this.nomeInstrutor = nomeInstrutor;
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
}
