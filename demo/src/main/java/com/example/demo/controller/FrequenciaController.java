package com.example.demo.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.FrequenciaRequestDTO;
import com.example.demo.dto.FrequenciaResponseDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Frequencia;
import com.example.demo.service.AlunoService;
import com.example.demo.service.FrequenciaService;

import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de Frequência
 */
@RestController
@RequestMapping("/api/frequencias")
public class FrequenciaController {
    
    @Autowired
    private FrequenciaService frequenciaService;
    
    @Autowired
    private AlunoService alunoService;
    
    /**
     * Registra uma nova presença
     * @param dto Dados da frequência
     * @return Frequência registrada
     */
    @PostMapping
    public ResponseEntity<FrequenciaResponseDTO> registrarPresenca(@Valid @RequestBody FrequenciaRequestDTO dto) {
        Aluno aluno = alunoService.buscarPorId(dto.getIdAluno());
        
        Frequencia frequencia = new Frequencia(
            aluno,
            dto.getData(),
            dto.getPresenca()
        );
        
        Frequencia saved = frequenciaService.registrarPresenca(frequencia);
        return new ResponseEntity<>(convertToResponseDTO(saved), HttpStatus.CREATED);
    }
    
    /**
     * Lista todas as frequências
     * @return Lista de frequências
     */
    @GetMapping
    public ResponseEntity<List<FrequenciaResponseDTO>> listarFrequencias() {
        List<Frequencia> frequencias = frequenciaService.listarTodos();
        List<FrequenciaResponseDTO> response = frequencias.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca uma frequência por ID
     * @param id ID da frequência
     * @return Frequência encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<FrequenciaResponseDTO> buscarPorId(@PathVariable Long id) {
        Frequencia frequencia = frequenciaService.buscarPorId(id);
        return ResponseEntity.ok(convertToResponseDTO(frequencia));
    }
    
    /**
     * Lista frequências de um aluno específico
     * @param idAluno ID do aluno
     * @return Lista de frequências
     */
    @GetMapping("/aluno/{idAluno}")
    public ResponseEntity<List<FrequenciaResponseDTO>> listarPorAluno(@PathVariable Long idAluno) {
        Aluno aluno = alunoService.buscarPorId(idAluno);
        List<Frequencia> frequencias = frequenciaService.listarPorAluno(aluno);
        List<FrequenciaResponseDTO> response = frequencias.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca frequência de um aluno em uma data específica
     * @param idAluno ID do aluno
     * @param data Data
     * @return Frequência encontrada
     */
    @GetMapping("/aluno/{idAluno}/data/{data}")
    public ResponseEntity<FrequenciaResponseDTO> buscarPorAlunoEData(
            @PathVariable Long idAluno,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        Aluno aluno = alunoService.buscarPorId(idAluno);
        Frequencia frequencia = frequenciaService.buscarPorAlunoEData(aluno, data);
        return ResponseEntity.ok(convertToResponseDTO(frequencia));
    }
    
    /**
     * Lista frequências de uma data específica
     * @param data Data
     * @return Lista de frequências
     */
    @GetMapping("/data/{data}")
    public ResponseEntity<List<FrequenciaResponseDTO>> listarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<Frequencia> frequencias = frequenciaService.listarPorData(data);
        List<FrequenciaResponseDTO> response = frequencias.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lista alunos presentes em uma data específica
     * @param data Data
     * @return Lista de alunos presentes
     */
    @GetMapping("/presencas/data/{data}")
    public ResponseEntity<List<FrequenciaResponseDTO>> listarPresencasPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<Frequencia> frequencias = frequenciaService.listarPresencasPorData(data);
        List<FrequenciaResponseDTO> response = frequencias.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca frequências em um período
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de frequências
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<FrequenciaResponseDTO>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<Frequencia> frequencias = frequenciaService.listarPorPeriodo(dataInicio, dataFim);
        List<FrequenciaResponseDTO> response = frequencias.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca frequências de um aluno em um período
     * @param idAluno ID do aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de frequências
     */
    @GetMapping("/aluno/{idAluno}/periodo")
    public ResponseEntity<List<FrequenciaResponseDTO>> buscarPorAlunoEPeriodo(
            @PathVariable Long idAluno,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Aluno aluno = alunoService.buscarPorId(idAluno);
        List<Frequencia> frequencias = frequenciaService.listarPorAlunoEPeriodo(aluno, dataInicio, dataFim);
        List<FrequenciaResponseDTO> response = frequencias.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Conta total de presenças de um aluno
     * @param idAluno ID do aluno
     * @return Número de presenças
     */
    @GetMapping("/aluno/{idAluno}/total-presencas")
    public ResponseEntity<Long> contarPresencas(@PathVariable Long idAluno) {
        Aluno aluno = alunoService.buscarPorId(idAluno);
        long total = frequenciaService.contarPresencas(aluno);
        return ResponseEntity.ok(total);
    }
    
    /**
     * Calcula taxa de presença de um aluno em um período
     * @param idAluno ID do aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Taxa de presença (0-100%)
     */
    @GetMapping("/aluno/{idAluno}/taxa-presenca")
    public ResponseEntity<Map<String, Object>> calcularTaxaPresenca(
            @PathVariable Long idAluno,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Aluno aluno = alunoService.buscarPorId(idAluno);
        double taxa = frequenciaService.calcularTaxaPresenca(aluno, dataInicio, dataFim);
        
        Map<String, Object> response = new HashMap<>();
        response.put("idAluno", idAluno);
        response.put("nomeAluno", aluno.getNome());
        response.put("dataInicio", dataInicio);
        response.put("dataFim", dataFim);
        response.put("taxaPresenca", taxa);
        response.put("taxaFormatada", String.format("%.2f%%", taxa));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Atualiza um registro de frequência
     * @param id ID da frequência
     * @param dto Novos dados da frequência
     * @return Frequência atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<FrequenciaResponseDTO> atualizarFrequencia(
            @PathVariable Long id,
            @Valid @RequestBody FrequenciaRequestDTO dto) {
        Aluno aluno = alunoService.buscarPorId(dto.getIdAluno());
        
        Frequencia frequencia = new Frequencia(
            aluno,
            dto.getData(),
            dto.getPresenca()
        );
        
        Frequencia updated = frequenciaService.atualizarFrequencia(id, frequencia);
        return ResponseEntity.ok(convertToResponseDTO(updated));
    }
    
    /**
     * Deleta um registro de frequência
     * @param id ID da frequência
     * @return Resposta vazia
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFrequencia(@PathVariable Long id) {
        frequenciaService.deletarFrequencia(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Converte Frequencia para FrequenciaResponseDTO
     * @param frequencia Frequencia
     * @return DTO de resposta
     */
    private FrequenciaResponseDTO convertToResponseDTO(Frequencia frequencia) {
        return new FrequenciaResponseDTO(
            frequencia.getIdFrequencia(),
            frequencia.getAluno().getIdAluno(),
            frequencia.getAluno().getNome(),
            frequencia.getAluno().getCpf(),
            frequencia.getData(),
            frequencia.getPresenca()
        );
    }
}
