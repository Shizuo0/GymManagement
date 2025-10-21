package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.enums.MatriculaStatus;

/**
 * Repository para operações com a entidade Matricula
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    
    /**
     * Busca todas as matrículas de um aluno
     * @param aluno Aluno
     * @return Lista de matrículas do aluno
     */
    List<Matricula> findByAluno(Aluno aluno);
    
    /**
     * Busca matrículas por status
     * @param status Status da matrícula (ex: ATIVA, INATIVA, PENDENTE, CANCELADA)
     * @return Lista de matrículas com o status especificado
     */
    List<Matricula> findByStatus(MatriculaStatus status);
    
    /**
     * Busca matrículas de um aluno por status
     * @param aluno Aluno
     * @param status Status da matrícula
     * @return Lista de matrículas
     */
    List<Matricula> findByAlunoAndStatus(Aluno aluno, MatriculaStatus status);
    
    /**
     * Busca todas as matrículas de um plano específico
     * @param plano Plano
     * @return Lista de matrículas do plano
     */
    List<Matricula> findByPlano(Plano plano);
    
    /**
     * Busca matrículas que vencem em uma data específica
     * @param dataFim Data de vencimento
     * @return Lista de matrículas
     */
    List<Matricula> findByDataFim(LocalDate dataFim);
    
    /**
     * Busca matrículas que vencem entre duas datas
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de matrículas
     */
    List<Matricula> findByDataFimBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca matrículas ativas (status = "ATIVA")
     * @return Lista de matrículas ativas
     */
    @Query("SELECT m FROM Matricula m WHERE m.status = 'ATIVA'")
    List<Matricula> findMatriculasAtivas();
    
    /**
     * Conta quantas matrículas um aluno possui
     * @param aluno Aluno
     * @return Número de matrículas
     */
    long countByAluno(Aluno aluno);
    
    /**
     * Verifica se existe uma matrícula ativa para o aluno
     * @param aluno Aluno
     * @param status Status (geralmente "ATIVA")
     * @return true se existir, false caso contrário
     */
    boolean existsByAlunoAndStatus(Aluno aluno, String status);
}
