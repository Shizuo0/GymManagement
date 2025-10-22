package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resumo de Matrícula no histórico do aluno
 */
public class MatriculaSummaryDTO {
    
    private Long idMatricula;
    private String nomePlano;
    private BigDecimal valorPlano;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private Integer duracaoDias;
    private BigDecimal totalPago;
    private Boolean emDia; // Se os pagamentos estão em dia
    
    // Construtores
    public MatriculaSummaryDTO() {
    }
    
    public MatriculaSummaryDTO(Long idMatricula, String nomePlano, BigDecimal valorPlano,
                               LocalDate dataInicio, LocalDate dataFim, String status) {
        this.idMatricula = idMatricula;
        this.nomePlano = nomePlano;
        this.valorPlano = valorPlano;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.duracaoDias = calcularDuracaoDias(dataInicio, dataFim);
    }
    
    // Método auxiliar para calcular duração em dias
    private Integer calcularDuracaoDias(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            return null;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(inicio, fim);
    }
    
    // Getters e Setters
    public Long getIdMatricula() {
        return idMatricula;
    }
    
    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }
    
    public String getNomePlano() {
        return nomePlano;
    }
    
    public void setNomePlano(String nomePlano) {
        this.nomePlano = nomePlano;
    }
    
    public BigDecimal getValorPlano() {
        return valorPlano;
    }
    
    public void setValorPlano(BigDecimal valorPlano) {
        this.valorPlano = valorPlano;
    }
    
    public LocalDate getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
        this.duracaoDias = calcularDuracaoDias(this.dataInicio, this.dataFim);
    }
    
    public LocalDate getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
        this.duracaoDias = calcularDuracaoDias(this.dataInicio, this.dataFim);
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getDuracaoDias() {
        return duracaoDias;
    }
    
    public void setDuracaoDias(Integer duracaoDias) {
        this.duracaoDias = duracaoDias;
    }
    
    public BigDecimal getTotalPago() {
        return totalPago;
    }
    
    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }
    
    public Boolean getEmDia() {
        return emDia;
    }
    
    public void setEmDia(Boolean emDia) {
        this.emDia = emDia;
    }
    
    @Override
    public String toString() {
        return "MatriculaSummaryDTO{" +
                "idMatricula=" + idMatricula +
                ", nomePlano='" + nomePlano + '\'' +
                ", valorPlano=" + valorPlano +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", status='" + status + '\'' +
                ", duracaoDias=" + duracaoDias +
                ", totalPago=" + totalPago +
                ", emDia=" + emDia +
                '}';
    }
}
