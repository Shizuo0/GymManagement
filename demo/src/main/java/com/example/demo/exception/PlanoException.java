package com.example.demo.exception;

public class PlanoException extends RuntimeException {
    
    public PlanoException(String message) {
        super(message);
    }
    
    public PlanoException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static class PlanoNotFoundException extends PlanoException {
        public PlanoNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class PlanoInvalidoException extends PlanoException {
        public PlanoInvalidoException(String message) {
            super(message);
        }
    }
    
    public static class PlanoInativoException extends PlanoException {
        public PlanoInativoException(String message) {
            super(message);
        }
    }
}