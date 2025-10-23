package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.MatriculaException;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.PlanoRepository;

@SpringBootTest
public class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private PlanoRepository planoRepository;

    @InjectMocks
    private MatriculaService matriculaService;

    private Aluno aluno;
    private Plano plano;
    private Matricula matricula;
    private LocalDate hoje;
    private LocalDate amanha;
    private LocalDate mesQueVem;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.now();
        amanha = hoje.plusDays(1);
        mesQueVem = hoje.plusMonths(1);

        aluno = new Aluno("João", "11111111111", LocalDate.of(2000, 1, 1));
        aluno.setIdAluno(1L);

        plano = new Plano("Plano Test", "Descrição", new BigDecimal("99.90"), 1);
        plano.setIdPlanoAssinatura(1L);
        plano.setStatus("ATIVO");

        matricula = new Matricula(aluno, plano, amanha, mesQueVem, MatriculaStatus.ATIVA);
        matricula.setIdMatricula(1L);
    }

    @Test
    void criarMatricula_QuandoValida_DeveSalvarComSucesso() {
        when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);

        Matricula saved = matriculaService.criarMatricula(matricula);

        assertNotNull(saved);
        assertEquals(MatriculaStatus.ATIVA, saved.getStatus());
        verify(matriculaRepository).save(any(Matricula.class));
    }

    @Test
    void criarMatricula_QuandoPlanoInativo_DeveLancarExcecao() {
        plano.setStatus("INATIVO");
        when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));

        assertThrows(MatriculaException.PlanoInvalidoException.class, () -> {
            matriculaService.criarMatricula(matricula);
        });

        verify(matriculaRepository, never()).save(any(Matricula.class));
    }

    @Test
    void buscarMatriculaPorId_QuandoExiste_DeveRetornarMatricula() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        Matricula found = matriculaService.buscarMatriculaPorId(1L);

        assertNotNull(found);
        assertEquals(matricula.getIdMatricula(), found.getIdMatricula());
    }

    @Test
    void buscarMatriculaPorId_QuandoNaoExiste_DeveLancarExcecao() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MatriculaException.MatriculaNotFoundException.class, () -> {
            matriculaService.buscarMatriculaPorId(1L);
        });
    }

    @Test
    void cancelarMatricula_QuandoAtiva_DeveAtualizarStatus() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);

        matriculaService.cancelarMatricula(1L);

        assertEquals(MatriculaStatus.CANCELADA, matricula.getStatus());
        verify(matriculaRepository).save(matricula);
    }

    @Test
    void cancelarMatricula_QuandoJaCancelada_DeveLancarExcecao() {
        matricula.setStatus(MatriculaStatus.CANCELADA);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        assertThrows(MatriculaException.StatusInvalidoException.class, () -> {
            matriculaService.cancelarMatricula(1L);
        });
    }

    @Test
    void ativarMatricula_QuandoInativaEDentroDoPeríodo_DeveAtivar() {
        matricula.setStatus(MatriculaStatus.INATIVA);
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);

        matriculaService.ativarMatricula(1L);

        assertEquals(MatriculaStatus.ATIVA, matricula.getStatus());
        verify(matriculaRepository).save(matricula);
    }

    @Test
    void ativarMatricula_QuandoExpirada_DeveLancarExcecao() {
        matricula.setStatus(MatriculaStatus.INATIVA);
        matricula.setDataFim(hoje.minusDays(1));
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        assertThrows(MatriculaException.DataInvalidaException.class, () -> {
            matriculaService.ativarMatricula(1L);
        });
    }

    @Test
    void listarMatriculasPorStatus_DeveRetornarListaFiltrada() {
        List<Matricula> matriculas = Arrays.asList(matricula);
        when(matriculaRepository.findByStatus(MatriculaStatus.ATIVA)).thenReturn(matriculas);

        List<Matricula> result = matriculaService.listarMatriculasPorStatus(MatriculaStatus.ATIVA);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(matriculaRepository).findByStatus(MatriculaStatus.ATIVA);
    }

    @Test
    void listarMatriculasPorPlano_DeveRetornarListaFiltrada() {
        List<Matricula> matriculas = Arrays.asList(matricula);
        when(matriculaRepository.findByPlano(plano)).thenReturn(matriculas);

        List<Matricula> result = matriculaService.listarMatriculasPorPlano(plano);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(matriculaRepository).findByPlano(plano);
    }
}