package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Plano;

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
     * Busca planos com duração específica em meses
     * @param duracaoMeses Duração em meses
     * @return Lista de planos com a duração especificada
     */
    List<Plano> findByDuracaoMeses(Integer duracaoMeses);
    
    /**
     * Busca planos por status
     * @param status Status do plano (ATIVO/INATIVO)
     * @return Lista de planos com o status especificado
     */
    List<Plano> findByStatus(String status);
}
