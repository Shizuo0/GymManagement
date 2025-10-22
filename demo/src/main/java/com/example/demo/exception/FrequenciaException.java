package com.example.demo.exception;

/**
 * Exceções específicas para regras de negócio de Frequência
 */
public class FrequenciaException extends RuntimeException {
    
    public FrequenciaException(String message) {
        super(message);
    }
    
    /**
     * Exceção lançada quando um registro de frequência não é encontrado
     */
    public static class FrequenciaNotFoundException extends FrequenciaException {
        public FrequenciaNotFoundException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há dados inválidos no registro de frequência
     */
    public static class FrequenciaInvalidaException extends FrequenciaException {
        public FrequenciaInvalidaException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há conflito de registro de frequência
     * (aluno já tem registro para a mesma data)
     */
    public static class FrequenciaConflictException extends FrequenciaException {
        public FrequenciaConflictException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando a data do registro é inválida
     */
    public static class DataInvalidaException extends FrequenciaException {
        public DataInvalidaException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando o aluno não possui matrícula ativa
     */
    public static class AlunoSemMatriculaAtivaException extends FrequenciaException {
        public AlunoSemMatriculaAtivaException(String message) {
            super(message);
        }
    }
}
