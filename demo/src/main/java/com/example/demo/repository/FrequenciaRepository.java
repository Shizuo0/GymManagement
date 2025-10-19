package com.example.demo.repository;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com a entidade Frequencia
 */
@Repository
public interface FrequenciaRepository extends JpaRepository<Frequencia, Long> {
    
    /**
     * Busca todos os registros de frequência de um aluno
     * @param aluno Aluno
     * @return Lista de registros de frequência
     */
    List<Frequencia> findByAluno(Aluno aluno);
    
    /**
     * Busca registros de frequência de um aluno em uma data específica
     * @param aluno Aluno
     * @param data Data
     * @return Optional com o registro se encontrado
     */
    Optional<Frequencia> findByAlunoAndData(Aluno aluno, LocalDate data);
    
    /**
     * Busca todos os registros de uma data específica
     * @param data Data
     * @return Lista de registros
     */
    List<Frequencia> findByData(LocalDate data);
    
    /**
     * Busca registros de frequência entre duas datas
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de registros
     */
    List<Frequencia> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca registros de um aluno entre duas datas
     * @param aluno Aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de registros
     */
    List<Frequencia> findByAlunoAndDataBetween(Aluno aluno, LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca registros com presença confirmada
     * @param presenca true para presença, false para ausência
     * @return Lista de registros
     */
    List<Frequencia> findByPresenca(Boolean presenca);
    
    /**
     * Busca registros de presença de um aluno
     * @param aluno Aluno
     * @param presenca true para presença, false para ausência
     * @return Lista de registros
     */
    List<Frequencia> findByAlunoAndPresenca(Aluno aluno, Boolean presenca);
    
    /**
     * Busca registros de um aluno ordenados por data (mais recentes primeiro)
     * @param aluno Aluno
     * @return Lista de registros ordenados
     */
    List<Frequencia> findByAlunoOrderByDataDesc(Aluno aluno);
    
    /**
     * Conta o total de registros de frequência de um aluno
     * @param aluno Aluno
     * @return Número de registros
     */
    long countByAluno(Aluno aluno);
    
    /**
     * Conta quantas presenças um aluno tem
     * @param aluno Aluno
     * @param presenca true para contar presenças
     * @return Número de presenças
     */
    long countByAlunoAndPresenca(Aluno aluno, Boolean presenca);
    
    /**
     * Verifica se existe registro de frequência para um aluno em uma data
     * @param aluno Aluno
     * @param data Data
     * @return true se existir, false caso contrário
     */
    boolean existsByAlunoAndData(Aluno aluno, LocalDate data);
    
    /**
     * Calcula a taxa de presença de um aluno em um período
     * @param aluno Aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Número de presenças no período
     */
    @Query("SELECT COUNT(f) FROM Frequencia f WHERE f.aluno = :aluno AND f.presenca = true AND f.data BETWEEN :dataInicio AND :dataFim")
    long contarPresencasNoPeriodo(Aluno aluno, LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca todos os registros ordenados por data (mais recentes primeiro)
     * @return Lista de registros ordenados
     */
    List<Frequencia> findAllByOrderByDataDesc();
    
    /**
     * Busca alunos presentes em uma data específica
     * @param data Data
     * @param presenca true para buscar presenças
     * @return Lista de registros
     */
    List<Frequencia> findByDataAndPresenca(LocalDate data, Boolean presenca);
}
