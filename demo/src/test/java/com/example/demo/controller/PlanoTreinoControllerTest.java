package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.PlanoTreinoRequestDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Instrutor;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.exception.PlanoTreinoException;
import com.example.demo.service.AlunoService;
import com.example.demo.service.InstrutorService;
import com.example.demo.service.PlanoTreinoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PlanoTreinoController.class)
public class PlanoTreinoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanoTreinoService planoTreinoService;

    @MockitoBean
    private AlunoService alunoService;

    @MockitoBean
    private InstrutorService instrutorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Aluno aluno;
    private Instrutor instrutor;
    private PlanoTreino planoTreino;
    private PlanoTreinoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        aluno = new Aluno("João Silva", "12345678900", LocalDate.of(2000, 1, 1));
        aluno.setIdAluno(1L);

        instrutor = new Instrutor("Carlos Instrutor", "98765432100");
        instrutor.setIdInstrutor(1L);

        planoTreino = new PlanoTreino(aluno, instrutor, LocalDate.now());
        planoTreino.setIdPlanoTreino(1L);
        planoTreino.setDescricao("Treino para hipertrofia");
        planoTreino.setDuracaoSemanas(12);

        requestDTO = new PlanoTreinoRequestDTO(1L, 1L, "Treino para hipertrofia", 12);
    }

    @Test
    void criarPlanoTreino_QuandoValido_DeveRetornarCreated() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(instrutorService.buscarPorId(1L)).thenReturn(instrutor);
        when(planoTreinoService.criarPlanoTreino(any(PlanoTreino.class))).thenReturn(planoTreino);

        mockMvc.perform(post("/api/planos-treino")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nomeAluno").value("João Silva"))
                .andExpect(jsonPath("$.nomeInstrutor").value("Carlos Instrutor"));
    }

    @Test
    void criarPlanoTreino_QuandoAlunoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(alunoService.buscarPorId(1L))
            .thenThrow(new RuntimeException("Aluno não encontrado"));

        mockMvc.perform(post("/api/planos-treino")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void criarPlanoTreino_QuandoInstrutorInvalido_DeveRetornarBadRequest() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(instrutorService.buscarPorId(1L)).thenReturn(instrutor);
        when(planoTreinoService.criarPlanoTreino(any(PlanoTreino.class)))
            .thenThrow(new PlanoTreinoException.InstrutorInvalidoException("Instrutor inválido"));

        mockMvc.perform(post("/api/planos-treino")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPlanoTreino_QuandoExiste_DeveRetornarPlano() throws Exception {
        when(planoTreinoService.buscarPorId(1L)).thenReturn(planoTreino);

        mockMvc.perform(get("/api/planos-treino/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.descricao").value("Treino para hipertrofia"));
    }

    @Test
    void buscarPlanoTreino_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(planoTreinoService.buscarPorId(1L))
            .thenThrow(new PlanoTreinoException.PlanoTreinoNotFoundException("Plano não encontrado"));

        mockMvc.perform(get("/api/planos-treino/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarPlanoTreino_QuandoValido_DeveRetornarOk() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(instrutorService.buscarPorId(1L)).thenReturn(instrutor);
        when(planoTreinoService.atualizarPlanoTreino(eq(1L), any(PlanoTreino.class))).thenReturn(planoTreino);

        mockMvc.perform(put("/api/planos-treino/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void deletarPlanoTreino_QuandoExiste_DeveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/api/planos-treino/1"))
                .andExpect(status().isNoContent());

        verify(planoTreinoService).deletarPlanoTreino(1L);
    }

    @Test
    void listarPlanosPorAluno_DeveRetornarLista() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(planoTreinoService.listarPlanosDoAluno(aluno)).thenReturn(Arrays.asList(planoTreino));

        mockMvc.perform(get("/api/planos-treino/aluno/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void listarPlanosPorInstrutor_DeveRetornarLista() throws Exception {
        when(instrutorService.buscarPorId(1L)).thenReturn(instrutor);
        when(planoTreinoService.listarPlanosPorInstrutor(instrutor)).thenReturn(Arrays.asList(planoTreino));

        mockMvc.perform(get("/api/planos-treino/instrutor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void buscarPlanosRecentes_DeveRetornarLista() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(planoTreinoService.buscarPlanosRecentes(aluno, 5)).thenReturn(Arrays.asList(planoTreino));

        mockMvc.perform(get("/api/planos-treino/aluno/1/recentes?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
