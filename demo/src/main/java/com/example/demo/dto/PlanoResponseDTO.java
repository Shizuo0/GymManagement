package com.example.demo.dto;

import java.math.BigDecimal;

import com.example.demo.entity.Plano;

public class PlanoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private Integer duracaoMeses;
    private String status;
    
    // Construtores
    public PlanoResponseDTO() {
    }
    
    public PlanoResponseDTO(Plano plano) {
        this.id = plano.getIdPlanoAssinatura();
        this.nome = plano.getNome();
        this.descricao = plano.getDescricao();
        this.valor = plano.getValor();
        this.duracaoMeses = plano.getDuracaoMeses();
        this.status = plano.getStatus();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}