package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade que representa uma Avaliação Física de um aluno
 */
@Entity
@Table(name = "avaliacoesfisicas")
public class AvaliacaoFisica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Long idAvaliacao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluno", nullable = false)
    private Aluno aluno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_instrutor", nullable = false)
    private Instrutor instrutor;
    
    @Column(name = "data_avaliacao", nullable = false)
    private LocalDate dataAvaliacao;
    
    @Column(name = "peso", precision = 5, scale = 2)
    private BigDecimal peso;
    
    @Column(name = "altura", precision = 5, scale = 2)
    private BigDecimal altura;
    
    @Column(name = "percentual_gordura", precision = 5, scale = 2)
    private BigDecimal percentualGordura;
    
    @Column(name = "medidas_corporais", columnDefinition = "TEXT")
    private String medidasCorporais;
    
    // Construtores
    public AvaliacaoFisica() {
    }
    
    public AvaliacaoFisica(Aluno aluno, Instrutor instrutor, LocalDate dataAvaliacao) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataAvaliacao = dataAvaliacao;
    }
    
    public AvaliacaoFisica(Aluno aluno, Instrutor instrutor, LocalDate dataAvaliacao, 
                          BigDecimal peso, BigDecimal altura, BigDecimal percentualGordura) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.dataAvaliacao = dataAvaliacao;
        this.peso = peso;
        this.altura = altura;
        this.percentualGordura = percentualGordura;
    }
    
    // Getters e Setters
    public Long getIdAvaliacao() {
        return idAvaliacao;
    }
    
    public void setIdAvaliacao(Long idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
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
    
    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }
    
    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }
    
    public BigDecimal getPeso() {
        return peso;
    }
    
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }
    
    public BigDecimal getAltura() {
        return altura;
    }
    
    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }
    
    public BigDecimal getPercentualGordura() {
        return percentualGordura;
    }
    
    public void setPercentualGordura(BigDecimal percentualGordura) {
        this.percentualGordura = percentualGordura;
    }
    
    public String getMedidasCorporais() {
        return medidasCorporais;
    }
    
    public void setMedidasCorporais(String medidasCorporais) {
        this.medidasCorporais = medidasCorporais;
    }
    
    @Override
    public String toString() {
        return "AvaliacaoFisica{" +
                "idAvaliacao=" + idAvaliacao +
                ", aluno=" + (aluno != null ? aluno.getNome() : "null") +
                ", instrutor=" + (instrutor != null ? instrutor.getNome() : "null") +
                ", dataAvaliacao=" + dataAvaliacao +
                ", peso=" + peso +
                ", altura=" + altura +
                ", percentualGordura=" + percentualGordura +
                '}';
    }
}
