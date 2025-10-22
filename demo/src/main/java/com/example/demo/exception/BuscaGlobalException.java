package com.example.demo.exception;

/**
 * Classe base para exceções relacionadas à busca global
 */
public class BuscaGlobalException extends RuntimeException {
    
    public BuscaGlobalException(String message) {
        super(message);
    }
    
    public BuscaGlobalException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Exceção lançada quando nenhum resultado é encontrado na busca
     */
    public static class NenhumResultadoException extends BuscaGlobalException {
        public NenhumResultadoException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando os critérios de busca são inválidos
     */
    public static class CriteriosBuscaInvalidosException extends BuscaGlobalException {
        public CriteriosBuscaInvalidosException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando o filtro aplicado é inválido
     */
    public static class FiltroInvalidoException extends BuscaGlobalException {
        public FiltroInvalidoException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há erro na execução da busca
     */
    public static class ErroBuscaException extends BuscaGlobalException {
        public ErroBuscaException(String message) {
            super(message);
        }
        
        public ErroBuscaException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exceção lançada quando o termo de busca é muito curto ou inválido
     */
    public static class TermoBuscaInvalidoException extends BuscaGlobalException {
        public TermoBuscaInvalidoException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando a paginação solicitada é inválida
     */
    public static class PaginacaoInvalidaException extends BuscaGlobalException {
        public PaginacaoInvalidaException(String message) {
            super(message);
        }
    }
    
    /**
     * Exceção lançada quando há muitos resultados e é necessário refinar a busca
     */
    public static class MuitosResultadosException extends BuscaGlobalException {
        public MuitosResultadosException(String message) {
            super(message);
        }
    }
}
