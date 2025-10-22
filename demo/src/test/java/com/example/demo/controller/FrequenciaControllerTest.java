package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.FrequenciaRequestDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Frequencia;
import com.example.demo.exception.FrequenciaException;
import com.example.demo.service.AlunoService;
import com.example.demo.service.FrequenciaService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testes para FrequenciaController
 */
@WebMvcTest(FrequenciaController.class)
public class FrequenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FrequenciaService frequenciaService;

    @MockitoBean
    private AlunoService alunoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Aluno aluno;
    private Frequencia frequencia;
    private FrequenciaRequestDTO requestDTO;
    private LocalDate hoje;
    private LocalDate ontem;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.now();
        ontem = hoje.minusDays(1);

        aluno = new Aluno("Maria Santos", "98765432100", LocalDate.of(1995, 8, 20));
        aluno.setIdAluno(1L);

        frequencia = new Frequencia(aluno, ontem, true);
        frequencia.setIdFrequencia(1L);

        requestDTO = new FrequenciaRequestDTO(1L, ontem, true);
    }

    @Test
    void registrarPresenca_QuandoValida_DeveRetornarCreated() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.registrarPresenca(any(Frequencia.class))).thenReturn(frequencia);

        mockMvc.perform(post("/api/frequencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idFrequencia").value(1))
                .andExpect(jsonPath("$.idAluno").value(1))
                .andExpect(jsonPath("$.nomeAluno").value("Maria Santos"))
                .andExpect(jsonPath("$.presenca").value(true))
                .andExpect(jsonPath("$.statusPresenca").value("Presente"));

        verify(frequenciaService).registrarPresenca(any(Frequencia.class));
    }

    @Test
    void registrarPresenca_QuandoAlunoNaoExiste_DeveRetornarInternalError() throws Exception {
        when(alunoService.buscarPorId(1L))
            .thenThrow(new RuntimeException("Aluno não encontrado"));

        mockMvc.perform(post("/api/frequencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registrarPresenca_QuandoDuplicada_DeveRetornarConflict() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.registrarPresenca(any(Frequencia.class)))
            .thenThrow(new FrequenciaException.FrequenciaConflictException("Já existe registro para esta data"));

        mockMvc.perform(post("/api/frequencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe registro para esta data"));
    }

    @Test
    void registrarPresenca_QuandoAlunoSemMatriculaAtiva_DeveRetornarBadRequest() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.registrarPresenca(any(Frequencia.class)))
            .thenThrow(new FrequenciaException.AlunoSemMatriculaAtivaException("Aluno não possui matrícula ativa"));

        mockMvc.perform(post("/api/frequencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Aluno não possui matrícula ativa"));
    }

    @Test
    void registrarPresenca_QuandoDataFutura_DeveRetornarBadRequest() throws Exception {
        FrequenciaRequestDTO invalidDTO = new FrequenciaRequestDTO(1L, hoje.plusDays(1), true);

        mockMvc.perform(post("/api/frequencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarFrequencias_DeveRetornarOk() throws Exception {
        List<Frequencia> frequencias = Arrays.asList(frequencia);
        when(frequenciaService.listarTodos()).thenReturn(frequencias);

        mockMvc.perform(get("/api/frequencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFrequencia").value(1))
                .andExpect(jsonPath("$[0].nomeAluno").value("Maria Santos"));

        verify(frequenciaService).listarTodos();
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarOk() throws Exception {
        when(frequenciaService.buscarPorId(1L)).thenReturn(frequencia);

        mockMvc.perform(get("/api/frequencias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFrequencia").value(1))
                .andExpect(jsonPath("$.presenca").value(true));

        verify(frequenciaService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(frequenciaService.buscarPorId(999L))
            .thenThrow(new FrequenciaException.FrequenciaNotFoundException("Frequência não encontrada"));

        mockMvc.perform(get("/api/frequencias/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Frequência não encontrada"));
    }

    @Test
    void listarPorAluno_DeveRetornarOk() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.listarPorAluno(aluno)).thenReturn(Arrays.asList(frequencia));

        mockMvc.perform(get("/api/frequencias/aluno/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAluno").value(1));

        verify(frequenciaService).listarPorAluno(aluno);
    }

    @Test
    void buscarPorAlunoEData_QuandoExiste_DeveRetornarOk() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.buscarPorAlunoEData(aluno, ontem)).thenReturn(frequencia);

        mockMvc.perform(get("/api/frequencias/aluno/1/data/" + ontem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFrequencia").value(1));

        verify(frequenciaService).buscarPorAlunoEData(aluno, ontem);
    }

    @Test
    void listarPorData_DeveRetornarOk() throws Exception {
        when(frequenciaService.listarPorData(ontem)).thenReturn(Arrays.asList(frequencia));

        mockMvc.perform(get("/api/frequencias/data/" + ontem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].data").value(ontem.toString()));

        verify(frequenciaService).listarPorData(ontem);
    }

    @Test
    void listarPresencasPorData_DeveRetornarOk() throws Exception {
        when(frequenciaService.listarPresencasPorData(ontem)).thenReturn(Arrays.asList(frequencia));

        mockMvc.perform(get("/api/frequencias/presencas/data/" + ontem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].presenca").value(true));

        verify(frequenciaService).listarPresencasPorData(ontem);
    }

    @Test
    void buscarPorPeriodo_DeveRetornarOk() throws Exception {
        LocalDate dataInicio = hoje.minusMonths(1);
        LocalDate dataFim = hoje;
        when(frequenciaService.listarPorPeriodo(dataInicio, dataFim)).thenReturn(Arrays.asList(frequencia));

        mockMvc.perform(get("/api/frequencias/periodo")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idFrequencia").value(1));

        verify(frequenciaService).listarPorPeriodo(dataInicio, dataFim);
    }

    @Test
    void buscarPorAlunoEPeriodo_DeveRetornarOk() throws Exception {
        LocalDate dataInicio = hoje.minusMonths(1);
        LocalDate dataFim = hoje;
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.listarPorAlunoEPeriodo(aluno, dataInicio, dataFim))
            .thenReturn(Arrays.asList(frequencia));

        mockMvc.perform(get("/api/frequencias/aluno/1/periodo")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAluno").value(1));

        verify(frequenciaService).listarPorAlunoEPeriodo(aluno, dataInicio, dataFim);
    }

    @Test
    void contarPresencas_DeveRetornarOk() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.contarPresencas(aluno)).thenReturn(10L);

        mockMvc.perform(get("/api/frequencias/aluno/1/total-presencas"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(frequenciaService).contarPresencas(aluno);
    }

    @Test
    void calcularTaxaPresenca_DeveRetornarOk() throws Exception {
        LocalDate dataInicio = hoje.minusMonths(1);
        LocalDate dataFim = hoje;
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.calcularTaxaPresenca(aluno, dataInicio, dataFim)).thenReturn(85.5);

        mockMvc.perform(get("/api/frequencias/aluno/1/taxa-presenca")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAluno").value(1))
                .andExpect(jsonPath("$.nomeAluno").value("Maria Santos"))
                .andExpect(jsonPath("$.taxaPresenca").value(85.5))
                .andExpect(jsonPath("$.taxaFormatada").value("85,50%"));

        verify(frequenciaService).calcularTaxaPresenca(aluno, dataInicio, dataFim);
    }

    @Test
    void atualizarFrequencia_QuandoValida_DeveRetornarOk() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.atualizarFrequencia(eq(1L), any(Frequencia.class))).thenReturn(frequencia);

        mockMvc.perform(put("/api/frequencias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFrequencia").value(1));

        verify(frequenciaService).atualizarFrequencia(eq(1L), any(Frequencia.class));
    }

    @Test
    void atualizarFrequencia_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(frequenciaService.atualizarFrequencia(eq(999L), any(Frequencia.class)))
            .thenThrow(new FrequenciaException.FrequenciaNotFoundException("Frequência não encontrada"));

        mockMvc.perform(put("/api/frequencias/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarFrequencia_QuandoExiste_DeveRetornarNoContent() throws Exception {
        doNothing().when(frequenciaService).deletarFrequencia(1L);

        mockMvc.perform(delete("/api/frequencias/1"))
                .andExpect(status().isNoContent());

        verify(frequenciaService).deletarFrequencia(1L);
    }

    @Test
    void deletarFrequencia_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(frequenciaService.buscarPorId(999L))
            .thenThrow(new FrequenciaException.FrequenciaNotFoundException("Frequência não encontrada"));

        doNothing().when(frequenciaService).deletarFrequencia(999L);

        mockMvc.perform(delete("/api/frequencias/999"))
                .andExpect(status().isNoContent());
    }
}
