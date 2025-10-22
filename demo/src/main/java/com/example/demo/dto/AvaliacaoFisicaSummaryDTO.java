package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resumo de Avaliação Física no histórico do aluno
 */
public class AvaliacaoFisicaSummaryDTO {
    
    private Long idAvaliacao;
    private LocalDate dataAvaliacao;
    private String nomeInstrutor;
    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal percentualGordura;
    private BigDecimal imc; // Calculado: peso / (altura * altura)
    private String medidasCorporais;
    
    // Construtores
    public AvaliacaoFisicaSummaryDTO() {
    }
    
    public AvaliacaoFisicaSummaryDTO(Long idAvaliacao, LocalDate dataAvaliacao, String nomeInstrutor,
                                     BigDecimal peso, BigDecimal altura, BigDecimal percentualGordura,
                                     String medidasCorporais) {
        this.idAvaliacao = idAvaliacao;
        this.dataAvaliacao = dataAvaliacao;
        this.nomeInstrutor = nomeInstrutor;
        this.peso = peso;
        this.altura = altura;
        this.percentualGordura = percentualGordura;
        this.medidasCorporais = medidasCorporais;
        this.imc = calcularIMC(peso, altura);
    }
    
    // Método auxiliar para calcular IMC
    private BigDecimal calcularIMC(BigDecimal peso, BigDecimal altura) {
        if (peso != null && altura != null && altura.compareTo(BigDecimal.ZERO) > 0) {
            return peso.divide(altura.multiply(altura), 2, java.math.RoundingMode.HALF_UP);
        }
        return null;
    }
    
    // Getters e Setters
    public Long getIdAvaliacao() {
        return idAvaliacao;
    }
    
    public void setIdAvaliacao(Long idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }
    
    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }
    
    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }
    
    public String getNomeInstrutor() {
        return nomeInstrutor;
    }
    
    public void setNomeInstrutor(String nomeInstrutor) {
        this.nomeInstrutor = nomeInstrutor;
    }
    
    public BigDecimal getPeso() {
        return peso;
    }
    
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
        this.imc = calcularIMC(this.peso, this.altura);
    }
    
    public BigDecimal getAltura() {
        return altura;
    }
    
    public void setAltura(BigDecimal altura) {
        this.altura = altura;
        this.imc = calcularIMC(this.peso, this.altura);
    }
    
    public BigDecimal getPercentualGordura() {
        return percentualGordura;
    }
    
    public void setPercentualGordura(BigDecimal percentualGordura) {
        this.percentualGordura = percentualGordura;
    }
    
    public BigDecimal getImc() {
        return imc;
    }
    
    public void setImc(BigDecimal imc) {
        this.imc = imc;
    }
    
    public String getMedidasCorporais() {
        return medidasCorporais;
    }
    
    public void setMedidasCorporais(String medidasCorporais) {
        this.medidasCorporais = medidasCorporais;
    }
    
    @Override
    public String toString() {
        return "AvaliacaoFisicaSummaryDTO{" +
                "idAvaliacao=" + idAvaliacao +
                ", dataAvaliacao=" + dataAvaliacao +
                ", nomeInstrutor='" + nomeInstrutor + '\'' +
                ", peso=" + peso +
                ", altura=" + altura +
                ", imc=" + imc +
                ", percentualGordura=" + percentualGordura +
                '}';
    }
}
