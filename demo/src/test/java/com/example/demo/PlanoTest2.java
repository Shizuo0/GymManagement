package com.example.demo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Plano;
import com.example.demo.repository.PlanoRepository;

/**
 * Testes para a entidade Plano e PlanoRepository
 */
@SpringBootTest
@Transactional
public class PlanoTest2 {

    @Autowired
    private PlanoRepository planoRepository;

    @Test
    public void testCriarPlano() {
        System.out.println("=== TESTE: Criar Plano ===");
        
        // Criar um novo plano
        Plano plano = new Plano(
            "Plano Mensal Premium", 
            "Acesso ilimitado à academia durante 1 mês com direito a todas as atividades",
            new BigDecimal("99.90"), 
            1
        );
        
        // Salvar no banco
        Plano planoSalvo = planoRepository.save(plano);
        
        // Validações
        assertNotNull(planoSalvo.getIdPlanoAssinatura(), "ID do plano não deve ser nulo após salvar");
        assertEquals("Plano Mensal Premium", planoSalvo.getNome());
        assertNotNull(planoSalvo.getDescricao());
        assertEquals(new BigDecimal("99.90"), planoSalvo.getValor());
        assertEquals(1, planoSalvo.getDuracaoMeses());
        assertEquals("ATIVO", planoSalvo.getStatus());
        
        System.out.println("✅ Plano criado: " + planoSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanoPorId() {
        System.out.println("=== TESTE: Buscar Plano por ID ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano(
            "Plano Trimestral",
            "Acesso completo por 3 meses com desconto especial",
            new BigDecimal("249.90"),
            3
        );
        Plano planoSalvo = planoRepository.save(plano);
        
        // Buscar pelo ID
        Optional<Plano> planoEncontrado = planoRepository.findById(planoSalvo.getIdPlanoAssinatura());
        
        // Validações
        assertTrue(planoEncontrado.isPresent(), "Plano deve ser encontrado");
        assertEquals("Plano Trimestral", planoEncontrado.get().getNome());
        
        System.out.println("✅ Plano encontrado: " + planoEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPlanoPorNome() {
        System.out.println("=== TESTE: Buscar Plano por Nome ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano(
            "Plano Semestral",
            "Melhor custo-benefício: 6 meses de academia com super desconto",
            new BigDecimal("449.90"),
            6
        );
        planoRepository.save(plano);
        
        // Buscar pelo nome
        Optional<Plano> planoEncontrado = planoRepository.findByNome("Plano Semestral");
        
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
        planoRepository.save(new Plano("Plano Básico", "Acesso básico à academia", new BigDecimal("79.90"), 1));
        planoRepository.save(new Plano("Plano Plus", "Acesso plus com aulas em grupo", new BigDecimal("199.90"), 3));
        planoRepository.save(new Plano("Plano Premium", "Acesso total com personal trainer", new BigDecimal("149.90"), 2));
        
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
        
        // Criar vários planos com diferentes valores
        planoRepository.save(new Plano("Premium Elite", "Acesso VIP completo", new BigDecimal("299.90"), 3));
        planoRepository.save(new Plano("Básico Fit", "Acesso básico econômico", new BigDecimal("99.90"), 1));
        planoRepository.save(new Plano("Intermediário Plus", "Acesso intermediário com benefícios", new BigDecimal("179.90"), 2));
        
        // Buscar ordenado por valor
        List<Plano> planos = planoRepository.findAllByOrderByValorAsc();
        
        // Validações
        assertTrue(planos.size() >= 3, "Deve ter pelo menos 3 planos");
        for (int i = 1; i < planos.size(); i++) {
            assertTrue(planos.get(i).getValor().compareTo(planos.get(i-1).getValor()) >= 0,
                    "Planos devem estar ordenados por valor");
        }
        
        System.out.println("✅ Planos ordenados por valor:");
        planos.forEach(p -> System.out.println("   - " + p.getNome() + ": R$ " + p.getValor()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarPlano() {
        System.out.println("=== TESTE: Atualizar Plano ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano(
            "Plano Anual",
            "1 ano de academia com máximo desconto e benefícios exclusivos",
            new BigDecimal("799.90"),
            12
        );
        Plano planoSalvo = planoRepository.save(plano);
        Long id = planoSalvo.getIdPlanoAssinatura();
        
        // Atualizar o valor e status
        planoSalvo.setValor(new BigDecimal("699.90"));
        planoSalvo.setStatus("INATIVO");
        planoRepository.save(planoSalvo);
        
        // Buscar novamente
        Optional<Plano> planoAtualizado = planoRepository.findById(id);
        
        // Validações
        assertTrue(planoAtualizado.isPresent());
        assertEquals(new BigDecimal("699.90"), planoAtualizado.get().getValor());
        assertEquals("INATIVO", planoAtualizado.get().getStatus());
        
        System.out.println("✅ Plano atualizado: " + planoAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarPlano() {
        System.out.println("=== TESTE: Deletar Plano ===");
        
        // Criar e salvar um plano
        Plano plano = new Plano(
            "Plano Teste",
            "Plano para teste de deleção",
            new BigDecimal("50.00"),
            1
        );
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
        planoRepository.save(new Plano(
            "Mensal Básico",
            "Plano mensal com acesso básico",
            new BigDecimal("89.90"),
            1
        ));
        planoRepository.save(new Plano(
            "Mensal Premium",
            "Plano mensal com acesso premium",
            new BigDecimal("99.90"),
            1
        ));
        
        // Buscar planos
        List<Plano> planos = planoRepository.findByDuracaoMeses(1);
        
        // Validações
        assertTrue(planos.size() >= 2, "Deve ter pelo menos 2 planos com duração de 1 mês");
        planos.forEach(p -> assertEquals(1, p.getDuracaoMeses()));
        
        System.out.println("✅ Planos com duração de 1 mês: " + planos.size());
        planos.forEach(p -> System.out.println("   - " + p));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
    
    @Test
    public void testBuscarPlanosPorStatus() {
        System.out.println("=== TESTE: Buscar Planos por Status ===");
        
        // Criar planos com diferentes status
        Plano planoAtivo = new Plano(
            "Plano Ativo",
            "Plano disponível para compra",
            new BigDecimal("99.90"),
            1
        );
        
        Plano planoInativo = new Plano(
            "Plano Inativo",
            "Plano temporariamente indisponível",
            new BigDecimal("149.90"),
            3
        );
        planoInativo.setStatus("INATIVO");
        
        planoRepository.save(planoAtivo);
        planoRepository.save(planoInativo);
        
        // Buscar planos ativos
        List<Plano> planosAtivos = planoRepository.findByStatus("ATIVO");
        List<Plano> planosInativos = planoRepository.findByStatus("INATIVO");
        
        // Validações
        assertTrue(planosAtivos.size() >= 1, "Deve ter pelo menos 1 plano ativo");
        assertTrue(planosInativos.size() >= 1, "Deve ter pelo menos 1 plano inativo");
        
        planosAtivos.forEach(p -> assertEquals("ATIVO", p.getStatus()));
        planosInativos.forEach(p -> assertEquals("INATIVO", p.getStatus()));
        
        System.out.println("✅ Planos por status:");
        System.out.println("Ativos:");
        planosAtivos.forEach(p -> System.out.println("   - " + p));
        System.out.println("Inativos:");
        planosInativos.forEach(p -> System.out.println("   - " + p));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}