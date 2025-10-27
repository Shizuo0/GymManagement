package com.example.demo.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidade que representa o registro de Frequência de um aluno na academia
 */
@Entity
@Table(name = "Frequencia")
public class Frequencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_frequencia")
    private Long idFrequencia;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluno", nullable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_frequencia_aluno"))
    private Aluno aluno;
    
    @Column(name = "data", nullable = false)
    private LocalDate data;
    
    @Column(name = "presenca", nullable = false)
    private Boolean presenca = false;
    
    // Construtores
    public Frequencia() {
    }
    
    public Frequencia(Aluno aluno, LocalDate data, Boolean presenca) {
        this.aluno = aluno;
        this.data = data;
        this.presenca = presenca;
    }
    
    // Getters e Setters
    public Long getIdFrequencia() {
        return idFrequencia;
    }
    
    public void setIdFrequencia(Long idFrequencia) {
        this.idFrequencia = idFrequencia;
    }
    
    public Aluno getAluno() {
        return aluno;
    }
    
    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public Boolean getPresenca() {
        return presenca;
    }
    
    public void setPresenca(Boolean presenca) {
        this.presenca = presenca;
    }
    
    @Override
    public String toString() {
        return "Frequencia{" +
                "idFrequencia=" + idFrequencia +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", data=" + data +
                ", presenca=" + presenca +
                '}';
    }
}
