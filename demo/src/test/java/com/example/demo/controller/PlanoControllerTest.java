package com.example.demo.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.PlanoRequestDTO;
import com.example.demo.repository.PlanoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class PlanoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanoRepository planoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        planoRepository.deleteAll();
    }

    @Test
    void criarPlano_Valido_RetornaCreated() throws Exception {
        PlanoRequestDTO dto = new PlanoRequestDTO("Plano Integra", "Descricao", new BigDecimal("59.90"), 1);

        mockMvc.perform(post("/api/planos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Plano Integra"))
                .andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test
    void criarPlano_Invalido_RetornaBadRequest() throws Exception {
        PlanoRequestDTO dto = new PlanoRequestDTO("", "Descricao", new BigDecimal("0.00"), 0);

        mockMvc.perform(post("/api/planos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}