package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.MatriculaRequestDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.MatriculaException;
import com.example.demo.service.AlunoService;
import com.example.demo.service.MatriculaService;
import com.example.demo.service.PlanoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MatriculaController.class)
public class MatriculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MatriculaService matriculaService;

    @MockitoBean
    private AlunoService alunoService;

    @MockitoBean
    private PlanoService planoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Aluno aluno;
    private Plano plano;
    private Matricula matricula;
    private MatriculaRequestDTO requestDTO;
    private LocalDate hoje;
    private LocalDate amanha;
    private LocalDate mesQueVem;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.now();
        amanha = hoje.plusDays(1);
        mesQueVem = hoje.plusMonths(1);

        aluno = new Aluno("João Test", "11111111111", LocalDate.of(2000, 1, 1));
        aluno.setIdAluno(1L);

        plano = new Plano("Plano Test", "Descrição", new BigDecimal("99.90"), 1);
        plano.setIdPlanoAssinatura(1L);
        plano.setStatus("ATIVO");

        matricula = new Matricula(aluno, plano, amanha, mesQueVem, MatriculaStatus.ATIVA);
        matricula.setIdMatricula(1L);

        requestDTO = new MatriculaRequestDTO(1L, 1L, amanha, mesQueVem);
    }

    @Test
    void criarMatricula_QuandoValida_DeveRetornarCreated() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(planoService.buscarPlanoPorId(1L)).thenReturn(plano);
        when(matriculaService.criarMatricula(any(Matricula.class))).thenReturn(matricula);

        mockMvc.perform(post("/api/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(matricula.getIdMatricula()))
                .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    @Test
    void criarMatricula_QuandoAlunoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(alunoService.buscarPorId(1L))
            .thenThrow(new com.example.demo.exception.RecursoNaoEncontradoException("Aluno não encontrado"));

        mockMvc.perform(post("/api/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarMatricula_QuandoPlanoInativo_DeveRetornarBadRequest() throws Exception {
        when(alunoService.buscarPorId(1L)).thenReturn(aluno);
        when(planoService.buscarPlanoPorId(1L)).thenReturn(plano);
        when(matriculaService.criarMatricula(any(Matricula.class)))
            .thenThrow(new MatriculaException.PlanoInvalidoException("Plano inativo"));

        mockMvc.perform(post("/api/matriculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarMatriculaPorId_QuandoExiste_DeveRetornarMatricula() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);

        mockMvc.perform(get("/api/matriculas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(matricula.getIdMatricula()));
    }

    @Test
    void buscarMatriculaPorId_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L))
            .thenThrow(new MatriculaException.MatriculaNotFoundException("Matrícula não encontrada"));

        mockMvc.perform(get("/api/matriculas/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarMatricula_QuandoExiste_DeveRetornarOk() throws Exception {
        matricula.setStatus(MatriculaStatus.CANCELADA);
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);

        mockMvc.perform(put("/api/matriculas/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        verify(matriculaService).cancelarMatricula(1L);
    }

    @Test
    void cancelarMatricula_QuandoJaCancelada_DeveRetornarBadRequest() throws Exception {
        doThrow(new MatriculaException.StatusInvalidoException("Matrícula já cancelada"))
            .when(matriculaService).cancelarMatricula(1L);

        mockMvc.perform(put("/api/matriculas/1/cancelar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarMatriculasPorStatus_DeveRetornarLista() throws Exception {
        when(matriculaService.listarMatriculasPorStatus(MatriculaStatus.ATIVA))
            .thenReturn(Arrays.asList(matricula));

        mockMvc.perform(get("/api/matriculas/status/ATIVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(matricula.getIdMatricula()));
    }

    @Test
    void listarMatriculasPorPlano_DeveRetornarLista() throws Exception {
        when(planoService.buscarPlanoPorId(1L)).thenReturn(plano);
        when(matriculaService.listarMatriculasPorPlano(plano))
            .thenReturn(Arrays.asList(matricula));

        mockMvc.perform(get("/api/matriculas/plano/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(matricula.getIdMatricula()));
    }
}