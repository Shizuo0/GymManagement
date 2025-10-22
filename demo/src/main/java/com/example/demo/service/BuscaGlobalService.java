package com.example.demo.service;

import com.example.demo.dto.BuscaGlobalResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.AlunoResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.InstrutorResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.PlanoResultadoDTO;
import com.example.demo.entity.Aluno;
import com.example.demo.entity.Instrutor;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.entity.PlanoTreino;
import com.example.demo.exception.BuscaGlobalException.*;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.InstrutorRepository;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.PlanoRepository;
import com.example.demo.repository.PlanoTreinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para busca global de alunos, instrutores e planos
 * Implementa validação de critérios e agregação de resultados
 */
@Service
@Transactional(readOnly = true)
public class BuscaGlobalService {

    private static final int MAX_RESULTADOS = 1000;
    private static final int MIN_TAMANHO_TERMO = 2;
    
    private final AlunoRepository alunoRepository;
    private final InstrutorRepository instrutorRepository;
    private final PlanoRepository planoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PlanoTreinoRepository planoTreinoRepository;

    public BuscaGlobalService(
            AlunoRepository alunoRepository,
            InstrutorRepository instrutorRepository,
            PlanoRepository planoRepository,
            MatriculaRepository matriculaRepository,
            PlanoTreinoRepository planoTreinoRepository) {
        this.alunoRepository = alunoRepository;
        this.instrutorRepository = instrutorRepository;
        this.planoRepository = planoRepository;
        this.matriculaRepository = matriculaRepository;
        this.planoTreinoRepository = planoTreinoRepository;
    }

    /**
     * Busca global em todas as entidades
     * 
     * @param termo Termo de busca
     * @return Resultados agregados de alunos, instrutores e planos
     * @throws TermoBuscaInvalidoException se o termo for inválido
     * @throws NenhumResultadoException se nenhum resultado for encontrado
     * @throws ErroBuscaException se ocorrer erro durante a busca
     */
    public BuscaGlobalResultadoDTO buscarTudo(String termo) {
        validarTermoBusca(termo);
        
        try {
            List<AlunoResultadoDTO> alunos = buscarAlunos(termo);
            List<InstrutorResultadoDTO> instrutores = buscarInstrutores(termo);
            List<PlanoResultadoDTO> planos = buscarPlanos(termo);
            
            BuscaGlobalResultadoDTO resultado = new BuscaGlobalResultadoDTO(termo);
            resultado.setAlunos(alunos);
            resultado.setInstrutores(instrutores);
            resultado.setPlanos(planos);
            
            if (resultado.getTotalResultados() == 0) {
                throw new NenhumResultadoException("Nenhum resultado encontrado para: " + termo);
            }
            
            if (resultado.getTotalResultados() > MAX_RESULTADOS) {
                throw new MuitosResultadosException(
                    "Busca retornou " + resultado.getTotalResultados() + " resultados. " +
                    "Refine os critérios de busca."
                );
            }
            
            return resultado;
            
        } catch (NenhumResultadoException | MuitosResultadosException e) {
            throw e;
        } catch (Exception e) {
            throw new ErroBuscaException("Erro ao realizar busca global", e);
        }
    }

    /**
     * Busca apenas alunos
     * 
     * @param termo Termo de busca
     * @param apenasAtivos Se true, busca apenas alunos com matrícula ativa
     * @return Lista de alunos encontrados
     */
    public List<AlunoResultadoDTO> buscarApenasAlunos(String termo, Boolean apenasAtivos) {
        validarTermoBusca(termo);
        
        try {
            List<AlunoResultadoDTO> alunos = buscarAlunos(termo);
            
            if (apenasAtivos != null && apenasAtivos) {
                alunos = alunos.stream()
                    .filter(a -> "ATIVA".equalsIgnoreCase(a.getStatusMatricula()))
                    .collect(Collectors.toList());
            }
            
            if (alunos.isEmpty()) {
                throw new NenhumResultadoException("Nenhum aluno encontrado para: " + termo);
            }
            
            return alunos;
            
        } catch (NenhumResultadoException e) {
            throw e;
        } catch (Exception e) {
            throw new ErroBuscaException("Erro ao buscar alunos", e);
        }
    }

    /**
     * Busca apenas instrutores
     * 
     * @param termo Termo de busca
     * @param especialidade Filtro por especialidade (opcional)
     * @return Lista de instrutores encontrados
     */
    public List<InstrutorResultadoDTO> buscarApenasInstrutores(String termo, String especialidade) {
        validarTermoBusca(termo);
        
        try {
            List<InstrutorResultadoDTO> instrutores = buscarInstrutores(termo);
            
            if (especialidade != null && !especialidade.isBlank()) {
                instrutores = instrutores.stream()
                    .filter(i -> i.getEspecialidade() != null && 
                                i.getEspecialidade().toLowerCase().contains(especialidade.toLowerCase()))
                    .collect(Collectors.toList());
            }
            
            if (instrutores.isEmpty()) {
                throw new NenhumResultadoException("Nenhum instrutor encontrado para: " + termo);
            }
            
            return instrutores;
            
        } catch (NenhumResultadoException e) {
            throw e;
        } catch (Exception e) {
            throw new ErroBuscaException("Erro ao buscar instrutores", e);
        }
    }

