package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.ItemTreinoRequestDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Exercicio;
import com.example.demo.entity.Instrutor;
import com.example.demo.entity.ItemTreino;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.exception.ItemTreinoException;
import com.example.demo.service.ExercicioService;
import com.example.demo.service.ItemTreinoService;
import com.example.demo.service.PlanoTreinoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ItemTreinoController.class)
public class ItemTreinoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemTreinoService itemTreinoService;

    @MockitoBean
    private PlanoTreinoService planoTreinoService;

    @MockitoBean
    private ExercicioService exercicioService;

    @Autowired
    private ObjectMapper objectMapper;

    private PlanoTreino planoTreino;
    private Exercicio exercicio;
    private ItemTreino itemTreino;
    private ItemTreinoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        Aluno aluno = new Aluno("João Silva", "12345678900", LocalDate.of(2000, 1, 1));
        aluno.setIdAluno(1L);

        Instrutor instrutor = new Instrutor("Carlos Instrutor", "98765432100");
        instrutor.setIdInstrutor(1L);

        planoTreino = new PlanoTreino(aluno, instrutor, LocalDate.now());
        planoTreino.setIdPlanoTreino(1L);
        planoTreino.setDescricao("Treino A");

        exercicio = new Exercicio("Supino Reto", "Peito");
        exercicio.setIdExercicio(1L);

        itemTreino = new ItemTreino(planoTreino, exercicio, 4, 12, new BigDecimal("80.00"));
        itemTreino.setIdItemTreino(1L);
        itemTreino.setObservacoes("Executar com boa técnica");

        requestDTO = new ItemTreinoRequestDTO();
        requestDTO.setPlanoTreinoId(1L);
        requestDTO.setExercicioId(1L);
        requestDTO.setSeries(4);
        requestDTO.setRepeticoes(12);
        requestDTO.setCarga(new BigDecimal("80.00"));
        requestDTO.setObservacoes("Executar com boa técnica");
    }

    @Test
    void adicionarExercicioAoPlano_QuandoValido_DeveRetornarCreated() throws Exception {
        when(planoTreinoService.buscarPorId(1L)).thenReturn(planoTreino);
        when(exercicioService.buscarPorId(1L)).thenReturn(exercicio);
        when(itemTreinoService.adicionarExercicioAoPlano(any(ItemTreino.class))).thenReturn(itemTreino);

        mockMvc.perform(post("/api/itens-treino")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.exercicioNome").value("Supino Reto"))
                .andExpect(jsonPath("$.series").value(4))
                .andExpect(jsonPath("$.repeticoes").value(12));
    }

    @Test
    void adicionarExercicioAoPlano_QuandoExercicioDuplicado_DeveRetornarConflict() throws Exception {
        when(planoTreinoService.buscarPorId(1L)).thenReturn(planoTreino);
        when(exercicioService.buscarPorId(1L)).thenReturn(exercicio);
        when(itemTreinoService.adicionarExercicioAoPlano(any(ItemTreino.class)))
            .thenThrow(new ItemTreinoException.DuplicateExerciseException("Exercício já existe no plano"));

        mockMvc.perform(post("/api/itens-treino")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void adicionarExercicioAoPlano_QuandoExercicioNaoExiste_DeveRetornarNotFound() throws Exception {
        when(planoTreinoService.buscarPorId(1L)).thenReturn(planoTreino);
        when(exercicioService.buscarPorId(999L))
            .thenThrow(new RuntimeException("Exercício não encontrado"));

        requestDTO.setExercicioId(999L);

        mockMvc.perform(post("/api/itens-treino")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void adicionarExercicioAoPlano_QuandoSeriesInvalidas_DeveRetornarBadRequest() throws Exception {
        requestDTO.setSeries(0);

        mockMvc.perform(post("/api/itens-treino")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarItemTreino_QuandoExiste_DeveRetornarItem() throws Exception {
        when(itemTreinoService.buscarPorId(1L)).thenReturn(itemTreino);

        mockMvc.perform(get("/api/itens-treino/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.series").value(4));
    }

    @Test
    void buscarItemTreino_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(itemTreinoService.buscarPorId(1L))
            .thenThrow(new ItemTreinoException.ItemTreinoNotFoundException("Item não encontrado"));

        mockMvc.perform(get("/api/itens-treino/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarItemTreino_QuandoValido_DeveRetornarOk() throws Exception {
        when(planoTreinoService.buscarPorId(1L)).thenReturn(planoTreino);
        when(exercicioService.buscarPorId(1L)).thenReturn(exercicio);
        when(itemTreinoService.atualizarItemTreino(eq(1L), any(ItemTreino.class))).thenReturn(itemTreino);

        mockMvc.perform(put("/api/itens-treino/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void atualizarItemTreino_QuandoCargaInvalida_DeveRetornarBadRequest() throws Exception {
        requestDTO.setCarga(new BigDecimal("-10.00"));

        mockMvc.perform(put("/api/itens-treino/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removerExercicioDoPlano_QuandoExiste_DeveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/api/itens-treino/1"))
                .andExpect(status().isNoContent());

        verify(itemTreinoService).removerExercicioDoPlano(1L);
    }

    @Test
    void listarExerciciosDoPlano_DeveRetornarLista() throws Exception {
        when(planoTreinoService.buscarPorId(1L)).thenReturn(planoTreino);
        when(itemTreinoService.listarExerciciosDoPlano(planoTreino)).thenReturn(Arrays.asList(itemTreino));

        mockMvc.perform(get("/api/itens-treino/plano/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].exercicioNome").value("Supino Reto"));
    }
}
