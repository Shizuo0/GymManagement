package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.enums.MatriculaStatus;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public class MatriculaRequestDTO {
    
    @NotNull(message = "ID do aluno é obrigatório")
    private Long idAluno;
    
    @NotNull(message = "ID do plano é obrigatório")
    private Long idPlano;
    
    @NotNull(message = "Data de início é obrigatória")
    @FutureOrPresent(message = "Data de início deve ser hoje ou futura")
    private LocalDate dataInicio;
    
    @FutureOrPresent(message = "Data de fim deve ser hoje ou futura")
    private LocalDate dataFim;
    
    private MatriculaStatus status;
    
    // Construtores
    public MatriculaRequestDTO() {
    }
    
    public MatriculaRequestDTO(Long idAluno, Long idPlano, LocalDate dataInicio, LocalDate dataFim) {
        this.idAluno = idAluno;
        this.idPlano = idPlano;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }
    
    // Getters e Setters
    public Long getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }
    
    public Long getIdPlano() {
        return idPlano;
    }
    
    public void setIdPlano(Long idPlano) {
        this.idPlano = idPlano;
    }
    
    public LocalDate getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public LocalDate getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
    
    public MatriculaStatus getStatus() {
        return status;
    }
    
    public void setStatus(MatriculaStatus status) {
        this.status = status;
    }
}