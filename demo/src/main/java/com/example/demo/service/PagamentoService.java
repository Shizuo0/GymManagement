package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Matricula;
import com.example.demo.entity.Pagamento;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.PagamentoException;
import com.example.demo.repository.PagamentoRepository;

/**
 * Service para gerenciamento de Pagamentos
 * Implementa regras de negócio para registro e validação de pagamentos
 */
@Service
@Transactional
public class PagamentoService {
    
    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    /**
     * Registra um novo pagamento
     * @param pagamento Dados do pagamento
     * @return Pagamento registrado
     */
    public Pagamento registrarPagamento(Pagamento pagamento) {
        validarPagamento(pagamento);
        validarMatricula(pagamento.getMatricula());
        
        return pagamentoRepository.save(pagamento);
    }
    
    /**
     * Busca um pagamento por ID
     * @param id ID do pagamento
     * @return Pagamento encontrado
     */
    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
            .orElseThrow(() -> new PagamentoException.PagamentoNotFoundException(
                "Pagamento não encontrado com ID: " + id));
    }
    
    /**
     * Lista todos os pagamentos
     * @return Lista de pagamentos
     */
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAllByOrderByDataPagamentoDesc();
    }
    
    /**
     * Lista todos os pagamentos de uma matrícula
     * @param matricula Matrícula
     * @return Lista de pagamentos
     */
    public List<Pagamento> listarPagamentosPorMatricula(Matricula matricula) {
        return pagamentoRepository.findByMatriculaOrderByDataPagamentoAsc(matricula);
    }
    
    /**
     * Busca pagamentos por forma de pagamento
     * @param formaPagamento Forma de pagamento
     * @return Lista de pagamentos
     */
    public List<Pagamento> buscarPorFormaPagamento(String formaPagamento) {
        return pagamentoRepository.findByFormaPagamento(formaPagamento);
    }
    
    /**
     * Busca pagamentos em um período
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de pagamentos
     */
    public List<Pagamento> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new PagamentoException.DataInvalidaException(
                "Data inicial não pode ser posterior à data final");
        }
        return pagamentoRepository.findByDataPagamentoBetween(dataInicio, dataFim);
    }
    
    /**
     * Calcula o total pago em uma matrícula
     * @param matricula Matrícula
     * @return Total pago
     */
    public BigDecimal calcularTotalPago(Matricula matricula) {
        BigDecimal total = pagamentoRepository.calcularTotalPagoPorMatricula(matricula);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Verifica se uma matrícula está com pagamentos em dia
     * @param matricula Matrícula
     * @return true se está em dia, false caso contrário
     */
    public boolean verificarPagamentosEmDia(Matricula matricula) {
        BigDecimal totalPago = calcularTotalPago(matricula);
        BigDecimal valorPlano = matricula.getPlano().getValor();
        
        return totalPago.compareTo(valorPlano) >= 0;
    }
    
    /**
     * Atualiza um pagamento existente
     * @param id ID do pagamento
     * @param pagamento Novos dados do pagamento
     * @return Pagamento atualizado
     */
    public Pagamento atualizarPagamento(Long id, Pagamento pagamento) {
        Pagamento pagamentoExistente = buscarPorId(id);
        
        validarPagamento(pagamento);
        
        pagamentoExistente.setDataPagamento(pagamento.getDataPagamento());
        pagamentoExistente.setValorPago(pagamento.getValorPago());
        pagamentoExistente.setFormaPagamento(pagamento.getFormaPagamento());
        
        return pagamentoRepository.save(pagamentoExistente);
    }
    
    /**
     * Deleta um pagamento
     * @param id ID do pagamento
     */
    public void deletarPagamento(Long id) {
        Pagamento pagamento = buscarPorId(id);
        pagamentoRepository.delete(pagamento);
    }
    
    /**
     * Valida os dados do pagamento
     * @param pagamento Pagamento a validar
     */
    private void validarPagamento(Pagamento pagamento) {
        if (pagamento.getMatricula() == null) {
            throw new PagamentoException.MatriculaInvalidaException(
                "Matrícula é obrigatória para o pagamento");
        }
        
        if (pagamento.getDataPagamento() == null) {
            throw new PagamentoException.DataInvalidaException(
                "Data do pagamento é obrigatória");
        }
        
        if (pagamento.getDataPagamento().isAfter(LocalDate.now())) {
            throw new PagamentoException.DataInvalidaException(
                "Data do pagamento não pode ser futura");
        }
        
        if (pagamento.getValorPago() == null) {
            throw new PagamentoException.ValorInvalidoException(
                "Valor do pagamento é obrigatório");
        }
        
        if (pagamento.getValorPago().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagamentoException.ValorInvalidoException(
                "Valor do pagamento deve ser maior que zero");
        }
        
        if (pagamento.getFormaPagamento() != null && pagamento.getFormaPagamento().trim().isEmpty()) {
            throw new PagamentoException.PagamentoInvalidoException(
                "Forma de pagamento não pode ser vazia");
        }
    }
    
    /**
     * Valida se a matrícula está ativa e válida para receber pagamento
     * @param matricula Matrícula a validar
     */
    private void validarMatricula(Matricula matricula) {
        if (matricula == null) {
            throw new PagamentoException.MatriculaInvalidaException(
                "Matrícula não pode ser nula");
        }
        
        if (matricula.getStatus() != MatriculaStatus.ATIVA) {
            throw new PagamentoException.MatriculaInvalidaException(
                "Não é possível registrar pagamento para matrícula com status: " + matricula.getStatus());
        }
        
        if (matricula.getDataFim() != null && matricula.getDataFim().isBefore(LocalDate.now())) {
            throw new PagamentoException.MatriculaInvalidaException(
                "Matrícula expirada. Data de término: " + matricula.getDataFim());
        }
    }
}
