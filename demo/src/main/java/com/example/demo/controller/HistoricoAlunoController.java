package com.example.demo.controller;

import com.example.demo.dto.HistoricoAlunoDTO;
import com.example.demo.service.HistoricoAlunoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller para endpoints de histórico de alunos
 * Fornece acesso ao histórico completo e filtrado por período
 */
@RestController
@RequestMapping("/api/historico/aluno")
public class HistoricoAlunoController {

    private final HistoricoAlunoService historicoAlunoService;

    public HistoricoAlunoController(HistoricoAlunoService historicoAlunoService) {
        this.historicoAlunoService = historicoAlunoService;
    }

    /**
     * Busca histórico completo do aluno
     * 
     * @param id ID do aluno
     * @return Histórico completo com matrículas, treinos, avaliações, frequência e pagamentos
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistoricoAlunoDTO> buscarHistoricoCompleto(@PathVariable Long id) {
        HistoricoAlunoDTO historico = historicoAlunoService.buscarHistoricoCompleto(id);
        return ResponseEntity.ok(historico);
    }

    /**
     * Busca histórico do aluno filtrado por período
     * 
     * @param id ID do aluno
     * @param inicio Data de início do período (formato: yyyy-MM-dd)
     * @param fim Data de fim do período (formato: yyyy-MM-dd)
     * @return Histórico filtrado pelo período especificado
     */
    @GetMapping("/{id}/periodo")
    public ResponseEntity<HistoricoAlunoDTO> buscarHistoricoPorPeriodo(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        
        HistoricoAlunoDTO historico = historicoAlunoService.buscarHistoricoPorPeriodo(id, inicio, fim);
        return ResponseEntity.ok(historico);
    }

    /**
     * Busca histórico do último mês
     * 
     * @param id ID do aluno
     * @return Histórico do último mês
     */
    @GetMapping("/{id}/ultimo-mes")
    public ResponseEntity<HistoricoAlunoDTO> buscarHistoricoUltimoMes(@PathVariable Long id) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusMonths(1);
        
        HistoricoAlunoDTO historico = historicoAlunoService.buscarHistoricoPorPeriodo(id, inicio, fim);
        return ResponseEntity.ok(historico);
    }

    /**
     * Busca histórico dos últimos 3 meses
     * 
     * @param id ID do aluno
     * @return Histórico dos últimos 3 meses
     */
    @GetMapping("/{id}/ultimos-3-meses")
    public ResponseEntity<HistoricoAlunoDTO> buscarHistoricoUltimos3Meses(@PathVariable Long id) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusMonths(3);
        
        HistoricoAlunoDTO historico = historicoAlunoService.buscarHistoricoPorPeriodo(id, inicio, fim);
        return ResponseEntity.ok(historico);
    }

    /**
     * Busca histórico do ano atual
     * 
     * @param id ID do aluno
     * @return Histórico do ano atual
     */
    @GetMapping("/{id}/ano-atual")
    public ResponseEntity<HistoricoAlunoDTO> buscarHistoricoAnoAtual(@PathVariable Long id) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = LocalDate.of(fim.getYear(), 1, 1);
        
        HistoricoAlunoDTO historico = historicoAlunoService.buscarHistoricoPorPeriodo(id, inicio, fim);
        return ResponseEntity.ok(historico);
    }
}
