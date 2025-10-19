package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidade que representa uma Matrícula de aluno em um plano na academia
 */
@Entity
@Table(name = "matriculas")
public class Matricula {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matricula")
    private Long idMatricula;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plano_assinatura", nullable = false)
    private Plano plano;
    
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status;
    
    // Construtores
    public Matricula() {
    }
    
    public Matricula(Aluno aluno, Plano plano, LocalDate dataInicio, LocalDate dataFim, String status) {
        this.aluno = aluno;
        this.plano = plano;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
    }
    
    // Getters e Setters
    public Long getIdMatricula() {
        return idMatricula;
    }
    
    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }
    
    public Aluno getAluno() {
        return aluno;
    }
    
    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
    
    public Plano getPlano() {
        return plano;
    }
    
    public void setPlano(Plano plano) {
        this.plano = plano;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Matricula{" +
                "idMatricula=" + idMatricula +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", plano=" + (plano != null ? plano.getNome() : "null") +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", status='" + status + '\'' +
                '}';
    }
}
