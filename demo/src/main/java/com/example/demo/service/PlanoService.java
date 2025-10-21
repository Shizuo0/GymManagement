package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Plano;
import com.example.demo.exception.PlanoException;
import com.example.demo.repository.PlanoRepository;

@Service
@Transactional
public class PlanoService {
    
    @Autowired
    private PlanoRepository planoRepository;
    
    public Plano criarPlano(Plano plano) {
        validarPlano(plano);
        return planoRepository.save(plano);
    }
    
    public Plano atualizarPlano(Long id, Plano plano) {
        Plano planoExistente = buscarPlanoPorId(id);
        
        validarPlano(plano);
        
        planoExistente.setNome(plano.getNome());
        planoExistente.setDescricao(plano.getDescricao());
        planoExistente.setValor(plano.getValor());
        planoExistente.setDuracaoMeses(plano.getDuracaoMeses());
        planoExistente.setStatus(plano.getStatus());
        
        return planoRepository.save(planoExistente);
    }
    
    public Plano buscarPlanoPorId(Long id) {
        return planoRepository.findById(id)
                .orElseThrow(() -> new PlanoException.PlanoNotFoundException(
                    "Plano não encontrado com ID: " + id));
    }
    
    public List<Plano> listarTodosPlanos() {
        return planoRepository.findAll();
    }
    
    public List<Plano> listarPlanosAtivos() {
        return planoRepository.findByStatus("ATIVO");
    }
    
    public void deletarPlano(Long id) {
        Plano plano = buscarPlanoPorId(id);
        planoRepository.delete(plano);
    }
    
    public void ativarPlano(Long id) {
        Plano plano = buscarPlanoPorId(id);
        plano.setStatus("ATIVO");
        planoRepository.save(plano);
    }
    
    public void inativarPlano(Long id) {
        Plano plano = buscarPlanoPorId(id);
        plano.setStatus("INATIVO");
        planoRepository.save(plano);
    }
    
    private void validarPlano(Plano plano) {
        if (plano.getNome() == null || plano.getNome().trim().isEmpty()) {
            throw new PlanoException.PlanoInvalidoException("Nome do plano é obrigatório");
        }
        
        if (plano.getValor() == null || plano.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PlanoException.PlanoInvalidoException("Valor do plano deve ser maior que zero");
        }
        
        if (plano.getDuracaoMeses() == null || plano.getDuracaoMeses() <= 0) {
            throw new PlanoException.PlanoInvalidoException("Duração do plano deve ser maior que zero");
        }
        
        // Garantir que o status seja válido
        String status = plano.getStatus();
        if (status != null && !status.equals("ATIVO") && !status.equals("INATIVO")) {
            throw new PlanoException.PlanoInvalidoException("Status do plano deve ser ATIVO ou INATIVO");
        }
    }
}