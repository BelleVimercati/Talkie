package com.tcc.talkie.bdd.steps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Map;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.LoginRequestDTO;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import org.springframework.http.MediaType;

public class CategoriaSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoriaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ResultActions resultado;
    private String tokenJwt;

    @Before
    public void limparBanco(){
        categoriaRepository.deleteAll();
    }

    @Dado("que o usuário esteja autenticado")
    public void que_o_usuario_esteja_autenticado() throws Exception {
        // Cria um usuário admin para autenticação
        User user = new User();
        user.setName("Admin Teste");
        user.setEmail("admin@teste.com");
        user.setPassword(passwordEncoder.encode("senha123"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        //Faz login para obter o token JWT
        String body = objectMapper.writeValueAsString(new LoginRequestDTO("admin@teste.com", "senha123"));

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();

        String resposta = result.getResponse().getContentAsString();
        this.tokenJwt = objectMapper.readTree(resposta).get("data").asText();

    }

    @E("que o usuário seja um administrador")
    public void que_o_usuario_seja_um_administrador() {
        // O usuário criado no passo anterior já é um administrador, então não é necessário fazer nada aqui
    }

    @Quando("o usuário enviar uma requisição POST para {string} com os seguintes dados:")
    public void o_usuario_enviar_uma_requisicao_post_para_com_os_seguintes_dados(String url, DataTable tabela) throws Exception {
        // Converte a tabela de dados para um Map
        Map<String, String> dados = tabela.asMaps().get(0);

        resultado = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + tokenJwt)
            .content(objectMapper.writeValueAsString(dados))).andDo(print());
    }

    @Então("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(int statusEsperado) throws Exception {
        resultado.andExpect(MockMvcResultMatchers.status().is(statusEsperado));
    }
    
}
