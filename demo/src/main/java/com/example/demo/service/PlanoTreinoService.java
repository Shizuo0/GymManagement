package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Instrutor;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.exception.PlanoTreinoException;
import com.example.demo.repository.PlanoTreinoRepository;

@Service
@Transactional
public class PlanoTreinoService {
    
    @Autowired
    private PlanoTreinoRepository planoTreinoRepository;
    
    public PlanoTreino criarPlanoTreino(PlanoTreino planoTreino) {
        validarPlanoTreino(planoTreino);
        
        if (planoTreino.getDataCriacao() == null) {
            planoTreino.setDataCriacao(LocalDate.now());
        }
        
        return planoTreinoRepository.save(planoTreino);
    }
    
    public PlanoTreino atualizarPlanoTreino(Long id, PlanoTreino planoTreino) {
        PlanoTreino planoExistente = buscarPorId(id);
        
        validarPlanoTreino(planoTreino);
        
        planoExistente.setAluno(planoTreino.getAluno());
        planoExistente.setInstrutor(planoTreino.getInstrutor());
        planoExistente.setDescricao(planoTreino.getDescricao());
        planoExistente.setDuracaoSemanas(planoTreino.getDuracaoSemanas());
        
        return planoTreinoRepository.save(planoExistente);
    }
    
    public void deletarPlanoTreino(Long id) {
        PlanoTreino plano = buscarPorId(id);
        planoTreinoRepository.delete(plano);
    }
    
    public PlanoTreino buscarPorId(Long id) {
        return planoTreinoRepository.findById(id)
            .orElseThrow(() -> new PlanoTreinoException.PlanoTreinoNotFoundException(
                "Plano de treino não encontrado com ID: " + id));
    }
    
    public List<PlanoTreino> listarTodos() {
        return planoTreinoRepository.findAll();
    }
    
    public List<PlanoTreino> listarPlanosDoAluno(Aluno aluno) {
        return planoTreinoRepository.findByAluno(aluno);
    }
    
    public List<PlanoTreino> listarPlanosPorInstrutor(Instrutor instrutor) {
        return planoTreinoRepository.findByInstrutor(instrutor);
    }
    
    public List<PlanoTreino> buscarPlanosRecentes(Aluno aluno, int limit) {
        return planoTreinoRepository.findMostRecentByAluno(aluno, limit);
    }
    
    private void validarPlanoTreino(PlanoTreino planoTreino) {
        if (planoTreino.getAluno() == null) {
            throw new PlanoTreinoException.AlunoInvalidoException("Aluno é obrigatório");
        }
        
        if (planoTreino.getInstrutor() == null) {
            throw new PlanoTreinoException.InstrutorInvalidoException("Instrutor é obrigatório");
        }
        
        if (planoTreino.getDuracaoSemanas() != null && planoTreino.getDuracaoSemanas() < 1) {
            throw new PlanoTreinoException.PlanoTreinoInvalidoException(
                "A duração deve ser de pelo menos 1 semana");
        }
        
        if (planoTreino.getDataCriacao() != null && 
            planoTreino.getDataCriacao().isAfter(LocalDate.now())) {
            throw new PlanoTreinoException.DataInvalidaException(
                "A data de criação não pode ser futura");
        }
    }
}