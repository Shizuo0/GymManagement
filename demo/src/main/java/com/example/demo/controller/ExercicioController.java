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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.ExercicioRequestDTO;
import com.example.demo.dto.ExercicioResponseDTO;
import com.example.demo.entity.Exercicio;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.service.ExercicioService;
import com.example.demo.service.PlanoTreinoService;

import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de Exercícios
 * Permite criar e gerenciar o catálogo de exercícios da academia
 */
@RestController
@RequestMapping("/api/exercicios")
@Validated
public class ExercicioController {
    
    @Autowired
    private ExercicioService exercicioService;
    
    @Autowired
    private PlanoTreinoService planoTreinoService;
    
    /**
     * Cria um novo exercício
     * @param requestDTO Dados do exercício
     * @return Exercício criado
     */
    @PostMapping
    public ResponseEntity<ExercicioResponseDTO> criarExercicio(
            @Valid @RequestBody ExercicioRequestDTO requestDTO) {
        
        Exercicio exercicio = new Exercicio(
            requestDTO.getNome(),
            requestDTO.getGrupoMuscular()
        );
        
        exercicio = exercicioService.criarExercicio(exercicio);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(exercicio.getIdExercicio())
            .toUri();
        
        return ResponseEntity.created(location).body(convertToDTO(exercicio));
    }
    
    /**
     * Busca um exercício por ID
     * @param id ID do exercício
     * @return Exercício encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExercicioResponseDTO> buscarExercicio(@PathVariable Long id) {
        Exercicio exercicio = exercicioService.buscarPorId(id);
        return ResponseEntity.ok(convertToDTO(exercicio));
    }
    
    /**
     * Lista todos os exercícios cadastrados (ordenados por nome)
     * @return Lista de exercícios
     */
    @GetMapping
    public ResponseEntity<List<ExercicioResponseDTO>> listarTodosExercicios() {
        List<ExercicioResponseDTO> exercicios = exercicioService.listarTodos()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(exercicios);
    }
    
    /**
     * Atualiza um exercício existente
     * @param id ID do exercício
     * @param requestDTO Novos dados do exercício
     * @return Exercício atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExercicioResponseDTO> atualizarExercicio(
            @PathVariable Long id,
            @Valid @RequestBody ExercicioRequestDTO requestDTO) {
        
        Exercicio exercicio = new Exercicio(
            requestDTO.getNome(),
            requestDTO.getGrupoMuscular()
        );
        
        exercicio = exercicioService.atualizarExercicio(id, exercicio);
        
        return ResponseEntity.ok(convertToDTO(exercicio));
    }
    
    /**
     * Deleta um exercício
     * @param id ID do exercício
     * @return Resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExercicio(@PathVariable Long id) {
        exercicioService.deletarExercicio(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Busca exercícios por grupo muscular
     * @param grupoMuscular Nome do grupo muscular
     * @return Lista de exercícios do grupo muscular
     */
    @GetMapping("/grupo-muscular/{grupoMuscular}")
    public ResponseEntity<List<ExercicioResponseDTO>> buscarPorGrupoMuscular(
            @PathVariable String grupoMuscular) {
        
        List<ExercicioResponseDTO> exercicios = exercicioService.buscarPorGrupoMuscular(grupoMuscular)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(exercicios);
    }
    
    /**
     * Busca exercícios por nome (pesquisa parcial)
     * @param nome Nome ou parte do nome do exercício
     * @return Lista de exercícios encontrados
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ExercicioResponseDTO>> buscarPorNome(
            @RequestParam String nome) {
        
        List<ExercicioResponseDTO> exercicios = exercicioService.buscarPorNome(nome)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(exercicios);
    }
    
    /**
     * Lista exercícios que não estão incluídos em um plano de treino específico
     * Útil para adicionar novos exercícios a um plano
     * @param idPlanoTreino ID do plano de treino
     * @return Lista de exercícios disponíveis
     */
    @GetMapping("/disponiveis/{idPlanoTreino}")
    public ResponseEntity<List<ExercicioResponseDTO>> buscarExerciciosDisponiveis(
            @PathVariable Long idPlanoTreino) {
        
        PlanoTreino planoTreino = planoTreinoService.buscarPorId(idPlanoTreino);
        List<ExercicioResponseDTO> exercicios = exercicioService.buscarExerciciosNaoIncluidos(planoTreino)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(exercicios);
    }
    
    /**
     * Converte entidade Exercicio para DTO de resposta
     * @param exercicio Entidade Exercicio
     * @return DTO de resposta
     */
    private ExercicioResponseDTO convertToDTO(Exercicio exercicio) {
        return new ExercicioResponseDTO(
            exercicio.getIdExercicio(),
            exercicio.getNome(),
            exercicio.getGrupoMuscular()
        );
    }
}
