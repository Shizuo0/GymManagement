package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade que representa um Plano de Assinatura na academia
 */
@Entity
@Table(name = "planos")
public class Plano {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plano_assinatura")
    private Long idPlanoAssinatura;
    
    @Column(name = "nome", nullable = false, length = 50)
    private String nome;
    
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;
    
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "duracao_meses", nullable = false)
    private Integer duracaoMeses;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "ATIVO";
    
    // Construtores
    public Plano() {
    }
    
    public Plano(String nome, String descricao, BigDecimal valor, Integer duracaoMeses) {
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.duracaoMeses = duracaoMeses;
        this.status = "ATIVO";
    }
    
    // Getters e Setters
    public Long getIdPlanoAssinatura() {
        return idPlanoAssinatura;
    }
    
    public void setIdPlanoAssinatura(Long idPlanoAssinatura) {
        this.idPlanoAssinatura = idPlanoAssinatura;
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
    
    @Override
    public String toString() {
        return "Plano{" +
                "idPlanoAssinatura=" + idPlanoAssinatura +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", duracaoMeses=" + duracaoMeses +
                ", status='" + status + '\'' +
                '}';
    }
}
