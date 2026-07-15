package com.tcc.talkie.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.UpdateDTO;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.infra.security.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    private User commonUser;
    private User adminUser;
    private String commonUserToken;
    private String adminUserToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        // Create common user
        commonUser = new User();
        commonUser.setName("João Common");
        commonUser.setEmail("joao@gmail.com");
        commonUser.setPassword(passwordEncoder.encode("123456"));
        commonUser.setCpf("111.1111.111-11");
        commonUser.setRole(Role.USER);
        commonUser = userRepository.save(commonUser);
        commonUserToken = tokenService.generateToken(commonUser);

        // Create admin user
        adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@gmail.com");
        adminUser.setPassword(passwordEncoder.encode("123456"));
        adminUser.setCpf("222.2222.222-22");
        adminUser.setRole(Role.ADMIN);
        adminUser = userRepository.save(adminUser);
        adminUserToken = tokenService.generateToken(adminUser);
    }

    /* Testes do GET /users */
    @Test
    @DisplayName("Deve listar todos os usuários com autenticação")
    void deveListarTodosOsUsuarios() throws Exception {
        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Não deve listar usuários sem autenticação")
    void naoDeveListarUsuariosSemAutenticacao() throws Exception {
        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve listar usuários com token admin")
    void deveListarUsuariosComTokenAdmin() throws Exception {
        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    /* Testes do GET /users/{id} */
    @Test
    @DisplayName("Deve obter usuário por ID com autenticação")
    void deveObterUsuarioPorId() throws Exception {
        mockMvc.perform(get("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(commonUser.getId().toString()))
            .andExpect(jsonPath("$.name").value("João Common"))
            .andExpect(jsonPath("$.email").value("joao@gmail.com"));
    }

    @Test
    @DisplayName("Não deve obter usuário sem autenticação")
    void naoDeveObterUsuarioSemAutenticacao() throws Exception {
        mockMvc.perform(get("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve obter usuário inexistente")
    void naoDeveObterUsuarioInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/users/" + idInexistente)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve obter usuário admin por ID")
    void deveObterUsuarioAdminPorId() throws Exception {
        mockMvc.perform(get("/users/" + adminUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(adminUser.getId().toString()))
            .andExpect(jsonPath("$.name").value("Admin User"))
            .andExpect(jsonPath("$.email").value("admin@gmail.com"));
    }

    /* Testes do PUT /users/{id} */
    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void deveAtualizarUsuarioComSucesso() throws Exception {
        UpdateDTO updateDTO = new UpdateDTO("João Atualizado", "joao.novo@gmail.com");

        mockMvc.perform(put("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(commonUser.getId().toString()))
            .andExpect(jsonPath("$.name").value("João Atualizado"))
            .andExpect(jsonPath("$.email").value("joao.novo@gmail.com"));
    }

    @Test
    @DisplayName("Não deve atualizar usuário sem autenticação")
    void naoDeveAtualizarUsuarioSemAutenticacao() throws Exception {
        UpdateDTO updateDTO = new UpdateDTO("João Atualizado", "joao.novo@gmail.com");

        mockMvc.perform(put("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve atualizar usuário inexistente")
    void naoDeveAtualizarUsuarioInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();
        UpdateDTO updateDTO = new UpdateDTO("Nome", "email@gmail.com");

        mockMvc.perform(put("/users/" + idInexistente)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Deve atualizar apenas nome do usuário")
    void deveAtualizarApenasNomeDoUsuario() throws Exception {
        UpdateDTO updateDTO = new UpdateDTO("Novo Nome", "joao@gmail.com");

        mockMvc.perform(put("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Novo Nome"))
            .andExpect(jsonPath("$.email").value("joao@gmail.com"));
    }

    @Test
    @DisplayName("Deve atualizar apenas email do usuário")
    void deveAtualizarApenasEmailDoUsuario() throws Exception {
        UpdateDTO updateDTO = new UpdateDTO("João Common", "novo.email@gmail.com");

        mockMvc.perform(put("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("João Common"))
            .andExpect(jsonPath("$.email").value("novo.email@gmail.com"));
    }

    @Test
    @DisplayName("Admin deve atualizar usuário comum")
    void adminDeveAtualizarUsuarioComum() throws Exception {
        UpdateDTO updateDTO = new UpdateDTO("Atualizado por Admin", "admin.update@gmail.com");

        mockMvc.perform(put("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Atualizado por Admin"))
            .andExpect(jsonPath("$.email").value("admin.update@gmail.com"));
    }

    /* Testes do DELETE /users/{id} */
    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deveDeletarUsuarioComSucesso() throws Exception {
        mockMvc.perform(delete("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Usuário deletado com sucesso"));

        // Verify user is deleted
        assert userRepository.findById(commonUser.getId()).isEmpty();
    }

    @Test
    @DisplayName("Não deve deletar usuário sem autenticação")
    void naoDeveDeletarUsuarioSemAutenticacao() throws Exception {
        mockMvc.perform(delete("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve deletar usuário inexistente")
    void naoDeveDeletarUsuarioInexistente() throws Exception {
        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(delete("/users/" + idInexistente)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    @DisplayName("Admin deve deletar usuário comum")
    void adminDeveDeletarUsuarioComum() throws Exception {
        mockMvc.perform(delete("/users/" + commonUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Usuário deletado com sucesso"));

        // Verify user is deleted
        assert userRepository.findById(commonUser.getId()).isEmpty();
    }

    @Test
    @DisplayName("Usuário deve deletar a si mesmo")
    void usuarioDeveDeletarASiMesmo() throws Exception {
        User userToDelete = new User();
        userToDelete.setName("User Deletar");
        userToDelete.setEmail("deletar@gmail.com");
        userToDelete.setPassword(passwordEncoder.encode("123456"));
        userToDelete.setCpf("333.3333.333-33");
        userToDelete.setRole(Role.USER);
        userToDelete = userRepository.save(userToDelete);

        String userToken = tokenService.generateToken(userToDelete);

        mockMvc.perform(delete("/users/" + userToDelete.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + userToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Usuário deletado com sucesso"));

        // Verify user is deleted
        assert userRepository.findById(userToDelete.getId()).isEmpty();
    }
}
