package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * DTO para requisição de criação/atualização de Frequência
 */
public class FrequenciaRequestDTO {
    
    @NotNull(message = "ID do aluno é obrigatório")
    private Long idAluno;
    
    @NotNull(message = "Data é obrigatória")
    @PastOrPresent(message = "Data não pode ser futura")
    private LocalDate data;
    
    @NotNull(message = "Status de presença é obrigatório")
    private Boolean presenca;
    
    // Construtores
    public FrequenciaRequestDTO() {
    }
    
    public FrequenciaRequestDTO(Long idAluno, LocalDate data, Boolean presenca) {
        this.idAluno = idAluno;
        this.data = data;
        this.presenca = presenca;
    }
    
    // Getters e Setters
    public Long getIdAluno() {
        return idAluno;
    }
    
    public void setIdAluno(Long idAluno) {
        this.idAluno = idAluno;
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
    }
    
    @Override
    public String toString() {
        return "FrequenciaRequestDTO{" +
                "idAluno=" + idAluno +
                ", data=" + data +
                ", presenca=" + presenca +
                '}';
    }
}
