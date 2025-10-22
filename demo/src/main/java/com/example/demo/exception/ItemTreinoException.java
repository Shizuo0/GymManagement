package com.example.demo.exception;

/**
 * Exceções específicas para regras de negócio de ItemTreino
 */
public class ItemTreinoException extends RuntimeException {
    
    public ItemTreinoException(String message) {
        super(message);
    }
    
    public static class ItemTreinoNotFoundException extends ItemTreinoException {
        public ItemTreinoNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class DuplicateExerciseException extends ItemTreinoException {
        public DuplicateExerciseException(String message) {
            super(message);
        }
    }
    
    public static class InvalidSeriesException extends ItemTreinoException {
        public InvalidSeriesException(String message) {
            super(message);
        }
    }
    
    public static class InvalidRepeticoesException extends ItemTreinoException {
        public InvalidRepeticoesException(String message) {
            super(message);
        }
    }
    
    public static class InvalidCargaException extends ItemTreinoException {
        public InvalidCargaException(String message) {
            super(message);
        }
    }
}
