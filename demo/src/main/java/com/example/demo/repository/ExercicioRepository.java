package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Exercicio;
import com.example.demo.entity.PlanoTreino;

/**
 * Repository para operações com a entidade Exercicio
 */
@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {
    
    /**
     * Busca exercício por nome exato
     * @param nome Nome do exercício
     * @return Optional com o exercício se encontrado
     */
    Optional<Exercicio> findByNome(String nome);
    
    /**
     * Busca exercícios por grupo muscular
     * @param grupoMuscular Grupo muscular (ex: "Peito", "Costas", "Pernas")
     * @return Lista de exercícios do grupo
     */
    List<Exercicio> findByGrupoMuscular(String grupoMuscular);
    
    /**
     * Busca exercícios por nome parcial (case insensitive)
     * @param nome Nome parcial para busca
     * @return Lista de exercícios encontrados
     */
    List<Exercicio> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca exercícios por grupo muscular (case insensitive)
     * @param grupoMuscular Grupo muscular
     * @return Lista de exercícios
     */
    List<Exercicio> findByGrupoMuscularIgnoreCase(String grupoMuscular);
    
    /**
     * Lista todos os exercícios ordenados por nome
     * @return Lista de exercícios ordenados
     */
    List<Exercicio> findAllByOrderByNomeAsc();
    
    /**
     * Verifica se existe um exercício com o nome especificado (ignorando case)
     * @param nome Nome do exercício
     * @return true se existir, false caso contrário
     */
    boolean existsByNomeIgnoreCase(String nome);
    
    /**
     * Busca exercícios que não estão em um plano específico
     * @param planoTreino Plano de treino
     * @return Lista de exercícios não incluídos no plano
     */
    @Query("SELECT e FROM Exercicio e WHERE e NOT IN " +
           "(SELECT i.exercicio FROM ItemTreino i WHERE i.planoTreino = :planoTreino)")
    List<Exercicio> findExerciciosNotInPlano(PlanoTreino planoTreino);
    
    /**
     * Lista exercícios de um grupo ordenados por nome
     * @param grupoMuscular Grupo muscular
     * @return Lista de exercícios ordenados
     */
    List<Exercicio> findByGrupoMuscularOrderByNomeAsc(String grupoMuscular);
    
    /**
     * Verifica se existe exercício com o nome
     * @param nome Nome do exercício
     * @return true se existir, false caso contrário
     */
    boolean existsByNome(String nome);
    
    /**
     * Conta exercícios de um grupo muscular
     * @param grupoMuscular Grupo muscular
     * @return Número de exercícios
     */
    long countByGrupoMuscular(String grupoMuscular);
    
    /**
     * Busca exercícios sem grupo muscular definido
     * @return Lista de exercícios sem grupo
     */
    List<Exercicio> findByGrupoMuscularIsNull();
    
    /**
     * Busca exercícios com grupo muscular definido
     * @return Lista de exercícios com grupo
     */
    List<Exercicio> findByGrupoMuscularIsNotNull();
}
