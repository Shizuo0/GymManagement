package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Instrutor;
import com.example.demo.entity.PlanoTreino;

/**
 * Repository para operações com a entidade PlanoTreino
 */
@Repository
public interface PlanoTreinoRepository extends JpaRepository<PlanoTreino, Long> {
    
    /**
     * Busca todos os planos de treino de um aluno
     * @param aluno Aluno
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByAluno(Aluno aluno);
    
    /**
     * Busca todos os planos criados por um instrutor
     * @param instrutor Instrutor
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByInstrutor(Instrutor instrutor);
    
    /**
     * Busca planos de treino de um aluno criados por um instrutor específico
     * @param aluno Aluno
     * @param instrutor Instrutor
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByAlunoAndInstrutor(Aluno aluno, Instrutor instrutor);
    
    /**
     * Busca planos de treino criados em uma data específica
     * @param dataCriacao Data de criação
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByDataCriacao(LocalDate dataCriacao);
    
    /**
     * Busca os planos de treino mais recentes de um aluno
     * @param aluno Aluno
     * @param limit Número máximo de planos a retornar
     * @return Lista de planos de treino ordenados por data de criação decrescente
     */
    @Query("SELECT p FROM PlanoTreino p WHERE p.aluno = :aluno ORDER BY p.dataCriacao DESC")
    List<PlanoTreino> findMostRecentByAluno(Aluno aluno, int limit);
    
    /**
     * Busca planos de treino criados entre duas datas
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByDataCriacaoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca planos de treino por duração em semanas
     * @param duracaoSemanas Duração em semanas
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByDuracaoSemanas(Integer duracaoSemanas);
    
    /**
     * Busca planos de treino de um aluno ordenados por data de criação (mais recentes primeiro)
     * @param aluno Aluno
     * @return Lista de planos ordenados
     */
    List<PlanoTreino> findByAlunoOrderByDataCriacaoDesc(Aluno aluno);
    
    /**
     * Busca planos de treino de um instrutor ordenados por data de criação (mais recentes primeiro)
     * @param instrutor Instrutor
     * @return Lista de planos ordenados
     */
    List<PlanoTreino> findByInstrutorOrderByDataCriacaoDesc(Instrutor instrutor);
    
    /**
     * Busca o plano de treino mais recente de um aluno
     * @param aluno Aluno
     * @return Optional com o plano mais recente
     */
    Optional<PlanoTreino> findFirstByAlunoOrderByDataCriacaoDesc(Aluno aluno);
    
    /**
     * Conta quantos planos de treino um aluno possui
     * @param aluno Aluno
     * @return Número de planos
     */
    long countByAluno(Aluno aluno);
    
    /**
     * Conta quantos planos de treino um instrutor criou
     * @param instrutor Instrutor
     * @return Número de planos
     */
    long countByInstrutor(Instrutor instrutor);
    
    /**
     * Verifica se existe plano de treino para um aluno em uma data específica
     * @param aluno Aluno
     * @param dataCriacao Data de criação
     * @return true se existir, false caso contrário
     */
    boolean existsByAlunoAndDataCriacao(Aluno aluno, LocalDate dataCriacao);
    
    /**
     * Busca planos de treino com duração maior ou igual a um valor
     * @param semanas Duração mínima em semanas
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByDuracaoSemanasGreaterThanEqual(Integer semanas);
    
    /**
     * Busca planos de treino com duração menor ou igual a um valor
     * @param semanas Duração máxima em semanas
     * @return Lista de planos de treino
     */
    List<PlanoTreino> findByDuracaoSemanasLessThanEqual(Integer semanas);
    
    /**
     * Busca todos os planos ordenados por data de criação (mais recentes primeiro)
     * @return Lista de planos ordenados
     */
    List<PlanoTreino> findAllByOrderByDataCriacaoDesc();
    
    /**
     * Busca planos de treino de um aluno em um período específico
     * @param aluno Aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de planos
     */
    @Query("SELECT p FROM PlanoTreino p WHERE p.aluno = :aluno AND p.dataCriacao BETWEEN :dataInicio AND :dataFim ORDER BY p.dataCriacao DESC")
    List<PlanoTreino> buscarPlanosAlunoNoPeriodo(Aluno aluno, LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca planos de treino criados por um instrutor em um período
     * @param instrutor Instrutor
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de planos
     */
    @Query("SELECT p FROM PlanoTreino p WHERE p.instrutor = :instrutor AND p.dataCriacao BETWEEN :dataInicio AND :dataFim ORDER BY p.dataCriacao DESC")
    List<PlanoTreino> buscarPlanosInstrutorNoPeriodo(Instrutor instrutor, LocalDate dataInicio, LocalDate dataFim);
}
