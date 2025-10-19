package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade que representa um Item de Treino (relacionamento N:N entre PlanoTreino e Exercicio)
 * Tabela associativa que contém informações específicas sobre cada exercício no plano
 */
@Entity
@Table(name = "itenstreino")
public class ItemTreino {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_treino")
    private Long idItemTreino;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plano_treino", nullable = false)
    private PlanoTreino planoTreino;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exercicio", nullable = false)
    private Exercicio exercicio;
    
    @Column(name = "series")
    private Integer series;
    
    @Column(name = "repeticoes")
    private Integer repeticoes;
    
    @Column(name = "carga", precision = 10, scale = 2)
    private BigDecimal carga;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    // Construtores
    public ItemTreino() {
    }
    
    public ItemTreino(PlanoTreino planoTreino, Exercicio exercicio) {
        this.planoTreino = planoTreino;
        this.exercicio = exercicio;
    }
    
    public ItemTreino(PlanoTreino planoTreino, Exercicio exercicio, Integer series, Integer repeticoes, BigDecimal carga) {
        this.planoTreino = planoTreino;
        this.exercicio = exercicio;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
    }
    
    public ItemTreino(PlanoTreino planoTreino, Exercicio exercicio, Integer series, Integer repeticoes, BigDecimal carga, String observacoes) {
        this.planoTreino = planoTreino;
        this.exercicio = exercicio;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
        this.observacoes = observacoes;
    }
    
    // Getters e Setters
    public Long getIdItemTreino() {
        return idItemTreino;
    }
    
    public void setIdItemTreino(Long idItemTreino) {
        this.idItemTreino = idItemTreino;
    }
    
    public PlanoTreino getPlanoTreino() {
        return planoTreino;
    }
    
    public void setPlanoTreino(PlanoTreino planoTreino) {
        this.planoTreino = planoTreino;
    }
    
    public Exercicio getExercicio() {
        return exercicio;
    }
    
    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
    
    public Integer getSeries() {
        return series;
    }
    
    public void setSeries(Integer series) {
        this.series = series;
    }
    
    public Integer getRepeticoes() {
        return repeticoes;
    }
    
    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }
    
    public BigDecimal getCarga() {
        return carga;
    }
    
    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    @Override
    public String toString() {
        return "ItemTreino{" +
                "idItemTreino=" + idItemTreino +
                ", planoTreino=" + (planoTreino != null ? planoTreino.getIdPlanoTreino() : "null") +
                ", exercicio=" + (exercicio != null ? exercicio.getNome() : "null") +
                ", series=" + series +
                ", repeticoes=" + repeticoes +
                ", carga=" + carga +
                '}';
    }
}
