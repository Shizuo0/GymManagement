package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Exercicio;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.exception.ExercicioException;
import com.example.demo.repository.ExercicioRepository;
import com.example.demo.repository.ItemTreinoRepository;

@Service
@Transactional
public class ExercicioService {
    
    @Autowired
    private ExercicioRepository exercicioRepository;
    
    @Autowired
    private ItemTreinoRepository itemTreinoRepository;
    
    public Exercicio criarExercicio(Exercicio exercicio) {
        validarExercicio(exercicio);
        
        // Verificar se já existe exercício com o mesmo nome
        if (exercicioRepository.existsByNomeIgnoreCase(exercicio.getNome())) {
            throw new ExercicioException.DuplicateExercicioException(
                "Já existe um exercício com o nome: " + exercicio.getNome());
        }
        
        return exercicioRepository.save(exercicio);
    }
    
    public Exercicio atualizarExercicio(Long id, Exercicio exercicio) {
        Exercicio exercicioExistente = buscarPorId(id);
        
        // Verificar se o novo nome já existe em outro exercício
        if (!exercicioExistente.getNome().equalsIgnoreCase(exercicio.getNome()) &&
            exercicioRepository.existsByNomeIgnoreCase(exercicio.getNome())) {
            throw new ExercicioException.DuplicateExercicioException(
                "Já existe um exercício com o nome: " + exercicio.getNome());
        }
        
        validarExercicio(exercicio);
        
        exercicioExistente.setNome(exercicio.getNome());
        exercicioExistente.setGrupoMuscular(exercicio.getGrupoMuscular());
        exercicioExistente.setDescricao(exercicio.getDescricao());
        
        return exercicioRepository.save(exercicioExistente);
    }
    
    public void deletarExercicio(Long id) {
        Exercicio exercicio = buscarPorId(id);
        
        // Verificar se o exercício está sendo usado em algum plano de treino
        if (!itemTreinoRepository.findByExercicio(exercicio).isEmpty()) {
            throw new ExercicioException.ExercicioEmUsoException(
                "Não é possível deletar o exercício pois ele está sendo usado em planos de treino");
        }
        
        exercicioRepository.delete(exercicio);
    }
    
    public Exercicio buscarPorId(Long id) {
        return exercicioRepository.findById(id)
            .orElseThrow(() -> new ExercicioException.ExercicioNotFoundException(
                "Exercício não encontrado com ID: " + id));
    }
    
    public List<Exercicio> listarTodos() {
        return exercicioRepository.findAllByOrderByNomeAsc();
    }
    
    public List<Exercicio> buscarPorGrupoMuscular(String grupoMuscular) {
        return exercicioRepository.findByGrupoMuscularIgnoreCase(grupoMuscular);
    }
    
    public List<Exercicio> buscarPorNome(String nome) {
        return exercicioRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<Exercicio> buscarExerciciosNaoIncluidos(PlanoTreino planoTreino) {
        return exercicioRepository.findExerciciosNotInPlano(planoTreino);
    }
    
    private void validarExercicio(Exercicio exercicio) {
        if (exercicio.getNome() == null || exercicio.getNome().trim().isEmpty()) {
            throw new ExercicioException.ExercicioInvalidoException("Nome do exercício é obrigatório");
        }
        
        if (exercicio.getNome().length() < 3) {
            throw new ExercicioException.ExercicioInvalidoException(
                "Nome do exercício deve ter pelo menos 3 caracteres");
        }
        
        if (exercicio.getNome().length() > 100) {
            throw new ExercicioException.ExercicioInvalidoException(
                "Nome do exercício deve ter no máximo 100 caracteres");
        }
        
        if (exercicio.getGrupoMuscular() != null && exercicio.getGrupoMuscular().length() > 50) {
            throw new ExercicioException.ExercicioInvalidoException(
                "Grupo muscular deve ter no máximo 50 caracteres");
        }
        
        if (exercicio.getDescricao() != null && exercicio.getDescricao().length() > 500) {
            throw new ExercicioException.ExercicioInvalidoException(
                "Descrição deve ter no máximo 500 caracteres");
        }
    }
}