package com.example.demo.exception;

/**
 * Classe base para exceções relacionadas ao histórico do aluno
 */
public class HistoricoException extends RuntimeException {
    
    public HistoricoException(String message) {
        super(message);
    }
    
    public HistoricoException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Exceção lançada quando o histórico não é encontrado
     */
    public static class HistoricoNotFoundException extends HistoricoException {
        public HistoricoNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há dados corrompidos no histórico
     */
    public static class DadosCorruptosException extends HistoricoException {
        public DadosCorruptosException(String message) {
            super(message);
        }
        
        public DadosCorruptosException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exceção lançada quando há falha na integridade relacional dos dados
     */
    public static class IntegridadeDadosException extends HistoricoException {
        public IntegridadeDadosException(String message) {
            super(message);
        }
        
        public IntegridadeDadosException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exceção lançada quando há dados ausentes necessários para o histórico
     */
    public static class DadosAusentesException extends HistoricoException {
        public DadosAusentesException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há erro ao agregar dados de múltiplas tabelas
     */
    public static class ErroAgregacaoException extends HistoricoException {
        public ErroAgregacaoException(String message) {
            super(message);
        }
        
        public ErroAgregacaoException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exceção lançada quando o período solicitado é inválido
     */
    public static class PeriodoInvalidoException extends HistoricoException {
        public PeriodoInvalidoException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando não há dados disponíveis para o período solicitado
     */
    public static class SemDadosPeriodoException extends HistoricoException {
        public SemDadosPeriodoException(String message) {
            super(message);
        }
    }
}
