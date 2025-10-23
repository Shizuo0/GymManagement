package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.BuscaGlobalResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.AlunoResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.InstrutorResultadoDTO;
import com.example.demo.dto.BuscaGlobalResultadoDTO.PlanoResultadoDTO;
import com.example.demo.exception.BuscaGlobalException;
import com.example.demo.service.BuscaGlobalService;

/**
 * Testes para BuscaGlobalController
 */
@WebMvcTest(BuscaGlobalController.class)
public class BuscaGlobalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BuscaGlobalService buscaGlobalService;

    private BuscaGlobalResultadoDTO resultadoCompleto;
    private List<AlunoResultadoDTO> alunos;
    private List<InstrutorResultadoDTO> instrutores;
    private List<PlanoResultadoDTO> planos;

    @BeforeEach
    void setUp() {
        // Criar dados de teste
        AlunoResultadoDTO aluno1 = new AlunoResultadoDTO(
            1L,
            "Maria Santos",
            "123.456.789-00",
            "ATIVA",
            "Plano Premium"
        );

        AlunoResultadoDTO aluno2 = new AlunoResultadoDTO(
            2L,
            "João Silva",
            "987.654.321-00",
            "INATIVA",
            null
        );

        alunos = Arrays.asList(aluno1, aluno2);

        InstrutorResultadoDTO instrutor1 = new InstrutorResultadoDTO(
            1L,
            "Carlos Personal",
            null,
            "Musculação",
            15
        );

        instrutores = Arrays.asList(instrutor1);

        PlanoResultadoDTO plano1 = new PlanoResultadoDTO(
            1L,
            "Plano Premium",
            "Acesso total à academia",
            "199.90",
            "ATIVO",
            50
        );

        PlanoResultadoDTO plano2 = new PlanoResultadoDTO(
            2L,
            "Plano Básico",
            "Acesso básico",
            "99.90",
            "ATIVO",
            30
        );

        planos = Arrays.asList(plano1, plano2);

        resultadoCompleto = new BuscaGlobalResultadoDTO("maria");
        resultadoCompleto.setAlunos(alunos);
        resultadoCompleto.setInstrutores(instrutores);
        resultadoCompleto.setPlanos(planos);
    }

    @Test
    void buscarTudo_QuandoTermoValido_DeveRetornarOk() throws Exception {
        when(buscaGlobalService.buscarTudo("maria")).thenReturn(resultadoCompleto);

        mockMvc.perform(get("/api/busca")
                .param("termo", "maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.termoBusca").value("maria"))
                .andExpect(jsonPath("$.totalResultados").value(5))
                .andExpect(jsonPath("$.alunos").isArray())
                .andExpect(jsonPath("$.alunos[0].nome").value("Maria Santos"))
                .andExpect(jsonPath("$.instrutores").isArray())
                .andExpect(jsonPath("$.instrutores[0].nome").value("Carlos Personal"))
                .andExpect(jsonPath("$.planos").isArray())
                .andExpect(jsonPath("$.planos[0].nome").value("Plano Premium"));
    }

    @Test
    void buscarTudo_QuandoTermoInvalido_DeveRetornarBadRequest() throws Exception {
        when(buscaGlobalService.buscarTudo("a"))
            .thenThrow(new BuscaGlobalException.TermoBuscaInvalidoException("Termo de busca deve ter pelo menos 2 caracteres"));

        mockMvc.perform(get("/api/busca")
                .param("termo", "a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Termo de busca deve ter pelo menos 2 caracteres"));
    }

    @Test
    void buscarTudo_QuandoNenhumResultado_DeveRetornarNotFound() throws Exception {
        when(buscaGlobalService.buscarTudo("xyz"))
            .thenThrow(new BuscaGlobalException.NenhumResultadoException("Nenhum resultado encontrado para: xyz"));

        mockMvc.perform(get("/api/busca")
                .param("termo", "xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum resultado encontrado para: xyz"));
    }

    @Test
    void buscarTudo_QuandoMuitosResultados_DeveRetornarPayloadTooLarge() throws Exception {
        when(buscaGlobalService.buscarTudo("a"))
            .thenThrow(new BuscaGlobalException.MuitosResultadosException("Busca retornou 1500 resultados. Refine os critérios de busca."));

        mockMvc.perform(get("/api/busca")
                .param("termo", "a"))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.message").value("Busca retornou 1500 resultados. Refine os critérios de busca."));
    }

    @Test
    void buscarAlunos_QuandoTermoValido_DeveRetornarOk() throws Exception {
        when(buscaGlobalService.buscarApenasAlunos("maria", null)).thenReturn(alunos);

        mockMvc.perform(get("/api/busca/alunos")
                .param("termo", "maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Maria Santos"))
                .andExpect(jsonPath("$[0].statusMatricula").value("ATIVA"))
                .andExpect(jsonPath("$[1].nome").value("João Silva"));
    }

    @Test
    void buscarAlunos_ComFiltroAtivos_DeveRetornarApenasAtivos() throws Exception {
        List<AlunoResultadoDTO> alunosAtivos = Arrays.asList(alunos.get(0));
        when(buscaGlobalService.buscarApenasAlunos("maria", true)).thenReturn(alunosAtivos);

        mockMvc.perform(get("/api/busca/alunos")
                .param("termo", "maria")
                .param("apenasAtivos", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].statusMatricula").value("ATIVA"));
    }

    @Test
    void buscarInstrutores_QuandoTermoValido_DeveRetornarOk() throws Exception {
        when(buscaGlobalService.buscarApenasInstrutores("carlos", null)).thenReturn(instrutores);

        mockMvc.perform(get("/api/busca/instrutores")
                .param("termo", "carlos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Carlos Personal"))
                .andExpect(jsonPath("$[0].especialidade").value("Musculação"))
                .andExpect(jsonPath("$[0].totalAlunos").value(15));
    }

    @Test
    void buscarInstrutores_ComFiltroEspecialidade_DeveRetornarFiltrado() throws Exception {
        when(buscaGlobalService.buscarApenasInstrutores("personal", "musculacao")).thenReturn(instrutores);

        mockMvc.perform(get("/api/busca/instrutores")
                .param("termo", "personal")
                .param("especialidade", "musculacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].especialidade").value("Musculação"));
    }

    @Test
    void buscarPlanos_QuandoTermoValido_DeveRetornarOk() throws Exception {
        when(buscaGlobalService.buscarApenasPlanos("plano", null)).thenReturn(planos);

        mockMvc.perform(get("/api/busca/planos")
                .param("termo", "plano"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Plano Premium"))
                .andExpect(jsonPath("$[0].status").value("ATIVO"))
                .andExpect(jsonPath("$[0].totalMatriculas").value(50));
    }

    @Test
    void buscarPlanos_ComFiltroAtivos_DeveRetornarApenasAtivos() throws Exception {
        when(buscaGlobalService.buscarApenasPlanos("premium", true)).thenReturn(Arrays.asList(planos.get(0)));

        mockMvc.perform(get("/api/busca/planos")
                .param("termo", "premium")
                .param("apenasAtivos", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("ATIVO"));
    }

    @Test
    void buscarAlunosAtivos_DeveRetornarApenasAtivos() throws Exception {
        List<AlunoResultadoDTO> alunosAtivos = Arrays.asList(alunos.get(0));
        when(buscaGlobalService.buscarApenasAlunos("maria", true)).thenReturn(alunosAtivos);

        mockMvc.perform(get("/api/busca/alunos/ativos")
                .param("termo", "maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].statusMatricula").value("ATIVA"));
    }

    @Test
    void buscarPlanosAtivos_DeveRetornarApenasAtivos() throws Exception {
        when(buscaGlobalService.buscarApenasPlanos("plano", true)).thenReturn(planos);

        mockMvc.perform(get("/api/busca/planos/ativos")
                .param("termo", "plano"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].status").value("ATIVO"));
    }

    @Test
    void buscarAlunos_QuandoNenhumResultado_DeveRetornarNotFound() throws Exception {
        when(buscaGlobalService.buscarApenasAlunos("xyz", null))
            .thenThrow(new BuscaGlobalException.NenhumResultadoException("Nenhum aluno encontrado para: xyz"));

        mockMvc.perform(get("/api/busca/alunos")
                .param("termo", "xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum aluno encontrado para: xyz"));
    }

    @Test
    void buscarInstrutores_QuandoNenhumResultado_DeveRetornarNotFound() throws Exception {
        when(buscaGlobalService.buscarApenasInstrutores("xyz", null))
            .thenThrow(new BuscaGlobalException.NenhumResultadoException("Nenhum instrutor encontrado para: xyz"));

        mockMvc.perform(get("/api/busca/instrutores")
                .param("termo", "xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum instrutor encontrado para: xyz"));
    }

    @Test
    void buscarPlanos_QuandoNenhumResultado_DeveRetornarNotFound() throws Exception {
        when(buscaGlobalService.buscarApenasPlanos("xyz", null))
            .thenThrow(new BuscaGlobalException.NenhumResultadoException("Nenhum plano encontrado para: xyz"));

        mockMvc.perform(get("/api/busca/planos")
                .param("termo", "xyz"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum plano encontrado para: xyz"));
    }

    @Test
    void buscarTudo_QuandoErroBusca_DeveRetornarInternalServerError() throws Exception {
        when(buscaGlobalService.buscarTudo("teste"))
            .thenThrow(new BuscaGlobalException.ErroBuscaException("Erro ao realizar busca global"));

        mockMvc.perform(get("/api/busca")
                .param("termo", "teste"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro ao realizar busca global"));
    }
}
