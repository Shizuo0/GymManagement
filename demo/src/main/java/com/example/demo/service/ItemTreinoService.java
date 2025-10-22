package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.ItemTreino;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.exception.ItemTreinoException;
import com.example.demo.repository.ItemTreinoRepository;

@Service
@Transactional
public class ItemTreinoService {
    
    @Autowired
    private ItemTreinoRepository itemTreinoRepository;
    
    public ItemTreino adicionarExercicioAoPlano(ItemTreino itemTreino) {
        validarItemTreino(itemTreino);
        
        // Verificar se o exercício já existe no plano
        if (itemTreinoRepository.existsByPlanoTreinoAndExercicio(
                itemTreino.getPlanoTreino(), itemTreino.getExercicio())) {
            throw new ItemTreinoException.DuplicateExerciseException(
                "Este exercício já está incluído no plano");
        }
        
        return itemTreinoRepository.save(itemTreino);
    }
    
    public ItemTreino atualizarItemTreino(Long id, ItemTreino itemTreino) {
        ItemTreino itemExistente = buscarPorId(id);
        
        validarItemTreino(itemTreino);
        
        // Se estiver mudando o exercício, verificar duplicidade
        if (!itemExistente.getExercicio().equals(itemTreino.getExercicio()) &&
            itemTreinoRepository.existsByPlanoTreinoAndExercicio(
                itemTreino.getPlanoTreino(), itemTreino.getExercicio())) {
            throw new ItemTreinoException.DuplicateExerciseException(
                "Este exercício já está incluído no plano");
        }
        
        itemExistente.setExercicio(itemTreino.getExercicio());
        itemExistente.setSeries(itemTreino.getSeries());
        itemExistente.setRepeticoes(itemTreino.getRepeticoes());
        itemExistente.setCarga(itemTreino.getCarga());
        itemExistente.setObservacoes(itemTreino.getObservacoes());
        
        return itemTreinoRepository.save(itemExistente);
    }
    
    public void removerExercicioDoPlano(Long id) {
        ItemTreino item = buscarPorId(id);
        itemTreinoRepository.delete(item);
    }
    
    public ItemTreino buscarPorId(Long id) {
        return itemTreinoRepository.findById(id)
            .orElseThrow(() -> new ItemTreinoException.ItemTreinoNotFoundException(
                "Item de treino não encontrado com ID: " + id));
    }
    
    public List<ItemTreino> listarExerciciosDoPlano(PlanoTreino planoTreino) {
        return itemTreinoRepository.findByPlanoTreino(planoTreino);
    }
    
    private void validarItemTreino(ItemTreino itemTreino) {
        if (itemTreino.getPlanoTreino() == null) {
            throw new ItemTreinoException.ItemTreinoNotFoundException("Plano de treino é obrigatório");
        }
        
        if (itemTreino.getExercicio() == null) {
            throw new ItemTreinoException.ItemTreinoNotFoundException("Exercício é obrigatório");
        }
        
        if (itemTreino.getSeries() == null || itemTreino.getSeries() < 1) {
            throw new ItemTreinoException.InvalidSeriesException(
                "Número de séries deve ser maior que zero");
        }
        
        if (itemTreino.getRepeticoes() == null || itemTreino.getRepeticoes() < 1) {
            throw new ItemTreinoException.InvalidRepeticoesException(
                "Número de repetições deve ser maior que zero");
        }
        
        if (itemTreino.getCarga() != null && itemTreino.getCarga().compareTo(BigDecimal.ZERO) < 0) {
            throw new ItemTreinoException.InvalidCargaException(
                "A carga não pode ser negativa");
        }
    }
}