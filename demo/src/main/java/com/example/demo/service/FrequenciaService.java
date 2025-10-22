package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Frequencia;
import com.example.demo.entity.Matricula;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.FrequenciaException;
import com.example.demo.repository.FrequenciaRepository;
import com.example.demo.repository.MatriculaRepository;

/**
 * Service para gerenciamento de Frequência
 * Implementa regras de negócio para registro e validação de presença
 */
@Service
@Transactional
public class FrequenciaService {
    
    @Autowired
    private FrequenciaRepository frequenciaRepository;
    
    @Autowired
    private MatriculaRepository matriculaRepository;
    
    /**
     * Registra presença de um aluno
     * @param frequencia Dados da frequência
     * @return Frequência registrada
     */
    public Frequencia registrarPresenca(Frequencia frequencia) {
        validarFrequencia(frequencia);
        validarAlunoTemMatriculaAtiva(frequencia.getAluno());
        verificarDuplicidade(frequencia.getAluno(), frequencia.getData());
        
        // Se não foi especificado, considera como presença confirmada
        if (frequencia.getPresenca() == null) {
            frequencia.setPresenca(true);
        }
        
        return frequenciaRepository.save(frequencia);
    }
    
    /**
     * Busca um registro de frequência por ID
     * @param id ID da frequência
     * @return Frequência encontrada
     */
    public Frequencia buscarPorId(Long id) {
        return frequenciaRepository.findById(id)
            .orElseThrow(() -> new FrequenciaException.FrequenciaNotFoundException(
                "Registro de frequência não encontrado com ID: " + id));
    }
    
    /**
     * Lista todos os registros de frequência
     * @return Lista de frequências ordenadas por data
     */
    public List<Frequencia> listarTodos() {
        return frequenciaRepository.findAllByOrderByDataDesc();
    }
    
    /**
     * Lista todos os registros de frequência de um aluno
     * @param aluno Aluno
     * @return Lista de frequências do aluno
     */
    public List<Frequencia> listarPorAluno(Aluno aluno) {
        return frequenciaRepository.findByAlunoOrderByDataDesc(aluno);
    }
    
    /**
     * Busca registro de frequência de um aluno em uma data específica
     * @param aluno Aluno
     * @param data Data
     * @return Frequência encontrada
     */
    public Frequencia buscarPorAlunoEData(Aluno aluno, LocalDate data) {
        return frequenciaRepository.findByAlunoAndData(aluno, data)
            .orElseThrow(() -> new FrequenciaException.FrequenciaNotFoundException(
                "Registro de frequência não encontrado para o aluno na data: " + data));
    }
    
    /**
     * Lista registros de uma data específica
     * @param data Data
     * @return Lista de frequências
     */
    public List<Frequencia> listarPorData(LocalDate data) {
        return frequenciaRepository.findByData(data);
    }
    
