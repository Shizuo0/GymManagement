package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para histórico completo do aluno
 * Agrega informações de treinos, avaliações físicas, frequência, matrículas e pagamentos
 */
public class HistoricoAlunoDTO {
    
    // Informações básicas do aluno
    private Long idAluno;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private Integer idade;
    
    // Estatísticas gerais
    private EstatisticasDTO estatisticas;
    
    // Matrículas
    private List<MatriculaSummaryDTO> matriculas;
    private MatriculaSummaryDTO matriculaAtual;
    
    // Planos de treino
    private List<PlanoTreinoSummaryDTO> planosTreino;
    private PlanoTreinoSummaryDTO planoTreinoAtual;
    
    // Avaliações físicas
    private List<AvaliacaoFisicaSummaryDTO> avaliacoesFisicas;
    private AvaliacaoFisicaSummaryDTO ultimaAvaliacaoFisica;
    
    // Frequência
    private List<FrequenciaSummaryDTO> frequenciaMensal;
    private FrequenciaSummaryDTO frequenciaMesAtual;
    
    // Pagamentos
    private List<PagamentoResponseDTO> pagamentos;
    private PagamentoResponseDTO ultimoPagamento;
    
    // Construtores
    public HistoricoAlunoDTO() {
    }
    
    public HistoricoAlunoDTO(Long idAluno, String nome, String cpf, LocalDate dataNascimento) {
        this.idAluno = idAluno;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.idade = calcularIdade(dataNascimento);
    }
    
    // Método auxiliar para calcular idade
    private Integer calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return null;
        }
        return (int) java.time.temporal.ChronoUnit.YEARS.between(dataNascimento, LocalDate.now());
    }
    
    // Getters e Setters
    public Long getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
        this.idade = calcularIdade(dataNascimento);
    }
    
    public Integer getIdade() {
        return idade;
    }
    
    public void setIdade(Integer idade) {
        this.idade = idade;
    }
    
    public EstatisticasDTO getEstatisticas() {
        return estatisticas;
    }
    
    public void setEstatisticas(EstatisticasDTO estatisticas) {
        this.estatisticas = estatisticas;
    }
    
    public List<MatriculaSummaryDTO> getMatriculas() {
        return matriculas;
    }
    
    public void setMatriculas(List<MatriculaSummaryDTO> matriculas) {
        this.matriculas = matriculas;
    }
    
    public MatriculaSummaryDTO getMatriculaAtual() {
        return matriculaAtual;
    }
    
    public void setMatriculaAtual(MatriculaSummaryDTO matriculaAtual) {
        this.matriculaAtual = matriculaAtual;
    }
    
    public List<PlanoTreinoSummaryDTO> getPlanosTreino() {
        return planosTreino;
    }
    
    public void setPlanosTreino(List<PlanoTreinoSummaryDTO> planosTreino) {
        this.planosTreino = planosTreino;
    }
    
    public PlanoTreinoSummaryDTO getPlanoTreinoAtual() {
        return planoTreinoAtual;
    }
    
    public void setPlanoTreinoAtual(PlanoTreinoSummaryDTO planoTreinoAtual) {
        this.planoTreinoAtual = planoTreinoAtual;
    }
    
    public List<AvaliacaoFisicaSummaryDTO> getAvaliacoesFisicas() {
        return avaliacoesFisicas;
    }
    
    public void setAvaliacoesFisicas(List<AvaliacaoFisicaSummaryDTO> avaliacoesFisicas) {
        this.avaliacoesFisicas = avaliacoesFisicas;
    }
    
    public AvaliacaoFisicaSummaryDTO getUltimaAvaliacaoFisica() {
        return ultimaAvaliacaoFisica;
    }
    
    public void setUltimaAvaliacaoFisica(AvaliacaoFisicaSummaryDTO ultimaAvaliacaoFisica) {
        this.ultimaAvaliacaoFisica = ultimaAvaliacaoFisica;
    }
    
    public List<FrequenciaSummaryDTO> getFrequenciaMensal() {
        return frequenciaMensal;
    }
    
    public void setFrequenciaMensal(List<FrequenciaSummaryDTO> frequenciaMensal) {
        this.frequenciaMensal = frequenciaMensal;
    }
    
    public FrequenciaSummaryDTO getFrequenciaMesAtual() {
        return frequenciaMesAtual;
    }
    
    public void setFrequenciaMesAtual(FrequenciaSummaryDTO frequenciaMesAtual) {
        this.frequenciaMesAtual = frequenciaMesAtual;
    }
    
    public List<PagamentoResponseDTO> getPagamentos() {
        return pagamentos;
    }
    
    public void setPagamentos(List<PagamentoResponseDTO> pagamentos) {
        this.pagamentos = pagamentos;
    }
    
    public PagamentoResponseDTO getUltimoPagamento() {
        return ultimoPagamento;
    }
    
    public void setUltimoPagamento(PagamentoResponseDTO ultimoPagamento) {
        this.ultimoPagamento = ultimoPagamento;
    }
    
    @Override
    public String toString() {
        return "HistoricoAlunoDTO{" +
                "idAluno=" + idAluno +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", idade=" + idade +
                ", totalMatriculas=" + (matriculas != null ? matriculas.size() : 0) +
                ", totalPlanosTreino=" + (planosTreino != null ? planosTreino.size() : 0) +
                ", totalAvaliacoes=" + (avaliacoesFisicas != null ? avaliacoesFisicas.size() : 0) +
                '}';
    }
    
    /**
     * DTO interno para estatísticas gerais do aluno
     */
    public static class EstatisticasDTO {
        private Integer totalMatriculas;
        private Integer totalPlanosTreino;
        private Integer totalAvaliacoesFisicas;
        private Long totalPresencas;
        private Double taxaPresencaGeral;
        private Integer diasComoAluno; // Dias desde a primeira matrícula
        
        public EstatisticasDTO() {
        }
        
        // Getters e Setters
        public Integer getTotalMatriculas() {
            return totalMatriculas;
        }
        
        public void setTotalMatriculas(Integer totalMatriculas) {
            this.totalMatriculas = totalMatriculas;
        }
        
        public Integer getTotalPlanosTreino() {
            return totalPlanosTreino;
        }
        
        public void setTotalPlanosTreino(Integer totalPlanosTreino) {
            this.totalPlanosTreino = totalPlanosTreino;
        }
        
        public Integer getTotalAvaliacoesFisicas() {
            return totalAvaliacoesFisicas;
        }
        
        public void setTotalAvaliacoesFisicas(Integer totalAvaliacoesFisicas) {
            this.totalAvaliacoesFisicas = totalAvaliacoesFisicas;
        }
        
        public Long getTotalPresencas() {
            return totalPresencas;
        }
        
        public void setTotalPresencas(Long totalPresencas) {
            this.totalPresencas = totalPresencas;
        }
        
        public Double getTaxaPresencaGeral() {
            return taxaPresencaGeral;
        }
        
        public void setTaxaPresencaGeral(Double taxaPresencaGeral) {
            this.taxaPresencaGeral = taxaPresencaGeral;
        }
        
        public Integer getDiasComoAluno() {
            return diasComoAluno;
        }
        
        public void setDiasComoAluno(Integer diasComoAluno) {
            this.diasComoAluno = diasComoAluno;
        }
    }
}
