package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.PlanoRequestDTO;
import com.example.demo.dto.PlanoResponseDTO;
import com.example.demo.entity.Plano;
import com.example.demo.exception.PlanoException;
import com.example.demo.service.PlanoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planos")
public class PlanoController {
    
    @Autowired
    private PlanoService planoService;
    
    @PostMapping
    public ResponseEntity<PlanoResponseDTO> criarPlano(@Valid @RequestBody PlanoRequestDTO dto) {
        Plano plano = new Plano(dto.getNome(), dto.getDescricao(), dto.getValor(), dto.getDuracaoMeses());
        Plano salvo = planoService.criarPlano(plano);
        return new ResponseEntity<>(new PlanoResponseDTO(salvo), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<PlanoResponseDTO>> listarPlanos() {
        List<Plano> planos = planoService.listarTodosPlanos();
        List<PlanoResponseDTO> response = planos.stream().map(PlanoResponseDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> buscarPorId(@PathVariable Long id) {
        Plano plano = planoService.buscarPlanoPorId(id);
        return ResponseEntity.ok(new PlanoResponseDTO(plano));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PlanoResponseDTO> atualizarPlano(@PathVariable Long id, @Valid @RequestBody PlanoRequestDTO dto) {
        Plano plano = new Plano(dto.getNome(), dto.getDescricao(), dto.getValor(), dto.getDuracaoMeses());
        Plano atualizado = planoService.atualizarPlano(id, plano);
        return ResponseEntity.ok(new PlanoResponseDTO(atualizado));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPlano(@PathVariable Long id) {
        planoService.deletarPlano(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<PlanoResponseDTO> ativarPlano(@PathVariable Long id) {
        planoService.ativarPlano(id);
        Plano plano = planoService.buscarPlanoPorId(id);
        return ResponseEntity.ok(new PlanoResponseDTO(plano));
    }

    @PutMapping("/{id}/inativar")
    public ResponseEntity<PlanoResponseDTO> inativarPlano(@PathVariable Long id) {
        planoService.inativarPlano(id);
        Plano plano = planoService.buscarPlanoPorId(id);
        return ResponseEntity.ok(new PlanoResponseDTO(plano));
    }
    
    @ExceptionHandler(PlanoException.class)
    public ResponseEntity<String> handlePlanoException(PlanoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
