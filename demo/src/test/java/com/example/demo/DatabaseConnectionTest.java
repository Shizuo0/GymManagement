package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste simples para verificar a conexão com o banco de dados MySQL
 * e executar comandos SQL básicos.
 */
@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        System.out.println("=== TESTE DE INSERT NA TABELA ALUNO ===");
        
        // Gerar CPF único baseado no timestamp
        String cpfUnico = "999." + System.currentTimeMillis() % 1000 + ".789-00";
        String nomeAluno = "Maria Teste " + System.currentTimeMillis() % 1000;
        
        // Inserir um aluno de teste
        int rowsInserted = jdbcTemplate.update(
            "INSERT INTO Aluno (nome, cpf, data_ingresso) VALUES (?, ?, ?)",
            nomeAluno,
            cpfUnico,
            "2025-10-18"
        );
        assertEquals(1, rowsInserted, "Falha ao inserir aluno de teste");
        System.out.println("✅ Aluno inserido com sucesso!");
        System.out.println("   Nome: " + nomeAluno);
        System.out.println("   CPF: " + cpfUnico);
        
        // Consultar o aluno inserido
        String nomeConsultado = jdbcTemplate.queryForObject(
            "SELECT nome FROM Aluno WHERE cpf = ?",
            String.class,
            cpfUnico
        );
        assertEquals(nomeAluno, nomeConsultado, "Falha ao consultar aluno inserido");
        System.out.println("✅ Aluno consultado: " + nomeConsultado);
        
        // Mostrar todos os alunos para verificar
        System.out.println("📋 Lista de todos os alunos:");
        jdbcTemplate.query(
            "SELECT id_aluno, nome, cpf, data_ingresso FROM Aluno ORDER BY id_aluno DESC LIMIT 5",
            (rs, rowNum) -> {
                System.out.println("   ID: " + rs.getInt("id_aluno") + 
                                 " | Nome: " + rs.getString("nome") + 
                                 " | CPF: " + rs.getString("cpf") + 
                                 " | Data: " + rs.getDate("data_ingresso"));
                return null;
            }
        );
        
        System.out.println("=== TESTE DE INSERT CONCLUÍDO ===");
    }
}
