package com.example.demo;

import com.example.demo.entity.Aluno;
import com.example.demo.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Aluno e AlunoRepository
 */
@SpringBootTest
@Transactional
public class AlunoTest {

    @Autowired
    private AlunoRepository alunoRepository;

    @Test
    public void testCriarAluno() {
        System.out.println("=== TESTE: Criar Aluno ===");
        
        // Criar um novo aluno
        Aluno aluno = new Aluno("João Silva", "123.456.789-00", LocalDate.of(2025, 10, 1));
        
        // Salvar no banco
        Aluno alunoSalvo = alunoRepository.save(aluno);
        
        // Validações
        assertNotNull(alunoSalvo.getIdAluno(), "ID do aluno não deve ser nulo após salvar");
        assertEquals("João Silva", alunoSalvo.getNome());
        assertEquals("123.456.789-00", alunoSalvo.getCpf());
        assertEquals(LocalDate.of(2025, 10, 1), alunoSalvo.getDataIngresso());
        
        System.out.println("✅ Aluno criado: " + alunoSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCriarAlunoSemDataIngresso() {
        System.out.println("=== TESTE: Criar Aluno sem Data de Ingresso ===");
        
        // Criar aluno sem data de ingresso
        Aluno aluno = new Aluno("Maria Santos", "987.654.321-00");
        
        // Salvar no banco
        Aluno alunoSalvo = alunoRepository.save(aluno);
        
        // Validações
        assertNotNull(alunoSalvo.getIdAluno());
        assertEquals("Maria Santos", alunoSalvo.getNome());
        assertEquals("987.654.321-00", alunoSalvo.getCpf());
        assertNull(alunoSalvo.getDataIngresso(), "Data de ingresso deve ser null");
        
        System.out.println("✅ Aluno criado sem data de ingresso: " + alunoSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAlunoPorId() {
        System.out.println("=== TESTE: Buscar Aluno por ID ===");
        
        // Criar e salvar um aluno
        Aluno aluno = new Aluno("Carlos Oliveira", "111.222.333-44", LocalDate.of(2025, 9, 15));
        Aluno alunoSalvo = alunoRepository.save(aluno);
        
        // Buscar pelo ID
        Optional<Aluno> alunoEncontrado = alunoRepository.findById(alunoSalvo.getIdAluno());
        
        // Validações
        assertTrue(alunoEncontrado.isPresent(), "Aluno deve ser encontrado");
        assertEquals("Carlos Oliveira", alunoEncontrado.get().getNome());
        assertEquals("111.222.333-44", alunoEncontrado.get().getCpf());
        
        System.out.println("✅ Aluno encontrado: " + alunoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAlunoPorCpf() {
        System.out.println("=== TESTE: Buscar Aluno por CPF ===");
        
        // Criar e salvar um aluno
        Aluno aluno = new Aluno("Ana Paula", "555.666.777-88", LocalDate.of(2025, 8, 20));
        alunoRepository.save(aluno);
        
        // Buscar pelo CPF
        Optional<Aluno> alunoEncontrado = alunoRepository.findByCpf("555.666.777-88");
        
        // Validações
        assertTrue(alunoEncontrado.isPresent(), "Aluno deve ser encontrado pelo CPF");
        assertEquals("Ana Paula", alunoEncontrado.get().getNome());
        
        System.out.println("✅ Aluno encontrado: " + alunoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAlunoPorNome() {
        System.out.println("=== TESTE: Buscar Aluno por Nome ===");
        
        // Criar e salvar um aluno
        Aluno aluno = new Aluno("Pedro Costa", "222.333.444-55");
        alunoRepository.save(aluno);
        
        // Buscar pelo nome
        Optional<Aluno> alunoEncontrado = alunoRepository.findByNome("Pedro Costa");
        
        // Validações
        assertTrue(alunoEncontrado.isPresent(), "Aluno deve ser encontrado pelo nome");
        assertEquals("222.333.444-55", alunoEncontrado.get().getCpf());
        
        System.out.println("✅ Aluno encontrado: " + alunoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarAlunoPorNomeParcial() {
        System.out.println("=== TESTE: Buscar Aluno por Nome Parcial ===");
        
        // Criar alunos
        alunoRepository.save(new Aluno("Fernando Silva", "100.200.300-40"));
        alunoRepository.save(new Aluno("Fernanda Costa", "100.200.300-41"));
        alunoRepository.save(new Aluno("Roberto Lima", "100.200.300-42"));
        
        // Buscar alunos com "fern" no nome
        List<Aluno> alunosEncontrados = alunoRepository.findByNomeContainingIgnoreCase("fern");
        
        // Validações
        assertTrue(alunosEncontrados.size() >= 2, "Deve encontrar pelo menos 2 alunos");
        
        System.out.println("✅ Alunos com 'fern' no nome: " + alunosEncontrados.size());
        alunosEncontrados.forEach(a -> System.out.println("   - " + a));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarTodosAlunos() {
        System.out.println("=== TESTE: Listar Todos os Alunos ===");
        
        // Criar vários alunos
        alunoRepository.save(new Aluno("Aluno A", "400.500.600-70"));
        alunoRepository.save(new Aluno("Aluno B", "400.500.600-71"));
        alunoRepository.save(new Aluno("Aluno C", "400.500.600-72"));
        
        // Listar todos
        List<Aluno> alunos = alunoRepository.findAll();
        
        // Validações
        assertTrue(alunos.size() >= 3, "Deve ter pelo menos 3 alunos");
        
        System.out.println("✅ Total de alunos encontrados: " + alunos.size());
        alunos.forEach(a -> System.out.println("   - " + a));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarAlunosOrdenadosPorNome() {
        System.out.println("=== TESTE: Listar Alunos Ordenados por Nome ===");
        
        // Criar alunos com nomes diferentes
        alunoRepository.save(new Aluno("Zilda Costa", "700.800.900-01"));
        alunoRepository.save(new Aluno("Ana Maria", "700.800.900-02"));
        alunoRepository.save(new Aluno("Marcos Paulo", "700.800.900-03"));
        
        // Listar ordenados por nome
        List<Aluno> alunosOrdenados = alunoRepository.findAllByOrderByNomeAsc();
        
        // Validações
        assertTrue(alunosOrdenados.size() >= 3, "Deve ter pelo menos 3 alunos");
        
        // Verificar ordenação
        for (int i = 0; i < alunosOrdenados.size() - 1; i++) {
            assertTrue(
                alunosOrdenados.get(i).getNome().compareTo(alunosOrdenados.get(i + 1).getNome()) <= 0,
                "Alunos devem estar ordenados por nome"
            );
        }
        
        System.out.println("✅ Alunos ordenados por nome:");
        alunosOrdenados.forEach(a -> System.out.println("   - " + a.getNome()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarAluno() {
        System.out.println("=== TESTE: Atualizar Aluno ===");
        
        // Criar e salvar um aluno
        Aluno aluno = new Aluno("Lucas Martins", "800.900.100-11");
        Aluno alunoSalvo = alunoRepository.save(aluno);
        Long id = alunoSalvo.getIdAluno();
        
        // Atualizar a data de ingresso
        alunoSalvo.setDataIngresso(LocalDate.of(2025, 10, 15));
        alunoRepository.save(alunoSalvo);
        
        // Buscar novamente
        Optional<Aluno> alunoAtualizado = alunoRepository.findById(id);
        
        // Validações
        assertTrue(alunoAtualizado.isPresent());
        assertEquals(LocalDate.of(2025, 10, 15), alunoAtualizado.get().getDataIngresso());
        
        System.out.println("✅ Aluno atualizado: " + alunoAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarAluno() {
        System.out.println("=== TESTE: Deletar Aluno ===");
        
        // Criar e salvar um aluno
        Aluno aluno = new Aluno("Teste Delete", "999.888.777-66");
        Aluno alunoSalvo = alunoRepository.save(aluno);
        Long id = alunoSalvo.getIdAluno();
        
        // Deletar
        alunoRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<Aluno> alunoDeletado = alunoRepository.findById(id);
        
        // Validações
        assertFalse(alunoDeletado.isPresent(), "Aluno não deve existir após deleção");
        
        System.out.println("✅ Aluno deletado com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaPorCpf() {
        System.out.println("=== TESTE: Verificar Existência por CPF ===");
        
        // Criar e salvar um aluno
        alunoRepository.save(new Aluno("CPF Único", "123.123.123-12"));
        
        // Verificar existência
        boolean existe = alunoRepository.existsByCpf("123.123.123-12");
        boolean naoExiste = alunoRepository.existsByCpf("999.999.999-99");
        
        // Validações
        assertTrue(existe, "Deve encontrar o CPF existente");
        assertFalse(naoExiste, "Não deve encontrar CPF inexistente");
        
        System.out.println("✅ Verificação de existência por CPF funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCpfUnico() {
        System.out.println("=== TESTE: CPF Único (Constraint) ===");
        
        // Criar primeiro aluno
        Aluno aluno1 = new Aluno("Primeiro Aluno", "444.555.666-77");
        alunoRepository.save(aluno1);
        
        // Tentar criar outro aluno com mesmo CPF
        Aluno aluno2 = new Aluno("Segundo Aluno", "444.555.666-77");
        
        // Deve lançar exceção por CPF duplicado
        assertThrows(Exception.class, () -> {
            alunoRepository.save(aluno2);
            alunoRepository.flush(); // Força a execução imediata
        }, "Deve lançar exceção ao tentar salvar CPF duplicado");
        
        System.out.println("✅ Constraint de CPF único funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
