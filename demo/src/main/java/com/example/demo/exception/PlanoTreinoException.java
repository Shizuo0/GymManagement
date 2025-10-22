package com.example.demo.exception;

/**
 * Exceções específicas para regras de negócio de Plano de Treino
 */
public class PlanoTreinoException extends RuntimeException {
    
    public PlanoTreinoException(String message) {
        super(message);
    }
    
    public static class PlanoTreinoNotFoundException extends PlanoTreinoException {
        public PlanoTreinoNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class PlanoTreinoInvalidoException extends PlanoTreinoException {
        public PlanoTreinoInvalidoException(String message) {
            super(message);
        }
    }
    
    public static class AlunoInvalidoException extends PlanoTreinoException {
        public AlunoInvalidoException(String message) {
            super(message);
        }
    }
    
    public static class InstrutorInvalidoException extends PlanoTreinoException {
        public InstrutorInvalidoException(String message) {
            super(message);
        }
    }
    
    public static class DataInvalidaException extends PlanoTreinoException {
        public DataInvalidaException(String message) {
            super(message);
        }
    }
}