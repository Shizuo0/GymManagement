package com.example.demo.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AvaliacaoFisicaSummaryDTO;
import com.example.demo.dto.FrequenciaSummaryDTO;
import com.example.demo.dto.HistoricoAlunoDTO;
import com.example.demo.dto.MatriculaSummaryDTO;
import com.example.demo.dto.PagamentoResponseDTO;
import com.example.demo.dto.PlanoTreinoSummaryDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.AvaliacaoFisica;
import com.example.demo.entity.Frequencia;
import com.example.demo.entity.ItemTreino;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Pagamento;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.HistoricoException;
import com.example.demo.repository.AvaliacaoFisicaRepository;
import com.example.demo.repository.FrequenciaRepository;
import com.example.demo.repository.ItemTreinoRepository;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.PagamentoRepository;
import com.example.demo.repository.PlanoTreinoRepository;

/**
 * Service para gerenciamento do histórico completo do aluno
 * Agrega dados de múltiplas tabelas: treinos, avaliações, frequência, matrículas e pagamentos
 */
@Service
@Transactional(readOnly = true)
public class HistoricoAlunoService {
    
    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private MatriculaRepository matriculaRepository;
    
    @Autowired
    private PlanoTreinoRepository planoTreinoRepository;
    
    @Autowired
    private AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    
    @Autowired
    private FrequenciaRepository frequenciaRepository;
    
    @Autowired
    private PagamentoRepository pagamentoRepository;
    
    @Autowired
    private ItemTreinoRepository itemTreinoRepository;
    
