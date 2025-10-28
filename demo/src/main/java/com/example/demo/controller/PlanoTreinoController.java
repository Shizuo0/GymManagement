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

import com.example.demo.dto.PlanoTreinoRequestDTO;
import com.example.demo.dto.PlanoTreinoResponseDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Instrutor;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.service.AlunoService;
import com.example.demo.service.InstrutorService;
import com.example.demo.service.PlanoTreinoService;

import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de Planos de Treino
 * Permite instrutores criarem e gerenciarem rotinas de treino para alunos
 */
@RestController
@RequestMapping("/api/planos-treino")
@Validated
public class PlanoTreinoController {
    
    @Autowired
    private PlanoTreinoService planoTreinoService;
    
    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private InstrutorService instrutorService;
    
    /**
     * Cria um novo plano de treino
     * @param requestDTO Dados do plano de treino
     * @return Plano de treino criado
     */
    @PostMapping
    public ResponseEntity<PlanoTreinoResponseDTO> criarPlanoTreino(
            @Valid @RequestBody PlanoTreinoRequestDTO requestDTO) {
        
        // Buscar aluno e instrutor
        Aluno aluno = alunoService.buscarPorId(requestDTO.getIdAluno());
        Instrutor instrutor = instrutorService.buscarPorId(requestDTO.getIdInstrutor());
        
        // Criar plano de treino
        PlanoTreino planoTreino = new PlanoTreino();
        planoTreino.setAluno(aluno);
        planoTreino.setInstrutor(instrutor);
        planoTreino.setDescricao(requestDTO.getDescricao());
        planoTreino.setDuracaoSemanas(requestDTO.getDuracaoSemanas());
        planoTreino.setDataCriacao(requestDTO.getDataCriacao());
        
        planoTreino = planoTreinoService.criarPlanoTreino(planoTreino);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(planoTreino.getIdPlanoTreino())
            .toUri();
        
        return ResponseEntity.created(location).body(convertToDTO(planoTreino));
    }
    
    /**
     * Lista todos os planos de treino
     * @return Lista de todos os planos de treino
     */
    @GetMapping
    public ResponseEntity<List<PlanoTreinoResponseDTO>> listarTodosPlanos() {
        List<PlanoTreinoResponseDTO> planos = planoTreinoService.listarTodos()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(planos);
    }
    
    /**
     * Busca um plano de treino por ID
     * @param id ID do plano de treino
     * @return Plano de treino encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlanoTreinoResponseDTO> buscarPlanoTreino(@PathVariable Long id) {
        PlanoTreino planoTreino = planoTreinoService.buscarPorId(id);
        return ResponseEntity.ok(convertToDTO(planoTreino));
    }
    
    /**
     * Atualiza um plano de treino existente
     * @param id ID do plano de treino
     * @param requestDTO Novos dados do plano
     * @return Plano de treino atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlanoTreinoResponseDTO> atualizarPlanoTreino(
            @PathVariable Long id,
            @Valid @RequestBody PlanoTreinoRequestDTO requestDTO) {
        
        // Buscar aluno e instrutor
        Aluno aluno = alunoService.buscarPorId(requestDTO.getIdAluno());
        Instrutor instrutor = instrutorService.buscarPorId(requestDTO.getIdInstrutor());
        
        // Criar objeto com novos dados
        PlanoTreino planoTreino = new PlanoTreino();
        planoTreino.setAluno(aluno);
        planoTreino.setInstrutor(instrutor);
        planoTreino.setDescricao(requestDTO.getDescricao());
        planoTreino.setDuracaoSemanas(requestDTO.getDuracaoSemanas());
        
        planoTreino = planoTreinoService.atualizarPlanoTreino(id, planoTreino);
        
        return ResponseEntity.ok(convertToDTO(planoTreino));
    }
    
    /**
     * Deleta um plano de treino
     * @param id ID do plano de treino
     * @return Resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlanoTreino(@PathVariable Long id) {
        planoTreinoService.deletarPlanoTreino(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Lista todos os planos de treino de um aluno
     * @param idAluno ID do aluno
     * @return Lista de planos de treino
     */
    @GetMapping("/aluno/{idAluno}")
    public ResponseEntity<List<PlanoTreinoResponseDTO>> listarPlanosPorAluno(
            @PathVariable Long idAluno) {
        
        Aluno aluno = alunoService.buscarPorId(idAluno);
        List<PlanoTreinoResponseDTO> planos = planoTreinoService.listarPlanosDoAluno(aluno)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(planos);
    }
    
    /**
     * Lista todos os planos de treino criados por um instrutor
     * @param idInstrutor ID do instrutor
     * @return Lista de planos de treino
     */
    @GetMapping("/instrutor/{idInstrutor}")
    public ResponseEntity<List<PlanoTreinoResponseDTO>> listarPlanosPorInstrutor(
            @PathVariable Long idInstrutor) {
        
        Instrutor instrutor = instrutorService.buscarPorId(idInstrutor);
        List<PlanoTreinoResponseDTO> planos = planoTreinoService.listarPlanosPorInstrutor(instrutor)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(planos);
    }
    
    /**
     * Busca os planos de treino mais recentes de um aluno
     * @param idAluno ID do aluno
     * @param limit Número máximo de resultados
     * @return Lista de planos de treino mais recentes
     */
    @GetMapping("/aluno/{idAluno}/recentes")
    public ResponseEntity<List<PlanoTreinoResponseDTO>> buscarPlanosRecentes(
            @PathVariable Long idAluno,
            @RequestParam(defaultValue = "5") int limit) {
        
        Aluno aluno = alunoService.buscarPorId(idAluno);
        List<PlanoTreinoResponseDTO> planos = planoTreinoService.buscarPlanosRecentes(aluno, limit)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(planos);
    }
    
    /**
     * Converte entidade PlanoTreino para DTO de resposta
     * @param planoTreino Entidade PlanoTreino
     * @return DTO de resposta
     */
    private PlanoTreinoResponseDTO convertToDTO(PlanoTreino planoTreino) {
        Long alunoId = null;
        String alunoNome = "[Aluno Removido]";
        Long instrutorId = null;
        String instrutorNome = "[Instrutor Removido]";
        
        try {
            if (planoTreino.getAluno() != null) {
                alunoId = planoTreino.getAluno().getIdAluno();
                alunoNome = planoTreino.getAluno().getNome();
            }
        } catch (Exception e) {
            // Aluno foi removido, usar valores padrão
        }
        
        try {
            if (planoTreino.getInstrutor() != null) {
                instrutorId = planoTreino.getInstrutor().getIdInstrutor();
                instrutorNome = planoTreino.getInstrutor().getNome();
            }
        } catch (Exception e) {
            // Instrutor foi removido, usar valores padrão
        }
        
        return new PlanoTreinoResponseDTO(
            planoTreino.getIdPlanoTreino(),
            alunoId,
            alunoNome,
            instrutorId,
            instrutorNome,
            planoTreino.getDataCriacao(),
            planoTreino.getDescricao(),
            planoTreino.getDuracaoSemanas()
        );
    }
}
