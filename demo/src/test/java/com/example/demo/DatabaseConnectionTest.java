package com.example.demo;

import com.example.demo.entity.Aluno;
import com.example.demo.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste simples para verificar a conexão com o banco de dados MySQL
 * e executar comandos SQL básicos.
 */
@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private AlunoRepository alunoRepository;

    @Test
    public void testDatabaseConnection() {
        System.out.println("=== TESTE DE CONEXÃO COM BANCO DE DADOS ===");
        
        // Teste 1: Verificar se consegue conectar
        assertDoesNotThrow(() -> {
            String result = jdbcTemplate.queryForObject("SELECT 'Conexão funcionando!' as status", String.class);
            System.out.println("✅ Conexão estabelecida: " + result);
        }, "Falha ao conectar com o banco de dados");
        
        // Teste 2: Verificar informações do banco
        String databaseName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        System.out.println("📊 Banco atual: " + databaseName);
        assertEquals("sistema_gestao_academia", databaseName, "Nome do banco incorreto");
        
        // Teste 3: Verificar se as tabelas existem (comando simples)
        Integer tableCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ?", 
            Integer.class, 
            "sistema_gestao_academia"
        );
        System.out.println("📋 Número de tabelas encontradas: " + tableCount);
        assertTrue(tableCount >= 0, "Erro ao contar tabelas");
        
        // Teste 4: Listar algumas tabelas (se existirem)
        try {
            jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = ? LIMIT 5",
                String.class,
                "sistema_gestao_academia"
            ).forEach(table -> System.out.println("   - Tabela: " + table));
        } catch (Exception e) {
            System.out.println("ℹ️  Nenhuma tabela encontrada ainda (normal se o banco estiver vazio)");
        }
        
        System.out.println("=== TESTE CONCLUÍDO COM SUCESSO ===");
    }
    
    @Test
    public void testInsertInAlunoTable() {
        System.out.println("=== TESTE DE INSERT NA TABELA ALUNO (JPA) ===");
        
        // Gerar CPF único baseado no timestamp
        String cpfUnico = "999." + System.currentTimeMillis() % 1000 + ".789-00";
        String nomeAluno = "Maria Teste " + System.currentTimeMillis() % 1000;
        
        // Criar um novo aluno usando JPA
        Aluno novoAluno = new Aluno(nomeAluno, cpfUnico, LocalDate.of(2025, 10, 18));
        
        // Salvar o aluno no banco
        Aluno alunoSalvo = alunoRepository.save(novoAluno);
        assertNotNull(alunoSalvo.getIdAluno(), "Falha ao inserir aluno de teste");
        System.out.println("✅ Aluno inserido com sucesso!");
        System.out.println("   ID: " + alunoSalvo.getIdAluno());
        System.out.println("   Nome: " + alunoSalvo.getNome());
        System.out.println("   CPF: " + alunoSalvo.getCpf());
        
        // Consultar o aluno inserido pelo CPF
        Aluno alunoConsultado = alunoRepository.findByCpf(cpfUnico)
                .orElseThrow(() -> new AssertionError("Aluno não encontrado"));
        assertEquals(nomeAluno, alunoConsultado.getNome(), "Falha ao consultar aluno inserido");
        System.out.println("✅ Aluno consultado: " + alunoConsultado.getNome());
        
        // Mostrar todos os alunos para verificar
        System.out.println("📋 Lista de todos os alunos:");
        List<Aluno> alunos = alunoRepository.findAll();
        System.out.println("   Total de alunos: " + alunos.size());
        alunos.stream()
              .limit(5)
              .forEach(aluno -> System.out.println("   " + aluno));
        
        System.out.println("=== TESTE DE INSERT CONCLUÍDO ===");
    }
}
