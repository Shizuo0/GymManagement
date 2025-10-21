package com.example.demo.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Instrutor;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.ValidacaoException;
import com.example.demo.repository.InstrutorRepository;

@Service
public class InstrutorService {
    
    private static final Set<String> ESPECIALIDADES_VALIDAS = Set.of(
        "Musculação", "Personal Trainer", "CrossFit", "Pilates",
        "Yoga", "Funcional", "Natação", "Lutas", "Dança"
    );

    @Autowired
    private InstrutorRepository instrutorRepository;

    @Transactional
    public Instrutor cadastrarInstrutor(Instrutor instrutor) {
        validarInstrutor(instrutor);
        return instrutorRepository.save(instrutor);
    }

    @Transactional(readOnly = true)
    public Instrutor buscarPorId(Long id) {
        return instrutorRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Instrutor não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Instrutor> listarTodos() {
        return instrutorRepository.findAll();
    }

    @Transactional
    public Instrutor atualizarInstrutor(Long id, Instrutor instrutorAtualizado) {
        Instrutor instrutorExistente = buscarPorId(id);
        
        validarInstrutor(instrutorAtualizado);

        // Atualiza os campos
        instrutorExistente.setNome(instrutorAtualizado.getNome());
        instrutorExistente.setEspecialidade(instrutorAtualizado.getEspecialidade());

        return instrutorRepository.save(instrutorExistente);
    }

    @Transactional
    public void excluirInstrutor(Long id) {
        if (!instrutorRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Instrutor não encontrado com ID: " + id);
        }
        instrutorRepository.deleteById(id);
    }

    private void validarInstrutor(Instrutor instrutor) {
        if (instrutor.getNome() == null || instrutor.getNome().trim().isEmpty()) {
            throw new ValidacaoException("Nome do instrutor é obrigatório");
        }
        if (instrutor.getNome().length() > 100) {
            throw new ValidacaoException("Nome do instrutor não pode ter mais que 100 caracteres");
        }
        if (instrutor.getEspecialidade() != null) {
            if (instrutor.getEspecialidade().length() > 50) {
                throw new ValidacaoException("Especialidade não pode ter mais que 50 caracteres");
            }
            if (!ESPECIALIDADES_VALIDAS.contains(instrutor.getEspecialidade())) {
                throw new ValidacaoException("Especialidade inválida");
            }
        }
    }
}