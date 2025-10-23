package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
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
 * Testes para a entidade ItemTreino e ItemTreinoRepository
 */
@SpringBootTest
@Transactional
public class ItemTreinoTest {

    @Autowired
    private ItemTreinoRepository itemTreinoRepository;
    
    @Autowired
    private PlanoTreinoRepository planoTreinoRepository;
    
    @Autowired
    private ExercicioRepository exercicioRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private InstrutorRepository instrutorRepository;
    
    private PlanoTreino planoTreinoTeste;
    private Exercicio exercicio1;
    private Exercicio exercicio2;
    private Exercicio exercicio3;

    @BeforeEach
    public void setUp() {
        // Criar aluno e instrutor
        Aluno aluno = alunoRepository.save(new Aluno("João Treino", "111.222.333-44", LocalDate.now()));
        Instrutor instrutor = instrutorRepository.save(new Instrutor("Prof. Silva", "Personal Trainer"));
        
        // Criar plano de treino
        planoTreinoTeste = new PlanoTreino(aluno, instrutor, LocalDate.now(), "Treino ABC", 12);
        planoTreinoTeste = planoTreinoRepository.save(planoTreinoTeste);
        
        // Criar exercícios
        exercicio1 = exercicioRepository.save(new Exercicio("Supino Reto", "Peito"));
        exercicio2 = exercicioRepository.save(new Exercicio("Agachamento Livre", "Pernas"));
        exercicio3 = exercicioRepository.save(new Exercicio("Rosca Direta", "Bíceps"));
    }

