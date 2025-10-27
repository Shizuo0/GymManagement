package com.example.demo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Matricula;
import com.example.demo.entity.Plano;
import com.example.demo.enums.MatriculaStatus;
import com.example.demo.exception.MatriculaException;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.PlanoRepository;

@Service
@Transactional
public class MatriculaService {
    
    @Autowired
    private MatriculaRepository matriculaRepository;
    
    @Autowired
    private PlanoRepository planoRepository;
    
    public Matricula criarMatricula(Matricula matricula) {
        validarMatricula(matricula);
        validarPlano(matricula.getPlano());
        
        // Verificar se já existe matrícula ativa
        if (matriculaRepository.existsByAlunoAndStatus(
            matricula.getAluno(), MatriculaStatus.ATIVA)) {
            throw new MatriculaException.MatriculaInvalidaException(
                "Aluno já possui matrícula ativa");
        }
        
        // Calcular data fim baseada no plano
        matricula.setDataFim(
            matricula.getDataInicio().plusMonths(matricula.getPlano().getDuracaoMeses())
        );
        
        validarPeriodo(matricula.getDataInicio(), matricula.getDataFim());
        validarDuracaoPlano(matricula.getPlano(), matricula.getDataInicio(), matricula.getDataFim());
        
        // Matrícula nova sempre começa com status ATIVA
        matricula.setStatus(MatriculaStatus.ATIVA);
        
        return matriculaRepository.save(matricula);
    }
    
    public Matricula atualizarMatricula(Long id, Matricula matricula) {
        Matricula matriculaExistente = buscarMatriculaPorId(id);
        
        validarMatricula(matricula);
        validarPlano(matricula.getPlano());
        validarPeriodo(matricula.getDataInicio(), matricula.getDataFim());
        
        matriculaExistente.setAluno(matricula.getAluno());
        matriculaExistente.setPlano(matricula.getPlano());
        matriculaExistente.setDataInicio(matricula.getDataInicio());
        matriculaExistente.setDataFim(matricula.getDataFim());
        
        return matriculaRepository.save(matriculaExistente);
    }
    
    public Matricula buscarMatriculaPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new MatriculaException.MatriculaNotFoundException(
                    "Matrícula não encontrada com ID: " + id));
    }
    
    public List<Matricula> listarTodasMatriculas() {
        return matriculaRepository.findAll();
    }
    
    public List<Matricula> listarMatriculasPorAluno(Aluno aluno) {
        return matriculaRepository.findByAluno(aluno);
    }
    
    public List<Matricula> listarMatriculasPorStatus(MatriculaStatus status) {
        return matriculaRepository.findByStatus(status);
    }
    
    public List<Matricula> listarMatriculasPorPlano(Plano plano) {
        return matriculaRepository.findByPlano(plano);
    }
    
    public void cancelarMatricula(Long id) {
        Matricula matricula = buscarMatriculaPorId(id);
        
        if (matricula.getStatus() == MatriculaStatus.CANCELADA) {
            throw new MatriculaException.StatusInvalidoException(
                "Matrícula já está cancelada");
        }
        
        matricula.setStatus(MatriculaStatus.CANCELADA);
        matriculaRepository.save(matricula);
    }
    
    public void ativarMatricula(Long id) {
        Matricula matricula = buscarMatriculaPorId(id);
        
        if (matricula.getStatus() == MatriculaStatus.ATIVA) {
            throw new MatriculaException.StatusInvalidoException(
                "Matrícula já está ativa");
        }
        
        // Não pode ativar uma matrícula que já expirou
        if (matricula.getDataFim().isBefore(LocalDate.now())) {
            throw new MatriculaException.DataInvalidaException(
                "Não é possível ativar uma matrícula expirada");
        }
        
        matricula.setStatus(MatriculaStatus.ATIVA);
        matriculaRepository.save(matricula);
    }
    
    public void inativarMatricula(Long id) {
        Matricula matricula = buscarMatriculaPorId(id);
        
        if (matricula.getStatus() == MatriculaStatus.INATIVA) {
            throw new MatriculaException.StatusInvalidoException(
                "Matrícula já está inativa");
        }
        
        matricula.setStatus(MatriculaStatus.INATIVA);
        matriculaRepository.save(matricula);
    }
    
    private void validarMatricula(Matricula matricula) {
        if (matricula.getAluno() == null) {
            throw new MatriculaException.MatriculaInvalidaException(
                "Aluno é obrigatório");
        }
        
        if (matricula.getPlano() == null) {
            throw new MatriculaException.MatriculaInvalidaException(
                "Plano é obrigatório");
        }
    }
    
    private void validarPlano(Plano plano) {
        planoRepository.findById(plano.getIdPlanoAssinatura())
            .filter(p -> "ATIVO".equals(p.getStatus()))
            .orElseThrow(() -> new MatriculaException.PlanoInvalidoException(
                "Plano não encontrado ou inativo"));
    }
    
    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new MatriculaException.DataInvalidaException(
                "Data de início e fim são obrigatórias");
        }
        
        if (dataInicio.isAfter(dataFim)) {
            throw new MatriculaException.DataInvalidaException(
                "Data de início deve ser anterior à data de fim");
        }
        
        // Para novas matrículas ou atualizações, não permitir datas no passado
        LocalDate hoje = LocalDate.now();
        if (dataInicio.isBefore(hoje)) {
            throw new MatriculaException.DataInvalidaException(
                "Data de início não pode ser no passado");
        }
    }

    private void validarDuracaoPlano(Plano plano, LocalDate dataInicio, LocalDate dataFim) {
        long mesesMatricula = ChronoUnit.MONTHS.between(dataInicio, dataFim);
        if (mesesMatricula != plano.getDuracaoMeses()) {
            throw new MatriculaException.DataInvalidaException(
                "Duração da matrícula deve corresponder à duração do plano: " + 
                plano.getDuracaoMeses() + " meses");
        }
    }

    public Matricula renovarMatricula(Long idMatricula) {
        Matricula atual = buscarMatriculaPorId(idMatricula);
        
        if (atual.getStatus() != MatriculaStatus.ATIVA) {
            throw new MatriculaException.StatusInvalidoException(
                "Apenas matrículas ativas podem ser renovadas");
        }
        
        // Validar se o plano ainda está ativo
        validarPlano(atual.getPlano());
        
        LocalDate novaDataInicio = atual.getDataFim().plusDays(1);
        LocalDate novaDataFim = novaDataInicio.plusMonths(atual.getPlano().getDuracaoMeses());
        
        Matricula novaMatricula = new Matricula(
            atual.getAluno(),
            atual.getPlano(),
            novaDataInicio,
            novaDataFim,
            MatriculaStatus.ATIVA
        );
        
        return matriculaRepository.save(novaMatricula);
    }
}