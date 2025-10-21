package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.entity.Plano;
import com.example.demo.exception.PlanoException;
import com.example.demo.repository.PlanoRepository;

@SpringBootTest
public class PlanoServiceTest {

    @Mock
    private PlanoRepository planoRepository;

    @InjectMocks
    private PlanoService planoService;

    private Plano planoValido;

    @BeforeEach
    void setUp() {
        planoValido = new Plano(
            "Plano Test",
            "Descrição do plano de teste",
            new BigDecimal("99.90"),
            1
        );
        planoValido.setIdPlanoAssinatura(1L);
    }

    @Test
    void criarPlano_QuandoPlanoValido_DeveSalvarComSucesso() {
        when(planoRepository.save(any(Plano.class))).thenReturn(planoValido);

        Plano planoSalvo = planoService.criarPlano(planoValido);

        assertNotNull(planoSalvo);
        assertEquals(planoValido.getNome(), planoSalvo.getNome());
        verify(planoRepository).save(any(Plano.class));
    }

    @Test
    void criarPlano_QuandoNomeVazio_DeveLancarExcecao() {
        planoValido.setNome("");

        assertThrows(PlanoException.PlanoInvalidoException.class, () -> {
            planoService.criarPlano(planoValido);
        });

        verify(planoRepository, never()).save(any(Plano.class));
    }

    @Test
    void criarPlano_QuandoValorInvalido_DeveLancarExcecao() {
        planoValido.setValor(new BigDecimal("0.00"));

        assertThrows(PlanoException.PlanoInvalidoException.class, () -> {
            planoService.criarPlano(planoValido);
        });

        verify(planoRepository, never()).save(any(Plano.class));
    }

    @Test
    void atualizarPlano_QuandoPlanoExiste_DeveAtualizarComSucesso() {
        when(planoRepository.findById(1L)).thenReturn(Optional.of(planoValido));
        when(planoRepository.save(any(Plano.class))).thenReturn(planoValido);

        Plano planoAtualizado = new Plano(
            "Plano Atualizado",
            "Nova descrição",
            new BigDecimal("149.90"),
            2
        );

        Plano resultado = planoService.atualizarPlano(1L, planoAtualizado);

        assertNotNull(resultado);
        assertEquals(planoAtualizado.getNome(), resultado.getNome());
        assertEquals(planoAtualizado.getValor(), resultado.getValor());
        verify(planoRepository).save(any(Plano.class));
    }

    @Test
    void atualizarPlano_QuandoPlanoNaoExiste_DeveLancarExcecao() {
        when(planoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlanoException.PlanoNotFoundException.class, () -> {
            planoService.atualizarPlano(1L, planoValido);
        });

        verify(planoRepository, never()).save(any(Plano.class));
    }

    @Test
    void buscarPlanoPorId_QuandoPlanoExiste_DeveRetornarPlano() {
        when(planoRepository.findById(1L)).thenReturn(Optional.of(planoValido));

        Plano resultado = planoService.buscarPlanoPorId(1L);

        assertNotNull(resultado);
        assertEquals(planoValido.getIdPlanoAssinatura(), resultado.getIdPlanoAssinatura());
    }

    @Test
    void buscarPlanoPorId_QuandoPlanoNaoExiste_DeveLancarExcecao() {
        when(planoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlanoException.PlanoNotFoundException.class, () -> {
            planoService.buscarPlanoPorId(1L);
        });
    }

    @Test
    void listarTodosPlanos_DeveRetornarListaDePlanos() {
        List<Plano> planos = Arrays.asList(
            planoValido,
            new Plano("Plano 2", "Desc 2", new BigDecimal("199.90"), 3)
        );

        when(planoRepository.findAll()).thenReturn(planos);

        List<Plano> resultado = planoService.listarTodosPlanos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(planoRepository).findAll();
    }

    @Test
    void listarPlanosAtivos_DeveRetornarApenasAtivos() {
        List<Plano> planosAtivos = Arrays.asList(
            planoValido,
            new Plano("Plano 2", "Desc 2", new BigDecimal("199.90"), 3)
        );

        when(planoRepository.findByStatus("ATIVO")).thenReturn(planosAtivos);

        List<Plano> resultado = planoService.listarPlanosAtivos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(planoRepository).findByStatus("ATIVO");
    }

    @Test
    void deletarPlano_QuandoPlanoExiste_DeveDeletarComSucesso() {
        when(planoRepository.findById(1L)).thenReturn(Optional.of(planoValido));

        planoService.deletarPlano(1L);

        verify(planoRepository).delete(planoValido);
    }

    @Test
    void deletarPlano_QuandoPlanoNaoExiste_DeveLancarExcecao() {
        when(planoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PlanoException.PlanoNotFoundException.class, () -> {
            planoService.deletarPlano(1L);
        });

        verify(planoRepository, never()).delete(any(Plano.class));
    }

    @Test
    void ativarPlano_QuandoPlanoExiste_DeveAtivarComSucesso() {
        planoValido.setStatus("INATIVO");
        when(planoRepository.findById(1L)).thenReturn(Optional.of(planoValido));
        when(planoRepository.save(any(Plano.class))).thenReturn(planoValido);

        planoService.ativarPlano(1L);

        assertEquals("ATIVO", planoValido.getStatus());
        verify(planoRepository).save(planoValido);
    }

    @Test
    void inativarPlano_QuandoPlanoExiste_DeveInativarComSucesso() {
        when(planoRepository.findById(1L)).thenReturn(Optional.of(planoValido));
        when(planoRepository.save(any(Plano.class))).thenReturn(planoValido);

        planoService.inativarPlano(1L);

        assertEquals("INATIVO", planoValido.getStatus());
        verify(planoRepository).save(planoValido);
    }
}