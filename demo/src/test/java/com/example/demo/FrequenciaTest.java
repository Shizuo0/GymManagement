package com.example.demo;

import com.example.demo.entity.Aluno;
import com.example.demo.entity.Frequencia;
import com.example.demo.repository.AlunoRepository;
import com.example.demo.repository.FrequenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Frequencia e FrequenciaRepository
 */
@SpringBootTest
@Transactional
public class FrequenciaTest {

    @Autowired
    private FrequenciaRepository frequenciaRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;
    
    private Aluno alunoTeste;

    @BeforeEach
    public void setUp() {
        // Criar aluno para usar nos testes
        alunoTeste = new Aluno("Pedro Frequência", "101.202.303-40", LocalDate.now());
        alunoTeste = alunoRepository.save(alunoTeste);
    }

    @Test
    public void testCriarFrequenciaComPresenca() {
        System.out.println("=== TESTE: Criar Frequência com Presença ===");
        
        // Criar um registro de presença
        Frequencia frequencia = new Frequencia(alunoTeste, LocalDate.of(2025, 10, 15), true);
        
        // Salvar no banco
        Frequencia frequenciaSalva = frequenciaRepository.save(frequencia);
        
        // Validações
        assertNotNull(frequenciaSalva.getIdFrequencia(), "ID da frequência não deve ser nulo após salvar");
        assertNotNull(frequenciaSalva.getAluno());
        assertEquals(LocalDate.of(2025, 10, 15), frequenciaSalva.getData());
        assertTrue(frequenciaSalva.getPresenca(), "Presença deve ser true");
        
        System.out.println("✅ Frequência com presença criada: " + frequenciaSalva);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCriarFrequenciaComAusencia() {
        System.out.println("=== TESTE: Criar Frequência com Ausência ===");
        
        // Criar um registro de ausência
        Frequencia frequencia = new Frequencia(alunoTeste, LocalDate.of(2025, 10, 16), false);
        
        // Salvar no banco
        Frequencia frequenciaSalva = frequenciaRepository.save(frequencia);
        
        // Validações
        assertNotNull(frequenciaSalva.getIdFrequencia());
        assertFalse(frequenciaSalva.getPresenca(), "Presença deve ser false");
        
        System.out.println("✅ Frequência com ausência criada: " + frequenciaSalva);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarFrequenciaPorId() {
        System.out.println("=== TESTE: Buscar Frequência por ID ===");
        
        // Criar e salvar uma frequência
        Frequencia frequencia = new Frequencia(alunoTeste, LocalDate.now(), true);
        Frequencia frequenciaSalva = frequenciaRepository.save(frequencia);
        
        // Buscar pelo ID
        Optional<Frequencia> frequenciaEncontrada = frequenciaRepository.findById(frequenciaSalva.getIdFrequencia());
        
        // Validações
        assertTrue(frequenciaEncontrada.isPresent(), "Frequência deve ser encontrada");
        assertTrue(frequenciaEncontrada.get().getPresenca());
        
        System.out.println("✅ Frequência encontrada: " + frequenciaEncontrada.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarFrequenciasPorAluno() {
        System.out.println("=== TESTE: Buscar Frequências por Aluno ===");
        
        // Criar vários registros para o aluno
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 1), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 2), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 3), false));
        
        // Buscar frequências do aluno
        List<Frequencia> frequencias = frequenciaRepository.findByAluno(alunoTeste);
        
        // Validações
        assertTrue(frequencias.size() >= 3, "Deve ter pelo menos 3 registros");
        frequencias.forEach(f -> assertEquals(alunoTeste.getIdAluno(), f.getAluno().getIdAluno()));
        