    @Test
    public void testCriarItemTreinoCompleto() {
        System.out.println("=== TESTE: Criar Item de Treino Completo ===");
        
        // Criar item completo
        ItemTreino item = new ItemTreino(
            planoTreinoTeste,
            exercicio1,
            4,  // séries
            12, // repetições
            new BigDecimal("80.0"), // carga
            "Descanso de 60 segundos entre séries"
        );
        
        // Salvar no banco
        ItemTreino itemSalvo = itemTreinoRepository.save(item);
        
        // Validações
        assertNotNull(itemSalvo.getIdItemTreino(), "ID do item não deve ser nulo após salvar");
        assertNotNull(itemSalvo.getPlanoTreino());
        assertNotNull(itemSalvo.getExercicio());
        assertEquals(4, itemSalvo.getSeries());
        assertEquals(12, itemSalvo.getRepeticoes());
        assertEquals(new BigDecimal("80.0"), itemSalvo.getCarga());
        assertEquals("Descanso de 60 segundos entre séries", itemSalvo.getObservacoes());
        
        System.out.println("✅ Item de treino completo criado: " + itemSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCriarItemTreinoSimples() {
        System.out.println("=== TESTE: Criar Item de Treino Simples ===");
        
        // Criar item com valores mínimos obrigatórios
        ItemTreino item = new ItemTreino(planoTreinoTeste, exercicio2, 1, 1, BigDecimal.ZERO);
        
        // Salvar no banco
        ItemTreino itemSalvo = itemTreinoRepository.save(item);
        
        // Validações
        assertNotNull(itemSalvo.getIdItemTreino());
        assertNotNull(itemSalvo.getPlanoTreino());
        assertNotNull(itemSalvo.getExercicio());
        assertEquals(1, itemSalvo.getSeries());
        assertEquals(1, itemSalvo.getRepeticoes());
        assertEquals(BigDecimal.ZERO, itemSalvo.getCarga());
        
        System.out.println("✅ Item de treino simples criado: " + itemSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItemPorId() {
        System.out.println("=== TESTE: Buscar Item por ID ===");
        
        // Criar e salvar item
        ItemTreino item = new ItemTreino(planoTreinoTeste, exercicio1, 3, 10, new BigDecimal("50.0"));
        ItemTreino itemSalvo = itemTreinoRepository.save(item);
        
        // Buscar pelo ID
        Optional<ItemTreino> itemEncontrado = itemTreinoRepository.findById(itemSalvo.getIdItemTreino());
        
        // Validações
        assertTrue(itemEncontrado.isPresent(), "Item deve ser encontrado");
        assertEquals(3, itemEncontrado.get().getSeries());
        assertEquals(10, itemEncontrado.get().getRepeticoes());
        
        System.out.println("✅ Item encontrado: " + itemEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensPorPlanoTreino() {
        System.out.println("=== TESTE: Buscar Itens por Plano de Treino ===");
        
        // Criar vários itens para o plano
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 15, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Buscar itens do plano
        List<ItemTreino> itens = itemTreinoRepository.findByPlanoTreino(planoTreinoTeste);
        
        // Validações
        assertTrue(itens.size() >= 3, "Deve ter pelo menos 3 itens");
        itens.forEach(i -> assertEquals(planoTreinoTeste.getIdPlanoTreino(), i.getPlanoTreino().getIdPlanoTreino()));
        
        System.out.println("✅ Itens do plano encontrados: " + itens.size());
        itens.forEach(i -> System.out.println("   - " + i.getExercicio().getNome() + " | " + i.getSeries() + "x" + i.getRepeticoes()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensPorExercicio() {
        System.out.println("=== TESTE: Buscar Itens por Exercício ===");
        
        // Criar outro plano
        Aluno aluno2 = alunoRepository.save(new Aluno("Maria Teste", "222.333.444-55"));
        Instrutor instrutor2 = instrutorRepository.save(new Instrutor("Prof. Santos", "Musculação"));
        PlanoTreino plano2 = planoTreinoRepository.save(new PlanoTreino(aluno2, instrutor2, LocalDate.now()));
        
        // Mesmo exercício em planos diferentes
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(plano2, exercicio1, 3, 10, new BigDecimal("70.0")));
        
        // Buscar planos que usam o exercício
        List<ItemTreino> itens = itemTreinoRepository.findByExercicio(exercicio1);
        
        // Validações
        assertTrue(itens.size() >= 2, "Deve ter pelo menos 2 planos usando o exercício");
        itens.forEach(i -> assertEquals(exercicio1.getIdExercicio(), i.getExercicio().getIdExercicio()));
        
        System.out.println("✅ Planos que usam '" + exercicio1.getNome() + "': " + itens.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItemEspecificoPlanoExercicio() {
        System.out.println("=== TESTE: Buscar Item Específico (Plano + Exercício) ===");
        
        // Criar item
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 5, 10, new BigDecimal("120.0")));
        
        // Buscar combinação específica
        Optional<ItemTreino> item = itemTreinoRepository.findByPlanoTreinoAndExercicio(planoTreinoTeste, exercicio2);
        
        // Validações
        assertTrue(item.isPresent(), "Deve encontrar o item");
        assertEquals(5, item.get().getSeries());
        assertEquals(new BigDecimal("120.0"), item.get().getCarga());
        
        System.out.println("✅ Item específico encontrado: " + item.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensPorSeries() {
        System.out.println("=== TESTE: Buscar Itens por Número de Séries ===");
        
        // Criar itens com diferentes séries
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 4, 15, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Buscar itens com 4 séries
        List<ItemTreino> itens4Series = itemTreinoRepository.findBySeries(4);
        
        // Validações
        assertTrue(itens4Series.size() >= 2, "Deve ter pelo menos 2 itens com 4 séries");
        itens4Series.forEach(i -> assertEquals(4, i.getSeries()));
        
        System.out.println("✅ Itens com 4 séries: " + itens4Series.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensPorRepeticoes() {
        System.out.println("=== TESTE: Buscar Itens por Número de Repetições ===");
        
        // Criar itens
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Buscar itens com 12 repetições
        List<ItemTreino> itens12Reps = itemTreinoRepository.findByRepeticoes(12);
        
        // Validações
        assertTrue(itens12Reps.size() >= 2, "Deve ter pelo menos 2 itens com 12 repetições");
        itens12Reps.forEach(i -> assertEquals(12, i.getRepeticoes()));
        
        System.out.println("✅ Itens com 12 repetições: " + itens12Reps.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensPorCarga() {
        System.out.println("=== TESTE: Buscar Itens por Carga ===");
        
        BigDecimal carga = new BigDecimal("80.0");
        
        // Criar itens com mesma carga
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, carga));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 10, carga));
        
        // Buscar por carga
        List<ItemTreino> itens = itemTreinoRepository.findByCarga(carga);
        
        // Validações
        assertTrue(itens.size() >= 2, "Deve ter pelo menos 2 itens com carga 80kg");
        itens.forEach(i -> assertEquals(0, carga.compareTo(i.getCarga())));
        
        System.out.println("✅ Itens com carga " + carga + "kg: " + itens.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensPorCargaMinima() {
        System.out.println("=== TESTE: Buscar Itens por Carga Mínima ===");
        
        // Criar itens com diferentes cargas
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 10, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Buscar itens com carga >= 70kg
        List<ItemTreino> itensPesados = itemTreinoRepository.findByCargaGreaterThanEqual(new BigDecimal("70.0"));
        
        // Validações
        assertTrue(itensPesados.size() >= 2, "Deve ter pelo menos 2 itens com carga >= 70kg");
        itensPesados.forEach(i -> assertTrue(i.getCarga().compareTo(new BigDecimal("70.0")) >= 0));
        
        System.out.println("✅ Itens com carga >= 70kg: " + itensPesados.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensPorCargaMaxima() {
        System.out.println("=== TESTE: Buscar Itens por Carga Máxima ===");
        
        // Criar itens
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 10, new BigDecimal("15.0")));
        
        // Buscar itens com carga <= 30kg
        List<ItemTreino> itensLeves = itemTreinoRepository.findByCargaLessThanEqual(new BigDecimal("30.0"));
        
        // Validações
        assertTrue(itensLeves.size() >= 2, "Deve ter pelo menos 2 itens com carga <= 30kg");
        itensLeves.forEach(i -> assertTrue(i.getCarga().compareTo(new BigDecimal("30.0")) <= 0));
        
        System.out.println("✅ Itens com carga <= 30kg: " + itensLeves.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensDoPlanoOrdenadosPorSeries() {
        System.out.println("=== TESTE: Buscar Itens Ordenados por Séries ===");
        
        // Criar itens com diferentes séries
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 2, 15, new BigDecimal("50.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 10, new BigDecimal("20.0")));
        
        // Buscar ordenados por séries
        List<ItemTreino> itensOrdenados = itemTreinoRepository.findByPlanoTreinoOrderBySeriesAsc(planoTreinoTeste);
        
        // Validações
        assertTrue(itensOrdenados.size() >= 3, "Deve ter pelo menos 3 itens");
        
        // Verificar ordenação
        for (int i = 0; i < itensOrdenados.size() - 1; i++) {
            assertTrue(itensOrdenados.get(i).getSeries() <= itensOrdenados.get(i + 1).getSeries(),
                    "Itens devem estar ordenados por séries crescente");
        }
        
        System.out.println("✅ Itens ordenados por séries:");
        itensOrdenados.forEach(i -> System.out.println("   - " + i.getExercicio().getNome() + " | " + i.getSeries() + " séries"));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensDoPlanoOrdenadosPorCarga() {
        System.out.println("=== TESTE: Buscar Itens Ordenados por Carga ===");
        
        // Criar itens com diferentes cargas
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 10, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Buscar ordenados por carga (decrescente)
        List<ItemTreino> itensOrdenados = itemTreinoRepository.findByPlanoTreinoOrderByCargaDesc(planoTreinoTeste);
        
        // Validações
        assertTrue(itensOrdenados.size() >= 3, "Deve ter pelo menos 3 itens");
        
        // Verificar ordenação decrescente
        for (int i = 0; i < itensOrdenados.size() - 1; i++) {
            assertTrue(itensOrdenados.get(i).getCarga().compareTo(itensOrdenados.get(i + 1).getCarga()) >= 0,
                    "Itens devem estar ordenados por carga decrescente");
        }
        
        System.out.println("✅ Itens ordenados por carga (maior para menor):");
        itensOrdenados.forEach(i -> System.out.println("   - " + i.getExercicio().getNome() + " | " + i.getCarga() + "kg"));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarItensDoPlano() {
        System.out.println("=== TESTE: Contar Itens do Plano ===");
        
        // Criar vários itens
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 15, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Contar itens
        long count = itemTreinoRepository.countByPlanoTreino(planoTreinoTeste);
        
        // Validações
        assertTrue(count >= 3, "Deve ter pelo menos 3 exercícios no plano");
        
        System.out.println("✅ Total de exercícios no plano: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarPlanosQueUsamExercicio() {
        System.out.println("=== TESTE: Contar Planos que Usam Exercício ===");
        
        // Criar outros planos
        Aluno aluno2 = alunoRepository.save(new Aluno("Pedro Teste", "333.444.555-66"));
        Instrutor instrutor2 = instrutorRepository.save(new Instrutor("Prof. Lima", "Funcional"));
        PlanoTreino plano2 = planoTreinoRepository.save(new PlanoTreino(aluno2, instrutor2, LocalDate.now()));
        
        // Mesmo exercício em planos diferentes
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(plano2, exercicio1, 3, 10, new BigDecimal("70.0")));
        
        // Contar usos do exercício
        long count = itemTreinoRepository.countByExercicio(exercicio1);
        
        // Validações
        assertTrue(count >= 2, "Exercício deve aparecer em pelo menos 2 planos");
        
        System.out.println("✅ Planos que incluem '" + exercicio1.getNome() + "': " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarSePlanoContemExercicio() {
        System.out.println("=== TESTE: Verificar se Plano Contém Exercício ===");
        
        // Adicionar exercício ao plano
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        
        // Verificar existência
        boolean contem = itemTreinoRepository.existsByPlanoTreinoAndExercicio(planoTreinoTeste, exercicio1);
        boolean naoContem = itemTreinoRepository.existsByPlanoTreinoAndExercicio(planoTreinoTeste, exercicio3);
        
        // Validações
        assertTrue(contem, "Plano deve conter o exercício 1");
        assertFalse(naoContem, "Plano não deve conter o exercício 3");
        
        System.out.println("✅ Verificação de existência funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarItensComObservacoes() {
        System.out.println("=== TESTE: Buscar Itens com Observações ===");
        
        // Criar itens com e sem observações
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0"), "Executar devagar"));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 15, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0"), "Atenção à postura"));
        
        // Buscar itens com observações
        List<ItemTreino> itensComObs = itemTreinoRepository.findByObservacoesIsNotNull();
        
        // Validações
        assertTrue(itensComObs.size() >= 2, "Deve ter pelo menos 2 itens com observações");
        itensComObs.forEach(i -> assertNotNull(i.getObservacoes()));
        
        System.out.println("✅ Itens com observações: " + itensComObs.size());
        itensComObs.forEach(i -> System.out.println("   - " + i.getExercicio().getNome() + ": " + i.getObservacoes()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarCargaMaximaDoPlano() {
        System.out.println("=== TESTE: Buscar Carga Máxima do Plano (Query Custom) ===");
        
        // Criar itens com diferentes cargas
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 10, new BigDecimal("120.0"))); // Maior
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Buscar carga máxima
        BigDecimal cargaMaxima = itemTreinoRepository.buscarCargaMaximaDoPlano(planoTreinoTeste);
        
        // Validações
        assertNotNull(cargaMaxima, "Deve encontrar a carga máxima");
        assertEquals(0, new BigDecimal("120.0").compareTo(cargaMaxima), "Carga máxima deve ser 120.0");
        
        System.out.println("✅ Carga máxima do plano: " + cargaMaxima + "kg");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarExerciciosDoPlano() {
        System.out.println("=== TESTE: Buscar Exercícios do Plano (Query Custom) ===");
        
        // Adicionar exercícios ao plano
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 15, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Buscar exercícios únicos
        List<Exercicio> exercicios = itemTreinoRepository.buscarExerciciosDoPlano(planoTreinoTeste);
        
        // Validações
        assertTrue(exercicios.size() >= 3, "Deve ter pelo menos 3 exercícios únicos");
        
        System.out.println("✅ Exercícios do plano:");
        exercicios.forEach(e -> System.out.println("   - " + e.getNome() + " (" + e.getGrupoMuscular() + ")"));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarItemTreino() {
        System.out.println("=== TESTE: Atualizar Item de Treino ===");
        
        // Criar e salvar item
        ItemTreino item = new ItemTreino(planoTreinoTeste, exercicio1, 3, 10, new BigDecimal("60.0"));
        ItemTreino itemSalvo = itemTreinoRepository.save(item);
        Long id = itemSalvo.getIdItemTreino();
        
        // Atualizar informações (progressão de carga)
        itemSalvo.setSeries(4);
        itemSalvo.setRepeticoes(12);
        itemSalvo.setCarga(new BigDecimal("70.0"));
        itemSalvo.setObservacoes("Progressão de carga - aumentar 10kg");
        itemTreinoRepository.save(itemSalvo);
        
        // Buscar novamente
        Optional<ItemTreino> itemAtualizado = itemTreinoRepository.findById(id);
        
        // Validações
        assertTrue(itemAtualizado.isPresent());
        assertEquals(4, itemAtualizado.get().getSeries());
        assertEquals(12, itemAtualizado.get().getRepeticoes());
        assertEquals(new BigDecimal("70.0"), itemAtualizado.get().getCarga());
        assertNotNull(itemAtualizado.get().getObservacoes());
        
        System.out.println("✅ Item atualizado: " + itemAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarItemTreino() {
        System.out.println("=== TESTE: Deletar Item de Treino ===");
        
        // Criar e salvar item
        ItemTreino item = new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0"));
        ItemTreino itemSalvo = itemTreinoRepository.save(item);
        Long id = itemSalvo.getIdItemTreino();
        
        // Deletar
        itemTreinoRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<ItemTreino> itemDeletado = itemTreinoRepository.findById(id);
        
        // Validações
        assertFalse(itemDeletado.isPresent(), "Item não deve existir após deleção");
        
        System.out.println("✅ Item de treino deletado com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testRelacionamentoNtoN() {
        System.out.println("=== TESTE: Validar Relacionamento N:N ===");
        
        // Criar múltiplos planos e exercícios
        Aluno aluno2 = alunoRepository.save(new Aluno("Ana Teste", "444.555.666-77"));
        Instrutor instrutor2 = instrutorRepository.save(new Instrutor("Prof. Costa", "CrossFit"));
        PlanoTreino plano2 = planoTreinoRepository.save(new PlanoTreino(aluno2, instrutor2, LocalDate.now()));
        
        // Mesmo exercício em planos diferentes
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio1, 4, 12, new BigDecimal("80.0")));
        itemTreinoRepository.save(new ItemTreino(plano2, exercicio1, 3, 10, new BigDecimal("60.0")));
        
        // Exercícios diferentes no mesmo plano
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio2, 3, 15, new BigDecimal("100.0")));
        itemTreinoRepository.save(new ItemTreino(planoTreinoTeste, exercicio3, 3, 12, new BigDecimal("20.0")));
        
        // Validar relacionamento
        List<ItemTreino> itensPlano1 = itemTreinoRepository.findByPlanoTreino(planoTreinoTeste);
        List<ItemTreino> itensExercicio1 = itemTreinoRepository.findByExercicio(exercicio1);
        
        // Validações
        assertTrue(itensPlano1.size() >= 3, "Plano 1 deve ter pelo menos 3 exercícios");
        assertTrue(itensExercicio1.size() >= 2, "Exercício 1 deve estar em pelo menos 2 planos");
        
        System.out.println("✅ Relacionamento N:N validado com sucesso");
        System.out.println("   - Plano 1 tem " + itensPlano1.size() + " exercícios");
        System.out.println("   - Exercício 1 está em " + itensExercicio1.size() + " planos");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
