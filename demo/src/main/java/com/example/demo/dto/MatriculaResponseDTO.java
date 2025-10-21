package com.example.demo.dto;

import java.time.LocalDate;

import com.example.demo.entity.Matricula;
import com.example.demo.enums.MatriculaStatus;

public class MatriculaResponseDTO {
    
    private Long id;
    private Long idAluno;
    private String nomeAluno;
    private Long idPlano;
    private String nomePlano;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private MatriculaStatus status;
    
    public MatriculaResponseDTO() {
    }
    
    public MatriculaResponseDTO(Matricula matricula) {
        this.id = matricula.getIdMatricula();
        this.idAluno = matricula.getAluno().getIdAluno();
        this.nomeAluno = matricula.getAluno().getNome();
        this.idPlano = matricula.getPlano().getIdPlanoAssinatura();
        this.nomePlano = matricula.getPlano().getNome();
        this.dataInicio = matricula.getDataInicio();
        this.dataFim = matricula.getDataFim();
        this.status = matricula.getStatus();
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
    
    public Long getIdPlano() {
        return idPlano;
    }
    
    public void setIdPlano(Long idPlano) {
        this.idPlano = idPlano;
    }
    
    public String getNomePlano() {
        return nomePlano;
    }
    
    public void setNomePlano(String nomePlano) {
        this.nomePlano = nomePlano;
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