    /**
     * Lista registros em um período
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de frequências
     */
    public List<Frequencia> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new FrequenciaException.DataInvalidaException(
                "Data inicial não pode ser posterior à data final");
        }
        return frequenciaRepository.findByDataBetween(dataInicio, dataFim);
    }
    
    /**
     * Lista registros de um aluno em um período
     * @param aluno Aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de frequências
     */
    public List<Frequencia> listarPorAlunoEPeriodo(Aluno aluno, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new FrequenciaException.DataInvalidaException(
                "Data inicial não pode ser posterior à data final");
        }
        return frequenciaRepository.findByAlunoAndDataBetween(aluno, dataInicio, dataFim);
    }
    
    /**
     * Lista alunos presentes em uma data específica
     * @param data Data
     * @return Lista de frequências com presença confirmada
     */
    public List<Frequencia> listarPresencasPorData(LocalDate data) {
        return frequenciaRepository.findByDataAndPresenca(data, true);
    }
    
    /**
     * Conta total de presenças de um aluno
     * @param aluno Aluno
     * @return Número de presenças
     */
    public long contarPresencas(Aluno aluno) {
        return frequenciaRepository.countByAlunoAndPresenca(aluno, true);
    }
    
    /**
     * Calcula taxa de presença de um aluno em um período
     * @param aluno Aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Taxa de presença (0-100%)
     */
    public double calcularTaxaPresenca(Aluno aluno, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new FrequenciaException.DataInvalidaException(
                "Data inicial não pode ser posterior à data final");
        }
        
        long totalRegistros = frequenciaRepository.findByAlunoAndDataBetween(aluno, dataInicio, dataFim).size();
        if (totalRegistros == 0) {
            return 0.0;
        }
        
        long presencas = frequenciaRepository.contarPresencasNoPeriodo(aluno, dataInicio, dataFim);
        return (presencas * 100.0) / totalRegistros;
    }
    
    /**
     * Atualiza um registro de frequência
     * @param id ID da frequência
     * @param frequencia Novos dados
     * @return Frequência atualizada
     */
    public Frequencia atualizarFrequencia(Long id, Frequencia frequencia) {
        Frequencia frequenciaExistente = buscarPorId(id);
        
        validarFrequencia(frequencia);
        
        // Se mudou a data ou aluno, verificar duplicidade
        if (!frequenciaExistente.getData().equals(frequencia.getData()) ||
            !frequenciaExistente.getAluno().equals(frequencia.getAluno())) {
            verificarDuplicidade(frequencia.getAluno(), frequencia.getData());
        }
        
        frequenciaExistente.setAluno(frequencia.getAluno());
        frequenciaExistente.setData(frequencia.getData());
        frequenciaExistente.setPresenca(frequencia.getPresenca());
        
        return frequenciaRepository.save(frequenciaExistente);
    }
    
    /**
     * Deleta um registro de frequência
     * @param id ID da frequência
     */
    public void deletarFrequencia(Long id) {
        Frequencia frequencia = buscarPorId(id);
        frequenciaRepository.delete(frequencia);
    }
    
    /**
     * Valida os dados da frequência
     * @param frequencia Frequência a validar
     */
    private void validarFrequencia(Frequencia frequencia) {
        if (frequencia.getAluno() == null) {
            throw new FrequenciaException.FrequenciaInvalidaException(
                "Aluno é obrigatório para o registro de frequência");
        }
        
        if (frequencia.getData() == null) {
            throw new FrequenciaException.DataInvalidaException(
                "Data é obrigatória para o registro de frequência");
        }
        
        if (frequencia.getData().isAfter(LocalDate.now())) {
            throw new FrequenciaException.DataInvalidaException(
                "Data do registro não pode ser futura");
        }
        
        if (frequencia.getPresenca() == null) {
            throw new FrequenciaException.FrequenciaInvalidaException(
                "Status de presença é obrigatório");
        }
    }
    
    /**
     * Verifica se já existe registro de frequência para o aluno na data
     * @param aluno Aluno
     * @param data Data
     */
    private void verificarDuplicidade(Aluno aluno, LocalDate data) {
        if (frequenciaRepository.existsByAlunoAndData(aluno, data)) {
            throw new FrequenciaException.FrequenciaConflictException(
                "Já existe registro de frequência para este aluno na data: " + data);
        }
    }
    
    /**
     * Valida se o aluno possui matrícula ativa
     * @param aluno Aluno
     */
    private void validarAlunoTemMatriculaAtiva(Aluno aluno) {
        List<Matricula> matriculas = matriculaRepository.findByAlunoAndStatus(aluno, MatriculaStatus.ATIVA);
        
        if (matriculas.isEmpty()) {
            throw new FrequenciaException.AlunoSemMatriculaAtivaException(
                "Aluno não possui matrícula ativa para registrar frequência");
        }
        
        // Verifica se a matrícula ativa não está expirada
        Matricula matriculaAtiva = matriculas.get(0);
        if (matriculaAtiva.getDataFim().isBefore(LocalDate.now())) {
            throw new FrequenciaException.AlunoSemMatriculaAtivaException(
                "A matrícula do aluno está expirada. Data de término: " + matriculaAtiva.getDataFim());
        }
    }
}
