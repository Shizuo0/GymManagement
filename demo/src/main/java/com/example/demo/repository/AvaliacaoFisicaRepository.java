package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.AvaliacaoFisica;
import com.example.demo.entity.Instrutor;

/**
 * Repository para operações com a entidade AvaliacaoFisica
 */
@Repository
public interface AvaliacaoFisicaRepository extends JpaRepository<AvaliacaoFisica, Long> {
    
    /**
     * Busca todas as avaliações com aluno e instrutor carregados
     * @return Lista de avaliações físicas
     */
    @Query("SELECT af FROM AvaliacaoFisica af JOIN FETCH af.aluno JOIN FETCH af.instrutor ORDER BY af.dataAvaliacao DESC")
    List<AvaliacaoFisica> findAllWithAlunoAndInstrutor();
    
    /**
     * Busca todas as avaliações de um aluno
     * @param aluno Aluno
     * @return Lista de avaliações do aluno
     */
    List<AvaliacaoFisica> findByAluno(Aluno aluno);
    
    /**
     * Busca todas as avaliações realizadas por um instrutor
     * @param instrutor Instrutor
     * @return Lista de avaliações do instrutor
     */
    List<AvaliacaoFisica> findByInstrutor(Instrutor instrutor);
    
    /**
     * Busca avaliações de um aluno ordenadas por data (mais recente primeiro)
     * @param aluno Aluno
     * @return Lista de avaliações ordenadas
     */
    List<AvaliacaoFisica> findByAlunoOrderByDataAvaliacaoDesc(Aluno aluno);
    
    /**
     * Busca avaliações realizadas em uma data específica
     * @param dataAvaliacao Data da avaliação
     * @return Lista de avaliações
     */
    List<AvaliacaoFisica> findByDataAvaliacao(LocalDate dataAvaliacao);
    
    /**
     * Busca avaliações realizadas entre duas datas
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de avaliações
     */
    List<AvaliacaoFisica> findByDataAvaliacaoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca a avaliação mais recente de um aluno
     * @param aluno Aluno
     * @return Optional com a avaliação mais recente
     */
    Optional<AvaliacaoFisica> findFirstByAlunoOrderByDataAvaliacaoDesc(Aluno aluno);
    
    /**
     * Busca avaliações de um aluno realizadas por um instrutor específico
     * @param aluno Aluno
     * @param instrutor Instrutor
     * @return Lista de avaliações
     */
    List<AvaliacaoFisica> findByAlunoAndInstrutor(Aluno aluno, Instrutor instrutor);
    
    /**
     * Conta quantas avaliações um aluno possui
     * @param aluno Aluno
     * @return Número de avaliações
     */
    long countByAluno(Aluno aluno);
    
    /**
     * Conta quantas avaliações um instrutor realizou
     * @param instrutor Instrutor
     * @return Número de avaliações
     */
    long countByInstrutor(Instrutor instrutor);
    
    /**
     * Verifica se existe avaliação para um aluno em uma data específica
     * @param aluno Aluno
     * @param dataAvaliacao Data da avaliação
     * @return true se existir, false caso contrário
     */
    boolean existsByAlunoAndDataAvaliacao(Aluno aluno, LocalDate dataAvaliacao);
    
    /**
     * Busca todas as avaliações ordenadas por data (mais recentes primeiro)
     * @return Lista de avaliações ordenadas
     */
    List<AvaliacaoFisica> findAllByOrderByDataAvaliacaoDesc();
    
    /**
     * Busca avaliações de um aluno em um período
     * @param aluno Aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de avaliações
     */
    @Query("SELECT a FROM AvaliacaoFisica a WHERE a.aluno = :aluno AND a.dataAvaliacao BETWEEN :dataInicio AND :dataFim ORDER BY a.dataAvaliacao DESC")
    List<AvaliacaoFisica> findAvaliacoesAlunoNoPeriodo(Aluno aluno, LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca todas as avaliações realizadas por um instrutor (por ID)
     * @param idInstrutor ID do instrutor
     * @return Lista de avaliações do instrutor
     */
    List<AvaliacaoFisica> findByInstrutorIdInstrutor(Long idInstrutor);
}
