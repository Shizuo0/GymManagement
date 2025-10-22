package com.example.demo.dto;

import java.time.LocalDate;

/**
 * DTO para resposta de Frequência
 */
public class FrequenciaResponseDTO {
    
    private Long idFrequencia;
    private Long idAluno;
    private String nomeAluno;
    private String cpfAluno;
    private LocalDate data;
    private Boolean presenca;
    private String statusPresenca; // "Presente" ou "Ausente"
    
    // Construtores
    public FrequenciaResponseDTO() {
    }
    
    public FrequenciaResponseDTO(Long idFrequencia, Long idAluno, String nomeAluno, String cpfAluno, 
                                 LocalDate data, Boolean presenca) {
        this.idFrequencia = idFrequencia;
        this.idAluno = idAluno;
        this.nomeAluno = nomeAluno;
        this.cpfAluno = cpfAluno;
        this.data = data;
        this.presenca = presenca;
        this.statusPresenca = presenca ? "Presente" : "Ausente";
    }
    
    // Getters e Setters
    public Long getIdFrequencia() {
        return idFrequencia;
    }
    
    public void setIdFrequencia(Long idFrequencia) {
        this.idFrequencia = idFrequencia;
    }
    
    public Long getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
    }
    
    public String getNomeAluno() {
        return nomeAluno;
    }
    
    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }
    
    public String getCpfAluno() {
        return cpfAluno;
    }
    
    public void setCpfAluno(String cpfAluno) {
        this.cpfAluno = cpfAluno;
    }
    
    public LocalDate getData() {
        return data;
    }
    
    public void setData(LocalDate data) {
        this.data = data;
    }
    
    public Boolean getPresenca() {
        return presenca;
    }
    
    public void setPresenca(Boolean presenca) {
        this.presenca = presenca;
        this.statusPresenca = presenca ? "Presente" : "Ausente";
    }
    
    public String getStatusPresenca() {
        return statusPresenca;
    }
    
    public void setStatusPresenca(String statusPresenca) {
        this.statusPresenca = statusPresenca;
    }
    
    @Override
    public String toString() {
        return "FrequenciaResponseDTO{" +
                "idFrequencia=" + idFrequencia +
                ", idAluno=" + idAluno +
                ", nomeAluno='" + nomeAluno + '\'' +
                ", cpfAluno='" + cpfAluno + '\'' +
                ", data=" + data +
                ", presenca=" + presenca +
                ", statusPresenca='" + statusPresenca + '\'' +
                '}';
    }
}