    /**
     * Busca apenas planos
     * 
     * @param termo Termo de busca
     * @param apenasAtivos Se true, busca apenas planos ativos
     * @return Lista de planos encontrados
     */
    public List<PlanoResultadoDTO> buscarApenasPlanos(String termo, Boolean apenasAtivos) {
        validarTermoBusca(termo);
        
        try {
            List<PlanoResultadoDTO> planos = buscarPlanos(termo);
            
            if (apenasAtivos != null && apenasAtivos) {
                planos = planos.stream()
                    .filter(p -> "ATIVO".equalsIgnoreCase(p.getStatus()))
                    .collect(Collectors.toList());
            }
            
            if (planos.isEmpty()) {
                throw new NenhumResultadoException("Nenhum plano encontrado para: " + termo);
            }
            
            return planos;
            
        } catch (NenhumResultadoException e) {
            throw e;
        } catch (Exception e) {
            throw new ErroBuscaException("Erro ao buscar planos", e);
        }
    }

    /**
     * Busca alunos por nome ou CPF
     */
    private List<AlunoResultadoDTO> buscarAlunos(String termo) {
        List<Aluno> todosAlunos = alunoRepository.findAll();
        String termoLower = termo.toLowerCase();
        
        return todosAlunos.stream()
            .filter(a -> a.getNome().toLowerCase().contains(termoLower) ||
                        a.getCpf().contains(termo))
            .map(this::converterParaAlunoResultado)
            .collect(Collectors.toList());
    }

    /**
     * Busca instrutores por nome ou especialidade
     */
    private List<InstrutorResultadoDTO> buscarInstrutores(String termo) {
        List<Instrutor> todosInstrutores = instrutorRepository.findAll();
        String termoLower = termo.toLowerCase();
        
        return todosInstrutores.stream()
            .filter(i -> i.getNome().toLowerCase().contains(termoLower) ||
                        (i.getEspecialidade() != null && i.getEspecialidade().toLowerCase().contains(termoLower)))
            .map(this::converterParaInstrutorResultado)
            .collect(Collectors.toList());
    }

    /**
     * Busca planos por nome ou descrição
     */
    private List<PlanoResultadoDTO> buscarPlanos(String termo) {
        List<Plano> todosPlanos = planoRepository.findAll();
        String termoLower = termo.toLowerCase();
        
        return todosPlanos.stream()
            .filter(p -> p.getNome().toLowerCase().contains(termoLower) ||
                        (p.getDescricao() != null && p.getDescricao().toLowerCase().contains(termoLower)))
            .map(this::converterParaPlanoResultado)
            .collect(Collectors.toList());
    }

    /**
     * Converte Aluno para DTO de resultado
     */
    private AlunoResultadoDTO converterParaAlunoResultado(Aluno aluno) {
        String statusMatricula = verificarMatriculaAtiva(aluno) ? "ATIVA" : "INATIVA";
        String planoAtual = buscarPlanoAtual(aluno);
        
        return new AlunoResultadoDTO(
            aluno.getIdAluno(),
            aluno.getNome(),
            aluno.getCpf(),
            statusMatricula,
            planoAtual
        );
    }

    /**
     * Converte Instrutor para DTO de resultado
     */
    private InstrutorResultadoDTO converterParaInstrutorResultado(Instrutor instrutor) {
        int totalAlunos = contarAlunosDoInstrutor(instrutor);
        
        return new InstrutorResultadoDTO(
            instrutor.getIdInstrutor(),
            instrutor.getNome(),
            null, // Instrutor não tem CPF
            instrutor.getEspecialidade(),
            totalAlunos
        );
    }

    /**
     * Converte Plano para DTO de resultado
     */
    private PlanoResultadoDTO converterParaPlanoResultado(Plano plano) {
        String status = plano.getStatus() != null ? plano.getStatus() : "ATIVO";
        int totalMatriculas = contarMatriculasDoPlano(plano);
        
        return new PlanoResultadoDTO(
            plano.getIdPlanoAssinatura(),
            plano.getNome(),
            plano.getDescricao(),
            plano.getValor().toString(),
            status,
            totalMatriculas
        );
    }

    /**
     * Verifica se aluno possui matrícula ativa
     */
    private boolean verificarMatriculaAtiva(Aluno aluno) {
        List<Matricula> matriculas = matriculaRepository.findByAluno(aluno);
        LocalDate hoje = LocalDate.now();
        
        return matriculas.stream()
            .anyMatch(m -> !m.getDataInicio().isAfter(hoje) && 
                          (m.getDataFim() == null || !m.getDataFim().isBefore(hoje)));
    }

    /**
     * Busca nome do plano atual do aluno
     */
    private String buscarPlanoAtual(Aluno aluno) {
        List<Matricula> matriculas = matriculaRepository.findByAluno(aluno);
        LocalDate hoje = LocalDate.now();
        
        return matriculas.stream()
            .filter(m -> !m.getDataInicio().isAfter(hoje) && 
                        (m.getDataFim() == null || !m.getDataFim().isBefore(hoje)))
            .findFirst()
            .map(m -> m.getPlano().getNome())
            .orElse(null);
    }

    /**
     * Conta total de alunos (únicos) do instrutor
     */
    private int contarAlunosDoInstrutor(Instrutor instrutor) {
        List<PlanoTreino> planos = planoTreinoRepository.findByInstrutor(instrutor);
        
        return (int) planos.stream()
            .map(PlanoTreino::getAluno)
            .distinct()
            .count();
    }

    /**
     * Conta total de matrículas do plano
     */
    private int contarMatriculasDoPlano(Plano plano) {
        List<Matricula> matriculas = matriculaRepository.findByPlano(plano);
        return matriculas.size();
    }

    /**
     * Valida termo de busca
     */
    private void validarTermoBusca(String termo) {
        if (termo == null || termo.isBlank()) {
            throw new TermoBuscaInvalidoException("Termo de busca não pode ser vazio");
        }
        
        if (termo.trim().length() < MIN_TAMANHO_TERMO) {
            throw new TermoBuscaInvalidoException(
                "Termo de busca deve ter pelo menos " + MIN_TAMANHO_TERMO + " caracteres"
            );
        }
    }
}
