package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Handler global para tratamento centralizado de exceções da API
 * Garante respostas consistentes e adequadas para cada tipo de erro
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // ==================== PlanoTreino Exceptions ====================
    
    /**
     * Trata exceções de plano de treino não encontrado
     */
    @ExceptionHandler(PlanoTreinoException.PlanoTreinoNotFoundException.class)
    public ResponseEntity<Object> handlePlanoTreinoNotFoundException(
            PlanoTreinoException.PlanoTreinoNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de validação de plano de treino (aluno, instrutor, data)
     */
    @ExceptionHandler({
        PlanoTreinoException.PlanoTreinoInvalidoException.class,
        PlanoTreinoException.AlunoInvalidoException.class,
        PlanoTreinoException.InstrutorInvalidoException.class,
        PlanoTreinoException.DataInvalidaException.class
    })
    public ResponseEntity<Object> handlePlanoTreinoValidationException(
            PlanoTreinoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    // ==================== Exercicio Exceptions ====================
    
    /**
     * Trata exceções de exercício não encontrado
     */
    @ExceptionHandler(ExercicioException.ExercicioNotFoundException.class)
    public ResponseEntity<Object> handleExercicioNotFoundException(
            ExercicioException.ExercicioNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de exercícios duplicados
     */
    @ExceptionHandler(ExercicioException.DuplicateExercicioException.class)
    public ResponseEntity<Object> handleDuplicateExercicioException(
            ExercicioException.DuplicateExercicioException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * Trata exceções de exercício em uso (não pode ser deletado)
     */
    @ExceptionHandler(ExercicioException.ExercicioEmUsoException.class)
    public ResponseEntity<Object> handleExercicioEmUsoException(
            ExercicioException.ExercicioEmUsoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * Trata exceções de validação de exercício
     */
    @ExceptionHandler(ExercicioException.ExercicioInvalidoException.class)
    public ResponseEntity<Object> handleExercicioInvalidoException(
            ExercicioException.ExercicioInvalidoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    // ==================== ItemTreino Exceptions ====================
    
    /**
     * Trata exceções de item de treino não encontrado
     */
    @ExceptionHandler(ItemTreinoException.ItemTreinoNotFoundException.class)
    public ResponseEntity<Object> handleItemTreinoNotFoundException(
            ItemTreinoException.ItemTreinoNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de exercício duplicado no mesmo plano
     */
    @ExceptionHandler(ItemTreinoException.DuplicateExerciseException.class)
    public ResponseEntity<Object> handleDuplicateExerciseException(
            ItemTreinoException.DuplicateExerciseException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * Trata exceções de validação de item de treino (séries, repetições, carga)
     */
    @ExceptionHandler({
        ItemTreinoException.InvalidSeriesException.class,
        ItemTreinoException.InvalidRepeticoesException.class,
        ItemTreinoException.InvalidCargaException.class
    })
    public ResponseEntity<Object> handleItemTreinoValidationException(
            ItemTreinoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    // ==================== Pagamento Exceptions ====================
    
    /**
     * Trata exceções de pagamento não encontrado
     */
    @ExceptionHandler(PagamentoException.PagamentoNotFoundException.class)
    public ResponseEntity<Object> handlePagamentoNotFoundException(
            PagamentoException.PagamentoNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de pagamento duplicado
     */
    @ExceptionHandler(PagamentoException.PagamentoDuplicadoException.class)
    public ResponseEntity<Object> handlePagamentoDuplicadoException(
            PagamentoException.PagamentoDuplicadoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * Trata exceções de validação de pagamento (valor, data, matrícula)
     */
    @ExceptionHandler({
        PagamentoException.PagamentoInvalidoException.class,
        PagamentoException.ValorInvalidoException.class,
        PagamentoException.DataInvalidaException.class,
        PagamentoException.MatriculaInvalidaException.class
    })
    public ResponseEntity<Object> handlePagamentoValidationException(
            PagamentoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    // ==================== Frequencia Exceptions ====================
    
    /**
     * Trata exceções de frequência não encontrada
     */
    @ExceptionHandler(FrequenciaException.FrequenciaNotFoundException.class)
    public ResponseEntity<Object> handleFrequenciaNotFoundException(
            FrequenciaException.FrequenciaNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de conflito de frequência (registro duplicado)
     */
    @ExceptionHandler(FrequenciaException.FrequenciaConflictException.class)
    public ResponseEntity<Object> handleFrequenciaConflictException(
            FrequenciaException.FrequenciaConflictException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * Trata exceções de validação de frequência (data, presença, aluno sem matrícula)
     */
    @ExceptionHandler({
        FrequenciaException.FrequenciaInvalidaException.class,
        FrequenciaException.DataInvalidaException.class,
        FrequenciaException.AlunoSemMatriculaAtivaException.class
    })
    public ResponseEntity<Object> handleFrequenciaValidationException(
            FrequenciaException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    // ==================== Historico Exceptions ====================
    
    /**
     * Trata exceções de histórico não encontrado
     */
    @ExceptionHandler(HistoricoException.HistoricoNotFoundException.class)
    public ResponseEntity<Object> handleHistoricoNotFoundException(
            HistoricoException.HistoricoNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de dados corrompidos no histórico
     */
    @ExceptionHandler(HistoricoException.DadosCorruptosException.class)
    public ResponseEntity<Object> handleDadosCorruptosException(
            HistoricoException.DadosCorruptosException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Trata exceções de integridade de dados entre tabelas
     */
    @ExceptionHandler(HistoricoException.IntegridadeDadosException.class)
    public ResponseEntity<Object> handleIntegridadeDadosException(
            HistoricoException.IntegridadeDadosException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    /**
     * Trata exceções de dados ausentes necessários
     */
    @ExceptionHandler(HistoricoException.DadosAusentesException.class)
    public ResponseEntity<Object> handleDadosAusentesException(
            HistoricoException.DadosAusentesException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de erro na agregação de dados
     */
    @ExceptionHandler(HistoricoException.ErroAgregacaoException.class)
    public ResponseEntity<Object> handleErroAgregacaoException(
            HistoricoException.ErroAgregacaoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Trata exceções de período inválido (data início após data fim, datas futuras)
     */
    @ExceptionHandler(HistoricoException.PeriodoInvalidoException.class)
    public ResponseEntity<Object> handlePeriodoInvalidoException(
            HistoricoException.PeriodoInvalidoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata exceções de ausência de dados no período
     */
    @ExceptionHandler(HistoricoException.SemDadosPeriodoException.class)
    public ResponseEntity<Object> handleSemDadosPeriodoException(
            HistoricoException.SemDadosPeriodoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    // ==================== Busca Global Exceptions ====================
    
    /**
     * Trata exceções de nenhum resultado encontrado na busca
     */
    @ExceptionHandler(BuscaGlobalException.NenhumResultadoException.class)
    public ResponseEntity<Object> handleNenhumResultadoException(
            BuscaGlobalException.NenhumResultadoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Trata exceções de critérios de busca inválidos
     */
    @ExceptionHandler(BuscaGlobalException.CriteriosBuscaInvalidosException.class)
    public ResponseEntity<Object> handleCriteriosBuscaInvalidosException(
            BuscaGlobalException.CriteriosBuscaInvalidosException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata exceções de filtro inválido
     */
    @ExceptionHandler(BuscaGlobalException.FiltroInvalidoException.class)
    public ResponseEntity<Object> handleFiltroInvalidoException(
            BuscaGlobalException.FiltroInvalidoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata exceções de erro durante a busca
     */
    @ExceptionHandler(BuscaGlobalException.ErroBuscaException.class)
    public ResponseEntity<Object> handleErroBuscaException(
            BuscaGlobalException.ErroBuscaException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Trata exceções de termo de busca inválido (muito curto, vazio)
     */
    @ExceptionHandler(BuscaGlobalException.TermoBuscaInvalidoException.class)
    public ResponseEntity<Object> handleTermoBuscaInvalidoException(
            BuscaGlobalException.TermoBuscaInvalidoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata exceções de paginação inválida
     */
    @ExceptionHandler(BuscaGlobalException.PaginacaoInvalidaException.class)
    public ResponseEntity<Object> handlePaginacaoInvalidaException(
            BuscaGlobalException.PaginacaoInvalidaException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata exceções de muitos resultados (limite excedido)
     */
    @ExceptionHandler(BuscaGlobalException.MuitosResultadosException.class)
    public ResponseEntity<Object> handleMuitosResultadosException(
            BuscaGlobalException.MuitosResultadosException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.PAYLOAD_TOO_LARGE);
    }
    
    // ==================== Generic Exceptions ====================
    
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> handleRecursoNaoEncontradoException(
            RecursoNaoEncontradoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Object> handleValidacaoException(
            ValidacaoException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Trata exceções de validação do Bean Validation (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Erro de validação");
        
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception ex, WebRequest request) {
        return buildErrorResponse("Ocorreu um erro interno no servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Método auxiliar para construir resposta de erro padronizada
     */
    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        
        return new ResponseEntity<>(body, status);
    }
}