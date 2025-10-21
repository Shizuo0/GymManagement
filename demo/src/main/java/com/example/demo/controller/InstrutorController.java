package com.example.demo.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.InstrutorDTO;
import com.example.demo.entity.Instrutor;
import com.example.demo.service.InstrutorService;

@RestController
@RequestMapping("/api/instrutores")
public class InstrutorController {

    @Autowired
    private InstrutorService instrutorService;

    @PostMapping
    public ResponseEntity<InstrutorDTO> cadastrarInstrutor(@RequestBody InstrutorDTO instrutorDTO) {
        Instrutor instrutor = new Instrutor(instrutorDTO.getNome(), instrutorDTO.getEspecialidade());
        instrutor = instrutorService.cadastrarInstrutor(instrutor);
        
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(instrutor.getIdInstrutor())
            .toUri();

        return ResponseEntity.created(location).body(convertToDTO(instrutor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstrutorDTO> buscarInstrutor(@PathVariable Long id) {
        Instrutor instrutor = instrutorService.buscarPorId(id);
        return ResponseEntity.ok(convertToDTO(instrutor));
    }

    @GetMapping
    public ResponseEntity<List<InstrutorDTO>> listarInstrutores() {
        List<InstrutorDTO> instrutores = instrutorService.listarTodos()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(instrutores);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstrutorDTO> atualizarInstrutor(@PathVariable Long id, @RequestBody InstrutorDTO instrutorDTO) {
        Instrutor instrutor = new Instrutor(instrutorDTO.getNome(), instrutorDTO.getEspecialidade());
        instrutor = instrutorService.atualizarInstrutor(id, instrutor);
        return ResponseEntity.ok(convertToDTO(instrutor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirInstrutor(@PathVariable Long id) {
        instrutorService.excluirInstrutor(id);
        return ResponseEntity.noContent().build();
    }

    private InstrutorDTO convertToDTO(Instrutor instrutor) {
        return new InstrutorDTO(
            instrutor.getIdInstrutor(),
            instrutor.getNome(),
            instrutor.getEspecialidade()
        );
    }
}