package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.*;
import com.example.demo.exception.HistoricoException;
import com.example.demo.service.HistoricoAlunoService;

/**
 * Testes para HistoricoAlunoController
 */
@WebMvcTest(HistoricoAlunoController.class)
public class HistoricoAlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistoricoAlunoService historicoAlunoService;

    private HistoricoAlunoDTO historicoCompleto;
    private Long idAluno;

    @BeforeEach
    void setUp() {
        idAluno = 1L;

        // Criar dados de teste
        MatriculaSummaryDTO matricula = new MatriculaSummaryDTO(
            1L,
            "Plano Premium",
            new BigDecimal("100.00"),
            LocalDate.now().minusMonths(6),
            LocalDate.now().plusMonths(6),
            "ATIVA"
        );
        matricula.setTotalPago(new BigDecimal("1200.00"));
        matricula.setEmDia(true);

        PlanoTreinoSummaryDTO planoTreino = new PlanoTreinoSummaryDTO(
            1L,
            "João Personal",
            LocalDate.now().minusMonths(3),
            LocalDate.now().plusMonths(1),
            "Hipertrofia",
            12
        );

        AvaliacaoFisicaSummaryDTO avaliacao = new AvaliacaoFisicaSummaryDTO(
            1L,
            LocalDate.now().minusMonths(2),
            "Carlos Nutricionista",
            new BigDecimal("75.5"),
            new BigDecimal("1.75"),
            new BigDecimal("15.5"),
            "Peito: 100cm, Cintura: 80cm"
        );

        FrequenciaSummaryDTO frequencia = new FrequenciaSummaryDTO(
            LocalDate.now().withDayOfMonth(1),
            30L,
            25L,
            5L
        );

        HistoricoAlunoDTO.EstatisticasDTO estatisticas = new HistoricoAlunoDTO.EstatisticasDTO();
        estatisticas.setTotalMatriculas(1);
        estatisticas.setTotalPlanosTreino(2);
        estatisticas.setTotalAvaliacoesFisicas(3);
        estatisticas.setTotalPresencas(75L);
        estatisticas.setTaxaPresencaGeral(83.33);
        estatisticas.setDiasComoAluno(180);

        historicoCompleto = new HistoricoAlunoDTO(
            idAluno,
            "Maria Santos",
            "123.456.789-00",
            LocalDate.now().minusMonths(6)
        );
        historicoCompleto.setMatriculas(Arrays.asList(matricula));
        historicoCompleto.setPlanosTreino(Arrays.asList(planoTreino));
        historicoCompleto.setPlanoTreinoAtual(planoTreino);
        historicoCompleto.setAvaliacoesFisicas(Arrays.asList(avaliacao));
        historicoCompleto.setFrequenciaMensal(Arrays.asList(frequencia));
        historicoCompleto.setEstatisticas(estatisticas);
    }

    @Test
    void buscarHistoricoCompleto_QuandoAlunoExiste_DeveRetornarOk() throws Exception {
        when(historicoAlunoService.buscarHistoricoCompleto(idAluno)).thenReturn(historicoCompleto);

        mockMvc.perform(get("/api/historico/aluno/{id}", idAluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAluno").value(idAluno))
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"))
                .andExpect(jsonPath("$.matriculas").isArray())
                .andExpect(jsonPath("$.matriculas[0].nomePlano").value("Plano Premium"))
                .andExpect(jsonPath("$.planosTreino").isArray())
                .andExpect(jsonPath("$.planosTreino[0].nomeInstrutor").value("João Personal"))
                .andExpect(jsonPath("$.planoTreinoAtual.objetivo").value("Hipertrofia"))
                .andExpect(jsonPath("$.avaliacoesFisicas").isArray())
                .andExpect(jsonPath("$.frequenciaMensal").isArray())
                .andExpect(jsonPath("$.estatisticas.totalMatriculas").value(1))
                .andExpect(jsonPath("$.estatisticas.taxaPresencaGeral").value(83.33));
    }

    @Test
    void buscarHistoricoCompleto_QuandoAlunoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(historicoAlunoService.buscarHistoricoCompleto(999L))
            .thenThrow(new HistoricoException.HistoricoNotFoundException("Histórico não encontrado para aluno com ID 999"));

        mockMvc.perform(get("/api/historico/aluno/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Histórico não encontrado para aluno com ID 999"));
    }

    @Test
    void buscarHistoricoPorPeriodo_QuandoPeriodoValido_DeveRetornarOk() throws Exception {
        LocalDate inicio = LocalDate.now().minusMonths(3);
        LocalDate fim = LocalDate.now();

        when(historicoAlunoService.buscarHistoricoPorPeriodo(eq(idAluno), eq(inicio), eq(fim)))
            .thenReturn(historicoCompleto);

        mockMvc.perform(get("/api/historico/aluno/{id}/periodo", idAluno)
                .param("inicio", inicio.toString())
                .param("fim", fim.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAluno").value(idAluno))
                .andExpect(jsonPath("$.nome").value("Maria Santos"));
    }

    @Test
    void buscarHistoricoPorPeriodo_QuandoPeriodoInvalido_DeveRetornarBadRequest() throws Exception {
        LocalDate inicio = LocalDate.now();
        LocalDate fim = LocalDate.now().minusMonths(3); // fim antes do início

        when(historicoAlunoService.buscarHistoricoPorPeriodo(eq(idAluno), eq(inicio), eq(fim)))
            .thenThrow(new HistoricoException.PeriodoInvalidoException("Data início não pode ser posterior à data fim"));

        mockMvc.perform(get("/api/historico/aluno/{id}/periodo", idAluno)
                .param("inicio", inicio.toString())
                .param("fim", fim.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Data início não pode ser posterior à data fim"));
    }

    @Test
    void buscarHistoricoUltimoMes_DeveRetornarOk() throws Exception {
        when(historicoAlunoService.buscarHistoricoPorPeriodo(eq(idAluno), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(historicoCompleto);

        mockMvc.perform(get("/api/historico/aluno/{id}/ultimo-mes", idAluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAluno").value(idAluno));
    }

    @Test
    void buscarHistoricoUltimos3Meses_DeveRetornarOk() throws Exception {
        when(historicoAlunoService.buscarHistoricoPorPeriodo(eq(idAluno), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(historicoCompleto);

        mockMvc.perform(get("/api/historico/aluno/{id}/ultimos-3-meses", idAluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAluno").value(idAluno));
    }

    @Test
    void buscarHistoricoAnoAtual_DeveRetornarOk() throws Exception {
        when(historicoAlunoService.buscarHistoricoPorPeriodo(eq(idAluno), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(historicoCompleto);

        mockMvc.perform(get("/api/historico/aluno/{id}/ano-atual", idAluno))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAluno").value(idAluno));
    }

    @Test
    void buscarHistoricoCompleto_QuandoDadosCorruptos_DeveRetornarInternalServerError() throws Exception {
        when(historicoAlunoService.buscarHistoricoCompleto(idAluno))
            .thenThrow(new HistoricoException.DadosCorruptosException("Dados corrompidos detectados"));

        mockMvc.perform(get("/api/historico/aluno/{id}", idAluno))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Dados corrompidos detectados"));
    }

    @Test
    void buscarHistoricoCompleto_QuandoErroAgregacao_DeveRetornarInternalServerError() throws Exception {
        when(historicoAlunoService.buscarHistoricoCompleto(idAluno))
            .thenThrow(new HistoricoException.ErroAgregacaoException("Erro ao agregar dados de múltiplas tabelas"));

        mockMvc.perform(get("/api/historico/aluno/{id}", idAluno))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro ao agregar dados de múltiplas tabelas"));
    }

    @Test
    void buscarHistoricoPorPeriodo_QuandoSemDadosNoPeriodo_DeveRetornarNotFound() throws Exception {
        LocalDate inicio = LocalDate.now().minusMonths(1);
        LocalDate fim = LocalDate.now();

        when(historicoAlunoService.buscarHistoricoPorPeriodo(eq(idAluno), eq(inicio), eq(fim)))
            .thenThrow(new HistoricoException.SemDadosPeriodoException("Nenhum dado encontrado no período especificado"));

        mockMvc.perform(get("/api/historico/aluno/{id}/periodo", idAluno)
                .param("inicio", inicio.toString())
                .param("fim", fim.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum dado encontrado no período especificado"));
    }

    @Test
    void buscarHistoricoCompleto_QuandoDadosAusentes_DeveRetornarNotFound() throws Exception {
        when(historicoAlunoService.buscarHistoricoCompleto(idAluno))
            .thenThrow(new HistoricoException.DadosAusentesException("Dados essenciais ausentes no histórico"));

        mockMvc.perform(get("/api/historico/aluno/{id}", idAluno))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Dados essenciais ausentes no histórico"));
    }

    @Test
    void buscarHistoricoCompleto_QuandoIntegridadeDados_DeveRetornarConflict() throws Exception {
        when(historicoAlunoService.buscarHistoricoCompleto(idAluno))
            .thenThrow(new HistoricoException.IntegridadeDadosException("Inconsistência nos dados relacionais"));

        mockMvc.perform(get("/api/historico/aluno/{id}", idAluno))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Inconsistência nos dados relacionais"));
    }
}
