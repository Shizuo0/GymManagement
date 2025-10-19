package com.example.demo;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.MatriculaRepository;
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
 * Testes para a entidade Matricula e MatriculaRepository
 */
@SpringBootTest
@Transactional
public class MatriculaTest {

    @Autowired
    private MatriculaRepository matriculaRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    @Autowired
    private PlanoRepository planoRepository;
    
    private Aluno alunoTeste;
    private Plano planoTeste;

    @BeforeEach
    public void setUp() {
        // Criar aluno e plano para usar nos testes
        alunoTeste = new Aluno("João Test", "111.111.111-11", LocalDate.now());
        alunoTeste = alunoRepository.save(alunoTeste);
        
        planoTeste = new Plano("Plano Test", new BigDecimal("99.90"), 30);
        planoTeste = planoRepository.save(planoTeste);
    }

    @Test
    public void testCriarMatricula() {
        System.out.println("=== TESTE: Criar Matrícula ===");
        
        // Criar uma nova matrícula
        LocalDate dataInicio = LocalDate.of(2025, 10, 1);
        LocalDate dataFim = LocalDate.of(2025, 10, 31);
        Matricula matricula = new Matricula(alunoTeste, planoTeste, dataInicio, dataFim, "ATIVA");
        
        // Salvar no banco
        Matricula matriculaSalva = matriculaRepository.save(matricula);
        
        // Validações
        assertNotNull(matriculaSalva.getIdMatricula(), "ID da matrícula não deve ser nulo após salvar");
        assertNotNull(matriculaSalva.getAluno());
        assertNotNull(matriculaSalva.getPlano());
        assertEquals("ATIVA", matriculaSalva.getStatus());
        assertEquals(dataInicio, matriculaSalva.getDataInicio());
        assertEquals(dataFim, matriculaSalva.getDataFim());
        
        System.out.println("✅ Matrícula criada: " + matriculaSalva);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculaPorId() {
        System.out.println("=== TESTE: Buscar Matrícula por ID ===");
        
        // Criar e salvar uma matrícula
        Matricula matricula = new Matricula(
            alunoTeste, 
            planoTeste, 
            LocalDate.of(2025, 9, 1), 
            LocalDate.of(2025, 9, 30), 
            "ATIVA"
        );
        Matricula matriculaSalva = matriculaRepository.save(matricula);
        
        // Buscar pelo ID
        Optional<Matricula> matriculaEncontrada = matriculaRepository.findById(matriculaSalva.getIdMatricula());
        
        // Validações
        assertTrue(matriculaEncontrada.isPresent(), "Matrícula deve ser encontrada");
        assertEquals("ATIVA", matriculaEncontrada.get().getStatus());
        
        System.out.println("✅ Matrícula encontrada: " + matriculaEncontrada.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculasPorAluno() {
        System.out.println("=== TESTE: Buscar Matrículas por Aluno ===");
        
        // Criar várias matrículas para o mesmo aluno
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), "INATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 31), "ATIVA"
        ));
        
        // Buscar matrículas do aluno
        List<Matricula> matriculas = matriculaRepository.findByAluno(alunoTeste);
        
        // Validações
        assertTrue(matriculas.size() >= 2, "Deve ter pelo menos 2 matrículas");
        matriculas.forEach(m -> assertEquals(alunoTeste.getIdAluno(), m.getAluno().getIdAluno()));
        
        System.out.println("✅ Matrículas do aluno encontradas: " + matriculas.size());
        matriculas.forEach(m -> System.out.println("   - " + m));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculasPorStatus() {
        System.out.println("=== TESTE: Buscar Matrículas por Status ===");
        
        // Criar alunos e matrículas com diferentes status
        Aluno aluno2 = alunoRepository.save(new Aluno("Maria Test", "222.222.222-22"));
        
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            aluno2, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now().minusDays(60), LocalDate.now().minusDays(30), "INATIVA"
        ));
        
