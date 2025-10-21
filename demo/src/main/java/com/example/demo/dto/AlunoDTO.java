package com.example.demo.dto;

import java.time.LocalDate;

public class AlunoDTO {
    private Long idAluno;
    private String nome;
    private String cpf;
    private LocalDate dataIngresso;

    // Construtores
    public AlunoDTO() {}

    public AlunoDTO(Long idAluno, String nome, String cpf, LocalDate dataIngresso) {
        this.idAluno = idAluno;
        this.nome = nome;
        this.cpf = cpf;
        this.dataIngresso = dataIngresso;
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

    public LocalDate getDataIngresso() {
        return dataIngresso;
    }

    public void setDataIngresso(LocalDate dataIngresso) {
        this.dataIngresso = dataIngresso;
    }
}