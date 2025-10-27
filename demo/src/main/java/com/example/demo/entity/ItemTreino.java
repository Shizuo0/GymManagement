package com.example.demo.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidade que representa um Item de Treino (relacionamento N:N entre PlanoTreino e Exercicio)
 * Tabela associativa que contém informações específicas sobre cada exercício no plano
 */
@Entity
@Table(name = "ItensTreino", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_plano", "id_exercicio"},
        name = "uk_plano_exercicio")
})
public class ItemTreino {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_treino")
    private Long idItemTreino;
    
    @NotNull(message = "O plano de treino é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plano", nullable = false)
    private PlanoTreino planoTreino;
    
    @NotNull(message = "O exercício é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_exercicio", nullable = false)
    private Exercicio exercicio;
    
    @NotNull(message = "O número de séries é obrigatório")
    @Min(value = 1, message = "O número de séries deve ser pelo menos 1")
    @Column(name = "series", nullable = false)
    private Integer series;
    
    @NotNull(message = "O número de repetições é obrigatório")
    @Min(value = 1, message = "O número de repetições deve ser pelo menos 1")
    @Column(name = "repeticoes", nullable = false)
    private Integer repeticoes;
    
    @Min(value = 0, message = "A carga não pode ser negativa")
    @Column(name = "carga", precision = 10, scale = 2)
    private BigDecimal carga;
    
    @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres")
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