        // Buscar matrículas ativas
        List<Matricula> matriculasAtivas = matriculaRepository.findByStatus("ATIVA");
        
        // Validações
        assertTrue(matriculasAtivas.size() >= 2, "Deve ter pelo menos 2 matrículas ativas");
        matriculasAtivas.forEach(m -> assertEquals("ATIVA", m.getStatus()));
        
        System.out.println("✅ Matrículas ativas encontradas: " + matriculasAtivas.size());
        matriculasAtivas.forEach(m -> System.out.println("   - " + m));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculasPorAlunoEStatus() {
        System.out.println("=== TESTE: Buscar Matrículas por Aluno e Status ===");
        
        // Criar matrículas com diferentes status
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now().minusDays(60), LocalDate.now().minusDays(30), "INATIVA"
        ));
        
        // Buscar matrículas ativas do aluno
        List<Matricula> matriculasAtivas = matriculaRepository.findByAlunoAndStatus(alunoTeste, "ATIVA");
        
        // Validações
        assertTrue(matriculasAtivas.size() >= 1, "Deve ter pelo menos 1 matrícula ativa");
        matriculasAtivas.forEach(m -> {
            assertEquals("ATIVA", m.getStatus());
            assertEquals(alunoTeste.getIdAluno(), m.getAluno().getIdAluno());
        });
        
        System.out.println("✅ Matrículas ativas do aluno: " + matriculasAtivas.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculasPorPlano() {
        System.out.println("=== TESTE: Buscar Matrículas por Plano ===");
        
        // Criar outro plano
        Plano plano2 = planoRepository.save(new Plano("Plano Premium", new BigDecimal("199.90"), 90));
        
        // Criar matrículas em diferentes planos
        Aluno aluno2 = alunoRepository.save(new Aluno("Pedro Test", "333.333.333-33"));
        
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            aluno2, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, plano2, LocalDate.now(), LocalDate.now().plusDays(90), "ATIVA"
        ));
        
        // Buscar matrículas do plano teste
        List<Matricula> matriculasPlanoTeste = matriculaRepository.findByPlano(planoTeste);
        
        // Validações
        assertTrue(matriculasPlanoTeste.size() >= 2, "Deve ter pelo menos 2 matrículas no plano teste");
        matriculasPlanoTeste.forEach(m -> assertEquals(planoTeste.getIdPlanoAssinatura(), m.getPlano().getIdPlanoAssinatura()));
        
        System.out.println("✅ Matrículas do plano encontradas: " + matriculasPlanoTeste.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculasPorDataFim() {
        System.out.println("=== TESTE: Buscar Matrículas por Data de Fim ===");
        
        LocalDate dataFimEspecifica = LocalDate.of(2025, 12, 31);
        
        // Criar matrículas com data de fim específica
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.of(2025, 12, 1), dataFimEspecifica, "ATIVA"
        ));
        
        // Buscar matrículas que vencem nesta data
        List<Matricula> matriculas = matriculaRepository.findByDataFim(dataFimEspecifica);
        
        // Validações
        assertTrue(matriculas.size() >= 1, "Deve ter pelo menos 1 matrícula com esta data de fim");
        matriculas.forEach(m -> assertEquals(dataFimEspecifica, m.getDataFim()));
        
        System.out.println("✅ Matrículas que vencem em " + dataFimEspecifica + ": " + matriculas.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculasPorPeriodo() {
        System.out.println("=== TESTE: Buscar Matrículas por Período ===");
        
        // Criar matrículas com diferentes datas de vencimento
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 15), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.of(2025, 11, 5), LocalDate.of(2025, 11, 20), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 31), "ATIVA"
        ));
        
        // Buscar matrículas que vencem em novembro
        List<Matricula> matriculasNovembro = matriculaRepository.findByDataFimBetween(
            LocalDate.of(2025, 11, 1), 
            LocalDate.of(2025, 11, 30)
        );
        
        // Validações
        assertTrue(matriculasNovembro.size() >= 2, "Deve ter pelo menos 2 matrículas vencendo em novembro");
        
        System.out.println("✅ Matrículas vencendo em novembro: " + matriculasNovembro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarMatriculasAtivas() {
        System.out.println("=== TESTE: Buscar Matrículas Ativas (Query Custom) ===");
        
        // Criar matrículas com diferentes status
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now().minusDays(60), LocalDate.now().minusDays(30), "INATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(15), "SUSPENSA"
        ));
        
        // Buscar matrículas ativas usando query customizada
        List<Matricula> matriculasAtivas = matriculaRepository.findMatriculasAtivas();
        
        // Validações
        assertTrue(matriculasAtivas.size() >= 1, "Deve ter pelo menos 1 matrícula ativa");
        matriculasAtivas.forEach(m -> assertEquals("ATIVA", m.getStatus()));
        
        System.out.println("✅ Matrículas ativas (query custom): " + matriculasAtivas.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarMatriculasPorAluno() {
        System.out.println("=== TESTE: Contar Matrículas por Aluno ===");
        
        // Criar várias matrículas para o aluno
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now().minusDays(90), LocalDate.now().minusDays(60), "INATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now().minusDays(30), LocalDate.now(), "INATIVA"
        ));
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        
        // Contar matrículas
        long count = matriculaRepository.countByAluno(alunoTeste);
        
        // Validações
        assertTrue(count >= 3, "Deve ter pelo menos 3 matrículas");
        
        System.out.println("✅ Total de matrículas do aluno: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaMatriculaAtiva() {
        System.out.println("=== TESTE: Verificar Existência de Matrícula Ativa ===");
        
        // Criar matrícula ativa
        matriculaRepository.save(new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        ));
        
        // Verificar existência
        boolean temMatriculaAtiva = matriculaRepository.existsByAlunoAndStatus(alunoTeste, "ATIVA");
        
        // Criar outro aluno sem matrícula ativa
        Aluno alunoSemMatricula = alunoRepository.save(new Aluno("Sem Matrícula", "444.444.444-44"));
        boolean naoTemMatriculaAtiva = matriculaRepository.existsByAlunoAndStatus(alunoSemMatricula, "ATIVA");
        
        // Validações
        assertTrue(temMatriculaAtiva, "Aluno deve ter matrícula ativa");
        assertFalse(naoTemMatriculaAtiva, "Aluno não deve ter matrícula ativa");
        
        System.out.println("✅ Verificação de matrícula ativa funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarMatricula() {
        System.out.println("=== TESTE: Atualizar Matrícula ===");
        
        // Criar e salvar uma matrícula
        Matricula matricula = new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        );
        Matricula matriculaSalva = matriculaRepository.save(matricula);
        Long id = matriculaSalva.getIdMatricula();
        
        // Atualizar o status
        matriculaSalva.setStatus("SUSPENSA");
        matriculaRepository.save(matriculaSalva);
        
        // Buscar novamente
        Optional<Matricula> matriculaAtualizada = matriculaRepository.findById(id);
        
        // Validações
        assertTrue(matriculaAtualizada.isPresent());
        assertEquals("SUSPENSA", matriculaAtualizada.get().getStatus());
        
        System.out.println("✅ Matrícula atualizada: " + matriculaAtualizada.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarMatricula() {
        System.out.println("=== TESTE: Deletar Matrícula ===");
        
        // Criar e salvar uma matrícula
        Matricula matricula = new Matricula(
            alunoTeste, planoTeste, LocalDate.now(), LocalDate.now().plusDays(30), "ATIVA"
        );
        Matricula matriculaSalva = matriculaRepository.save(matricula);
        Long id = matriculaSalva.getIdMatricula();
        
        // Deletar
        matriculaRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<Matricula> matriculaDeletada = matriculaRepository.findById(id);
        
        // Validações
        assertFalse(matriculaDeletada.isPresent(), "Matrícula não deve existir após deleção");
        
        System.out.println("✅ Matrícula deletada com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
