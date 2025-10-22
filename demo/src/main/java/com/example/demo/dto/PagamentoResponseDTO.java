package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta de Pagamento
 */
public class PagamentoResponseDTO {
    
    private Long idPagamento;
    private Long idMatricula;
    private String nomeAluno;
    private String nomePlano;
    private LocalDate dataPagamento;
    private BigDecimal valorPago;
    private String formaPagamento;
    
    // Construtores
    public PagamentoResponseDTO() {
    }
    
    public PagamentoResponseDTO(Long idPagamento, Long idMatricula, String nomeAluno, String nomePlano, 
                                LocalDate dataPagamento, BigDecimal valorPago, String formaPagamento) {
        this.idPagamento = idPagamento;
        this.idMatricula = idMatricula;
        this.nomeAluno = nomeAluno;
        this.nomePlano = nomePlano;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
        this.formaPagamento = formaPagamento;
    }
    
    // Getters e Setters
    public Long getIdPagamento() {
        return idPagamento;
    }
    
    public void setIdPagamento(Long idPagamento) {
        this.idPagamento = idPagamento;
    }
    
    public Long getIdMatricula() {
        return idMatricula;
    }
    
    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
    }
    
    public String getNomeAluno() {
        return nomeAluno;
    }
    
    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }
    
    public String getNomePlano() {
        return nomePlano;
    }
    
    public void setNomePlano(String nomePlano) {
        this.nomePlano = nomePlano;
    }
    
    public LocalDate getDataPagamento() {
        return dataPagamento;
    }
    
    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    
    public BigDecimal getValorPago() {
        return valorPago;
    }
    
    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }
    
    public String getFormaPagamento() {
        return formaPagamento;
    }
    
    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
    
    @Override
    public String toString() {
        return "PagamentoResponseDTO{" +
                "idPagamento=" + idPagamento +
                ", idMatricula=" + idMatricula +
                ", nomeAluno='" + nomeAluno + '\'' +
                ", nomePlano='" + nomePlano + '\'' +
                ", dataPagamento=" + dataPagamento +
                ", valorPago=" + valorPago +
                ", formaPagamento='" + formaPagamento + '\'' +
                '}';
    }
}
