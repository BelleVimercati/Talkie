package com.tcc.talkie.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.LoginRequestDTO;
import com.tcc.talkie.dto.request.RegisterDTO;
import com.tcc.talkie.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();
    }

    /* Testes do register */
    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void deveRegistrarNovoUsuarioComSucesso() throws Exception {
        RegisterDTO dto = new RegisterDTO("João", "joao@gmail.com", "123456", "000.0000.000-00", Role.USER);

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso"));
    }

    @Test
    @DisplayName("Não deve registrar usuário com CPF já existente")
    void naoDeveRegistrarUsuarioComCPFExistente() throws Exception {
        RegisterDTO dto = new RegisterDTO("João", "joao@gmail.com", "123456", "000.0000.000-00", Role.USER);

        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso"));

        mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("CPF já cadastrado"));
    }


    /* Testes do login */
    @Test
    @DisplayName("Deve autenticar usuário com credenciais válidas")
    void deveAutenticarUsuarioComCredenciaisValidas() throws Exception {
        User user = new User();
        user.setName("João");
        user.setEmail("joao@gmail.com");
        user.setPassword(passwordEncoder.encode("123456"));
        user.setCpf("000.0000.000-00");
        user.setRole(Role.USER);
        userRepository.save(user);

        LoginRequestDTO loginDTO = new LoginRequestDTO("joao@gmail.com", "123456");

        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Login bem-sucedido"))
            .andExpect(jsonPath("$.data").isNotEmpty());
    }
}