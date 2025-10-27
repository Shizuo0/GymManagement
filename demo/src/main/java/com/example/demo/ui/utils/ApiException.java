package com.example.demo.ui.utils;

/**
 * Exceção customizada para erros de comunicação com a API.
 * Encapsula informações sobre erros HTTP e problemas de rede.
 */
public class ApiException extends Exception {
    
    private final int statusCode;
    private final String errorMessage;
    
    /**
     * Construtor completo
     * 
     * @param message Mensagem de erro principal
     * @param statusCode Código HTTP de status (0 para erros de conexão)
     * @param errorMessage Mensagem de erro detalhada do servidor
     */
    public ApiException(String message, int statusCode, String errorMessage) {
        super(message);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
    
    /**
     * Construtor simples
     * 
     * @param message Mensagem de erro
     */
    public ApiException(String message) {
        this(message, 0, message);
    }
    
    /**
     * Construtor com causa
     * 
     * @param message Mensagem de erro
     * @param cause Causa raiz
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.errorMessage = message;
    }
    
    /**
     * Retorna o código HTTP de status
     * 
     * @return Código de status (0 se não for erro HTTP)
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Retorna a mensagem de erro detalhada
     * 
     * @return Mensagem de erro
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Verifica se é um erro de conexão (sem resposta do servidor)
     * 
     * @return true se for erro de conexão
     */
    public boolean isConnectionError() {
        return statusCode == 0;
    }
    
    /**
     * Verifica se é um erro de validação (400)
     * 
     * @return true se for erro de validação
     */
    public boolean isValidationError() {
        return statusCode == 400;
    }
    
    /**
     * Verifica se é um erro de não encontrado (404)
     * 
     * @return true se o recurso não foi encontrado
     */
    public boolean isNotFoundError() {
        return statusCode == 404;
    }
    
    /**
     * Verifica se é um erro de servidor (500+)
     * 
     * @return true se for erro interno do servidor
     */
    public boolean isServerError() {
        return statusCode >= 500;
    }
    
    /**
     * Retorna uma mensagem de erro amigável para exibir ao usuário
     * 
     * @return Mensagem formatada
     */
    public String getUserFriendlyMessage() {
        if (isConnectionError()) {
            return "Não foi possível conectar ao servidor.\nVerifique sua conexão e tente novamente.";
        } else if (isValidationError()) {
            return "Dados inválidos:\n" + errorMessage;
        } else if (isNotFoundError()) {
            return "Registro não encontrado.";
        } else if (isServerError()) {
            return "Erro no servidor.\nTente novamente mais tarde.";
        } else {
            return "Erro ao processar requisição:\n" + errorMessage;
        }
    }
    
    @Override
    public String toString() {
        return String.format("ApiException[statusCode=%d, message=%s, errorMessage=%s]",
                statusCode, getMessage(), errorMessage);
    }
}
