package com.example.demo;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Pagamento;
import com.example.demo.entity.Plano;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.PagamentoRepository;
import com.example.demo.repository.PlanoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Pagamento e PagamentoRepository
 */
@SpringBootTest
@Transactional
public class PagamentoTest {

    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    @Autowired
    private MatriculaRepository matriculaRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private PlanoRepository planoRepository;
    
    private Matricula matriculaTeste;

    @BeforeEach
    public void setUp() {
        // Criar aluno, plano e matrícula para usar nos testes
        Aluno aluno = new Aluno("João Pagamento", "555.555.555-55", LocalDate.now());
        aluno = alunoRepository.save(aluno);
        
        Plano plano = new Plano("Plano Mensal", new BigDecimal("99.90"), 30);
        plano = planoRepository.save(plano);
        
        matriculaTeste = new Matricula(aluno, plano, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA");
        matriculaTeste = matriculaRepository.save(matriculaTeste);
    }

    @Test
    public void testCriarPagamento() {
        System.out.println("=== TESTE: Criar Pagamento ===");
        
        // Criar um novo pagamento
        Pagamento pagamento = new Pagamento(
            matriculaTeste, 
            LocalDate.of(2025, 10, 15), 
            new BigDecimal("99.90"), 
            "CARTAO"
        );
        
        // Salvar no banco
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        
        // Validações
        assertNotNull(pagamentoSalvo.getIdPagamento(), "ID do pagamento não deve ser nulo após salvar");
        assertNotNull(pagamentoSalvo.getMatricula());
        assertEquals(new BigDecimal("99.90"), pagamentoSalvo.getValorPago());
        assertEquals("CARTAO", pagamentoSalvo.getFormaPagamento());
        assertEquals(LocalDate.of(2025, 10, 15), pagamentoSalvo.getDataPagamento());
        
        System.out.println("✅ Pagamento criado: " + pagamentoSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentoPorId() {
        System.out.println("=== TESTE: Buscar Pagamento por ID ===");
        
        // Criar e salvar um pagamento
        Pagamento pagamento = new Pagamento(
            matriculaTeste, 
            LocalDate.now(), 
            new BigDecimal("150.00"), 
            "PIX"
        );
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        
        // Buscar pelo ID
        Optional<Pagamento> pagamentoEncontrado = pagamentoRepository.findById(pagamentoSalvo.getIdPagamento());
        
        // Validações
        assertTrue(pagamentoEncontrado.isPresent(), "Pagamento deve ser encontrado");
        assertEquals("PIX", pagamentoEncontrado.get().getFormaPagamento());
        assertEquals(new BigDecimal("150.00"), pagamentoEncontrado.get().getValorPago());
        
        System.out.println("✅ Pagamento encontrado: " + pagamentoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentosPorMatricula() {
        System.out.println("=== TESTE: Buscar Pagamentos por Matrícula ===");
        
        // Criar vários pagamentos para a mesma matrícula
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 1), new BigDecimal("99.90"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 11, 1), new BigDecimal("99.90"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 12, 1), new BigDecimal("99.90"), "DINHEIRO"
        ));
        
        // Buscar pagamentos da matrícula
        List<Pagamento> pagamentos = pagamentoRepository.findByMatricula(matriculaTeste);
        
        // Validações
        assertTrue(pagamentos.size() >= 3, "Deve ter pelo menos 3 pagamentos");
        pagamentos.forEach(p -> assertEquals(matriculaTeste.getIdMatricula(), p.getMatricula().getIdMatricula()));
        
        System.out.println("✅ Pagamentos da matrícula encontrados: " + pagamentos.size());
        pagamentos.forEach(p -> System.out.println("   - " + p));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentosPorFormaPagamento() {
        System.out.println("=== TESTE: Buscar Pagamentos por Forma de Pagamento ===");
        
        // Criar pagamentos com diferentes formas de pagamento
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("99.90"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now().minusDays(1), new BigDecimal("99.90"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now().minusDays(2), new BigDecimal("99.90"), "CARTAO"
        ));
        
        // Buscar pagamentos via PIX
        List<Pagamento> pagamentosPix = pagamentoRepository.findByFormaPagamento("PIX");
        
        // Validações
        assertTrue(pagamentosPix.size() >= 2, "Deve ter pelo menos 2 pagamentos via PIX");
        pagamentosPix.forEach(p -> assertEquals("PIX", p.getFormaPagamento()));
        
        System.out.println("✅ Pagamentos via PIX encontrados: " + pagamentosPix.size());
        pagamentosPix.forEach(p -> System.out.println("   - " + p));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentosPorData() {
        System.out.println("=== TESTE: Buscar Pagamentos por Data ===");
        
        LocalDate dataEspecifica = LocalDate.of(2025, 10, 20);
        
        // Criar pagamentos em datas diferentes
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, dataEspecifica, new BigDecimal("99.90"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, dataEspecifica, new BigDecimal("50.00"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 21), new BigDecimal("99.90"), "DINHEIRO"
        ));
        
        // Buscar pagamentos da data específica
        List<Pagamento> pagamentos = pagamentoRepository.findByDataPagamento(dataEspecifica);
        
        // Validações
        assertTrue(pagamentos.size() >= 2, "Deve ter pelo menos 2 pagamentos nesta data");
        pagamentos.forEach(p -> assertEquals(dataEspecifica, p.getDataPagamento()));
        
        System.out.println("✅ Pagamentos na data " + dataEspecifica + ": " + pagamentos.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentosPorPeriodo() {
        System.out.println("=== TESTE: Buscar Pagamentos por Período ===");
        
        // Criar pagamentos em diferentes datas
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 5), new BigDecimal("99.90"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 15), new BigDecimal("99.90"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 25), new BigDecimal("99.90"), "DINHEIRO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 11, 5), new BigDecimal("99.90"), "CARTAO"
        ));
        
        // Buscar pagamentos de outubro
        List<Pagamento> pagamentosOutubro = pagamentoRepository.findByDataPagamentoBetween(
            LocalDate.of(2025, 10, 1), 
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(pagamentosOutubro.size() >= 3, "Deve ter pelo menos 3 pagamentos em outubro");
        
        System.out.println("✅ Pagamentos em outubro: " + pagamentosOutubro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentosPorValorMinimo() {
        System.out.println("=== TESTE: Buscar Pagamentos por Valor Mínimo ===");
        
        // Criar pagamentos com valores diferentes
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("50.00"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("100.00"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("150.00"), "DINHEIRO"
        ));
        
        // Buscar pagamentos com valor >= 100
        List<Pagamento> pagamentosAltos = pagamentoRepository.findByValorPagoGreaterThanEqual(new BigDecimal("100.00"));
        
        // Validações
        assertTrue(pagamentosAltos.size() >= 2, "Deve ter pelo menos 2 pagamentos com valor >= 100");
        pagamentosAltos.forEach(p -> 
            assertTrue(p.getValorPago().compareTo(new BigDecimal("100.00")) >= 0)
        );
        
        System.out.println("✅ Pagamentos com valor >= 100: " + pagamentosAltos.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentosPorValorMaximo() {
        System.out.println("=== TESTE: Buscar Pagamentos por Valor Máximo ===");
        
        // Criar pagamentos com valores diferentes
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("50.00"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("100.00"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("150.00"), "DINHEIRO"
        ));
        
        // Buscar pagamentos com valor <= 100
        List<Pagamento> pagamentosBaixos = pagamentoRepository.findByValorPagoLessThanEqual(new BigDecimal("100.00"));
        
        // Validações
        assertTrue(pagamentosBaixos.size() >= 2, "Deve ter pelo menos 2 pagamentos com valor <= 100");
        pagamentosBaixos.forEach(p -> 
            assertTrue(p.getValorPago().compareTo(new BigDecimal("100.00")) <= 0)
        );
        
        System.out.println("✅ Pagamentos com valor <= 100: " + pagamentosBaixos.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarPagamentosOrdenadosPorData() {
        System.out.println("=== TESTE: Listar Pagamentos Ordenados por Data (Desc) ===");
        
        // Criar pagamentos em datas diferentes
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 1), new BigDecimal("99.90"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 15), new BigDecimal("99.90"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 30), new BigDecimal("99.90"), "DINHEIRO"
        ));
        
        // Listar ordenados por data (mais recentes primeiro)
        List<Pagamento> pagamentosOrdenados = pagamentoRepository.findAllByOrderByDataPagamentoDesc();
        
        // Validações
        assertTrue(pagamentosOrdenados.size() >= 3, "Deve ter pelo menos 3 pagamentos");
        
        // Verificar ordenação decrescente
        for (int i = 0; i < pagamentosOrdenados.size() - 1; i++) {
            assertTrue(
                pagamentosOrdenados.get(i).getDataPagamento()
                    .compareTo(pagamentosOrdenados.get(i + 1).getDataPagamento()) >= 0,
                "Pagamentos devem estar ordenados por data decrescente"
            );
        }
        
        System.out.println("✅ Pagamentos ordenados (mais recentes primeiro):");
        pagamentosOrdenados.forEach(p -> System.out.println("   - " + p.getDataPagamento() + " | " + p.getValorPago()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCalcularTotalPagoPorMatricula() {
        System.out.println("=== TESTE: Calcular Total Pago por Matrícula ===");
        
        // Criar vários pagamentos
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("50.00"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("75.50"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("24.50"), "DINHEIRO"
        ));
        
        // Calcular total
        BigDecimal total = pagamentoRepository.calcularTotalPagoPorMatricula(matriculaTeste);
        
        // Validações
        assertNotNull(total, "Total não deve ser nulo");
        assertTrue(total.compareTo(new BigDecimal("150.00")) == 0, "Total deve ser 150.00");
        
        System.out.println("✅ Total pago na matrícula: R$ " + total);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarPagamentosPorMatricula() {
        System.out.println("=== TESTE: Contar Pagamentos por Matrícula ===");
        
        // Criar vários pagamentos
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("99.90"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now().minusDays(1), new BigDecimal("99.90"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.now().minusDays(2), new BigDecimal("99.90"), "DINHEIRO"
        ));
        
        // Contar pagamentos
        long count = pagamentoRepository.countByMatricula(matriculaTeste);
        
        // Validações
        assertTrue(count >= 3, "Deve ter pelo menos 3 pagamentos");
        
        System.out.println("✅ Total de pagamentos da matrícula: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPagamentosMatriculaOrdenados() {
        System.out.println("=== TESTE: Buscar Pagamentos da Matrícula Ordenados ===");
        
        // Criar pagamentos em ordem não sequencial
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 15), new BigDecimal("99.90"), "CARTAO"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 5), new BigDecimal("99.90"), "PIX"
        ));
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, LocalDate.of(2025, 10, 25), new BigDecimal("99.90"), "DINHEIRO"
        ));
        
        // Buscar pagamentos ordenados
        List<Pagamento> pagamentosOrdenados = pagamentoRepository.findByMatriculaOrderByDataPagamentoAsc(matriculaTeste);
        
        // Validações
        assertTrue(pagamentosOrdenados.size() >= 3, "Deve ter pelo menos 3 pagamentos");
        
        // Verificar ordenação crescente
        for (int i = 0; i < pagamentosOrdenados.size() - 1; i++) {
            assertTrue(
                pagamentosOrdenados.get(i).getDataPagamento()
                    .compareTo(pagamentosOrdenados.get(i + 1).getDataPagamento()) <= 0,
                "Pagamentos devem estar ordenados por data crescente"
            );
        }
        
        System.out.println("✅ Pagamentos da matrícula ordenados:");
        pagamentosOrdenados.forEach(p -> System.out.println("   - " + p.getDataPagamento()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaPagamentoNaData() {
        System.out.println("=== TESTE: Verificar Existência de Pagamento na Data ===");
        
        LocalDate dataComPagamento = LocalDate.of(2025, 10, 18);
        LocalDate dataSemPagamento = LocalDate.of(2025, 12, 25);
        
        // Criar pagamento
        pagamentoRepository.save(new Pagamento(
            matriculaTeste, dataComPagamento, new BigDecimal("99.90"), "CARTAO"
        ));
        
        // Verificar existência
        boolean existe = pagamentoRepository.existsByDataPagamento(dataComPagamento);
        boolean naoExiste = pagamentoRepository.existsByDataPagamento(dataSemPagamento);
        
        // Validações
        assertTrue(existe, "Deve existir pagamento na data " + dataComPagamento);
        assertFalse(naoExiste, "Não deve existir pagamento na data " + dataSemPagamento);
        
        System.out.println("✅ Verificação de existência de pagamento funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarPagamento() {
        System.out.println("=== TESTE: Atualizar Pagamento ===");
        
        // Criar e salvar um pagamento
        Pagamento pagamento = new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("99.90"), "CARTAO"
        );
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        Long id = pagamentoSalvo.getIdPagamento();
        
        // Atualizar a forma de pagamento
        pagamentoSalvo.setFormaPagamento("PIX");
        pagamentoRepository.save(pagamentoSalvo);
        
        // Buscar novamente
        Optional<Pagamento> pagamentoAtualizado = pagamentoRepository.findById(id);
        
        // Validações
        assertTrue(pagamentoAtualizado.isPresent());
        assertEquals("PIX", pagamentoAtualizado.get().getFormaPagamento());
        
        System.out.println("✅ Pagamento atualizado: " + pagamentoAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarPagamento() {
        System.out.println("=== TESTE: Deletar Pagamento ===");
        
        // Criar e salvar um pagamento
        Pagamento pagamento = new Pagamento(
            matriculaTeste, LocalDate.now(), new BigDecimal("99.90"), "CARTAO"
        );
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        Long id = pagamentoSalvo.getIdPagamento();
        
        // Deletar
        pagamentoRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<Pagamento> pagamentoDeletado = pagamentoRepository.findById(id);
        
        // Validações
        assertFalse(pagamentoDeletado.isPresent(), "Pagamento não deve existir após deleção");
        
        System.out.println("✅ Pagamento deletado com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
