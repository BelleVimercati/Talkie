package com.tcc.talkie.bdd.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.talkie.bdd.TestContext;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.LoginRequestDTO;
import com.tcc.talkie.repository.UserRepository;

import io.cucumber.java.pt.Dado;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;

@RequiredArgsConstructor
public class AuthSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final TestContext context;

    @Dado("que o usuário esteja autenticado")
    public void usuarioAdm() throws Exception {
        String email = "admin" + UUID.randomUUID() + "@teste.com";

        String cpf = UUID.randomUUID()
        .toString()
        .replaceAll("[^0-9]", "")
        .substring(0, 11);

        // Cria um usuário admin para autenticação
        User user = new User();
        user.setName("Admin Teste");
        user.setEmail(email);
        user.setCpf(cpf);
        user.setPassword(passwordEncoder.encode("senha123"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        String body = objectMapper.writeValueAsString(new LoginRequestDTO(email, "senha123"));

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();

        String resposta = result.getResponse().getContentAsString();
        context.setTokenJWT(objectMapper.readTree(resposta).get("data").asText());

        context.setAuthenticatedUser(user);
    }

    @Dado("que o usuário não esteja autenticado")
    public void usuarioNaoAutenticado() {
        context.setTokenJWT(null);
        context.setAuthenticatedUser(null);
    }
}
