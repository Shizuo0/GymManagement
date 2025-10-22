package com.example.demo.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.ItemTreinoRequestDTO;
import com.example.demo.dto.ItemTreinoResponseDTO;
import com.example.demo.entity.Exercicio;
import com.example.demo.entity.ItemTreino;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.service.ExercicioService;
import com.example.demo.service.ItemTreinoService;
import com.example.demo.service.PlanoTreinoService;

import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de Itens de Treino
 * Permite instrutores adicionarem e configurarem exercícios dentro dos planos de treino
 * com séries, repetições, carga e observações específicas
 */
@RestController
@RequestMapping("/api/itens-treino")
@Validated
public class ItemTreinoController {
    
    @Autowired
    private ItemTreinoService itemTreinoService;
    
    @Autowired
    private PlanoTreinoService planoTreinoService;
    
    @Autowired
    private ExercicioService exercicioService;
    
    /**
     * Adiciona um exercício a um plano de treino
     * @param requestDTO Dados do item de treino (exercício com configurações)
     * @return Item de treino criado
     */
    @PostMapping
    public ResponseEntity<ItemTreinoResponseDTO> adicionarExercicioAoPlano(
            @Valid @RequestBody ItemTreinoRequestDTO requestDTO) {
        
        // Buscar plano e exercício
        PlanoTreino planoTreino = planoTreinoService.buscarPorId(requestDTO.getPlanoTreinoId());
        Exercicio exercicio = exercicioService.buscarPorId(requestDTO.getExercicioId());
        
        // Criar item de treino
        ItemTreino itemTreino = new ItemTreino();
        itemTreino.setPlanoTreino(planoTreino);
        itemTreino.setExercicio(exercicio);
        itemTreino.setSeries(requestDTO.getSeries());
        itemTreino.setRepeticoes(requestDTO.getRepeticoes());
        itemTreino.setCarga(requestDTO.getCarga());
        itemTreino.setObservacoes(requestDTO.getObservacoes());
        
        itemTreino = itemTreinoService.adicionarExercicioAoPlano(itemTreino);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(itemTreino.getIdItemTreino())
            .toUri();
        
        return ResponseEntity.created(location).body(convertToDTO(itemTreino));
    }
    
    /**
     * Busca um item de treino por ID
     * @param id ID do item de treino
     * @return Item de treino encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> buscarItemTreino(@PathVariable Long id) {
        ItemTreino itemTreino = itemTreinoService.buscarPorId(id);
        return ResponseEntity.ok(convertToDTO(itemTreino));
    }
    
    /**
     * Atualiza as configurações de um exercício no plano de treino
     * (séries, repetições, carga, observações)
     * @param id ID do item de treino
     * @param requestDTO Novos dados do item
     * @return Item de treino atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemTreinoResponseDTO> atualizarItemTreino(
            @PathVariable Long id,
            @Valid @RequestBody ItemTreinoRequestDTO requestDTO) {
        
        // Buscar plano e exercício
        PlanoTreino planoTreino = planoTreinoService.buscarPorId(requestDTO.getPlanoTreinoId());
        Exercicio exercicio = exercicioService.buscarPorId(requestDTO.getExercicioId());
        
        // Criar objeto com novos dados
        ItemTreino itemTreino = new ItemTreino();
        itemTreino.setPlanoTreino(planoTreino);
        itemTreino.setExercicio(exercicio);
        itemTreino.setSeries(requestDTO.getSeries());
        itemTreino.setRepeticoes(requestDTO.getRepeticoes());
        itemTreino.setCarga(requestDTO.getCarga());
        itemTreino.setObservacoes(requestDTO.getObservacoes());
        
        itemTreino = itemTreinoService.atualizarItemTreino(id, itemTreino);
        
        return ResponseEntity.ok(convertToDTO(itemTreino));
    }
    
    /**
     * Remove um exercício de um plano de treino
     * @param id ID do item de treino
     * @return Resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerExercicioDoPlano(@PathVariable Long id) {
        itemTreinoService.removerExercicioDoPlano(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Lista todos os exercícios de um plano de treino específico
     * @param idPlanoTreino ID do plano de treino
     * @return Lista de exercícios do plano com suas configurações
     */
    @GetMapping("/plano/{idPlanoTreino}")
    public ResponseEntity<List<ItemTreinoResponseDTO>> listarExerciciosDoPlano(
            @PathVariable Long idPlanoTreino) {
        
        PlanoTreino planoTreino = planoTreinoService.buscarPorId(idPlanoTreino);
        List<ItemTreinoResponseDTO> itens = itemTreinoService.listarExerciciosDoPlano(planoTreino)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(itens);
    }
    
    /**
     * Converte entidade ItemTreino para DTO de resposta
     * @param itemTreino Entidade ItemTreino
     * @return DTO de resposta
     */
    private ItemTreinoResponseDTO convertToDTO(ItemTreino itemTreino) {
        String planoDescricao = itemTreino.getPlanoTreino().getDescricao() != null 
            ? itemTreino.getPlanoTreino().getDescricao() 
            : "Plano " + itemTreino.getPlanoTreino().getAluno().getNome();
            
        return new ItemTreinoResponseDTO(
            itemTreino.getIdItemTreino(),
            itemTreino.getPlanoTreino().getIdPlanoTreino(),
            planoDescricao,
            itemTreino.getExercicio().getIdExercicio(),
            itemTreino.getExercicio().getNome(),
            itemTreino.getExercicio().getGrupoMuscular(),
            itemTreino.getSeries(),
            itemTreino.getRepeticoes(),
            itemTreino.getCarga(),
            itemTreino.getObservacoes()
        );
    }
}
