package com.example.demo.repository;

import com.example.demo.entity.Instrutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com a entidade Instrutor
 */
@Repository
public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {
    
    /**
     * Busca um instrutor pelo nome
     * @param nome Nome do instrutor
     * @return Optional contendo o instrutor se encontrado
     */
    Optional<Instrutor> findByNome(String nome);
    
    /**
     * Busca instrutores por especialidade
     * @param especialidade Especialidade do instrutor
     * @return Lista de instrutores com a especialidade especificada
     */
    List<Instrutor> findByEspecialidade(String especialidade);
    
    /**
     * Busca instrutores cujo nome contém a string fornecida (case insensitive)
     * @param nome Parte do nome do instrutor
     * @return Lista de instrutores encontrados
     */
    List<Instrutor> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Verifica se existe um instrutor com o nome especificado
     * @param nome Nome do instrutor
     * @return true se existir, false caso contrário
     */
    boolean existsByNome(String nome);
}
