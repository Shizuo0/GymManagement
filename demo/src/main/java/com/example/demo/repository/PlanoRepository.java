package com.example.demo.repository;

import com.example.demo.entity.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com a entidade Plano
 */
@Repository
public interface PlanoRepository extends JpaRepository<Plano, Long> {
    
    /**
     * Busca um plano pelo nome
     * @param nome Nome do plano
     * @return Optional contendo o plano se encontrado
     */
    Optional<Plano> findByNome(String nome);
    
    /**
     * Lista todos os planos ordenados por valor
     * @return Lista de planos ordenados
     */
    List<Plano> findAllByOrderByValorAsc();
    
    /**
     * Busca planos com duração específica
     * @param duracaoDias Duração em dias
     * @return Lista de planos com a duração especificada
     */
    List<Plano> findByDuracaoDias(Integer duracaoDias);
}
