package com.example.demo;

import com.example.demo.entity.Instrutor;
import com.example.demo.repository.InstrutorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Instrutor e InstrutorRepository
 */
@SpringBootTest
@Transactional
public class InstrutorTest {

    @Autowired
    private InstrutorRepository instrutorRepository;

    @Test
    public void testCriarInstrutor() {
        System.out.println("=== TESTE: Criar Instrutor ===");
        
        // Criar um novo instrutor
        Instrutor instrutor = new Instrutor("João Silva", "Musculação");
        
        // Salvar no banco
        Instrutor instrutorSalvo = instrutorRepository.save(instrutor);
        
        // Validações
        assertNotNull(instrutorSalvo.getIdInstrutor(), "ID do instrutor não deve ser nulo após salvar");
        assertEquals("João Silva", instrutorSalvo.getNome());
        assertEquals("Musculação", instrutorSalvo.getEspecialidade());
        
        System.out.println("✅ Instrutor criado: " + instrutorSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCriarInstrutorSemEspecialidade() {
        System.out.println("=== TESTE: Criar Instrutor sem Especialidade ===");
        
        // Criar instrutor sem especialidade
        Instrutor instrutor = new Instrutor("Maria Santos", null);
        
        // Salvar no banco
        Instrutor instrutorSalvo = instrutorRepository.save(instrutor);
        
        // Validações
        assertNotNull(instrutorSalvo.getIdInstrutor());
        assertEquals("Maria Santos", instrutorSalvo.getNome());
        assertNull(instrutorSalvo.getEspecialidade());
        
        System.out.println("✅ Instrutor criado sem especialidade: " + instrutorSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarInstrutorPorId() {
        System.out.println("=== TESTE: Buscar Instrutor por ID ===");
        
        // Criar e salvar um instrutor
        Instrutor instrutor = new Instrutor("Carlos Oliveira", "Crossfit");
        Instrutor instrutorSalvo = instrutorRepository.save(instrutor);
        
        // Buscar pelo ID
        Optional<Instrutor> instrutorEncontrado = instrutorRepository.findById(instrutorSalvo.getIdInstrutor());
        
        // Validações
        assertTrue(instrutorEncontrado.isPresent(), "Instrutor deve ser encontrado");
        assertEquals("Carlos Oliveira", instrutorEncontrado.get().getNome());
        assertEquals("Crossfit", instrutorEncontrado.get().getEspecialidade());
        
        System.out.println("✅ Instrutor encontrado: " + instrutorEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarInstrutorPorNome() {
        System.out.println("=== TESTE: Buscar Instrutor por Nome ===");
        
        // Criar e salvar um instrutor
        Instrutor instrutor = new Instrutor("Ana Paula", "Yoga");
        instrutorRepository.save(instrutor);
        
        // Buscar pelo nome
        Optional<Instrutor> instrutorEncontrado = instrutorRepository.findByNome("Ana Paula");
        
        // Validações
        assertTrue(instrutorEncontrado.isPresent(), "Instrutor deve ser encontrado pelo nome");
        assertEquals("Yoga", instrutorEncontrado.get().getEspecialidade());
        
        System.out.println("✅ Instrutor encontrado: " + instrutorEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarInstrutoresPorEspecialidade() {
        System.out.println("=== TESTE: Buscar Instrutores por Especialidade ===");
        
        // Criar vários instrutores com mesma especialidade
        instrutorRepository.save(new Instrutor("Pedro Costa", "Natação"));
        instrutorRepository.save(new Instrutor("Julia Mendes", "Natação"));
        instrutorRepository.save(new Instrutor("Roberto Lima", "Pilates"));
        
        // Buscar instrutores de Natação
        List<Instrutor> instrutoresNatacao = instrutorRepository.findByEspecialidade("Natação");
        
        // Validações
        assertTrue(instrutoresNatacao.size() >= 2, "Deve ter pelo menos 2 instrutores de Natação");
        instrutoresNatacao.forEach(i -> assertEquals("Natação", i.getEspecialidade()));
        
        System.out.println("✅ Instrutores de Natação encontrados: " + instrutoresNatacao.size());
        instrutoresNatacao.forEach(i -> System.out.println("   - " + i));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarInstrutorPorNomeParcial() {
        System.out.println("=== TESTE: Buscar Instrutor por Nome Parcial ===");
        
        // Criar instrutores
        instrutorRepository.save(new Instrutor("Fernando Silva", "Boxe"));
        instrutorRepository.save(new Instrutor("Fernanda Costa", "Zumba"));
        instrutorRepository.save(new Instrutor("Carlos Ferreira", "Karatê"));
        
        // Buscar instrutores com "fern" no nome
        List<Instrutor> instrutoresEncontrados = instrutorRepository.findByNomeContainingIgnoreCase("fern");
        
        // Validações
        assertTrue(instrutoresEncontrados.size() >= 2, "Deve encontrar pelo menos 2 instrutores");
        
        System.out.println("✅ Instrutores com 'fern' no nome: " + instrutoresEncontrados.size());
        instrutoresEncontrados.forEach(i -> System.out.println("   - " + i));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarTodosInstrutores() {
        System.out.println("=== TESTE: Listar Todos os Instrutores ===");
        
        // Criar vários instrutores
        instrutorRepository.save(new Instrutor("Instrutor A", "Dança"));
        instrutorRepository.save(new Instrutor("Instrutor B", "Spinning"));
        instrutorRepository.save(new Instrutor("Instrutor C", "Funcional"));
        
        // Listar todos
        List<Instrutor> instrutores = instrutorRepository.findAll();
        
        // Validações
        assertTrue(instrutores.size() >= 3, "Deve ter pelo menos 3 instrutores");
        
        System.out.println("✅ Total de instrutores encontrados: " + instrutores.size());
        instrutores.forEach(i -> System.out.println("   - " + i));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarInstrutor() {
        System.out.println("=== TESTE: Atualizar Instrutor ===");
        
        // Criar e salvar um instrutor
        Instrutor instrutor = new Instrutor("Lucas Martins", "Ginástica");
        Instrutor instrutorSalvo = instrutorRepository.save(instrutor);
        Long id = instrutorSalvo.getIdInstrutor();
        
        // Atualizar a especialidade
        instrutorSalvo.setEspecialidade("Ginástica Artística");
        instrutorRepository.save(instrutorSalvo);
        
        // Buscar novamente
        Optional<Instrutor> instrutorAtualizado = instrutorRepository.findById(id);
        
        // Validações
        assertTrue(instrutorAtualizado.isPresent());
        assertEquals("Ginástica Artística", instrutorAtualizado.get().getEspecialidade());
        
        System.out.println("✅ Instrutor atualizado: " + instrutorAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarInstrutor() {
        System.out.println("=== TESTE: Deletar Instrutor ===");
        
        // Criar e salvar um instrutor
        Instrutor instrutor = new Instrutor("Teste Delete", "Teste");
        Instrutor instrutorSalvo = instrutorRepository.save(instrutor);
        Long id = instrutorSalvo.getIdInstrutor();
        
        // Deletar
        instrutorRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<Instrutor> instrutorDeletado = instrutorRepository.findById(id);
        
        // Validações
        assertFalse(instrutorDeletado.isPresent(), "Instrutor não deve existir após deleção");
        
        System.out.println("✅ Instrutor deletado com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaPorNome() {
        System.out.println("=== TESTE: Verificar Existência por Nome ===");
        
        // Criar e salvar um instrutor
        instrutorRepository.save(new Instrutor("Instrutor Único", "Especialidade"));
        
        // Verificar existência
        boolean existe = instrutorRepository.existsByNome("Instrutor Único");
        boolean naoExiste = instrutorRepository.existsByNome("Nome Inexistente");
        
        // Validações
        assertTrue(existe, "Deve encontrar o instrutor existente");
        assertFalse(naoExiste, "Não deve encontrar instrutor inexistente");
        
        System.out.println("✅ Verificação de existência funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
