package com.example.demo.exception;

/**
 * Exceções específicas para regras de negócio de Matrícula
 */
public class MatriculaException extends RuntimeException {
    
    public MatriculaException(String message) {
        super(message);
    }
    
    public static class MatriculaNotFoundException extends MatriculaException {
        public MatriculaNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class MatriculaInvalidaException extends MatriculaException {
        public MatriculaInvalidaException(String message) {
            super(message);
        }
    }
    
    public static class DataInvalidaException extends MatriculaException {
        public DataInvalidaException(String message) {
            super(message);
        }
    }
    
    public static class StatusInvalidoException extends MatriculaException {
        public StatusInvalidoException(String message) {
            super(message);
        }
    }
    
    public static class PlanoInvalidoException extends MatriculaException {
        public PlanoInvalidoException(String message) {
            super(message);
        }
    }
}