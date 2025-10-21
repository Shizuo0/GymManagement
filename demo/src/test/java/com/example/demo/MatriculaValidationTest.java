package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.PlanoRepository;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@Transactional
public class MatriculaValidationTest {
    
    @Autowired
    private MatriculaRepository matriculaRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private PlanoRepository planoRepository;
    
    private Aluno alunoTeste;
    private Plano planoTeste;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @BeforeEach
    public void setUp() {
        alunoTeste = alunoRepository.save(new Aluno("João Test", "111.111.111-11", LocalDate.now()));
        planoTeste = planoRepository.save(new Plano("Plano Test", "Plano de teste", new BigDecimal("99.90"), 1));
        dataInicio = LocalDate.now();
        dataFim = dataInicio.plusMonths(1);
    }

    @Test
    void testCriarMatricula_ComStatusValido() {
        Matricula matricula = new Matricula(alunoTeste, planoTeste, dataInicio, dataFim, MatriculaStatus.ATIVA);
        Matricula saved = matriculaRepository.save(matricula);
        assertEquals(MatriculaStatus.ATIVA, saved.getStatus());
    }

    @Test
    void testCriarMatricula_SemAluno_DeveLancarExcecao() {
        Matricula matricula = new Matricula(null, planoTeste, dataInicio, dataFim, MatriculaStatus.ATIVA);
        assertThrows(ConstraintViolationException.class, () -> {
            matriculaRepository.save(matricula);
        });
    }

    @Test
    void testCriarMatricula_SemPlano_DeveLancarExcecao() {
        Matricula matricula = new Matricula(alunoTeste, null, dataInicio, dataFim, MatriculaStatus.ATIVA);
        assertThrows(ConstraintViolationException.class, () -> {
            matriculaRepository.save(matricula);
        });
    }

    @Test
    void testCriarMatricula_SemDataInicio_DeveLancarExcecao() {
        Matricula matricula = new Matricula(alunoTeste, planoTeste, null, dataFim, MatriculaStatus.ATIVA);
        assertThrows(ConstraintViolationException.class, () -> {
            matriculaRepository.save(matricula);
        });
    }

    @Test
    void testCriarMatricula_SemDataFim_DeveLancarExcecao() {
        Matricula matricula = new Matricula(alunoTeste, planoTeste, dataInicio, null, MatriculaStatus.ATIVA);
        assertThrows(ConstraintViolationException.class, () -> {
            matriculaRepository.save(matricula);
        });
    }

    @Test
    void testCriarMatricula_SemStatus_DeveLancarExcecao() {
        Matricula matricula = new Matricula(alunoTeste, planoTeste, dataInicio, dataFim, null);
        assertThrows(ConstraintViolationException.class, () -> {
            matriculaRepository.save(matricula);
        });
    }

    @Test
    void testAlterarStatusMatricula() {
        Matricula matricula = new Matricula(alunoTeste, planoTeste, dataInicio, dataFim, MatriculaStatus.ATIVA);
        Matricula saved = matriculaRepository.save(matricula);
        
        saved.setStatus(MatriculaStatus.INATIVA);
        saved = matriculaRepository.save(saved);
        assertEquals(MatriculaStatus.INATIVA, saved.getStatus());
        
        saved.setStatus(MatriculaStatus.PENDENTE);
        saved = matriculaRepository.save(saved);
        assertEquals(MatriculaStatus.PENDENTE, saved.getStatus());
        
        saved.setStatus(MatriculaStatus.CANCELADA);
        saved = matriculaRepository.save(saved);
        assertEquals(MatriculaStatus.CANCELADA, saved.getStatus());
    }
}