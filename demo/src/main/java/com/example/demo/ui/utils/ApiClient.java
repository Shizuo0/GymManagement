package com.example.demo.ui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Cliente HTTP para comunicação com o backend REST.
 * Gerencia todas as requisições HTTP (GET, POST, PUT, DELETE).
 */
public class ApiClient {
    
    private final String baseUrl;
    private final ObjectMapper objectMapper;
    private static final int TIMEOUT = 10000; // 10 segundos
    
    /**
     * Construtor padrão - conecta ao backend local
     */
    public ApiClient() {
        this("http://localhost:8080/api");
    }
    
    /**
     * Construtor com URL customizada
     * 
     * @param baseUrl URL base do backend
     */
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    /**
     * Realiza uma requisição GET
     * 
     * @param endpoint Endpoint da API (ex: "/alunos")
     * @return Resposta JSON como String
     * @throws ApiException em caso de erro na requisição
     */
    public String get(String endpoint) throws ApiException {
        return executeRequest("GET", endpoint, null);
    }
    
    /**
     * Realiza uma requisição POST
     * 
     * @param endpoint Endpoint da API
     * @param body Corpo da requisição (objeto será convertido para JSON)
     * @return Resposta JSON como String
     * @throws ApiException em caso de erro na requisição
     */
    public String post(String endpoint, Object body) throws ApiException {
        return executeRequest("POST", endpoint, body);
    }
    
    /**
     * Realiza uma requisição PUT
     * 
     * @param endpoint Endpoint da API
     * @param body Corpo da requisição (objeto será convertido para JSON)
     * @return Resposta JSON como String
     * @throws ApiException em caso de erro na requisição
     */
    public String put(String endpoint, Object body) throws ApiException {
        return executeRequest("PUT", endpoint, body);
    }
    
    /**
     * Realiza uma requisição DELETE
     * 
     * @param endpoint Endpoint da API
     * @return Resposta JSON como String (pode ser vazio)
     * @throws ApiException em caso de erro na requisição
     */
    public String delete(String endpoint) throws ApiException {
        return executeRequest("DELETE", endpoint, null);
    }
    
    /**
     * Executa uma requisição HTTP
     * 
     * @param method Método HTTP (GET, POST, PUT, DELETE)
     * @param endpoint Endpoint da API
     * @param body Corpo da requisição (opcional)
     * @return Resposta como String
     * @throws ApiException em caso de erro
     */
    private String executeRequest(String method, String endpoint, Object body) throws ApiException {
        HttpURLConnection connection = null;
        
        try {
            // Cria conexão
            @SuppressWarnings("deprecation")
            URL url = new URL(baseUrl + endpoint);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);
            
            // Envia corpo da requisição se necessário
            if (body != null && (method.equals("POST") || method.equals("PUT"))) {
                connection.setDoOutput(true);
                String jsonBody = objectMapper.writeValueAsString(body);
                
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }
            
            // Lê resposta
            int responseCode = connection.getResponseCode();
            
            if (responseCode >= 200 && responseCode < 300) {
                return readResponse(connection);
            } else {
                String errorMessage = readErrorResponse(connection);
                throw new ApiException(
                    "Erro na requisição: " + responseCode + " - " + errorMessage,
                    responseCode,
                    errorMessage
                );
            }
            
        } catch (IOException e) {
            throw new ApiException(
                "Erro de conexão com o servidor: " + e.getMessage(),
                0,
                e.getMessage()
            );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Lê a resposta bem-sucedida da conexão
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        StringBuilder response = new StringBuilder();
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }
        
        return response.toString();
    }
    
    /**
     * Lê a mensagem de erro da conexão
     */
    private String readErrorResponse(HttpURLConnection connection) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        } catch (Exception e) {
            return "Não foi possível ler a mensagem de erro";
        }
    }
    
    /**
     * Converte JSON para objeto
     * 
     * @param json String JSON
     * @param clazz Classe do objeto
     * @return Objeto deserializado
     * @throws ApiException em caso de erro na conversão
     */
    public <T> T fromJson(String json, Class<T> clazz) throws ApiException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new ApiException("Erro ao converter JSON: " + e.getMessage(), 0, e.getMessage());
        }
    }
    
    /**
     * Converte JSON array para lista
     * 
     * @param json String JSON array
     * @param clazz Classe dos elementos
     * @return Lista de objetos
     * @throws ApiException em caso de erro na conversão
     */
    public <T> java.util.List<T> fromJsonArray(String json, Class<T> clazz) throws ApiException {
        try {
            return objectMapper.readValue(json, 
                objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, clazz));
        } catch (IOException e) {
            throw new ApiException("Erro ao converter JSON array: " + e.getMessage(), 0, e.getMessage());
        }
    }
    
    /**
     * Testa a conexão com o backend
     * 
     * @return true se conectado, false caso contrário
     */
    public boolean testConnection() {
        try {
            get("/");
            return true;
        } catch (ApiException e) {
            return false;
        }
    }
}
