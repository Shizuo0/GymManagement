package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Aluno;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.exception.ValidacaoException;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.util.ValidadorCPF;

@Service
public class AlunoService {
    
    @Autowired
    private AlunoRepository alunoRepository;

    @Transactional
    public Aluno cadastrarAluno(Aluno aluno) {
        // Formata o CPF antes de validar
        if (aluno.getCpf() != null) {
            aluno.setCpf(ValidadorCPF.format(aluno.getCpf()));
        }
        
        validarAluno(aluno);
        
        return alunoRepository.save(aluno);
    }

    @Transactional(readOnly = true)
    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Aluno> listarTodos() {
        return alunoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional
    public Aluno atualizarAluno(Long id, Aluno alunoAtualizado) {
        Aluno alunoExistente = buscarPorId(id);
        
        // Formata o CPF antes de validar
        if (alunoAtualizado.getCpf() != null) {
            alunoAtualizado.setCpf(ValidadorCPF.format(alunoAtualizado.getCpf()));
        }
        
        validarAluno(alunoAtualizado);

        // Atualiza os campos
        alunoExistente.setNome(alunoAtualizado.getNome());
        if (alunoAtualizado.getCpf() != null && !alunoAtualizado.getCpf().equals(alunoExistente.getCpf())) {
            alunoExistente.setCpf(alunoAtualizado.getCpf());
        }
        alunoExistente.setDataIngresso(alunoAtualizado.getDataIngresso());

        return alunoRepository.save(alunoExistente);
    }

    @Transactional
    public void excluirAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Aluno não encontrado com ID: " + id);
        }
        alunoRepository.deleteById(id);
    }

    private void validarAluno(Aluno aluno) {
        if (aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
            throw new ValidacaoException("Nome do aluno é obrigatório");
        }
        if (aluno.getNome().length() > 100) {
            throw new ValidacaoException("Nome do aluno não pode ter mais que 100 caracteres");
        }
        if (aluno.getCpf() == null || !ValidadorCPF.isValid(aluno.getCpf())) {
            throw new ValidacaoException("CPF inválido");
        }
    }
}