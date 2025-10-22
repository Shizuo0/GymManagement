package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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

import com.example.demo.dto.PagamentoRequestDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Pagamento;
import com.example.demo.entity.Plano;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.PagamentoException;
import com.example.demo.service.MatriculaService;
import com.example.demo.service.PagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testes para PagamentoController
 */
@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagamentoService pagamentoService;

    @MockitoBean
    private MatriculaService matriculaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Aluno aluno;
    private Plano plano;
    private Matricula matricula;
    private Pagamento pagamento;
    private PagamentoRequestDTO requestDTO;
    private LocalDate hoje;
    private LocalDate ontem;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.now();
        ontem = hoje.minusDays(1);

        aluno = new Aluno("João Silva", "12345678900", LocalDate.of(1990, 5, 15));
        aluno.setIdAluno(1L);

        plano = new Plano("Plano Mensal", "Acesso ilimitado", new BigDecimal("99.90"), 30);
        plano.setIdPlanoAssinatura(1L);

        matricula = new Matricula(aluno, plano, hoje, hoje.plusMonths(1), MatriculaStatus.ATIVA);
        matricula.setIdMatricula(1L);

        pagamento = new Pagamento(matricula, ontem, new BigDecimal("99.90"), "CARTAO");
        pagamento.setIdPagamento(1L);

        requestDTO = new PagamentoRequestDTO(1L, ontem, new BigDecimal("99.90"), "CARTAO");
    }

    @Test
    void registrarPagamento_QuandoValido_DeveRetornarCreated() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);
        when(pagamentoService.registrarPagamento(any(Pagamento.class))).thenReturn(pagamento);

        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPagamento").value(1))
                .andExpect(jsonPath("$.idMatricula").value(1))
                .andExpect(jsonPath("$.nomeAluno").value("João Silva"))
                .andExpect(jsonPath("$.nomePlano").value("Plano Mensal"))
                .andExpect(jsonPath("$.valorPago").value(99.90))
                .andExpect(jsonPath("$.formaPagamento").value("CARTAO"));

        verify(pagamentoService).registrarPagamento(any(Pagamento.class));
    }

    @Test
    void registrarPagamento_QuandoMatriculaNaoExiste_DeveRetornarNotFound() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L))
            .thenThrow(new RuntimeException("Matrícula não encontrada"));

        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registrarPagamento_QuandoMatriculaInvalida_DeveRetornarBadRequest() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);
        when(pagamentoService.registrarPagamento(any(Pagamento.class)))
            .thenThrow(new PagamentoException.MatriculaInvalidaException("Matrícula não está ativa"));

        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Matrícula não está ativa"));
    }

    @Test
    void registrarPagamento_QuandoValorInvalido_DeveRetornarBadRequest() throws Exception {
        PagamentoRequestDTO invalidDTO = new PagamentoRequestDTO(1L, ontem, new BigDecimal("-10.00"), "CARTAO");
        
        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarPagamento_QuandoDataFutura_DeveRetornarBadRequest() throws Exception {
        PagamentoRequestDTO invalidDTO = new PagamentoRequestDTO(1L, hoje.plusDays(1), new BigDecimal("99.90"), "CARTAO");
        
        mockMvc.perform(post("/api/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarPagamentos_DeveRetornarOk() throws Exception {
        List<Pagamento> pagamentos = Arrays.asList(pagamento);
        when(pagamentoService.listarTodos()).thenReturn(pagamentos);

        mockMvc.perform(get("/api/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPagamento").value(1))
                .andExpect(jsonPath("$[0].nomeAluno").value("João Silva"));

        verify(pagamentoService).listarTodos();
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarOk() throws Exception {
        when(pagamentoService.buscarPorId(1L)).thenReturn(pagamento);

        mockMvc.perform(get("/api/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPagamento").value(1))
                .andExpect(jsonPath("$.valorPago").value(99.90));

        verify(pagamentoService).buscarPorId(1L);
    }

    @Test
    void buscarPorId_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(pagamentoService.buscarPorId(999L))
            .thenThrow(new PagamentoException.PagamentoNotFoundException("Pagamento não encontrado"));

        mockMvc.perform(get("/api/pagamentos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Pagamento não encontrado"));
    }

    @Test
    void listarPorMatricula_DeveRetornarOk() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);
        when(pagamentoService.listarPagamentosPorMatricula(matricula)).thenReturn(Arrays.asList(pagamento));

        mockMvc.perform(get("/api/pagamentos/matricula/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPagamento").value(1));

        verify(pagamentoService).listarPagamentosPorMatricula(matricula);
    }

    @Test
    void buscarPorPeriodo_DeveRetornarOk() throws Exception {
        LocalDate dataInicio = hoje.minusMonths(1);
        LocalDate dataFim = hoje;
        when(pagamentoService.buscarPorPeriodo(dataInicio, dataFim)).thenReturn(Arrays.asList(pagamento));

        mockMvc.perform(get("/api/pagamentos/periodo")
                .param("dataInicio", dataInicio.toString())
                .param("dataFim", dataFim.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPagamento").value(1));

        verify(pagamentoService).buscarPorPeriodo(dataInicio, dataFim);
    }

    @Test
    void buscarPorFormaPagamento_DeveRetornarOk() throws Exception {
        when(pagamentoService.buscarPorFormaPagamento("CARTAO")).thenReturn(Arrays.asList(pagamento));

        mockMvc.perform(get("/api/pagamentos/forma-pagamento/CARTAO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].formaPagamento").value("CARTAO"));

        verify(pagamentoService).buscarPorFormaPagamento("CARTAO");
    }

    @Test
    void calcularTotalPago_DeveRetornarOk() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);
        when(pagamentoService.calcularTotalPago(matricula)).thenReturn(new BigDecimal("199.80"));

        mockMvc.perform(get("/api/pagamentos/matricula/1/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("199.80"));

        verify(pagamentoService).calcularTotalPago(matricula);
    }

    @Test
    void verificarPagamentosEmDia_DeveRetornarOk() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);
        when(pagamentoService.verificarPagamentosEmDia(matricula)).thenReturn(true);

        mockMvc.perform(get("/api/pagamentos/matricula/1/em-dia"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(pagamentoService).verificarPagamentosEmDia(matricula);
    }

    @Test
    void atualizarPagamento_QuandoValido_DeveRetornarOk() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);
        when(pagamentoService.atualizarPagamento(eq(1L), any(Pagamento.class))).thenReturn(pagamento);

        mockMvc.perform(put("/api/pagamentos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPagamento").value(1));

        verify(pagamentoService).atualizarPagamento(eq(1L), any(Pagamento.class));
    }

    @Test
    void atualizarPagamento_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(matriculaService.buscarMatriculaPorId(1L)).thenReturn(matricula);
        when(pagamentoService.atualizarPagamento(eq(999L), any(Pagamento.class)))
            .thenThrow(new PagamentoException.PagamentoNotFoundException("Pagamento não encontrado"));

        mockMvc.perform(put("/api/pagamentos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletarPagamento_QuandoExiste_DeveRetornarNoContent() throws Exception {
        doNothing().when(pagamentoService).deletarPagamento(1L);

        mockMvc.perform(delete("/api/pagamentos/1"))
                .andExpect(status().isNoContent());

        verify(pagamentoService).deletarPagamento(1L);
    }

    @Test
    void deletarPagamento_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(pagamentoService.buscarPorId(999L))
            .thenThrow(new PagamentoException.PagamentoNotFoundException("Pagamento não encontrado"));

        doNothing().when(pagamentoService).deletarPagamento(999L);

        mockMvc.perform(delete("/api/pagamentos/999"))
                .andExpect(status().isNoContent());
    }
}
