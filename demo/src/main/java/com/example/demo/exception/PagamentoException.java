package com.example.demo.exception;

/**
 * Exceções específicas para regras de negócio de Pagamento
 */
public class PagamentoException extends RuntimeException {
    
    public PagamentoException(String message) {
        super(message);
    }
    
    /**
     * Exceção lançada quando um pagamento não é encontrado
     */
    public static class PagamentoNotFoundException extends PagamentoException {
        public PagamentoNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há inconsistências nos dados do pagamento
     */
    public static class PagamentoInvalidoException extends PagamentoException {
        public PagamentoInvalidoException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando a matrícula associada ao pagamento é inválida
     */
    public static class MatriculaInvalidaException extends PagamentoException {
        public MatriculaInvalidaException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando o valor do pagamento é inválido
     */
    public static class ValorInvalidoException extends PagamentoException {
        public ValorInvalidoException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando a data do pagamento é inválida
     */
    public static class DataInvalidaException extends PagamentoException {
        public DataInvalidaException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há pagamento duplicado
     */
    public static class PagamentoDuplicadoException extends PagamentoException {
        public PagamentoDuplicadoException(String message) {
            super(message);
        }
    }
}
