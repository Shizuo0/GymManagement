package com.example.demo.repository;

import com.example.demo.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
