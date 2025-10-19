package com.example.demo;

import com.example.demo.entity.Plano;
import com.example.demo.repository.PlanoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Plano e PlanoRepository
 */
@SpringBootTest
@Transactional
public class PlanoTest {

    @Autowired
    private PlanoRepository planoRepository;

    @Test
    public void testCriarPlano() {
        System.out.println("=== TESTE: Criar Plano ===");
        
        // Criar um novo plano
        Plano plano = new Plano("Mensal", new BigDecimal("99.90"), 30);
        
        // Salvar no banco
        Plano planoSalvo = planoRepository.save(plano);
        
        // Validações
        assertNotNull(planoSalvo.getIdPlanoAssinatura(), "ID do plano não deve ser nulo após salvar");
        assertEquals("Mensal", planoSalvo.getNome());
        assertEquals(new BigDecimal("99.90"), planoSalvo.getValor());
        assertEquals(30, planoSalvo.getDuracaoDias());
        
        System.out.println("✅ Plano criado: " + planoSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanoPorId() {
        System.out.println("=== TESTE: Buscar Plano por ID ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano("Trimestral", new BigDecimal("249.90"), 90);
        Plano planoSalvo = planoRepository.save(plano);
        
        // Buscar pelo ID
        Optional<Plano> planoEncontrado = planoRepository.findById(planoSalvo.getIdPlanoAssinatura());
        
        // Validações
        assertTrue(planoEncontrado.isPresent(), "Plano deve ser encontrado");
        assertEquals("Trimestral", planoEncontrado.get().getNome());
        
        System.out.println("✅ Plano encontrado: " + planoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanoPorNome() {
        System.out.println("=== TESTE: Buscar Plano por Nome ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano("Semestral", new BigDecimal("449.90"), 180);
        planoRepository.save(plano);
        
        // Buscar pelo nome
        Optional<Plano> planoEncontrado = planoRepository.findByNome("Semestral");
        
        // Validações
        assertTrue(planoEncontrado.isPresent(), "Plano deve ser encontrado pelo nome");
        assertEquals(new BigDecimal("449.90"), planoEncontrado.get().getValor());
        
        System.out.println("✅ Plano encontrado: " + planoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarTodosPlanos() {
        System.out.println("=== TESTE: Listar Todos os Planos ===");
        
        // Criar vários planos
        planoRepository.save(new Plano("Plano A", new BigDecimal("79.90"), 30));
        planoRepository.save(new Plano("Plano B", new BigDecimal("199.90"), 90));
        planoRepository.save(new Plano("Plano C", new BigDecimal("149.90"), 60));
        
        // Listar todos
        List<Plano> planos = planoRepository.findAll();
        
        // Validações
        assertTrue(planos.size() >= 3, "Deve ter pelo menos 3 planos");
        
        System.out.println("✅ Total de planos encontrados: " + planos.size());
        planos.forEach(p -> System.out.println("   - " + p));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarPlanosOrdenadosPorValor() {
        System.out.println("=== TESTE: Listar Planos Ordenados por Valor ===");
        
        // Criar planos com valores diferentes
        planoRepository.save(new Plano("Premium", new BigDecimal("299.90"), 90));
        planoRepository.save(new Plano("Basic", new BigDecimal("99.90"), 30));
        planoRepository.save(new Plano("Standard", new BigDecimal("179.90"), 60));
        
        // Listar ordenados por valor
        List<Plano> planosOrdenados = planoRepository.findAllByOrderByValorAsc();
        
        // Validações
        assertTrue(planosOrdenados.size() >= 3, "Deve ter pelo menos 3 planos");
        
        // Verificar ordenação
        for (int i = 0; i < planosOrdenados.size() - 1; i++) {
            assertTrue(
                planosOrdenados.get(i).getValor().compareTo(planosOrdenados.get(i + 1).getValor()) <= 0,
                "Planos devem estar ordenados por valor crescente"
            );
        }
        
        System.out.println("✅ Planos ordenados por valor:");
        planosOrdenados.forEach(p -> System.out.println("   - " + p));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarPlano() {
        System.out.println("=== TESTE: Atualizar Plano ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano("Anual", new BigDecimal("799.90"), 365);
        Plano planoSalvo = planoRepository.save(plano);
        Long id = planoSalvo.getIdPlanoAssinatura();
        
        // Atualizar o valor
        planoSalvo.setValor(new BigDecimal("699.90"));
        planoRepository.save(planoSalvo);
        
        // Buscar novamente
        Optional<Plano> planoAtualizado = planoRepository.findById(id);
        
        // Validações
        assertTrue(planoAtualizado.isPresent());
        assertEquals(new BigDecimal("699.90"), planoAtualizado.get().getValor());
        
        System.out.println("✅ Plano atualizado: " + planoAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarPlano() {
        System.out.println("=== TESTE: Deletar Plano ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano("Teste Delete", new BigDecimal("50.00"), 15);
        Plano planoSalvo = planoRepository.save(plano);
        Long id = planoSalvo.getIdPlanoAssinatura();
        
        // Deletar
        planoRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<Plano> planoDeletado = planoRepository.findById(id);
        
        // Validações
        assertFalse(planoDeletado.isPresent(), "Plano não deve existir após deleção");
        
        System.out.println("✅ Plano deletado com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanosPorDuracao() {
        System.out.println("=== TESTE: Buscar Planos por Duração ===");
        
        // Criar planos com mesma duração
        planoRepository.save(new Plano("Mensal A", new BigDecimal("89.90"), 30));
        planoRepository.save(new Plano("Mensal B", new BigDecimal("99.90"), 30));
        planoRepository.save(new Plano("Trimestral", new BigDecimal("249.90"), 90));
        
        // Buscar planos de 30 dias
        List<Plano> planosMensais = planoRepository.findByDuracaoDias(30);
        
        // Validações
        assertTrue(planosMensais.size() >= 2, "Deve ter pelo menos 2 planos mensais");
        planosMensais.forEach(p -> assertEquals(30, p.getDuracaoDias()));
        
        System.out.println("✅ Planos de 30 dias encontrados: " + planosMensais.size());
        planosMensais.forEach(p -> System.out.println("   - " + p));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
