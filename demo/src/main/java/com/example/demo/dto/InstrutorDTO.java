package com.example.demo.dto;

public class InstrutorDTO {
    private Long idInstrutor;
    private String nome;
    private String especialidade;

    // Construtores
    public InstrutorDTO() {}

    public InstrutorDTO(Long idInstrutor, String nome, String especialidade) {
        this.idInstrutor = idInstrutor;
        this.nome = nome;
        this.especialidade = especialidade;
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

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}