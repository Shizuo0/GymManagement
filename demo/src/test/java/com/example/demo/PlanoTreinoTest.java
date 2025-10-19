package com.example.demo;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Instrutor;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.InstrutorRepository;
import com.example.demo.repository.PlanoTreinoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade PlanoTreino e PlanoTreinoRepository
 */
@SpringBootTest
@Transactional
public class PlanoTreinoTest {

    @Autowired
    private PlanoTreinoRepository planoTreinoRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private InstrutorRepository instrutorRepository;
    
    private Aluno alunoTeste;
    private Instrutor instrutorTeste;

    @BeforeEach
    public void setUp() {
        // Criar aluno e instrutor para usar nos testes
        alunoTeste = new Aluno("Maria Treino", "505.606.707-80", LocalDate.now());
        alunoTeste = alunoRepository.save(alunoTeste);
        
        instrutorTeste = new Instrutor("Carlos Personal", "Musculação");
        instrutorTeste = instrutorRepository.save(instrutorTeste);
    }

    @Test
    public void testCriarPlanoTreinoCompleto() {
        System.out.println("=== TESTE: Criar Plano de Treino Completo ===");
        
        // Criar plano de treino completo
        PlanoTreino planoTreino = new PlanoTreino(
            alunoTeste, 
            instrutorTeste, 
            LocalDate.of(2025, 10, 1),
            "Treino ABC - Foco em hipertrofia",
            12
        );
        
        // Salvar no banco
        PlanoTreino planoSalvo = planoTreinoRepository.save(planoTreino);
        
        // Validações
        assertNotNull(planoSalvo.getIdPlanoTreino(), "ID do plano não deve ser nulo após salvar");
        assertNotNull(planoSalvo.getAluno());
        assertNotNull(planoSalvo.getInstrutor());
        assertEquals(LocalDate.of(2025, 10, 1), planoSalvo.getDataCriacao());
        assertEquals("Treino ABC - Foco em hipertrofia", planoSalvo.getDescricao());
        assertEquals(12, planoSalvo.getDuracaoSemanas());
        
        System.out.println("✅ Plano de treino completo criado: " + planoSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCriarPlanoTreinoSimples() {
        System.out.println("=== TESTE: Criar Plano de Treino Simples ===");
        
        // Criar plano de treino apenas com dados obrigatórios
        PlanoTreino planoTreino = new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now());
        
        // Salvar no banco
        PlanoTreino planoSalvo = planoTreinoRepository.save(planoTreino);
        
        // Validações
        assertNotNull(planoSalvo.getIdPlanoTreino());
        assertNotNull(planoSalvo.getAluno());
        assertNotNull(planoSalvo.getInstrutor());
        assertNotNull(planoSalvo.getDataCriacao());
        
        System.out.println("✅ Plano de treino simples criado: " + planoSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanoTreinoPorId() {
        System.out.println("=== TESTE: Buscar Plano de Treino por ID ===");
        
        // Criar e salvar plano
        PlanoTreino planoTreino = new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Treino iniciante", 8);
        PlanoTreino planoSalvo = planoTreinoRepository.save(planoTreino);
        
        // Buscar pelo ID
        Optional<PlanoTreino> planoEncontrado = planoTreinoRepository.findById(planoSalvo.getIdPlanoTreino());
        
        // Validações
        assertTrue(planoEncontrado.isPresent(), "Plano deve ser encontrado");
        assertEquals("Treino iniciante", planoEncontrado.get().getDescricao());
        assertEquals(8, planoEncontrado.get().getDuracaoSemanas());
        
        System.out.println("✅ Plano de treino encontrado: " + planoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorAluno() {
        System.out.println("=== TESTE: Buscar Planos por Aluno ===");
        
        // Criar vários planos para o aluno
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 1), "Treino 1", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 15), "Treino 2", 12));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 11, 1), "Treino 3", 10));
        
        // Buscar planos do aluno
        List<PlanoTreino> planos = planoTreinoRepository.findByAluno(alunoTeste);
        
        // Validações
        assertTrue(planos.size() >= 3, "Deve ter pelo menos 3 planos");
        planos.forEach(p -> assertEquals(alunoTeste.getIdAluno(), p.getAluno().getIdAluno()));
        
        System.out.println("✅ Planos do aluno encontrados: " + planos.size());
        planos.forEach(p -> System.out.println("   - " + p.getDescricao() + " (" + p.getDuracaoSemanas() + " semanas)"));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorInstrutor() {
        System.out.println("=== TESTE: Buscar Planos por Instrutor ===");
        
        // Criar outro aluno
        Aluno aluno2 = alunoRepository.save(new Aluno("João Silva", "606.707.808-90"));
        
        // Criar planos para diferentes alunos mas mesmo instrutor
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Plano A", 8));
        planoTreinoRepository.save(new PlanoTreino(aluno2, instrutorTeste, LocalDate.now(), "Plano B", 10));
        
        // Buscar planos do instrutor
        List<PlanoTreino> planos = planoTreinoRepository.findByInstrutor(instrutorTeste);
        
        // Validações
        assertTrue(planos.size() >= 2, "Deve ter pelo menos 2 planos");
        planos.forEach(p -> assertEquals(instrutorTeste.getIdInstrutor(), p.getInstrutor().getIdInstrutor()));
        
        System.out.println("✅ Planos do instrutor encontrados: " + planos.size());
        planos.forEach(p -> System.out.println("   - Aluno: " + p.getAluno().getNome() + " | " + p.getDescricao()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorAlunoEInstrutor() {
        System.out.println("=== TESTE: Buscar Planos por Aluno e Instrutor ===");
        
        // Criar outro instrutor
        Instrutor instrutor2 = instrutorRepository.save(new Instrutor("Ana Personal", "Pilates"));
        
        // Criar planos com diferentes instrutores
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Musculação", 12));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutor2, LocalDate.now(), "Pilates", 8));
        
        // Buscar planos do aluno com o instrutor específico
        List<PlanoTreino> planos = planoTreinoRepository.findByAlunoAndInstrutor(alunoTeste, instrutorTeste);
        
        // Validações
        assertTrue(planos.size() >= 1, "Deve ter pelo menos 1 plano");
        planos.forEach(p -> {
            assertEquals(alunoTeste.getIdAluno(), p.getAluno().getIdAluno());
            assertEquals(instrutorTeste.getIdInstrutor(), p.getInstrutor().getIdInstrutor());
        });
        
        System.out.println("✅ Planos do aluno com instrutor específico: " + planos.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorDataCriacao() {
        System.out.println("=== TESTE: Buscar Planos por Data de Criação ===");
        
        LocalDate dataEspecifica = LocalDate.of(2025, 10, 20);
        
        // Criar planos na mesma data
        Aluno aluno2 = alunoRepository.save(new Aluno("Pedro Teste", "707.808.909-00"));
        
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, dataEspecifica, "Treino 1", 10));
        planoTreinoRepository.save(new PlanoTreino(aluno2, instrutorTeste, dataEspecifica, "Treino 2", 8));
        
        // Buscar planos da data
        List<PlanoTreino> planos = planoTreinoRepository.findByDataCriacao(dataEspecifica);
        
        // Validações
        assertTrue(planos.size() >= 2, "Deve ter pelo menos 2 planos");
        planos.forEach(p -> assertEquals(dataEspecifica, p.getDataCriacao()));
        
        System.out.println("✅ Planos criados em " + dataEspecifica + ": " + planos.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorPeriodo() {
        System.out.println("=== TESTE: Buscar Planos por Período ===");
        
        // Criar planos em diferentes datas
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 5), "Outubro 1", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 15), "Outubro 2", 10));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 25), "Outubro 3", 12));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 11, 5), "Novembro", 8));
        
        // Buscar planos de outubro
        List<PlanoTreino> planosOutubro = planoTreinoRepository.findByDataCriacaoBetween(
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(planosOutubro.size() >= 3, "Deve ter pelo menos 3 planos em outubro");
        planosOutubro.forEach(p -> assertEquals(10, p.getDataCriacao().getMonthValue()));
        
        System.out.println("✅ Planos criados em outubro: " + planosOutubro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorDuracao() {
        System.out.println("=== TESTE: Buscar Planos por Duração ===");
        
        // Criar planos com diferentes durações
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Treino 8 semanas", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Treino 12 semanas", 12));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Treino 8 semanas 2", 8));
        
        // Buscar planos de 8 semanas
        List<PlanoTreino> planos8Semanas = planoTreinoRepository.findByDuracaoSemanas(8);
        
        // Validações
        assertTrue(planos8Semanas.size() >= 2, "Deve ter pelo menos 2 planos de 8 semanas");
        planos8Semanas.forEach(p -> assertEquals(8, p.getDuracaoSemanas()));
        
        System.out.println("✅ Planos de 8 semanas: " + planos8Semanas.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosAlunoOrdenados() {
        System.out.println("=== TESTE: Buscar Planos do Aluno Ordenados ===");
        
        // Criar planos em ordem não sequencial
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 15), "Treino 2", 10));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 5), "Treino 1", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 25), "Treino 3", 12));
        
        // Buscar ordenados
        List<PlanoTreino> planosOrdenados = planoTreinoRepository.findByAlunoOrderByDataCriacaoDesc(alunoTeste);
        
        // Validações
        assertTrue(planosOrdenados.size() >= 3, "Deve ter pelo menos 3 planos");
        
        // Verificar ordenação decrescente
        for (int i = 0; i < planosOrdenados.size() - 1; i++) {
            assertTrue(
                planosOrdenados.get(i).getDataCriacao()
                    .compareTo(planosOrdenados.get(i + 1).getDataCriacao()) >= 0,
                "Planos devem estar ordenados por data decrescente"
            );
        }
        
        System.out.println("✅ Planos ordenados (mais recentes primeiro):");
        planosOrdenados.forEach(p -> System.out.println("   - " + p.getDataCriacao() + " | " + p.getDescricao()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosInstrutorOrdenados() {
        System.out.println("=== TESTE: Buscar Planos do Instrutor Ordenados ===");
        
        Aluno aluno2 = alunoRepository.save(new Aluno("Ana Silva", "808.909.010-11"));
        
        // Criar planos do instrutor para diferentes alunos
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 10), "Plano 1", 8));
        planoTreinoRepository.save(new PlanoTreino(aluno2, instrutorTeste, LocalDate.of(2025, 10, 20), "Plano 2", 10));
        
        // Buscar ordenados
        List<PlanoTreino> planosOrdenados = planoTreinoRepository.findByInstrutorOrderByDataCriacaoDesc(instrutorTeste);
        
        // Validações
        assertTrue(planosOrdenados.size() >= 2, "Deve ter pelo menos 2 planos");
        planosOrdenados.forEach(p -> assertEquals(instrutorTeste.getIdInstrutor(), p.getInstrutor().getIdInstrutor()));
        
        System.out.println("✅ Planos do instrutor ordenados: " + planosOrdenados.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanoMaisRecenteDoAluno() {
        System.out.println("=== TESTE: Buscar Plano Mais Recente do Aluno ===");
        
        // Criar planos com diferentes datas
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 5), "Antigo", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 25), "Mais recente", 12));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 15), "Intermediário", 10));
        
        // Buscar mais recente
        Optional<PlanoTreino> planoRecente = planoTreinoRepository.findFirstByAlunoOrderByDataCriacaoDesc(alunoTeste);
        
        // Validações
        assertTrue(planoRecente.isPresent(), "Deve encontrar o plano mais recente");
        assertEquals("Mais recente", planoRecente.get().getDescricao());
        assertEquals(LocalDate.of(2025, 10, 25), planoRecente.get().getDataCriacao());
        
        System.out.println("✅ Plano mais recente: " + planoRecente.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarPlanosDoAluno() {
        System.out.println("=== TESTE: Contar Planos do Aluno ===");
        
        // Criar planos
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Plano 1", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Plano 2", 10));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Plano 3", 12));
        
        // Contar planos
        long count = planoTreinoRepository.countByAluno(alunoTeste);
        
        // Validações
        assertTrue(count >= 3, "Deve ter pelo menos 3 planos");
        
        System.out.println("✅ Total de planos do aluno: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarPlanosDoInstrutor() {
        System.out.println("=== TESTE: Contar Planos do Instrutor ===");
        
        Aluno aluno2 = alunoRepository.save(new Aluno("Carlos Teste", "909.010.111-22"));
        
        // Criar planos para diferentes alunos
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Plano A", 8));
        planoTreinoRepository.save(new PlanoTreino(aluno2, instrutorTeste, LocalDate.now(), "Plano B", 10));
        
        // Contar planos
        long count = planoTreinoRepository.countByInstrutor(instrutorTeste);
        
        // Validações
        assertTrue(count >= 2, "Deve ter pelo menos 2 planos");
        
        System.out.println("✅ Total de planos do instrutor: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaPlano() {
        System.out.println("=== TESTE: Verificar Existência de Plano ===");
        
        LocalDate dataComPlano = LocalDate.of(2025, 10, 20);
        LocalDate dataSemPlano = LocalDate.of(2025, 12, 25);
        
        // Criar plano
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, dataComPlano, "Treino", 8));
        
        // Verificar existência
        boolean existe = planoTreinoRepository.existsByAlunoAndDataCriacao(alunoTeste, dataComPlano);
        boolean naoExiste = planoTreinoRepository.existsByAlunoAndDataCriacao(alunoTeste, dataSemPlano);
        
        // Validações
        assertTrue(existe, "Deve existir plano na data " + dataComPlano);
        assertFalse(naoExiste, "Não deve existir plano na data " + dataSemPlano);
        
        System.out.println("✅ Verificação de existência funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorDuracaoMinima() {
        System.out.println("=== TESTE: Buscar Planos por Duração Mínima ===");
        
        // Criar planos com diferentes durações
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Curto", 4));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Médio", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Longo", 12));
        
        // Buscar planos com duração >= 8 semanas
        List<PlanoTreino> planosLongos = planoTreinoRepository.findByDuracaoSemanasGreaterThanEqual(8);
        
        // Validações
        assertTrue(planosLongos.size() >= 2, "Deve ter pelo menos 2 planos longos");
        planosLongos.forEach(p -> assertTrue(p.getDuracaoSemanas() >= 8));
        
        System.out.println("✅ Planos com duração >= 8 semanas: " + planosLongos.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorDuracaoMaxima() {
        System.out.println("=== TESTE: Buscar Planos por Duração Máxima ===");
        
        // Criar planos com diferentes durações
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Curto 1", 4));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Curto 2", 6));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Longo", 12));
        
        // Buscar planos com duração <= 6 semanas
        List<PlanoTreino> planosCurtos = planoTreinoRepository.findByDuracaoSemanasLessThanEqual(6);
        
        // Validações
        assertTrue(planosCurtos.size() >= 2, "Deve ter pelo menos 2 planos curtos");
        planosCurtos.forEach(p -> assertTrue(p.getDuracaoSemanas() <= 6));
        
        System.out.println("✅ Planos com duração <= 6 semanas: " + planosCurtos.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosAlunoNoPeriodoCustomQuery() {
        System.out.println("=== TESTE: Buscar Planos do Aluno no Período (Query Custom) ===");
        
        // Criar planos em diferentes períodos
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 5), "Outubro 1", 8));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 20), "Outubro 2", 10));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 11, 5), "Novembro", 12));
        
        // Buscar planos de outubro usando query customizada
        List<PlanoTreino> planosOutubro = planoTreinoRepository.buscarPlanosAlunoNoPeriodo(
            alunoTeste,
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(planosOutubro.size() >= 2, "Deve ter pelo menos 2 planos em outubro");
        planosOutubro.forEach(p -> {
            assertEquals(alunoTeste.getIdAluno(), p.getAluno().getIdAluno());
            assertEquals(10, p.getDataCriacao().getMonthValue());
        });
        
        System.out.println("✅ Planos do aluno em outubro: " + planosOutubro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosInstrutorNoPeriodoCustomQuery() {
        System.out.println("=== TESTE: Buscar Planos do Instrutor no Período (Query Custom) ===");
        
        Aluno aluno2 = alunoRepository.save(new Aluno("Fernanda Teste", "010.111.212-33"));
        
        // Criar planos do instrutor para diferentes alunos em outubro
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 10, 10), "Plano 1", 8));
        planoTreinoRepository.save(new PlanoTreino(aluno2, instrutorTeste, LocalDate.of(2025, 10, 20), "Plano 2", 10));
        planoTreinoRepository.save(new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.of(2025, 11, 5), "Plano 3", 12));
        
        // Buscar planos do instrutor em outubro
        List<PlanoTreino> planosOutubro = planoTreinoRepository.buscarPlanosInstrutorNoPeriodo(
            instrutorTeste,
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(planosOutubro.size() >= 2, "Deve ter pelo menos 2 planos em outubro");
        planosOutubro.forEach(p -> {
            assertEquals(instrutorTeste.getIdInstrutor(), p.getInstrutor().getIdInstrutor());
            assertEquals(10, p.getDataCriacao().getMonthValue());
        });
        
        System.out.println("✅ Planos do instrutor em outubro: " + planosOutubro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarPlanoTreino() {
        System.out.println("=== TESTE: Atualizar Plano de Treino ===");
        
        // Criar e salvar plano
        PlanoTreino planoTreino = new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Treino inicial", 8);
        PlanoTreino planoSalvo = planoTreinoRepository.save(planoTreino);
        Long id = planoSalvo.getIdPlanoTreino();
        
        // Atualizar informações
        planoSalvo.setDescricao("Treino atualizado - Foco em força");
        planoSalvo.setDuracaoSemanas(12);
        planoTreinoRepository.save(planoSalvo);
        
        // Buscar novamente
        Optional<PlanoTreino> planoAtualizado = planoTreinoRepository.findById(id);
        
        // Validações
        assertTrue(planoAtualizado.isPresent());
        assertEquals("Treino atualizado - Foco em força", planoAtualizado.get().getDescricao());
        assertEquals(12, planoAtualizado.get().getDuracaoSemanas());
        
        System.out.println("✅ Plano atualizado: " + planoAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarPlanoTreino() {
        System.out.println("=== TESTE: Deletar Plano de Treino ===");
        
        // Criar e salvar plano
        PlanoTreino planoTreino = new PlanoTreino(alunoTeste, instrutorTeste, LocalDate.now(), "Treino temporário", 8);
        PlanoTreino planoSalvo = planoTreinoRepository.save(planoTreino);
        Long id = planoSalvo.getIdPlanoTreino();
        
        // Deletar
        planoTreinoRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<PlanoTreino> planoDeletado = planoTreinoRepository.findById(id);
        
        // Validações
        assertFalse(planoDeletado.isPresent(), "Plano não deve existir após deleção");
        
        System.out.println("✅ Plano de treino deletado com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
