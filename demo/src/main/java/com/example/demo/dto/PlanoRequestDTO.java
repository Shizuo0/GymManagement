package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PlanoRequestDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    @NotNull(message = "Duração em meses é obrigatória")
    @Min(value = 1, message = "Duração deve ser de pelo menos 1 mês")
    private Integer duracaoMeses;
    
    // Construtores
    public PlanoRequestDTO() {
    }
    
    public PlanoRequestDTO(String nome, String descricao, BigDecimal valor, Integer duracaoMeses) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.duracaoMeses = duracaoMeses;
    }
    
    // Getters e Setters
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public Integer getDuracaoMeses() {
        return duracaoMeses;
    }
    
    public void setDuracaoMeses(Integer duracaoMeses) {
        this.duracaoMeses = duracaoMeses;
    }
}