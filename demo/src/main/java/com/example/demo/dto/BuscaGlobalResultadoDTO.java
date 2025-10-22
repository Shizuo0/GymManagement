package com.example.demo.dto;

import java.util.List;

/**
 * DTO para resultado de busca global
 * Agrega resultados de alunos, instrutores e planos
 */
public class BuscaGlobalResultadoDTO {
    
    private String termoBusca;
    private Integer totalResultados;
    private List<AlunoResultadoDTO> alunos;
    private List<InstrutorResultadoDTO> instrutores;
    private List<PlanoResultadoDTO> planos;
    
    // Construtores
    public BuscaGlobalResultadoDTO() {
    }
    
    public BuscaGlobalResultadoDTO(String termoBusca) {
        this.termoBusca = termoBusca;
    }
    
    // Getters e Setters
    public String getTermoBusca() {
        return termoBusca;
    }
    
    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }
    
    public Integer getTotalResultados() {
        return totalResultados;
    }
    
    public void setTotalResultados(Integer totalResultados) {
        this.totalResultados = totalResultados;
    }
    
    public List<AlunoResultadoDTO> getAlunos() {
        return alunos;
    }
    
    public void setAlunos(List<AlunoResultadoDTO> alunos) {
        this.alunos = alunos;
        calcularTotal();
    }
    
    public List<InstrutorResultadoDTO> getInstrutores() {
        return instrutores;
    }
    
    public void setInstrutores(List<InstrutorResultadoDTO> instrutores) {
        this.instrutores = instrutores;
        calcularTotal();
    }
    
    public List<PlanoResultadoDTO> getPlanos() {
        return planos;
    }
    
    public void setPlanos(List<PlanoResultadoDTO> planos) {
        this.planos = planos;
        calcularTotal();
    }
    
    // Método auxiliar para calcular total de resultados
    private void calcularTotal() {
        int total = 0;
        if (alunos != null) total += alunos.size();
        if (instrutores != null) total += instrutores.size();
        if (planos != null) total += planos.size();
        this.totalResultados = total;
    }
    
    @Override
    public String toString() {
        return "BuscaGlobalResultadoDTO{" +
                "termoBusca='" + termoBusca + '\'' +
                ", totalResultados=" + totalResultados +
                ", alunos=" + (alunos != null ? alunos.size() : 0) +
                ", instrutores=" + (instrutores != null ? instrutores.size() : 0) +
                ", planos=" + (planos != null ? planos.size() : 0) +
                '}';
    }
    
    /**
     * DTO para resultado de busca de aluno
     */
    public static class AlunoResultadoDTO {
        private Long idAluno;
        private String nome;
        private String cpf;
        private String statusMatricula;
        private String planoAtual;
        
        public AlunoResultadoDTO() {
        }
        
        public AlunoResultadoDTO(Long idAluno, String nome, String cpf, String statusMatricula, String planoAtual) {
            this.idAluno = idAluno;
            this.nome = nome;
            this.cpf = cpf;
            this.statusMatricula = statusMatricula;
            this.planoAtual = planoAtual;
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
        
        public String getStatusMatricula() {
            return statusMatricula;
        }
        
        public void setStatusMatricula(String statusMatricula) {
            this.statusMatricula = statusMatricula;
        }
        
        public String getPlanoAtual() {
            return planoAtual;
        }
        
        public void setPlanoAtual(String planoAtual) {
            this.planoAtual = planoAtual;
        }
    }
    
    /**
     * DTO para resultado de busca de instrutor
     */
    public static class InstrutorResultadoDTO {
        private Long idInstrutor;
        private String nome;
        private String cpf;
        private String especialidade;
        private Integer totalAlunos;
        
        public InstrutorResultadoDTO() {
        }
        
        public InstrutorResultadoDTO(Long idInstrutor, String nome, String cpf, String especialidade, Integer totalAlunos) {
            this.idInstrutor = idInstrutor;
            this.nome = nome;
            this.cpf = cpf;
            this.especialidade = especialidade;
            this.totalAlunos = totalAlunos;
        }
        
        // Getters e Setters
        public Long getIdInstrutor() {
            return idInstrutor;
        }
        
        public void setIdInstrutor(Long idInstrutor) {
            this.idInstrutor = idInstrutor;
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
        
        public String getEspecialidade() {
            return especialidade;
        }
        
        public void setEspecialidade(String especialidade) {
            this.especialidade = especialidade;
        }
        
        public Integer getTotalAlunos() {
            return totalAlunos;
        }
        
        public void setTotalAlunos(Integer totalAlunos) {
            this.totalAlunos = totalAlunos;
        }
    }
    
    /**
     * DTO para resultado de busca de plano
     */
    public static class PlanoResultadoDTO {
        private Long idPlano;
        private String nome;
        private String descricao;
        private String valor;
        private String status;
        private Integer totalMatriculas;
        
        public PlanoResultadoDTO() {
        }
        
        public PlanoResultadoDTO(Long idPlano, String nome, String descricao, String valor, 
                                String status, Integer totalMatriculas) {
            this.idPlano = idPlano;
            this.nome = nome;
            this.descricao = descricao;
            this.valor = valor;
            this.status = status;
            this.totalMatriculas = totalMatriculas;
        }
        
        // Getters e Setters
        public Long getIdPlano() {
            return idPlano;
        }
        
        public void setIdPlano(Long idPlano) {
            this.idPlano = idPlano;
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
        
        public String getValor() {
            return valor;
        }
        
        public void setValor(String valor) {
            this.valor = valor;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public Integer getTotalMatriculas() {
            return totalMatriculas;
        }
        
        public void setTotalMatriculas(Integer totalMatriculas) {
            this.totalMatriculas = totalMatriculas;
        }
    }
}
