package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.dto.PlanoRequestDTO;
import com.example.demo.entity.Plano;
import com.example.demo.exception.PlanoException;
import com.example.demo.service.PlanoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PlanoController.class)
public class PlanoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanoService planoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Plano planoValido;
    private PlanoRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        planoValido = new Plano("Plano Test", "Descrição", new BigDecimal("99.90"), 1);
        planoValido.setIdPlanoAssinatura(1L);
        planoValido.setStatus("ATIVO");

        requestDTO = new PlanoRequestDTO("Plano Test", "Descrição", new BigDecimal("99.90"), 1);
    }

    @Test
    void criarPlano_QuandoValido_DeveRetornarCreated() throws Exception {
        when(planoService.criarPlano(any(Plano.class))).thenReturn(planoValido);

        mockMvc.perform(post("/api/planos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(planoValido.getNome()))
                .andExpect(jsonPath("$.status").value(planoValido.getStatus()));
    }

    @Test
    void criarPlano_QuandoInvalido_DeveRetornarBadRequest() throws Exception {
        requestDTO.setNome("");

        mockMvc.perform(post("/api/planos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarPlanos_DeveRetornarTodosPlanos() throws Exception {
        when(planoService.listarTodosPlanos()).thenReturn(Arrays.asList(planoValido));

        mockMvc.perform(get("/api/planos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value(planoValido.getNome()));
    }

    @Test
    void buscarPorId_QuandoExiste_DeveRetornarPlano() throws Exception {
        when(planoService.buscarPlanoPorId(1L)).thenReturn(planoValido);

        mockMvc.perform(get("/api/planos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(planoValido.getNome()));
    }

    @Test
    void buscarPorId_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(planoService.buscarPlanoPorId(1L))
            .thenThrow(new PlanoException.PlanoNotFoundException("Plano não encontrado"));

        mockMvc.perform(get("/api/planos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void atualizarPlano_QuandoExiste_DeveRetornarOk() throws Exception {
        when(planoService.atualizarPlano(any(Long.class), any(Plano.class))).thenReturn(planoValido);

        mockMvc.perform(put("/api/planos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(planoValido.getNome()));
    }

    @Test
    void deletarPlano_QuandoExiste_DeveRetornarNoContent() throws Exception {
        mockMvc.perform(delete("/api/planos/1"))
                .andExpect(status().isNoContent());

        verify(planoService).deletarPlano(1L);
    }

    @Test
    void ativarPlano_QuandoExiste_DeveRetornarOk() throws Exception {
        planoValido.setStatus("ATIVO");
        when(planoService.buscarPlanoPorId(1L)).thenReturn(planoValido);

        mockMvc.perform(put("/api/planos/1/ativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ATIVO"));

        verify(planoService).ativarPlano(1L);
    }

    @Test
    void ativarPlano_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        doThrow(new PlanoException.PlanoNotFoundException("Plano não encontrado"))
            .when(planoService).ativarPlano(1L);

        mockMvc.perform(put("/api/planos/1/ativar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void inativarPlano_QuandoExiste_DeveRetornarOk() throws Exception {
        planoValido.setStatus("INATIVO");
        when(planoService.buscarPlanoPorId(1L)).thenReturn(planoValido);

        mockMvc.perform(put("/api/planos/1/inativar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INATIVO"));

        verify(planoService).inativarPlano(1L);
    }

    @Test
    void inativarPlano_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        doThrow(new PlanoException.PlanoNotFoundException("Plano não encontrado"))
            .when(planoService).inativarPlano(1L);

        mockMvc.perform(put("/api/planos/1/inativar"))
                .andExpect(status().isNotFound());
    }
}