    /**
     * Busca o histórico completo do aluno
     * @param idAluno ID do aluno
     * @return DTO com histórico completo
     */
    public HistoricoAlunoDTO buscarHistoricoCompleto(Long idAluno) {
        try {
            // Buscar aluno
            Aluno aluno = alunoService.buscarPorId(idAluno);
            
            // Criar DTO base
            HistoricoAlunoDTO historico = new HistoricoAlunoDTO(
                aluno.getIdAluno(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getDataIngresso() // Usando dataIngresso no lugar de dataNascimento
            );
            
            // Agregar dados de múltiplas tabelas
            historico.setMatriculas(buscarMatriculas(aluno));
            historico.setMatriculaAtual(buscarMatriculaAtual(aluno));
            
            historico.setPlanosTreino(buscarPlanosTreino(aluno));
            historico.setPlanoTreinoAtual(buscarPlanoTreinoAtual(aluno));
            
            historico.setAvaliacoesFisicas(buscarAvaliacoesFisicas(aluno));
            historico.setUltimaAvaliacaoFisica(buscarUltimaAvaliacaoFisica(aluno));
            
            historico.setFrequenciaMensal(buscarFrequenciaMensal(aluno));
            historico.setFrequenciaMesAtual(buscarFrequenciaMesAtual(aluno));
            
            historico.setPagamentos(buscarPagamentos(aluno));
            historico.setUltimoPagamento(buscarUltimoPagamento(aluno));
            
            // Calcular estatísticas
            historico.setEstatisticas(calcularEstatisticas(aluno, historico));
            
            return historico;
            
        } catch (Exception e) {
            throw new HistoricoException.ErroAgregacaoException(
                "Erro ao agregar dados do histórico do aluno: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca histórico do aluno em um período específico
     * @param idAluno ID do aluno
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return DTO com histórico filtrado
     */
    public HistoricoAlunoDTO buscarHistoricoPorPeriodo(Long idAluno, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        
        try {
            Aluno aluno = alunoService.buscarPorId(idAluno);
            
            HistoricoAlunoDTO historico = new HistoricoAlunoDTO(
                aluno.getIdAluno(),
                aluno.getNome(),
                aluno.getCpf(),
                aluno.getDataIngresso() // Usando dataIngresso
            );
            
            // Buscar apenas dados do período
            historico.setPlanosTreino(buscarPlanosTreinoPorPeriodo(aluno, dataInicio, dataFim));
            historico.setAvaliacoesFisicas(buscarAvaliacoesFisicasPorPeriodo(aluno, dataInicio, dataFim));
            historico.setFrequenciaMensal(buscarFrequenciaPorPeriodo(aluno, dataInicio, dataFim));
            historico.setPagamentos(buscarPagamentosPorPeriodo(aluno, dataInicio, dataFim));
            
            return historico;
            
        } catch (Exception e) {
            throw new HistoricoException.ErroAgregacaoException(
                "Erro ao buscar histórico do período: " + e.getMessage(), e);
        }
    }
    
    // ==================== Métodos privados de agregação ====================
    
    /**
     * Busca todas as matrículas do aluno
     */
    private List<MatriculaSummaryDTO> buscarMatriculas(Aluno aluno) {
        try {
            List<Matricula> matriculas = matriculaRepository.findByAluno(aluno);
            
            // Ordenar manualmente por dataInicio desc
            matriculas.sort(Comparator.comparing(Matricula::getDataInicio).reversed());
            
            return matriculas.stream()
                .map(m -> {
                    MatriculaSummaryDTO dto = new MatriculaSummaryDTO(
                        m.getIdMatricula(),
                        m.getPlano().getNome(),
                        m.getPlano().getValor(),
                        m.getDataInicio(),
                        m.getDataFim(),
                        m.getStatus().toString()
                    );
                    
                    // Calcular total pago e status
                    dto.setTotalPago(pagamentoRepository.calcularTotalPagoPorMatricula(m));
                    dto.setEmDia(dto.getTotalPago() != null && 
                                dto.getTotalPago().compareTo(m.getPlano().getValor()) >= 0);
                    
                    return dto;
                })
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new HistoricoException.DadosCorruptosException(
                "Erro ao buscar matrículas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca a matrícula ativa atual do aluno
     */
    private MatriculaSummaryDTO buscarMatriculaAtual(Aluno aluno) {
        List<Matricula> matriculasAtivas = matriculaRepository.findByAlunoAndStatus(aluno, MatriculaStatus.ATIVA);
        
        if (matriculasAtivas.isEmpty()) {
            return null;
        }
        
        Matricula matricula = matriculasAtivas.get(0);
        MatriculaSummaryDTO dto = new MatriculaSummaryDTO(
            matricula.getIdMatricula(),
            matricula.getPlano().getNome(),
            matricula.getPlano().getValor(),
            matricula.getDataInicio(),
            matricula.getDataFim(),
            matricula.getStatus().toString()
        );
        
        dto.setTotalPago(pagamentoRepository.calcularTotalPagoPorMatricula(matricula));
        dto.setEmDia(dto.getTotalPago() != null && 
                    dto.getTotalPago().compareTo(matricula.getPlano().getValor()) >= 0);
        
        return dto;
    }
    
    /**
     * Busca todos os planos de treino do aluno
     */
    private List<PlanoTreinoSummaryDTO> buscarPlanosTreino(Aluno aluno) {
        try {
            List<PlanoTreino> planos = planoTreinoRepository.findByAluno(aluno);
            
            // Ordenar manualmente por dataCriacao desc
            planos.sort(Comparator.comparing(PlanoTreino::getDataCriacao).reversed());
            
            return planos.stream()
                .map(p -> {
                    // Calcular dataFim baseada na duração em semanas
                    LocalDate dataFim = p.getDuracaoSemanas() != null 
                        ? p.getDataCriacao().plusWeeks(p.getDuracaoSemanas()) 
                        : null;
                        
                    PlanoTreinoSummaryDTO dto = new PlanoTreinoSummaryDTO(
                        p.getIdPlanoTreino(),
                        p.getInstrutor().getNome(),
                        p.getDataCriacao(), // Usando dataCriacao como dataInicio
                        dataFim, // Calculado
                        p.getDescricao(), // Usando descrição como objetivo
                        null // será preenchido abaixo
                    );
                    
                    // Buscar exercícios do plano
                    List<ItemTreino> itens = itemTreinoRepository.findByPlanoTreino(p);
                    dto.setTotalExercicios(itens.size());
                    
                    // Converter itens para DTOs
                    List<PlanoTreinoSummaryDTO.ExercicioResumoDTO> exercicios = itens.stream()
                        .map(i -> new PlanoTreinoSummaryDTO.ExercicioResumoDTO(
                            i.getExercicio().getNome(),
                            i.getSeries(),
                            i.getRepeticoes(),
                            i.getCarga()
                        ))
                        .collect(Collectors.toList());
                    dto.setExercicios(exercicios);
                    
                    return dto;
                })
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new HistoricoException.DadosCorruptosException(
                "Erro ao buscar planos de treino: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca o plano de treino ativo atual
     */
    private PlanoTreinoSummaryDTO buscarPlanoTreinoAtual(Aluno aluno) {
        List<PlanoTreino> planos = planoTreinoRepository.findByAluno(aluno);
        
        // Ordenar por dataCriacao desc
        planos.sort(Comparator.comparing(PlanoTreino::getDataCriacao).reversed());
        
        LocalDate hoje = LocalDate.now();
        PlanoTreino planoAtual = planos.stream()
            .filter(p -> {
                LocalDate dataFim = p.getDuracaoSemanas() != null 
                    ? p.getDataCriacao().plusWeeks(p.getDuracaoSemanas()) 
                    : null;
                return !hoje.isBefore(p.getDataCriacao()) && 
                       (dataFim == null || !hoje.isAfter(dataFim));
            })
            .findFirst()
            .orElse(null);
        
        if (planoAtual == null) {
            return null;
        }
        
        LocalDate dataFim = planoAtual.getDuracaoSemanas() != null 
            ? planoAtual.getDataCriacao().plusWeeks(planoAtual.getDuracaoSemanas()) 
            : null;
        
        PlanoTreinoSummaryDTO dto = new PlanoTreinoSummaryDTO(
            planoAtual.getIdPlanoTreino(),
            planoAtual.getInstrutor().getNome(),
            planoAtual.getDataCriacao(),
            dataFim,
            planoAtual.getDescricao(),
            null
        );
        
        List<ItemTreino> itens = itemTreinoRepository.findByPlanoTreino(planoAtual);
        dto.setTotalExercicios(itens.size());
        
        List<PlanoTreinoSummaryDTO.ExercicioResumoDTO> exercicios = itens.stream()
            .map(i -> new PlanoTreinoSummaryDTO.ExercicioResumoDTO(
                i.getExercicio().getNome(),
                i.getSeries(),
                i.getRepeticoes(),
                i.getCarga()
            ))
            .collect(Collectors.toList());
        dto.setExercicios(exercicios);
        
        return dto;
    }
    
    /**
     * Busca todas as avaliações físicas do aluno
     */
    private List<AvaliacaoFisicaSummaryDTO> buscarAvaliacoesFisicas(Aluno aluno) {
        try {
            List<AvaliacaoFisica> avaliacoes = avaliacaoFisicaRepository.findByAlunoOrderByDataAvaliacaoDesc(aluno);
            
            return avaliacoes.stream()
                .map(a -> new AvaliacaoFisicaSummaryDTO(
                    a.getIdAvaliacao(),
                    a.getDataAvaliacao(),
                    a.getInstrutor().getNome(),
                    a.getPeso(),
                    a.getAltura(),
                    a.getPercentualGordura(),
                    a.getMedidasCorporais()
                ))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new HistoricoException.DadosCorruptosException(
                "Erro ao buscar avaliações físicas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca a última avaliação física do aluno
     */
    private AvaliacaoFisicaSummaryDTO buscarUltimaAvaliacaoFisica(Aluno aluno) {
        return avaliacaoFisicaRepository.findFirstByAlunoOrderByDataAvaliacaoDesc(aluno)
            .map(a -> new AvaliacaoFisicaSummaryDTO(
                a.getIdAvaliacao(),
                a.getDataAvaliacao(),
                a.getInstrutor().getNome(),
                a.getPeso(),
                a.getAltura(),
                a.getPercentualGordura(),
                a.getMedidasCorporais()
            ))
            .orElse(null);
    }
    
    /**
     * Busca frequência mensal agregada
     */
    private List<FrequenciaSummaryDTO> buscarFrequenciaMensal(Aluno aluno) {
        try {
            List<Frequencia> frequencias = frequenciaRepository.findByAlunoOrderByDataDesc(aluno);
            
            // Agrupar por mês
            Map<YearMonth, List<Frequencia>> porMes = frequencias.stream()
                .collect(Collectors.groupingBy(f -> YearMonth.from(f.getData())));
            
            return porMes.entrySet().stream()
                .map(entry -> {
                    List<Frequencia> freqMes = entry.getValue();
                    long totalDias = freqMes.size();
                    long totalPresencas = freqMes.stream().filter(Frequencia::getPresenca).count();
                    long totalAusencias = totalDias - totalPresencas;
                    
                    return new FrequenciaSummaryDTO(
                        entry.getKey().atDay(1),
                        totalDias,
                        totalPresencas,
                        totalAusencias
                    );
                })
                .sorted(Comparator.comparing(FrequenciaSummaryDTO::getMes).reversed())
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            throw new HistoricoException.DadosCorruptosException(
                "Erro ao buscar frequência: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca frequência do mês atual
     */
    private FrequenciaSummaryDTO buscarFrequenciaMesAtual(Aluno aluno) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
        
        List<Frequencia> frequencias = frequenciaRepository.findByAlunoAndDataBetween(aluno, inicioMes, fimMes);
        
        if (frequencias.isEmpty()) {
            return null;
        }
        
        long totalDias = frequencias.size();
        long totalPresencas = frequencias.stream().filter(Frequencia::getPresenca).count();
        long totalAusencias = totalDias - totalPresencas;
        
        return new FrequenciaSummaryDTO(inicioMes, totalDias, totalPresencas, totalAusencias);
    }
    
    /**
     * Busca todos os pagamentos do aluno
     */
    private List<PagamentoResponseDTO> buscarPagamentos(Aluno aluno) {
        try {
            List<Matricula> matriculas = matriculaRepository.findByAluno(aluno);
            List<PagamentoResponseDTO> todosPagamentos = new ArrayList<>();
            
            for (Matricula matricula : matriculas) {
                List<Pagamento> pagamentos = pagamentoRepository.findByMatriculaOrderByDataPagamentoAsc(matricula);
                
                pagamentos.forEach(p -> todosPagamentos.add(new PagamentoResponseDTO(
                    p.getIdPagamento(),
                    p.getMatricula().getIdMatricula(),
                    aluno.getNome(),
                    p.getMatricula().getPlano().getNome(),
                    p.getDataPagamento(),
                    p.getValorPago(),
                    p.getFormaPagamento()
                )));
            }
            
            // Ordenar por data decrescente
            todosPagamentos.sort(Comparator.comparing(PagamentoResponseDTO::getDataPagamento).reversed());
            
            return todosPagamentos;
            
        } catch (Exception e) {
            throw new HistoricoException.DadosCorruptosException(
                "Erro ao buscar pagamentos: " + e.getMessage(), e);
        }
    }
    
    /**
     * Busca o último pagamento do aluno
     */
    private PagamentoResponseDTO buscarUltimoPagamento(Aluno aluno) {
        List<PagamentoResponseDTO> pagamentos = buscarPagamentos(aluno);
        return pagamentos.isEmpty() ? null : pagamentos.get(0);
    }
    
    /**
     * Busca planos de treino em um período
     */
    private List<PlanoTreinoSummaryDTO> buscarPlanosTreinoPorPeriodo(Aluno aluno, LocalDate inicio, LocalDate fim) {
        List<PlanoTreino> planos = planoTreinoRepository.findByAluno(aluno);
        
        return planos.stream()
            .filter(p -> {
                LocalDate dataFim = p.getDuracaoSemanas() != null 
                    ? p.getDataCriacao().plusWeeks(p.getDuracaoSemanas()) 
                    : LocalDate.now().plusYears(10); // Se não tem fim, considera muito no futuro
                return !p.getDataCriacao().isAfter(fim) && !dataFim.isBefore(inicio);
            })
            .map(p -> {
                LocalDate dataFim = p.getDuracaoSemanas() != null 
                    ? p.getDataCriacao().plusWeeks(p.getDuracaoSemanas()) 
                    : null;
                return new PlanoTreinoSummaryDTO(
                    p.getIdPlanoTreino(),
                    p.getInstrutor().getNome(),
                    p.getDataCriacao(),
                    dataFim,
                    p.getDescricao(),
                    itemTreinoRepository.findByPlanoTreino(p).size()
                );
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Busca avaliações físicas em um período
     */
    private List<AvaliacaoFisicaSummaryDTO> buscarAvaliacoesFisicasPorPeriodo(Aluno aluno, LocalDate inicio, LocalDate fim) {
        List<AvaliacaoFisica> avaliacoes = avaliacaoFisicaRepository.findAvaliacoesAlunoNoPeriodo(aluno, inicio, fim);
        
        return avaliacoes.stream()
            .map(a -> new AvaliacaoFisicaSummaryDTO(
                a.getIdAvaliacao(),
                a.getDataAvaliacao(),
                a.getInstrutor().getNome(),
                a.getPeso(),
                a.getAltura(),
                a.getPercentualGordura(),
                a.getMedidasCorporais()
            ))
            .collect(Collectors.toList());
    }
    
    /**
     * Busca frequência em um período
     */
    private List<FrequenciaSummaryDTO> buscarFrequenciaPorPeriodo(Aluno aluno, LocalDate inicio, LocalDate fim) {
        List<Frequencia> frequencias = frequenciaRepository.findByAlunoAndDataBetween(aluno, inicio, fim);
        
        Map<YearMonth, List<Frequencia>> porMes = frequencias.stream()
            .collect(Collectors.groupingBy(f -> YearMonth.from(f.getData())));
        
        return porMes.entrySet().stream()
            .map(entry -> {
                List<Frequencia> freqMes = entry.getValue();
                long totalDias = freqMes.size();
                long totalPresencas = freqMes.stream().filter(Frequencia::getPresenca).count();
                long totalAusencias = totalDias - totalPresencas;
                
                return new FrequenciaSummaryDTO(
                    entry.getKey().atDay(1),
                    totalDias,
                    totalPresencas,
                    totalAusencias
                );
            })
            .sorted(Comparator.comparing(FrequenciaSummaryDTO::getMes).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Busca pagamentos em um período
     */
    private List<PagamentoResponseDTO> buscarPagamentosPorPeriodo(Aluno aluno, LocalDate inicio, LocalDate fim) {
        List<Pagamento> pagamentos = pagamentoRepository.findByDataPagamentoBetween(inicio, fim);
        
        return pagamentos.stream()
            .filter(p -> p.getMatricula().getAluno().equals(aluno))
            .map(p -> new PagamentoResponseDTO(
                p.getIdPagamento(),
                p.getMatricula().getIdMatricula(),
                aluno.getNome(),
                p.getMatricula().getPlano().getNome(),
                p.getDataPagamento(),
                p.getValorPago(),
                p.getFormaPagamento()
            ))
            .sorted(Comparator.comparing(PagamentoResponseDTO::getDataPagamento).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Calcula estatísticas gerais do aluno
     */
    private HistoricoAlunoDTO.EstatisticasDTO calcularEstatisticas(Aluno aluno, HistoricoAlunoDTO historico) {
        HistoricoAlunoDTO.EstatisticasDTO stats = new HistoricoAlunoDTO.EstatisticasDTO();
        
        stats.setTotalMatriculas(historico.getMatriculas() != null ? historico.getMatriculas().size() : 0);
        stats.setTotalPlanosTreino(historico.getPlanosTreino() != null ? historico.getPlanosTreino().size() : 0);
        stats.setTotalAvaliacoesFisicas(historico.getAvaliacoesFisicas() != null ? historico.getAvaliacoesFisicas().size() : 0);
        
        // Calcular total de presenças
        long totalPresencas = frequenciaRepository.countByAlunoAndPresenca(aluno, true);
        stats.setTotalPresencas(totalPresencas);
        
        // Calcular taxa de presença geral
        long totalRegistros = frequenciaRepository.countByAluno(aluno);
        if (totalRegistros > 0) {
            stats.setTaxaPresencaGeral((totalPresencas * 100.0) / totalRegistros);
        } else {
            stats.setTaxaPresencaGeral(0.0);
        }
        
        // Calcular dias como aluno (desde primeira matrícula)
        if (historico.getMatriculas() != null && !historico.getMatriculas().isEmpty()) {
            LocalDate primeiraMatricula = historico.getMatriculas().stream()
                .map(MatriculaSummaryDTO::getDataInicio)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
            
            stats.setDiasComoAluno((int) ChronoUnit.DAYS.between(primeiraMatricula, LocalDate.now()));
        } else {
            stats.setDiasComoAluno(0);
        }
        
        return stats;
    }
    
    /**
     * Valida o período informado
     */
    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new HistoricoException.PeriodoInvalidoException(
                "Data de início e fim são obrigatórias");
        }
        
        if (dataInicio.isAfter(dataFim)) {
            throw new HistoricoException.PeriodoInvalidoException(
                "Data de início não pode ser posterior à data final");
        }
        
        if (dataFim.isAfter(LocalDate.now())) {
            throw new HistoricoException.PeriodoInvalidoException(
                "Data final não pode ser futura");
        }
    }
}
