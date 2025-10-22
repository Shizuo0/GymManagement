package com.example.demo.controller;

import com.example.demo.dto.BuscaGlobalResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.AlunoResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.InstrutorResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.PlanoResultadoDTO;
import com.example.demo.service.BuscaGlobalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para endpoints de busca global
 * Permite buscar em múltiplas entidades simultaneamente ou individualmente
 */
@RestController
@RequestMapping("/api/busca")
public class BuscaGlobalController {

    private final BuscaGlobalService buscaGlobalService;

    public BuscaGlobalController(BuscaGlobalService buscaGlobalService) {
        this.buscaGlobalService = buscaGlobalService;
    }

    /**
     * Busca global em todas as entidades (alunos, instrutores e planos)
     * 
     * @param termo Termo de busca (mínimo 2 caracteres)
     * @return Resultados agregados de todas as entidades
     * 
     * Exemplo: GET /api/busca?termo=maria
     */
    @GetMapping
    public ResponseEntity<BuscaGlobalResultadoDTO> buscarTudo(@RequestParam String termo) {
        BuscaGlobalResultadoDTO resultado = buscaGlobalService.buscarTudo(termo);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Busca apenas alunos
     * 
     * @param termo Termo de busca (nome ou CPF)
     * @param apenasAtivos Filtrar apenas alunos com matrícula ativa (opcional)
     * @return Lista de alunos encontrados
     * 
     * Exemplos: 
     * - GET /api/busca/alunos?termo=maria
     * - GET /api/busca/alunos?termo=123&apenasAtivos=true
     */
    @GetMapping("/alunos")
    public ResponseEntity<List<AlunoResultadoDTO>> buscarAlunos(
            @RequestParam String termo,
            @RequestParam(required = false) Boolean apenasAtivos) {
        
        List<AlunoResultadoDTO> alunos = buscaGlobalService.buscarApenasAlunos(termo, apenasAtivos);
        return ResponseEntity.ok(alunos);
    }

    /**
     * Busca apenas instrutores
     * 
     * @param termo Termo de busca (nome ou especialidade)
     * @param especialidade Filtrar por especialidade específica (opcional)
     * @return Lista de instrutores encontrados
     * 
     * Exemplos:
     * - GET /api/busca/instrutores?termo=joão
     * - GET /api/busca/instrutores?termo=personal&especialidade=musculacao
     */
    @GetMapping("/instrutores")
    public ResponseEntity<List<InstrutorResultadoDTO>> buscarInstrutores(
            @RequestParam String termo,
            @RequestParam(required = false) String especialidade) {
        
        List<InstrutorResultadoDTO> instrutores = buscaGlobalService.buscarApenasInstrutores(termo, especialidade);
        return ResponseEntity.ok(instrutores);
    }

    /**
     * Busca apenas planos
     * 
     * @param termo Termo de busca (nome ou descrição)
     * @param apenasAtivos Filtrar apenas planos ativos (opcional)
     * @return Lista de planos encontrados
     * 
     * Exemplos:
     * - GET /api/busca/planos?termo=mensal
     * - GET /api/busca/planos?termo=premium&apenasAtivos=true
     */
    @GetMapping("/planos")
    public ResponseEntity<List<PlanoResultadoDTO>> buscarPlanos(
            @RequestParam String termo,
            @RequestParam(required = false) Boolean apenasAtivos) {
        
        List<PlanoResultadoDTO> planos = buscaGlobalService.buscarApenasPlanos(termo, apenasAtivos);
        return ResponseEntity.ok(planos);
    }

    /**
     * Busca alunos com matrícula ativa
     * Atalho para busca de alunos ativos
     * 
     * @param termo Termo de busca
     * @return Lista de alunos com matrícula ativa
     * 
     * Exemplo: GET /api/busca/alunos/ativos?termo=maria
     */
    @GetMapping("/alunos/ativos")
    public ResponseEntity<List<AlunoResultadoDTO>> buscarAlunosAtivos(@RequestParam String termo) {
        List<AlunoResultadoDTO> alunos = buscaGlobalService.buscarApenasAlunos(termo, true);
        return ResponseEntity.ok(alunos);
    }

    /**
     * Busca planos ativos
     * Atalho para busca de planos ativos
     * 
     * @param termo Termo de busca
     * @return Lista de planos ativos
     * 
     * Exemplo: GET /api/busca/planos/ativos?termo=premium
     */
    @GetMapping("/planos/ativos")
    public ResponseEntity<List<PlanoResultadoDTO>> buscarPlanosAtivos(@RequestParam String termo) {
        List<PlanoResultadoDTO> planos = buscaGlobalService.buscarApenasPlanos(termo, true);
        return ResponseEntity.ok(planos);
    }
}
