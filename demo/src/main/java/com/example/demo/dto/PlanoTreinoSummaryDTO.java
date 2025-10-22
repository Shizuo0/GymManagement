package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para resumo de Plano de Treino no histórico do aluno
 */
public class PlanoTreinoSummaryDTO {
    
    private Long idPlanoTreino;
    private String nomeInstrutor;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String objetivo;
    private Integer totalExercicios;
    private List<ExercicioResumoDTO> exercicios;
    private Boolean ativo; // Se está no período atual
    
    // Construtores
    public PlanoTreinoSummaryDTO() {
    }
    
    public PlanoTreinoSummaryDTO(Long idPlanoTreino, String nomeInstrutor, LocalDate dataInicio,
                                 LocalDate dataFim, String objetivo, Integer totalExercicios) {
        this.idPlanoTreino = idPlanoTreino;
        this.nomeInstrutor = nomeInstrutor;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.objetivo = objetivo;
        this.totalExercicios = totalExercicios;
        this.ativo = verificarSeAtivo(dataInicio, dataFim);
    }
    
    // Método auxiliar para verificar se o plano está ativo
    private Boolean verificarSeAtivo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDate hoje = LocalDate.now();
        if (dataInicio == null) return false;
        if (dataFim == null) return !dataInicio.isAfter(hoje);
        return !hoje.isBefore(dataInicio) && !hoje.isAfter(dataFim);
    }
    
    // Getters e Setters
    public Long getIdPlanoTreino() {
        return idPlanoTreino;
    }
    
    public void setIdPlanoTreino(Long idPlanoTreino) {
        this.idPlanoTreino = idPlanoTreino;
    }
    
    public String getNomeInstrutor() {
        return nomeInstrutor;
    }
    
    public void setNomeInstrutor(String nomeInstrutor) {
        this.nomeInstrutor = nomeInstrutor;
    }
    
    public LocalDate getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
        this.ativo = verificarSeAtivo(this.dataInicio, this.dataFim);
    }
    
    public LocalDate getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
        this.ativo = verificarSeAtivo(this.dataInicio, this.dataFim);
    }
    
    public String getObjetivo() {
        return objetivo;
    }
    
    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }
    
    public Integer getTotalExercicios() {
        return totalExercicios;
    }
    
    public void setTotalExercicios(Integer totalExercicios) {
        this.totalExercicios = totalExercicios;
    }
    
    public List<ExercicioResumoDTO> getExercicios() {
        return exercicios;
    }
    
    public void setExercicios(List<ExercicioResumoDTO> exercicios) {
        this.exercicios = exercicios;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    @Override
    public String toString() {
        return "PlanoTreinoSummaryDTO{" +
                "idPlanoTreino=" + idPlanoTreino +
                ", nomeInstrutor='" + nomeInstrutor + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", objetivo='" + objetivo + '\'' +
                ", totalExercicios=" + totalExercicios +
                ", ativo=" + ativo +
                '}';
    }
    
    /**
     * DTO interno para resumo de exercício
     */
    public static class ExercicioResumoDTO {
        private String nomeExercicio;
        private Integer series;
        private Integer repeticoes;
        private Double carga;
        
        public ExercicioResumoDTO() {
        }
        
        public ExercicioResumoDTO(String nomeExercicio, Integer series, Integer repeticoes, Double carga) {
            this.nomeExercicio = nomeExercicio;
            this.series = series;
            this.repeticoes = repeticoes;
            this.carga = carga;
        }
        
        // Getters e Setters
        public String getNomeExercicio() {
            return nomeExercicio;
        }
        
        public void setNomeExercicio(String nomeExercicio) {
            this.nomeExercicio = nomeExercicio;
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
        
        public Double getCarga() {
            return carga;
        }
        
        public void setCarga(Double carga) {
            this.carga = carga;
        }
    }
}
