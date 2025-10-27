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

import com.example.demo.dto.MatriculaRequestDTO;
import com.example.demo.dto.MatriculaResponseDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.MatriculaException;
import com.example.demo.service.AlunoService;
import com.example.demo.service.MatriculaService;
import com.example.demo.service.PlanoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {
    
    @Autowired
    private MatriculaService matriculaService;
    
    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private PlanoService planoService;
    
    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> criarMatricula(@Valid @RequestBody MatriculaRequestDTO dto) {
        Aluno aluno = alunoService.buscarPorId(dto.getIdAluno());
        Plano plano = planoService.buscarPlanoPorId(dto.getIdPlano());
        
        Matricula matricula = new Matricula(
            aluno,
            plano,
            dto.getDataInicio(),
            dto.getDataFim(),
            MatriculaStatus.ATIVA // Nova matrícula sempre começa ativa
        );
        
        Matricula saved = matriculaService.criarMatricula(matricula);
        return new ResponseEntity<>(new MatriculaResponseDTO(saved), HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<MatriculaResponseDTO>> listarMatriculas() {
        List<Matricula> matriculas = matriculaService.listarTodasMatriculas();
        List<MatriculaResponseDTO> response = matriculas.stream()
            .map(MatriculaResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> buscarPorId(@PathVariable Long id) {
        Matricula matricula = matriculaService.buscarMatriculaPorId(id);
        return ResponseEntity.ok(new MatriculaResponseDTO(matricula));
    }
    
    @GetMapping("/aluno/{idAluno}")
    public ResponseEntity<List<MatriculaResponseDTO>> buscarPorAluno(@PathVariable Long idAluno) {
        Aluno aluno = alunoService.buscarPorId(idAluno);
        List<Matricula> matriculas = matriculaService.listarMatriculasPorAluno(aluno);
        List<MatriculaResponseDTO> response = matriculas.stream()
            .map(MatriculaResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/plano/{idPlano}")
    public ResponseEntity<List<MatriculaResponseDTO>> buscarPorPlano(@PathVariable Long idPlano) {
        Plano plano = planoService.buscarPlanoPorId(idPlano);
        List<Matricula> matriculas = matriculaService.listarMatriculasPorPlano(plano);
        List<MatriculaResponseDTO> response = matriculas.stream()
            .map(MatriculaResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MatriculaResponseDTO>> buscarPorStatus(@PathVariable MatriculaStatus status) {
        List<Matricula> matriculas = matriculaService.listarMatriculasPorStatus(status);
        List<MatriculaResponseDTO> response = matriculas.stream()
            .map(MatriculaResponseDTO::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> atualizarMatricula(
            @PathVariable Long id, 
            @Valid @RequestBody MatriculaRequestDTO dto) {
        Aluno aluno = alunoService.buscarPorId(dto.getIdAluno());
        Plano plano = planoService.buscarPlanoPorId(dto.getIdPlano());
        
        Matricula matricula = new Matricula(
            aluno,
            plano,
            dto.getDataInicio(),
            dto.getDataFim(),
            dto.getStatus()
        );
        
        Matricula updated = matriculaService.atualizarMatricula(id, matricula);
        return ResponseEntity.ok(new MatriculaResponseDTO(updated));
    }
    
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<MatriculaResponseDTO> cancelarMatricula(@PathVariable Long id) {
        matriculaService.cancelarMatricula(id);
        Matricula matricula = matriculaService.buscarMatriculaPorId(id);
        return ResponseEntity.ok(new MatriculaResponseDTO(matricula));
    }
    
    @PutMapping("/{id}/ativar")
    public ResponseEntity<MatriculaResponseDTO> ativarMatricula(@PathVariable Long id) {
        matriculaService.ativarMatricula(id);
        Matricula matricula = matriculaService.buscarMatriculaPorId(id);
        return ResponseEntity.ok(new MatriculaResponseDTO(matricula));
    }
    
    @PutMapping("/{id}/inativar")
    public ResponseEntity<MatriculaResponseDTO> inativarMatricula(@PathVariable Long id) {
        matriculaService.inativarMatricula(id);
        Matricula matricula = matriculaService.buscarMatriculaPorId(id);
        return ResponseEntity.ok(new MatriculaResponseDTO(matricula));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMatricula(@PathVariable Long id) {
        matriculaService.deletarMatricula(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MatriculaException.MatriculaNotFoundException.class)
    public ResponseEntity<String> handleMatriculaNotFoundException(MatriculaException.MatriculaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({
        MatriculaException.MatriculaInvalidaException.class,
        MatriculaException.DataInvalidaException.class,
        MatriculaException.StatusInvalidoException.class,
        MatriculaException.PlanoInvalidoException.class
    })
    public ResponseEntity<String> handleMatriculaValidationException(MatriculaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}