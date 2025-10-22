package com.example.demo.exception;

/**
 * Exceções específicas para regras de negócio de Exercício
 */
public class ExercicioException extends RuntimeException {
    
    public ExercicioException(String message) {
        super(message);
    }
    
    public static class ExercicioNotFoundException extends ExercicioException {
        public ExercicioNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class ExercicioInvalidoException extends ExercicioException {
        public ExercicioInvalidoException(String message) {
            super(message);
        }
    }
    
    public static class ExercicioEmUsoException extends ExercicioException {
        public ExercicioEmUsoException(String message) {
            super(message);
        }
    }
    
    public static class DuplicateExercicioException extends ExercicioException {
        public DuplicateExercicioException(String message) {
            super(message);
        }
    }
}
