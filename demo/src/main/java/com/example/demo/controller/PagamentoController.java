package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
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

import com.example.demo.dto.PagamentoRequestDTO;
import com.example.demo.dto.PagamentoResponseDTO;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Pagamento;
import com.example.demo.service.MatriculaService;
import com.example.demo.service.PagamentoService;

import jakarta.validation.Valid;

/**
 * Controller REST para gerenciamento de Pagamentos
 */
@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {
    
    @Autowired
    private PagamentoService pagamentoService;
    
    @Autowired
    private MatriculaService matriculaService;
    
    /**
     * Registra um novo pagamento
     * @param dto Dados do pagamento
     * @return Pagamento registrado
     */
    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> registrarPagamento(@Valid @RequestBody PagamentoRequestDTO dto) {
        Matricula matricula = matriculaService.buscarMatriculaPorId(dto.getIdMatricula());
        
        Pagamento pagamento = new Pagamento(
            matricula,
            dto.getDataPagamento(),
            dto.getValorPago(),
            dto.getFormaPagamento()
        );
        
        Pagamento saved = pagamentoService.registrarPagamento(pagamento);
        return new ResponseEntity<>(convertToResponseDTO(saved), HttpStatus.CREATED);
    }
    
    /**
     * Lista todos os pagamentos
     * @return Lista de pagamentos
     */
    @GetMapping
    public ResponseEntity<List<PagamentoResponseDTO>> listarPagamentos() {
        List<Pagamento> pagamentos = pagamentoService.listarTodos();
        List<PagamentoResponseDTO> response = pagamentos.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca um pagamento por ID
     * @param id ID do pagamento
     * @return Pagamento encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        Pagamento pagamento = pagamentoService.buscarPorId(id);
        return ResponseEntity.ok(convertToResponseDTO(pagamento));
    }
    
    /**
     * Lista pagamentos de uma matrícula específica
     * @param idMatricula ID da matrícula
     * @return Lista de pagamentos
     */
    @GetMapping("/matricula/{idMatricula}")
    public ResponseEntity<List<PagamentoResponseDTO>> listarPorMatricula(@PathVariable Long idMatricula) {
        Matricula matricula = matriculaService.buscarMatriculaPorId(idMatricula);
        List<Pagamento> pagamentos = pagamentoService.listarPagamentosPorMatricula(matricula);
        List<PagamentoResponseDTO> response = pagamentos.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca pagamentos em um período
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de pagamentos
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<PagamentoResponseDTO>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<Pagamento> pagamentos = pagamentoService.buscarPorPeriodo(dataInicio, dataFim);
        List<PagamentoResponseDTO> response = pagamentos.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Busca pagamentos por forma de pagamento
     * @param formaPagamento Forma de pagamento (ex: DINHEIRO, CARTAO, PIX)
     * @return Lista de pagamentos
     */
    @GetMapping("/forma-pagamento/{formaPagamento}")
    public ResponseEntity<List<PagamentoResponseDTO>> buscarPorFormaPagamento(@PathVariable String formaPagamento) {
        List<Pagamento> pagamentos = pagamentoService.buscarPorFormaPagamento(formaPagamento);
        List<PagamentoResponseDTO> response = pagamentos.stream()
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Calcula o total pago em uma matrícula
     * @param idMatricula ID da matrícula
     * @return Total pago
     */
    @GetMapping("/matricula/{idMatricula}/total")
    public ResponseEntity<String> calcularTotalPago(@PathVariable Long idMatricula) {
        Matricula matricula = matriculaService.buscarMatriculaPorId(idMatricula);
        return ResponseEntity.ok(pagamentoService.calcularTotalPago(matricula).toString());
    }
    
    /**
     * Verifica se uma matrícula está em dia com os pagamentos
     * @param idMatricula ID da matrícula
     * @return true se está em dia, false caso contrário
     */
    @GetMapping("/matricula/{idMatricula}/em-dia")
    public ResponseEntity<Boolean> verificarPagamentosEmDia(@PathVariable Long idMatricula) {
        Matricula matricula = matriculaService.buscarMatriculaPorId(idMatricula);
        boolean emDia = pagamentoService.verificarPagamentosEmDia(matricula);
        return ResponseEntity.ok(emDia);
    }
    
    /**
     * Atualiza um pagamento
     * @param id ID do pagamento
     * @param dto Novos dados do pagamento
     * @return Pagamento atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> atualizarPagamento(
            @PathVariable Long id,
            @Valid @RequestBody PagamentoRequestDTO dto) {
        Matricula matricula = matriculaService.buscarMatriculaPorId(dto.getIdMatricula());
        
        Pagamento pagamento = new Pagamento(
            matricula,
            dto.getDataPagamento(),
            dto.getValorPago(),
            dto.getFormaPagamento()
        );
        
        Pagamento updated = pagamentoService.atualizarPagamento(id, pagamento);
        return ResponseEntity.ok(convertToResponseDTO(updated));
    }
    
    /**
     * Deleta um pagamento
     * @param id ID do pagamento
     * @return Resposta vazia
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPagamento(@PathVariable Long id) {
        pagamentoService.deletarPagamento(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Converte Pagamento para PagamentoResponseDTO
     * @param pagamento Pagamento
     * @return DTO de resposta
     */
    private PagamentoResponseDTO convertToResponseDTO(Pagamento pagamento) {
        return new PagamentoResponseDTO(
            pagamento.getIdPagamento(),
            pagamento.getMatricula().getIdMatricula(),
            pagamento.getMatricula().getAluno().getNome(),
            pagamento.getMatricula().getPlano().getNome(),
            pagamento.getDataPagamento(),
            pagamento.getValorPago(),
            pagamento.getFormaPagamento()
        );
    }
}
