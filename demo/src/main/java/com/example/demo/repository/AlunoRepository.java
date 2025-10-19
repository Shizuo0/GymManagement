package com.example.demo.repository;

import com.example.demo.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações com a entidade Aluno
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
    /**
     * Busca um aluno pelo CPF
     * @param cpf CPF do aluno
     * @return Optional contendo o aluno se encontrado
     */
    Optional<Aluno> findByCpf(String cpf);
    
    /**
     * Verifica se existe um aluno com o CPF informado
     * @param cpf CPF do aluno
     * @return true se existir, false caso contrário
     */
    boolean existsByCpf(String cpf);
    
    /**
     * Busca alunos cujo nome contém a string fornecida (case insensitive)
     * @param nome Parte do nome do aluno
     * @return Lista de alunos encontrados
     */
    List<Aluno> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca um aluno pelo nome exato
     * @param nome Nome do aluno
     * @return Optional contendo o aluno se encontrado
     */
    Optional<Aluno> findByNome(String nome);
    
    /**
     * Lista todos os alunos ordenados por nome
     * @return Lista de alunos ordenados
     */
    List<Aluno> findAllByOrderByNomeAsc();
}
