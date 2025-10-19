package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

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
    
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "duracao_dias", nullable = false)
    private Integer duracaoDias;
    
    // Construtores
    public Plano() {
    }
    
    public Plano(String nome, BigDecimal valor, Integer duracaoDias) {
        this.nome = nome;
        this.valor = valor;
        this.duracaoDias = duracaoDias;
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
    
    public BigDecimal getValor() {
        return valor;
    }
    
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    
    public Integer getDuracaoDias() {
        return duracaoDias;
    }
    
    public void setDuracaoDias(Integer duracaoDias) {
        this.duracaoDias = duracaoDias;
    }
    
    @Override
    public String toString() {
        return "Plano{" +
                "idPlanoAssinatura=" + idPlanoAssinatura +
                ", nome='" + nome + '\'' +
                ", valor=" + valor +
                ", duracaoDias=" + duracaoDias +
                '}';
    }
}
