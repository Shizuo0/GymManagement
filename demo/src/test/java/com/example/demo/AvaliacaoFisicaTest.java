package com.example.demo;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.AvaliacaoFisica;
import com.example.demo.entity.Instrutor;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.AvaliacaoFisicaRepository;
import com.example.demo.repository.InstrutorRepository;
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
 * Testes para a entidade AvaliacaoFisica e AvaliacaoFisicaRepository
 */
@SpringBootTest
@Transactional
public class AvaliacaoFisicaTest {

    @Autowired
    private AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private InstrutorRepository instrutorRepository;
    
    private Aluno alunoTeste;
    private Instrutor instrutorTeste;

    @BeforeEach
    public void setUp() {
        // Criar aluno e instrutor para usar nos testes
        alunoTeste = new Aluno("Carlos Avaliação", "666.666.666-66", LocalDate.now());
        alunoTeste = alunoRepository.save(alunoTeste);
        
        instrutorTeste = new Instrutor("João Instrutor", "Educação Física");
        instrutorTeste = instrutorRepository.save(instrutorTeste);
    }

    @Test
    public void testCriarAvaliacaoFisica() {
        System.out.println("=== TESTE: Criar Avaliação Física ===");
        
        // Criar uma nova avaliação
        AvaliacaoFisica avaliacao = new AvaliacaoFisica(
            alunoTeste, 
            instrutorTeste, 
            LocalDate.of(2025, 10, 15),
            new BigDecimal("80.5"),
            new BigDecimal("1.75"),
            new BigDecimal("18.5")
        );
        avaliacao.setMedidasCorporais("Braço: 35cm, Perna: 55cm, Cintura: 85cm");
        
        // Salvar no banco
        AvaliacaoFisica avaliacaoSalva = avaliacaoFisicaRepository.save(avaliacao);
        
        // Validações
        assertNotNull(avaliacaoSalva.getIdAvaliacao(), "ID da avaliação não deve ser nulo após salvar");
        assertNotNull(avaliacaoSalva.getAluno());
        assertNotNull(avaliacaoSalva.getInstrutor());
        assertEquals(new BigDecimal("80.5"), avaliacaoSalva.getPeso());
        assertEquals(new BigDecimal("1.75"), avaliacaoSalva.getAltura());
        assertEquals(new BigDecimal("18.5"), avaliacaoSalva.getPercentualGordura());
        assertNotNull(avaliacaoSalva.getMedidasCorporais());
        
        System.out.println("✅ Avaliação criada: " + avaliacaoSalva);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCriarAvaliacaoSemMedidas() {
        System.out.println("=== TESTE: Criar Avaliação sem Medidas Detalhadas ===");
        
        // Criar avaliação apenas com dados básicos
        AvaliacaoFisica avaliacao = new AvaliacaoFisica(
            alunoTeste, 
            instrutorTeste, 
            LocalDate.now()
        );
        
        // Salvar no banco
        AvaliacaoFisica avaliacaoSalva = avaliacaoFisicaRepository.save(avaliacao);
        
        // Validações
        assertNotNull(avaliacaoSalva.getIdAvaliacao());
        assertNull(avaliacaoSalva.getPeso());
        assertNull(avaliacaoSalva.getAltura());
        assertNull(avaliacaoSalva.getPercentualGordura());
        assertNull(avaliacaoSalva.getMedidasCorporais());
        
        System.out.println("✅ Avaliação criada sem medidas: " + avaliacaoSalva);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacaoPorId() {
        System.out.println("=== TESTE: Buscar Avaliação por ID ===");
        
        // Criar e salvar uma avaliação
        AvaliacaoFisica avaliacao = new AvaliacaoFisica(
            alunoTeste, 
            instrutorTeste, 
            LocalDate.now(),
            new BigDecimal("75.0"),
            new BigDecimal("1.70"),
            new BigDecimal("20.0")
        );
        AvaliacaoFisica avaliacaoSalva = avaliacaoFisicaRepository.save(avaliacao);
        
        // Buscar pelo ID
        Optional<AvaliacaoFisica> avaliacaoEncontrada = avaliacaoFisicaRepository.findById(avaliacaoSalva.getIdAvaliacao());
        
        // Validações
        assertTrue(avaliacaoEncontrada.isPresent(), "Avaliação deve ser encontrada");
        assertEquals(new BigDecimal("75.0"), avaliacaoEncontrada.get().getPeso());
        
        System.out.println("✅ Avaliação encontrada: " + avaliacaoEncontrada.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacoesPorAluno() {
        System.out.println("=== TESTE: Buscar Avaliações por Aluno ===");
        
        // Criar várias avaliações para o mesmo aluno
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 1, 1),
            new BigDecimal("85.0"), new BigDecimal("1.75"), new BigDecimal("22.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 7, 1),
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 1),
            new BigDecimal("78.0"), new BigDecimal("1.75"), new BigDecimal("17.5")
        ));
        
        // Buscar avaliações do aluno
        List<AvaliacaoFisica> avaliacoes = avaliacaoFisicaRepository.findByAluno(alunoTeste);
        
        // Validações
        assertTrue(avaliacoes.size() >= 3, "Deve ter pelo menos 3 avaliações");
        avaliacoes.forEach(a -> assertEquals(alunoTeste.getIdAluno(), a.getAluno().getIdAluno()));
        
        System.out.println("✅ Avaliações do aluno encontradas: " + avaliacoes.size());
        avaliacoes.forEach(a -> System.out.println("   - " + a));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacoesPorInstrutor() {
        System.out.println("=== TESTE: Buscar Avaliações por Instrutor ===");
        
        // Criar outro aluno
        Aluno aluno2 = alunoRepository.save(new Aluno("Maria Teste", "777.777.777-77"));
        
        // Criar avaliações realizadas pelo instrutor
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.now(),
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            aluno2, instrutorTeste, LocalDate.now(),
            new BigDecimal("65.0"), new BigDecimal("1.60"), new BigDecimal("22.0")
        ));
        
        // Buscar avaliações do instrutor
        List<AvaliacaoFisica> avaliacoes = avaliacaoFisicaRepository.findByInstrutor(instrutorTeste);
        
        // Validações
        assertTrue(avaliacoes.size() >= 2, "Deve ter pelo menos 2 avaliações");
        avaliacoes.forEach(a -> assertEquals(instrutorTeste.getIdInstrutor(), a.getInstrutor().getIdInstrutor()));
        
        System.out.println("✅ Avaliações do instrutor encontradas: " + avaliacoes.size());
        avaliacoes.forEach(a -> System.out.println("   - " + a));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacoesAlunoOrdenadas() {
        System.out.println("=== TESTE: Buscar Avaliações do Aluno Ordenadas ===");
        
        // Criar avaliações em ordem não sequencial
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 5, 15),
            new BigDecimal("82.0"), new BigDecimal("1.75"), new BigDecimal("20.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 1, 10),
            new BigDecimal("85.0"), new BigDecimal("1.75"), new BigDecimal("22.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 1),
            new BigDecimal("78.0"), new BigDecimal("1.75"), new BigDecimal("18.0")
        ));
        
        // Buscar avaliações ordenadas
        List<AvaliacaoFisica> avaliacoesOrdenadas = avaliacaoFisicaRepository.findByAlunoOrderByDataAvaliacaoDesc(alunoTeste);
        
        // Validações
        assertTrue(avaliacoesOrdenadas.size() >= 3, "Deve ter pelo menos 3 avaliações");
        
        // Verificar ordenação decrescente
        for (int i = 0; i < avaliacoesOrdenadas.size() - 1; i++) {
            assertTrue(
                avaliacoesOrdenadas.get(i).getDataAvaliacao()
                    .compareTo(avaliacoesOrdenadas.get(i + 1).getDataAvaliacao()) >= 0,
                "Avaliações devem estar ordenadas por data decrescente"
            );
        }
        
        System.out.println("✅ Avaliações ordenadas (mais recentes primeiro):");
        avaliacoesOrdenadas.forEach(a -> System.out.println("   - " + a.getDataAvaliacao() + " | Peso: " + a.getPeso()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacoesPorData() {
        System.out.println("=== TESTE: Buscar Avaliações por Data ===");
        
        LocalDate dataEspecifica = LocalDate.of(2025, 10, 20);
        
        // Criar avaliações em datas diferentes
        Aluno aluno2 = alunoRepository.save(new Aluno("Pedro Teste", "888.888.888-88"));
        
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, dataEspecifica,
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            aluno2, instrutorTeste, dataEspecifica,
            new BigDecimal("70.0"), new BigDecimal("1.70"), new BigDecimal("18.0")
        ));
        
        // Buscar avaliações da data específica
        List<AvaliacaoFisica> avaliacoes = avaliacaoFisicaRepository.findByDataAvaliacao(dataEspecifica);
        
        // Validações
        assertTrue(avaliacoes.size() >= 2, "Deve ter pelo menos 2 avaliações nesta data");
        avaliacoes.forEach(a -> assertEquals(dataEspecifica, a.getDataAvaliacao()));
        
        System.out.println("✅ Avaliações na data " + dataEspecifica + ": " + avaliacoes.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacoesPorPeriodo() {
        System.out.println("=== TESTE: Buscar Avaliações por Período ===");
        
        // Criar avaliações em diferentes meses
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 5),
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 15),
            new BigDecimal("79.0"), new BigDecimal("1.75"), new BigDecimal("18.5")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 11, 5),
            new BigDecimal("78.0"), new BigDecimal("1.75"), new BigDecimal("18.0")
        ));
        
        // Buscar avaliações de outubro
        List<AvaliacaoFisica> avaliacoesOutubro = avaliacaoFisicaRepository.findByDataAvaliacaoBetween(
            LocalDate.of(2025, 10, 1), 
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(avaliacoesOutubro.size() >= 2, "Deve ter pelo menos 2 avaliações em outubro");
        
        System.out.println("✅ Avaliações em outubro: " + avaliacoesOutubro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacaoMaisRecente() {
        System.out.println("=== TESTE: Buscar Avaliação Mais Recente do Aluno ===");
        
        // Criar várias avaliações
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 1, 1),
            new BigDecimal("85.0"), new BigDecimal("1.75"), new BigDecimal("22.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 5, 1),
            new BigDecimal("82.0"), new BigDecimal("1.75"), new BigDecimal("20.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 1),
            new BigDecimal("78.0"), new BigDecimal("1.75"), new BigDecimal("18.0")
        ));
        
        // Buscar a mais recente
        Optional<AvaliacaoFisica> avaliacaoMaisRecente = avaliacaoFisicaRepository.findFirstByAlunoOrderByDataAvaliacaoDesc(alunoTeste);
        
        // Validações
        assertTrue(avaliacaoMaisRecente.isPresent(), "Deve encontrar avaliação mais recente");
        assertEquals(LocalDate.of(2025, 10, 1), avaliacaoMaisRecente.get().getDataAvaliacao());
        assertEquals(new BigDecimal("78.0"), avaliacaoMaisRecente.get().getPeso());
        
        System.out.println("✅ Avaliação mais recente: " + avaliacaoMaisRecente.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacoesPorAlunoEInstrutor() {
        System.out.println("=== TESTE: Buscar Avaliações por Aluno e Instrutor ===");
        
        // Criar outro instrutor
        Instrutor instrutor2 = instrutorRepository.save(new Instrutor("Maria Instrutora", "Nutrição"));
        
        // Criar avaliações com diferentes instrutores
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.now(),
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutor2, LocalDate.now(),
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        ));
        
        // Buscar avaliações do aluno com instrutor específico
        List<AvaliacaoFisica> avaliacoes = avaliacaoFisicaRepository.findByAlunoAndInstrutor(alunoTeste, instrutorTeste);
        
        // Validações
        assertTrue(avaliacoes.size() >= 1, "Deve ter pelo menos 1 avaliação");
        avaliacoes.forEach(a -> {
            assertEquals(alunoTeste.getIdAluno(), a.getAluno().getIdAluno());
            assertEquals(instrutorTeste.getIdInstrutor(), a.getInstrutor().getIdInstrutor());
        });
        
        System.out.println("✅ Avaliações do aluno com o instrutor: " + avaliacoes.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarAvaliacoesPorAluno() {
        System.out.println("=== TESTE: Contar Avaliações por Aluno ===");
        
        // Criar várias avaliações
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(alunoTeste, instrutorTeste, LocalDate.of(2025, 1, 1)));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(alunoTeste, instrutorTeste, LocalDate.of(2025, 5, 1)));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 1)));
        
        // Contar avaliações
        long count = avaliacaoFisicaRepository.countByAluno(alunoTeste);
        
        // Validações
        assertTrue(count >= 3, "Deve ter pelo menos 3 avaliações");
        
        System.out.println("✅ Total de avaliações do aluno: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarAvaliacoesPorInstrutor() {
        System.out.println("=== TESTE: Contar Avaliações por Instrutor ===");
        
        // Criar vários alunos e avaliações
        Aluno aluno2 = alunoRepository.save(new Aluno("José Teste", "999.999.999-99"));
        
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(alunoTeste, instrutorTeste, LocalDate.now()));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(aluno2, instrutorTeste, LocalDate.now()));
        
        // Contar avaliações
        long count = avaliacaoFisicaRepository.countByInstrutor(instrutorTeste);
        
        // Validações
        assertTrue(count >= 2, "Deve ter pelo menos 2 avaliações");
        
        System.out.println("✅ Total de avaliações do instrutor: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaAvaliacaoNaData() {
        System.out.println("=== TESTE: Verificar Existência de Avaliação na Data ===");
        
        LocalDate dataComAvaliacao = LocalDate.of(2025, 10, 18);
        LocalDate dataSemAvaliacao = LocalDate.of(2025, 12, 25);
        
        // Criar avaliação
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, dataComAvaliacao
        ));
        
        // Verificar existência
        boolean existe = avaliacaoFisicaRepository.existsByAlunoAndDataAvaliacao(alunoTeste, dataComAvaliacao);
        boolean naoExiste = avaliacaoFisicaRepository.existsByAlunoAndDataAvaliacao(alunoTeste, dataSemAvaliacao);
        
        // Validações
        assertTrue(existe, "Deve existir avaliação na data " + dataComAvaliacao);
        assertFalse(naoExiste, "Não deve existir avaliação na data " + dataSemAvaliacao);
        
        System.out.println("✅ Verificação de existência funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAvaliacoesAlunoNoPeriodoCustomQuery() {
        System.out.println("=== TESTE: Buscar Avaliações do Aluno no Período (Query Custom) ===");
        
        // Criar avaliações em diferentes períodos
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 9, 15),
            new BigDecimal("82.0"), new BigDecimal("1.75"), new BigDecimal("20.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 10),
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        ));
        avaliacaoFisicaRepository.save(new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 20),
            new BigDecimal("79.0"), new BigDecimal("1.75"), new BigDecimal("18.5")
        ));
        
        // Buscar avaliações de outubro usando query customizada
        List<AvaliacaoFisica> avaliacoesOutubro = avaliacaoFisicaRepository.findAvaliacoesAlunoNoPeriodo(
            alunoTeste,
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(avaliacoesOutubro.size() >= 2, "Deve ter pelo menos 2 avaliações em outubro");
        avaliacoesOutubro.forEach(a -> {
            assertEquals(alunoTeste.getIdAluno(), a.getAluno().getIdAluno());
            assertTrue(a.getDataAvaliacao().getMonthValue() == 10);
        });
        
        System.out.println("✅ Avaliações do aluno em outubro (query custom): " + avaliacoesOutubro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarAvaliacao() {
        System.out.println("=== TESTE: Atualizar Avaliação ===");
        
        // Criar e salvar uma avaliação
        AvaliacaoFisica avaliacao = new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.now(),
            new BigDecimal("80.0"), new BigDecimal("1.75"), new BigDecimal("19.0")
        );
        AvaliacaoFisica avaliacaoSalva = avaliacaoFisicaRepository.save(avaliacao);
        Long id = avaliacaoSalva.getIdAvaliacao();
        
        // Atualizar o peso
        avaliacaoSalva.setPeso(new BigDecimal("78.5"));
        avaliacaoFisicaRepository.save(avaliacaoSalva);
        
        // Buscar novamente
        Optional<AvaliacaoFisica> avaliacaoAtualizada = avaliacaoFisicaRepository.findById(id);
        
        // Validações
        assertTrue(avaliacaoAtualizada.isPresent());
        assertEquals(new BigDecimal("78.5"), avaliacaoAtualizada.get().getPeso());
        
        System.out.println("✅ Avaliação atualizada: " + avaliacaoAtualizada.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarAvaliacao() {
        System.out.println("=== TESTE: Deletar Avaliação ===");
        
        // Criar e salvar uma avaliação
        AvaliacaoFisica avaliacao = new AvaliacaoFisica(
            alunoTeste, instrutorTeste, LocalDate.now()
        );
        AvaliacaoFisica avaliacaoSalva = avaliacaoFisicaRepository.save(avaliacao);
        Long id = avaliacaoSalva.getIdAvaliacao();
        
        // Deletar
        avaliacaoFisicaRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<AvaliacaoFisica> avaliacaoDeletada = avaliacaoFisicaRepository.findById(id);
        
        // Validações
        assertFalse(avaliacaoDeletada.isPresent(), "Avaliação não deve existir após deleção");
        
        System.out.println("✅ Avaliação deletada com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
