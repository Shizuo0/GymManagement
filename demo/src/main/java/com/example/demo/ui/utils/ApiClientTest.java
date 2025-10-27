package com.example.demo.ui.utils;

/**
 * Teste rápido do ApiClient para debug de conexão
 */
public class ApiClientTest {
    
    public static void main(String[] args) {
        System.out.println("=== TESTE DE CONEXÃO API ===");
        System.out.println();
        
        ApiClient client = new ApiClient();
        
        System.out.println("1. Testando conexão com /alunos...");
        try {
            String response = client.get("/alunos");
            System.out.println("[OK] SUCESSO! Resposta: " + response.substring(0, Math.min(100, response.length())) + "...");
        } catch (Exception e) {
            System.err.println("[ERRO] ERRO: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println("2. Testando conexão com /planos...");
        try {
            String response = client.get("/planos");
            System.out.println("[OK] SUCESSO! Resposta: " + response.substring(0, Math.min(100, response.length())) + "...");
        } catch (Exception e) {
            System.err.println("[ERRO] ERRO: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println("=== FIM DO TESTE ===");
    }
}
