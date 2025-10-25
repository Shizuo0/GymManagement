package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	@Autowired(required = false)
	private DataSource dataSource;

	@Autowired(required = false)
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (dataSource != null) {
			testDatabaseConnection();
		}
	}

	private void testDatabaseConnection() {
		System.out.println("=== TESTE DE CONEXAO COM BANCO DE DADOS ===");
		
		try (Connection connection = dataSource.getConnection()) {
			System.out.println("[OK] Conexao com o banco de dados estabelecida com sucesso!");
			System.out.println("     URL do banco: " + connection.getMetaData().getURL());
			System.out.println("     Usuario: " + connection.getMetaData().getUserName());
			System.out.println("     Driver: " + connection.getMetaData().getDriverName());
			System.out.println("     Versao do driver: " + connection.getMetaData().getDriverVersion());
			
			// Teste de query simples
			String result = jdbcTemplate.queryForObject("SELECT 'Conexao funcionando!' as status", String.class);
			System.out.println("     Resultado do teste: " + result);
			
			// Verificar tipo de banco
			String databaseProductName = connection.getMetaData().getDatabaseProductName();
			System.out.println("     Tipo de banco: " + databaseProductName);
			
			if (databaseProductName.toLowerCase().contains("h2")) {
				System.out.println("\n[INFO] Dica: Acesse o console H2 em: http://localhost:8081/h2-console");
				System.out.println("       JDBC URL: jdbc:h2:mem:testdb");
				System.out.println("       Usuario: sa | Senha: (vazio)");
			}
			
		} catch (SQLException e) {
			System.err.println("[ERRO] Falha ao conectar com o banco de dados:");
			System.err.println("       Codigo do erro: " + e.getErrorCode());
			System.err.println("       Mensagem: " + e.getMessage());
			System.err.println("       SQL State: " + e.getSQLState());
			System.err.println("\n[INFO] Dicas para resolver:");
			System.err.println("       1. Verifique se o MySQL esta rodando");
			System.err.println("       2. Execute com perfil H2: --spring.profiles.active=h2");
			System.err.println("       3. Verifique as credenciais no application.properties");
		}
		
		System.out.println("=== FIM DO TESTE ===");
	}
}