        System.out.println("✅ Frequências do aluno encontradas: " + frequencias.size());
        frequencias.forEach(f -> System.out.println("   - " + f));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarFrequenciaPorAlunoEData() {
        System.out.println("=== TESTE: Buscar Frequência por Aluno e Data ===");
        
        LocalDate dataEspecifica = LocalDate.of(2025, 10, 20);
        
        // Criar registro
        frequenciaRepository.save(new Frequencia(alunoTeste, dataEspecifica, true));
        
        // Buscar por aluno e data
        Optional<Frequencia> frequencia = frequenciaRepository.findByAlunoAndData(alunoTeste, dataEspecifica);
        
        // Validações
        assertTrue(frequencia.isPresent(), "Deve encontrar registro");
        assertEquals(dataEspecifica, frequencia.get().getData());
        assertTrue(frequencia.get().getPresenca());
        
        System.out.println("✅ Frequência encontrada: " + frequencia.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarFrequenciasPorData() {
        System.out.println("=== TESTE: Buscar Frequências por Data ===");
        
        LocalDate data = LocalDate.of(2025, 10, 18);
        
        // Criar registros de vários alunos na mesma data
        Aluno aluno2 = alunoRepository.save(new Aluno("Ana Teste", "202.303.404-50"));
        
        frequenciaRepository.save(new Frequencia(alunoTeste, data, true));
        frequenciaRepository.save(new Frequencia(aluno2, data, true));
        
        // Buscar frequências da data
        List<Frequencia> frequencias = frequenciaRepository.findByData(data);
        
        // Validações
        assertTrue(frequencias.size() >= 2, "Deve ter pelo menos 2 registros");
        frequencias.forEach(f -> assertEquals(data, f.getData()));
        
        System.out.println("✅ Frequências na data " + data + ": " + frequencias.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarFrequenciasPorPeriodo() {
        System.out.println("=== TESTE: Buscar Frequências por Período ===");
        
        // Criar registros em diferentes datas
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 5), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 15), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 25), false));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 11, 5), true));
        
        // Buscar frequências de outubro
        List<Frequencia> frequenciasOutubro = frequenciaRepository.findByDataBetween(
            LocalDate.of(2025, 10, 1), 
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(frequenciasOutubro.size() >= 3, "Deve ter pelo menos 3 registros em outubro");
        
        System.out.println("✅ Frequências em outubro: " + frequenciasOutubro.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarFrequenciasAlunoPorPeriodo() {
        System.out.println("=== TESTE: Buscar Frequências do Aluno por Período ===");
        
        // Criar registros
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 5), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 15), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 11, 5), true));
        
        // Buscar frequências do aluno em outubro
        List<Frequencia> frequencias = frequenciaRepository.findByAlunoAndDataBetween(
            alunoTeste,
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(frequencias.size() >= 2, "Deve ter pelo menos 2 registros");
        frequencias.forEach(f -> {
            assertEquals(alunoTeste.getIdAluno(), f.getAluno().getIdAluno());
            assertTrue(f.getData().getMonthValue() == 10);
        });
        
        System.out.println("✅ Frequências do aluno em outubro: " + frequencias.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPorPresenca() {
        System.out.println("=== TESTE: Buscar por Presença ===");
        
        // Criar registros com presenças e ausências
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 1), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 2), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 3), false));
        
        // Buscar apenas presenças
        List<Frequencia> presencas = frequenciaRepository.findByPresenca(true);
        
        // Validações
        assertTrue(presencas.size() >= 2, "Deve ter pelo menos 2 presenças");
        presencas.forEach(f -> assertTrue(f.getPresenca()));
        
        System.out.println("✅ Total de presenças: " + presencas.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPresencasDoAluno() {
        System.out.println("=== TESTE: Buscar Presenças do Aluno ===");
        
        // Criar registros mistos
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 1), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 2), false));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 3), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 4), true));
        
        // Buscar apenas presenças do aluno
        List<Frequencia> presencas = frequenciaRepository.findByAlunoAndPresenca(alunoTeste, true);
        
        // Validações
        assertTrue(presencas.size() >= 3, "Deve ter pelo menos 3 presenças");
        presencas.forEach(f -> {
            assertEquals(alunoTeste.getIdAluno(), f.getAluno().getIdAluno());
            assertTrue(f.getPresenca());
        });
        
        System.out.println("✅ Presenças do aluno: " + presencas.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarFrequenciasAlunoOrdenadas() {
        System.out.println("=== TESTE: Buscar Frequências do Aluno Ordenadas ===");
        
        // Criar registros em ordem não sequencial
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 15), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 5), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 25), false));
        
        // Buscar ordenadas
        List<Frequencia> frequenciasOrdenadas = frequenciaRepository.findByAlunoOrderByDataDesc(alunoTeste);
        
        // Validações
        assertTrue(frequenciasOrdenadas.size() >= 3, "Deve ter pelo menos 3 registros");
        
        // Verificar ordenação decrescente
        for (int i = 0; i < frequenciasOrdenadas.size() - 1; i++) {
            assertTrue(
                frequenciasOrdenadas.get(i).getData()
                    .compareTo(frequenciasOrdenadas.get(i + 1).getData()) >= 0,
                "Frequências devem estar ordenadas por data decrescente"
            );
        }
        
        System.out.println("✅ Frequências ordenadas (mais recentes primeiro):");
        frequenciasOrdenadas.forEach(f -> System.out.println("   - " + f.getData() + " | Presença: " + f.getPresenca()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarFrequenciasDoAluno() {
        System.out.println("=== TESTE: Contar Frequências do Aluno ===");
        
        // Criar vários registros
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 1), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 2), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 3), false));
        
        // Contar frequências
        long count = frequenciaRepository.countByAluno(alunoTeste);
        
        // Validações
        assertTrue(count >= 3, "Deve ter pelo menos 3 registros");
        
        System.out.println("✅ Total de registros do aluno: " + count);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarPresencasDoAluno() {
        System.out.println("=== TESTE: Contar Presenças do Aluno ===");
        
        // Criar registros mistos
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 1), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 2), false));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 3), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 4), true));
        
        // Contar apenas presenças
        long presencas = frequenciaRepository.countByAlunoAndPresenca(alunoTeste, true);
        long ausencias = frequenciaRepository.countByAlunoAndPresenca(alunoTeste, false);
        
        // Validações
        assertTrue(presencas >= 3, "Deve ter pelo menos 3 presenças");
        assertTrue(ausencias >= 1, "Deve ter pelo menos 1 ausência");
        
        System.out.println("✅ Presenças: " + presencas + " | Ausências: " + ausencias);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaFrequencia() {
        System.out.println("=== TESTE: Verificar Existência de Frequência ===");
        
        LocalDate dataComRegistro = LocalDate.of(2025, 10, 18);
        LocalDate dataSemRegistro = LocalDate.of(2025, 12, 25);
        
        // Criar registro
        frequenciaRepository.save(new Frequencia(alunoTeste, dataComRegistro, true));
        
        // Verificar existência
        boolean existe = frequenciaRepository.existsByAlunoAndData(alunoTeste, dataComRegistro);
        boolean naoExiste = frequenciaRepository.existsByAlunoAndData(alunoTeste, dataSemRegistro);
        
        // Validações
        assertTrue(existe, "Deve existir registro na data " + dataComRegistro);
        assertFalse(naoExiste, "Não deve existir registro na data " + dataSemRegistro);
        
        System.out.println("✅ Verificação de existência funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarPresencasNoPeriodo() {
        System.out.println("=== TESTE: Contar Presenças no Período (Query Custom) ===");
        
        // Criar registros em outubro
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 1), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 5), false));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 10), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 10, 15), true));
        frequenciaRepository.save(new Frequencia(alunoTeste, LocalDate.of(2025, 11, 1), true));
        
        // Contar presenças de outubro
        long presencasOutubro = frequenciaRepository.contarPresencasNoPeriodo(
            alunoTeste,
            LocalDate.of(2025, 10, 1),
            LocalDate.of(2025, 10, 31)
        );
        
        // Validações
        assertTrue(presencasOutubro >= 3, "Deve ter pelo menos 3 presenças em outubro");
        
        System.out.println("✅ Presenças em outubro: " + presencasOutubro);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAlunosPresentesNaData() {
        System.out.println("=== TESTE: Buscar Alunos Presentes na Data ===");
        
        LocalDate data = LocalDate.of(2025, 10, 20);
        
        // Criar vários alunos e registros
        Aluno aluno2 = alunoRepository.save(new Aluno("Carlos Teste", "303.404.505-60"));
        Aluno aluno3 = alunoRepository.save(new Aluno("Joana Teste", "404.505.606-70"));
        
        frequenciaRepository.save(new Frequencia(alunoTeste, data, true));
        frequenciaRepository.save(new Frequencia(aluno2, data, true));
        frequenciaRepository.save(new Frequencia(aluno3, data, false));
        
        // Buscar alunos presentes
        List<Frequencia> presentes = frequenciaRepository.findByDataAndPresenca(data, true);
        
        // Validações
        assertTrue(presentes.size() >= 2, "Deve ter pelo menos 2 alunos presentes");
        presentes.forEach(f -> {
            assertEquals(data, f.getData());
            assertTrue(f.getPresenca());
        });
        
        System.out.println("✅ Alunos presentes na data " + data + ": " + presentes.size());
        presentes.forEach(f -> System.out.println("   - " + f.getAluno().getNome()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarFrequencia() {
        System.out.println("=== TESTE: Atualizar Frequência ===");
        
        // Criar e salvar uma frequência
        Frequencia frequencia = new Frequencia(alunoTeste, LocalDate.now(), false);
        Frequencia frequenciaSalva = frequenciaRepository.save(frequencia);
        Long id = frequenciaSalva.getIdFrequencia();
        
        // Atualizar para presença
        frequenciaSalva.setPresenca(true);
        frequenciaRepository.save(frequenciaSalva);
        
        // Buscar novamente
        Optional<Frequencia> frequenciaAtualizada = frequenciaRepository.findById(id);
        
        // Validações
        assertTrue(frequenciaAtualizada.isPresent());
        assertTrue(frequenciaAtualizada.get().getPresenca());
        
        System.out.println("✅ Frequência atualizada: " + frequenciaAtualizada.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarFrequencia() {
        System.out.println("=== TESTE: Deletar Frequência ===");
        
        // Criar e salvar uma frequência
        Frequencia frequencia = new Frequencia(alunoTeste, LocalDate.now(), true);
        Frequencia frequenciaSalva = frequenciaRepository.save(frequencia);
        Long id = frequenciaSalva.getIdFrequencia();
        
        // Deletar
        frequenciaRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<Frequencia> frequenciaDeletada = frequenciaRepository.findById(id);
        
        // Validações
        assertFalse(frequenciaDeletada.isPresent(), "Frequência não deve existir após deleção");
        
        System.out.println("✅ Frequência deletada com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
