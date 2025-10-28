package com.example.demo.entity;

import java.math.BigDecimal;
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
 * Entidade que representa um Pagamento de uma matrícula
 */
@Entity
@Table(name = "Pagamentos")
public class Pagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long idPagamento;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_matricula", nullable = false, foreignKey = @jakarta.persistence.ForeignKey(name = "fk_pagamento_matricula"))
    private Matricula matricula;
    
    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;
    
    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;
    
    @Column(name = "forma_pagamento", length = 20)
    private String formaPagamento;
    
    // Construtores
    public Pagamento() {
    }
    
    public Pagamento(Matricula matricula, LocalDate dataPagamento, BigDecimal valorPago, String formaPagamento) {
        this.matricula = matricula;
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
    
    public Matricula getMatricula() {
        return matricula;
    }
    
    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
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
        return "Pagamento{" +
                "idPagamento=" + idPagamento +
                ", matricula=" + (matricula != null ? matricula.getIdMatricula() : "null") +
                ", dataPagamento=" + dataPagamento +
                ", valorPago=" + valorPago +
                ", formaPagamento='" + formaPagamento + '\'' +
                '}';
    }
}
