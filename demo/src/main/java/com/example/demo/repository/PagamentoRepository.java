package com.example.demo.repository;

import com.example.demo.entity.Matricula;
import com.example.demo.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository para operações com a entidade Pagamento
 */
@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    
    /**
     * Busca todos os pagamentos de uma matrícula
     * @param matricula Matrícula
     * @return Lista de pagamentos da matrícula
     */
    List<Pagamento> findByMatricula(Matricula matricula);
    
    /**
     * Busca pagamentos por forma de pagamento
     * @param formaPagamento Forma de pagamento (ex: "DINHEIRO", "CARTAO", "PIX")
     * @return Lista de pagamentos
     */
    List<Pagamento> findByFormaPagamento(String formaPagamento);
    
    /**
     * Busca pagamentos realizados em uma data específica
     * @param dataPagamento Data do pagamento
     * @return Lista de pagamentos
     */
    List<Pagamento> findByDataPagamento(LocalDate dataPagamento);
    
    /**
     * Busca pagamentos realizados entre duas datas
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de pagamentos
     */
    List<Pagamento> findByDataPagamentoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca pagamentos com valor maior ou igual ao especificado
     * @param valor Valor mínimo
     * @return Lista de pagamentos
     */
    List<Pagamento> findByValorPagoGreaterThanEqual(BigDecimal valor);
    
    /**
     * Busca pagamentos com valor menor ou igual ao especificado
     * @param valor Valor máximo
     * @return Lista de pagamentos
     */
    List<Pagamento> findByValorPagoLessThanEqual(BigDecimal valor);
    
    /**
     * Busca pagamentos ordenados por data (mais recentes primeiro)
     * @return Lista de pagamentos ordenados
     */
    List<Pagamento> findAllByOrderByDataPagamentoDesc();
    
    /**
     * Calcula o total pago em uma matrícula
     * @param matricula Matrícula
     * @return Soma dos valores pagos
     */
    @Query("SELECT SUM(p.valorPago) FROM Pagamento p WHERE p.matricula = :matricula")
    BigDecimal calcularTotalPagoPorMatricula(Matricula matricula);
    
    /**
     * Conta quantos pagamentos uma matrícula possui
     * @param matricula Matrícula
     * @return Número de pagamentos
     */
    long countByMatricula(Matricula matricula);
    
    /**
     * Busca pagamentos de uma matrícula ordenados por data
     * @param matricula Matrícula
     * @return Lista de pagamentos ordenados
     */
    List<Pagamento> findByMatriculaOrderByDataPagamentoAsc(Matricula matricula);
    
    /**
     * Verifica se existe algum pagamento em uma data específica
     * @param dataPagamento Data do pagamento
     * @return true se existir, false caso contrário
     */
    boolean existsByDataPagamento(LocalDate dataPagamento);
}
