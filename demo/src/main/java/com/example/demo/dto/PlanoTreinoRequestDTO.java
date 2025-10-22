package com.example.demo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para receber requisições de criação/atualização de PlanoTreino
 */
public class PlanoTreinoRequestDTO {
    
    @NotNull(message = "O ID do aluno é obrigatório")
    private Long idAluno;
    
    @NotNull(message = "O ID do instrutor é obrigatório")
    private Long idInstrutor;
    
    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String descricao;
    
    @Min(value = 1, message = "A duração deve ser de pelo menos 1 semana")
    private Integer duracaoSemanas;
    
    private LocalDate dataCriacao;
    
    // Construtores
    public PlanoTreinoRequestDTO() {
    }
    
    public PlanoTreinoRequestDTO(Long idAluno, Long idInstrutor, String descricao, Integer duracaoSemanas) {
        this.idAluno = idAluno;
        this.idInstrutor = idInstrutor;
        this.descricao = descricao;
        this.duracaoSemanas = duracaoSemanas;
    }
    
    // Getters e Setters
    public Long getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }
    
    public Long getIdInstrutor() {
        return idInstrutor;
    }
    
    public void setIdInstrutor(Long idInstrutor) {
        this.idInstrutor = idInstrutor;
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
    
    public LocalDate getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
