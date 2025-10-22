package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para requisição de criação/atualização de Pagamento
 */
public class PagamentoRequestDTO {
    
    @NotNull(message = "ID da matrícula é obrigatório")
    private Long idMatricula;
    
    @NotNull(message = "Data do pagamento é obrigatória")
    @PastOrPresent(message = "Data do pagamento não pode ser futura")
    private LocalDate dataPagamento;
    
    @NotNull(message = "Valor pago é obrigatório")
    @Positive(message = "Valor pago deve ser positivo")
    private BigDecimal valorPago;
    
    @Size(max = 20, message = "Forma de pagamento deve ter no máximo 20 caracteres")
    private String formaPagamento;
    
    // Construtores
    public PagamentoRequestDTO() {
    }
    
    public PagamentoRequestDTO(Long idMatricula, LocalDate dataPagamento, BigDecimal valorPago, String formaPagamento) {
        this.idMatricula = idMatricula;
        this.dataPagamento = dataPagamento;
        this.valorPago = valorPago;
        this.formaPagamento = formaPagamento;
    }
    
    // Getters e Setters
    public Long getIdMatricula() {
        return idMatricula;
    }
    
    public void setIdMatricula(Long idMatricula) {
        this.idMatricula = idMatricula;
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
        return "PagamentoRequestDTO{" +
                "idMatricula=" + idMatricula +
                ", dataPagamento=" + dataPagamento +
                ", valorPago=" + valorPago +
                ", formaPagamento='" + formaPagamento + '\'' +
                '}';
    }
}
