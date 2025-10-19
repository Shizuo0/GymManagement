package com.example.demo;

import com.example.demo.entity.Exercicio;
import com.example.demo.repository.ExercicioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Exercicio e ExercicioRepository
 */
@SpringBootTest
@Transactional
public class ExercicioTest {

    @Autowired
    private ExercicioRepository exercicioRepository;

    @Test
    public void testCriarExercicioCompleto() {
        System.out.println("=== TESTE: Criar Exercício Completo ===");
        
        // Criar exercício com nome e grupo muscular
        Exercicio exercicio = new Exercicio("Supino Reto", "Peito");
        
        // Salvar no banco
        Exercicio exercicioSalvo = exercicioRepository.save(exercicio);
        
        // Validações
        assertNotNull(exercicioSalvo.getIdExercicio(), "ID do exercício não deve ser nulo após salvar");
        assertEquals("Supino Reto", exercicioSalvo.getNome());
        assertEquals("Peito", exercicioSalvo.getGrupoMuscular());
        
        System.out.println("✅ Exercício completo criado: " + exercicioSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testCriarExercicioSemGrupoMuscular() {
        System.out.println("=== TESTE: Criar Exercício sem Grupo Muscular ===");
        
        // Criar exercício apenas com nome
        Exercicio exercicio = new Exercicio("Alongamento Geral");
        
        // Salvar no banco
        Exercicio exercicioSalvo = exercicioRepository.save(exercicio);
        
        // Validações
        assertNotNull(exercicioSalvo.getIdExercicio());
        assertEquals("Alongamento Geral", exercicioSalvo.getNome());
        assertNull(exercicioSalvo.getGrupoMuscular(), "Grupo muscular pode ser nulo");
        
        System.out.println("✅ Exercício sem grupo criado: " + exercicioSalvo);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarExercicioPorId() {
        System.out.println("=== TESTE: Buscar Exercício por ID ===");
        
        // Criar e salvar exercício
        Exercicio exercicio = new Exercicio("Agachamento", "Pernas");
        Exercicio exercicioSalvo = exercicioRepository.save(exercicio);
        
        // Buscar pelo ID
        Optional<Exercicio> exercicioEncontrado = exercicioRepository.findById(exercicioSalvo.getIdExercicio());
        
        // Validações
        assertTrue(exercicioEncontrado.isPresent(), "Exercício deve ser encontrado");
        assertEquals("Agachamento", exercicioEncontrado.get().getNome());
        assertEquals("Pernas", exercicioEncontrado.get().getGrupoMuscular());
        
        System.out.println("✅ Exercício encontrado: " + exercicioEncontrado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarExercicioPorNome() {
        System.out.println("=== TESTE: Buscar Exercício por Nome ===");
        
        // Criar e salvar exercício
        exercicioRepository.save(new Exercicio("Levantamento Terra", "Costas"));
        
        // Buscar por nome
        Optional<Exercicio> exercicio = exercicioRepository.findByNome("Levantamento Terra");
        
        // Validações
        assertTrue(exercicio.isPresent(), "Exercício deve ser encontrado pelo nome");
        assertEquals("Costas", exercicio.get().getGrupoMuscular());
        
        System.out.println("✅ Exercício encontrado: " + exercicio.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarExerciciosPorGrupoMuscular() {
        System.out.println("=== TESTE: Buscar Exercícios por Grupo Muscular ===");
        
        // Criar vários exercícios do mesmo grupo
        exercicioRepository.save(new Exercicio("Supino Reto", "Peito"));
        exercicioRepository.save(new Exercicio("Supino Inclinado", "Peito"));
        exercicioRepository.save(new Exercicio("Crucifixo", "Peito"));
        exercicioRepository.save(new Exercicio("Rosca Direta", "Bíceps"));
        
        // Buscar exercícios de peito
        List<Exercicio> exerciciosPeito = exercicioRepository.findByGrupoMuscular("Peito");
        
        // Validações
        assertTrue(exerciciosPeito.size() >= 3, "Deve ter pelo menos 3 exercícios de peito");
        exerciciosPeito.forEach(e -> assertEquals("Peito", e.getGrupoMuscular()));
        
        System.out.println("✅ Exercícios de Peito encontrados: " + exerciciosPeito.size());
        exerciciosPeito.forEach(e -> System.out.println("   - " + e.getNome()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarExercicioPorNomeParcial() {
        System.out.println("=== TESTE: Buscar Exercício por Nome Parcial ===");
        
        // Criar exercícios
        exercicioRepository.save(new Exercicio("Rosca Direta", "Bíceps"));
        exercicioRepository.save(new Exercicio("Rosca Martelo", "Bíceps"));
        exercicioRepository.save(new Exercicio("Rosca Concentrada", "Bíceps"));
        
        // Buscar por nome parcial
        List<Exercicio> exerciciosRosca = exercicioRepository.findByNomeContainingIgnoreCase("rosca");
        
        // Validações
        assertTrue(exerciciosRosca.size() >= 3, "Deve encontrar pelo menos 3 exercícios com 'rosca'");
        exerciciosRosca.forEach(e -> assertTrue(e.getNome().toLowerCase().contains("rosca")));
        
        System.out.println("✅ Exercícios com 'rosca' encontrados: " + exerciciosRosca.size());
        exerciciosRosca.forEach(e -> System.out.println("   - " + e.getNome()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarPorGrupoMuscularIgnoreCase() {
        System.out.println("=== TESTE: Buscar por Grupo Muscular (Case Insensitive) ===");
        
        // Criar exercícios
        exercicioRepository.save(new Exercicio("Tríceps Testa", "Tríceps"));
        exercicioRepository.save(new Exercicio("Tríceps Corda", "Tríceps"));
        
        // Buscar com case diferente
        List<Exercicio> exerciciosTriceps = exercicioRepository.findByGrupoMuscularIgnoreCase("TRÍCEPS");
        
        // Validações
        assertTrue(exerciciosTriceps.size() >= 2, "Deve encontrar exercícios independente do case");
        
        System.out.println("✅ Exercícios de Tríceps encontrados: " + exerciciosTriceps.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarTodosExerciciosOrdenados() {
        System.out.println("=== TESTE: Listar Todos os Exercícios Ordenados ===");
        
        // Criar exercícios em ordem não alfabética
        exercicioRepository.save(new Exercicio("Supino", "Peito"));
        exercicioRepository.save(new Exercicio("Agachamento", "Pernas"));
        exercicioRepository.save(new Exercicio("Rosca", "Bíceps"));
        
        // Listar ordenados
        List<Exercicio> exerciciosOrdenados = exercicioRepository.findAllByOrderByNomeAsc();
        
        // Validações
        assertTrue(exerciciosOrdenados.size() >= 3, "Deve ter pelo menos 3 exercícios");
        
        // Verificar ordenação alfabética
        for (int i = 0; i < exerciciosOrdenados.size() - 1; i++) {
            assertTrue(
                exerciciosOrdenados.get(i).getNome()
                    .compareToIgnoreCase(exerciciosOrdenados.get(i + 1).getNome()) <= 0,
                "Exercícios devem estar ordenados alfabeticamente"
            );
        }
        
        System.out.println("✅ Exercícios ordenados:");
        exerciciosOrdenados.forEach(e -> System.out.println("   - " + e.getNome()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarExerciciosDeGrupoOrdenados() {
        System.out.println("=== TESTE: Listar Exercícios de Grupo Ordenados ===");
        
        // Criar exercícios de costas
        exercicioRepository.save(new Exercicio("Remada Curvada", "Costas"));
        exercicioRepository.save(new Exercicio("Barra Fixa", "Costas"));
        exercicioRepository.save(new Exercicio("Pulldown", "Costas"));
        
        // Listar exercícios de costas ordenados
        List<Exercicio> exerciciosCostas = exercicioRepository.findByGrupoMuscularOrderByNomeAsc("Costas");
        
        // Validações
        assertTrue(exerciciosCostas.size() >= 3, "Deve ter pelo menos 3 exercícios");
        exerciciosCostas.forEach(e -> assertEquals("Costas", e.getGrupoMuscular()));
        
        // Verificar ordenação
        for (int i = 0; i < exerciciosCostas.size() - 1; i++) {
            assertTrue(
                exerciciosCostas.get(i).getNome()
                    .compareToIgnoreCase(exerciciosCostas.get(i + 1).getNome()) <= 0,
                "Exercícios devem estar ordenados"
            );
        }
        
        System.out.println("✅ Exercícios de Costas ordenados:");
        exerciciosCostas.forEach(e -> System.out.println("   - " + e.getNome()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testVerificarExistenciaPorNome() {
        System.out.println("=== TESTE: Verificar Existência por Nome ===");
        
        // Criar exercício
        exercicioRepository.save(new Exercicio("Desenvolvimento", "Ombros"));
        
        // Verificar existência
        boolean existe = exercicioRepository.existsByNome("Desenvolvimento");
        boolean naoExiste = exercicioRepository.existsByNome("Exercício Inexistente");
        
        // Validações
        assertTrue(existe, "Exercício criado deve existir");
        assertFalse(naoExiste, "Exercício não criado não deve existir");
        
        System.out.println("✅ Verificação de existência funcionando corretamente");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testContarExerciciosPorGrupo() {
        System.out.println("=== TESTE: Contar Exercícios por Grupo ===");
        
        // Criar exercícios de ombros
        exercicioRepository.save(new Exercicio("Desenvolvimento Frontal", "Ombros"));
        exercicioRepository.save(new Exercicio("Elevação Lateral", "Ombros"));
        exercicioRepository.save(new Exercicio("Elevação Frontal", "Ombros"));
        exercicioRepository.save(new Exercicio("Agachamento", "Pernas"));
        
        // Contar exercícios de ombros
        long countOmbros = exercicioRepository.countByGrupoMuscular("Ombros");
        
        // Validações
        assertTrue(countOmbros >= 3, "Deve ter pelo menos 3 exercícios de ombros");
        
        System.out.println("✅ Total de exercícios de Ombros: " + countOmbros);
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarExerciciosSemGrupoMuscular() {
        System.out.println("=== TESTE: Buscar Exercícios sem Grupo Muscular ===");
        
        // Criar exercícios com e sem grupo
        exercicioRepository.save(new Exercicio("Alongamento"));
        exercicioRepository.save(new Exercicio("Aquecimento"));
        exercicioRepository.save(new Exercicio("Supino", "Peito"));
        
        // Buscar exercícios sem grupo
        List<Exercicio> exerciciosSemGrupo = exercicioRepository.findByGrupoMuscularIsNull();
        
        // Validações
        assertTrue(exerciciosSemGrupo.size() >= 2, "Deve ter pelo menos 2 exercícios sem grupo");
        exerciciosSemGrupo.forEach(e -> assertNull(e.getGrupoMuscular()));
        
        System.out.println("✅ Exercícios sem grupo muscular: " + exerciciosSemGrupo.size());
        exerciciosSemGrupo.forEach(e -> System.out.println("   - " + e.getNome()));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testBuscarExerciciosComGrupoMuscular() {
        System.out.println("=== TESTE: Buscar Exercícios com Grupo Muscular ===");
        
        // Criar exercícios com e sem grupo
        exercicioRepository.save(new Exercicio("Leg Press", "Pernas"));
        exercicioRepository.save(new Exercicio("Cadeira Extensora", "Pernas"));
        exercicioRepository.save(new Exercicio("Alongamento"));
        
        // Buscar exercícios com grupo
        List<Exercicio> exerciciosComGrupo = exercicioRepository.findByGrupoMuscularIsNotNull();
        
        // Validações
        assertTrue(exerciciosComGrupo.size() >= 2, "Deve ter pelo menos 2 exercícios com grupo");
        exerciciosComGrupo.forEach(e -> assertNotNull(e.getGrupoMuscular()));
        
        System.out.println("✅ Exercícios com grupo muscular: " + exerciciosComGrupo.size());
        exerciciosComGrupo.forEach(e -> System.out.println("   - " + e.getNome() + " (" + e.getGrupoMuscular() + ")"));
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testAtualizarExercicio() {
        System.out.println("=== TESTE: Atualizar Exercício ===");
        
        // Criar e salvar exercício
        Exercicio exercicio = new Exercicio("Remada");
        Exercicio exercicioSalvo = exercicioRepository.save(exercicio);
        Long id = exercicioSalvo.getIdExercicio();
        
        // Atualizar informações
        exercicioSalvo.setNome("Remada Curvada");
        exercicioSalvo.setGrupoMuscular("Costas");
        exercicioRepository.save(exercicioSalvo);
        
        // Buscar novamente
        Optional<Exercicio> exercicioAtualizado = exercicioRepository.findById(id);
        
        // Validações
        assertTrue(exercicioAtualizado.isPresent());
        assertEquals("Remada Curvada", exercicioAtualizado.get().getNome());
        assertEquals("Costas", exercicioAtualizado.get().getGrupoMuscular());
        
        System.out.println("✅ Exercício atualizado: " + exercicioAtualizado.get());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testDeletarExercicio() {
        System.out.println("=== TESTE: Deletar Exercício ===");
        
        // Criar e salvar exercício
        Exercicio exercicio = new Exercicio("Abdominal", "Abdômen");
        Exercicio exercicioSalvo = exercicioRepository.save(exercicio);
        Long id = exercicioSalvo.getIdExercicio();
        
        // Deletar
        exercicioRepository.deleteById(id);
        
        // Tentar buscar novamente
        Optional<Exercicio> exercicioDeletado = exercicioRepository.findById(id);
        
        // Validações
        assertFalse(exercicioDeletado.isPresent(), "Exercício não deve existir após deleção");
        
        System.out.println("✅ Exercício deletado com sucesso");
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }

    @Test
    public void testListarTodosExercicios() {
        System.out.println("=== TESTE: Listar Todos os Exercícios ===");
        
        // Criar vários exercícios
        exercicioRepository.save(new Exercicio("Supino", "Peito"));
        exercicioRepository.save(new Exercicio("Agachamento", "Pernas"));
        exercicioRepository.save(new Exercicio("Rosca", "Bíceps"));
        
        // Listar todos
        List<Exercicio> todosExercicios = exercicioRepository.findAll();
        
        // Validações
        assertTrue(todosExercicios.size() >= 3, "Deve ter pelo menos 3 exercícios");
        
        System.out.println("✅ Total de exercícios cadastrados: " + todosExercicios.size());
        System.out.println("=== TESTE CONCLUÍDO ===\n");
    }
}
