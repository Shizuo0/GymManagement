package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.ExercicioRequestDTO;
import com.example.demo.entity.Exercicio;
import com.example.demo.exception.ExercicioException;
import com.example.demo.service.ExercicioService;
import com.example.demo.service.PlanoTreinoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ExercicioController.class)
public class ExercicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExercicioService exercicioService;

    @MockitoBean
    private PlanoTreinoService planoTreinoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Exercicio exercicio;
    private ExercicioRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        exercicio = new Exercicio("Supino Reto", "Peito");
        exercicio.setIdExercicio(1L);

        requestDTO = new ExercicioRequestDTO();
        requestDTO.setNome("Supino Reto");
        requestDTO.setGrupoMuscular("Peito");
    }

    @Test
    void criarExercicio_QuandoValido_DeveRetornarCreated() throws Exception {
        when(exercicioService.criarExercicio(any(Exercicio.class))).thenReturn(exercicio);

        mockMvc.perform(post("/api/exercicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Supino Reto"))
                .andExpect(jsonPath("$.grupoMuscular").value("Peito"));
    }

    @Test
    void criarExercicio_QuandoDuplicado_DeveRetornarConflict() throws Exception {
        when(exercicioService.criarExercicio(any(Exercicio.class)))
            .thenThrow(new ExercicioException.DuplicateExercicioException("Exercício já existe"));

        mockMvc.perform(post("/api/exercicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void criarExercicio_QuandoNomeInvalido_DeveRetornarBadRequest() throws Exception {
        requestDTO.setNome("");

        mockMvc.perform(post("/api/exercicios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarExercicio_QuandoExiste_DeveRetornarExercicio() throws Exception {
        when(exercicioService.buscarPorId(1L)).thenReturn(exercicio);

        mockMvc.perform(get("/api/exercicios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Supino Reto"));
    }

    @Test
    void buscarExercicio_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(exercicioService.buscarPorId(1L))
            .thenThrow(new ExercicioException.ExercicioNotFoundException("Exercício não encontrado"));

        mockMvc.perform(get("/api/exercicios/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarTodosExercicios_DeveRetornarLista() throws Exception {
        when(exercicioService.listarTodos()).thenReturn(Arrays.asList(exercicio));

        mockMvc.perform(get("/api/exercicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nome").value("Supino Reto"));
    }

    @Test
    void atualizarExercicio_QuandoValido_DeveRetornarOk() throws Exception {
        when(exercicioService.atualizarExercicio(eq(1L), any(Exercicio.class))).thenReturn(exercicio);

        mockMvc.perform(put("/api/exercicios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void atualizarExercicio_QuandoDuplicado_DeveRetornarConflict() throws Exception {
        when(exercicioService.atualizarExercicio(eq(1L), any(Exercicio.class)))
            .thenThrow(new ExercicioException.DuplicateExercicioException("Exercício já existe"));

        mockMvc.perform(put("/api/exercicios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    void deletarExercicio_QuandoExiste_DeveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/api/exercicios/1"))
                .andExpect(status().isNoContent());

        verify(exercicioService).deletarExercicio(1L);
    }

    @Test
    void deletarExercicio_QuandoEmUso_DeveRetornarConflict() throws Exception {
        doThrow(new ExercicioException.ExercicioEmUsoException("Exercício em uso"))
            .when(exercicioService).deletarExercicio(1L);

        mockMvc.perform(delete("/api/exercicios/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void buscarPorGrupoMuscular_DeveRetornarLista() throws Exception {
        when(exercicioService.buscarPorGrupoMuscular("Peito")).thenReturn(Arrays.asList(exercicio));

        mockMvc.perform(get("/api/exercicios/grupo-muscular/Peito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].grupoMuscular").value("Peito"));
    }

    @Test
    void buscarPorNome_DeveRetornarLista() throws Exception {
        when(exercicioService.buscarPorNome("Supino")).thenReturn(Arrays.asList(exercicio));

        mockMvc.perform(get("/api/exercicios/buscar?nome=Supino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Supino Reto"));
    }
}
