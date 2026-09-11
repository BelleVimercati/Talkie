package com.tcc.talkie.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.CategoryCreateDTO;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.infra.security.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

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
        categoryRepository.deleteAll();
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

    @Test
    @DisplayName("Deve criar uma categoria como admin")
    void deveCriarCategoriaCOmoAdmin() throws Exception {
        CategoryCreateDTO dto = new CategoryCreateDTO("Violência", "violence-icon.png");

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.name").value("Violência"))
            .andExpect(jsonPath("$.icon").value("violence-icon.png"));
    }

    @Test
    @DisplayName("Não deve criar categoria como usuário comum")
    void naoDeveCriarCategoriaComoUsuarioComum() throws Exception {
        CategoryCreateDTO dto = new CategoryCreateDTO("Violência", "violence-icon.png");

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve criar categoria sem autenticação")
    void naoDeveCriarCategoriaSemAutenticacao() throws Exception {
        CategoryCreateDTO dto = new CategoryCreateDTO("Violência", "violence-icon.png");

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve criar categoria com nome duplicado (case insensitive)")
    void naoDeveCriarCategoriaComNomeDuplicado() throws Exception {
        // Create first category
        Category category = new Category();
        category.setName("Violência");
        category.setIcon("icon.png");
        category.setUser(adminUser);
        categoryRepository.save(category);

        // Try to create duplicate
        CategoryCreateDTO dto = new CategoryCreateDTO("violência", "another-icon.png");

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Tipo já existe"));
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    void deveListarTodasAsCategorias() throws Exception {
        // Create some categories
        for (int i = 0; i < 3; i++) {
            Category cat = new Category();
            cat.setName("Categoria " + i);
            cat.setIcon("icon" + i + ".png");
            cat.setUser(adminUser);
            categoryRepository.save(cat);
        }

        mockMvc.perform(get("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("Deve obter categoria por ID")
    void deveObterCategoriaPorId() throws Exception {
        Category cat = new Category();
        cat.setName("Infraestrutura");
        cat.setIcon("infrastructure-icon.png");
        cat.setUser(adminUser);
        cat = categoryRepository.save(cat);

        mockMvc.perform(get("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(cat.getId()))
            .andExpect(jsonPath("$.name").value("Infraestrutura"))
            .andExpect(jsonPath("$.icon").value("infrastructure-icon.png"));
    }

    @Test
    @DisplayName("Não deve obter categoria inexistente")
    void naoDeveObterCategoriaInexistente() throws Exception {
        mockMvc.perform(get("/categories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve atualizar categoria como admin")
    void deveAtualizarCategoriaComoAdmin() throws Exception {
        Category cat = new Category();
        cat.setName("Violência");
        cat.setIcon("old-icon.png");
        cat.setUser(adminUser);
        cat = categoryRepository.save(cat);

        CategoryCreateDTO dto = new CategoryCreateDTO("Segurança", "new-icon.png");

        mockMvc.perform(put("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Segurança"))
            .andExpect(jsonPath("$.icon").value("new-icon.png"));
    }

    @Test
    @DisplayName("Não deve atualizar categoria como usuário comum")
    void naoDeveAtualizarCategoriaComoUsuarioComum() throws Exception {
        Category cat = new Category();
        cat.setName("Violência");
        cat.setIcon("old-icon.png");
        cat.setUser(adminUser);
        cat = categoryRepository.save(cat);

        CategoryCreateDTO dto = new CategoryCreateDTO("Segurança", "new-icon.png");

        mockMvc.perform(put("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve atualizar categoria inexistente")
    void naoDeveAtualizarCategoriaInexistente() throws Exception {
        CategoryCreateDTO dto = new CategoryCreateDTO("Segurança", "icon.png");

        mockMvc.perform(put("/categories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve deletar categoria como admin")
    void deveDeletarCategoriaComoAdmin() throws Exception {
        Category cat = new Category();
        cat.setName("Categoria Teste");
        cat.setIcon("icon.png");
        cat.setUser(adminUser);
        cat = categoryRepository.save(cat);

        mockMvc.perform(delete("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Categoria deletada com sucesso"));

        assert categoryRepository.findById(cat.getId()).isEmpty();
    }

    @Test
    @DisplayName("Não deve deletar categoria como usuário comum")
    void naoDeveDeletarCategoriaComoUsuarioComum() throws Exception {
        Category cat = new Category();
        cat.setName("Categoria Teste");
        cat.setIcon("icon.png");
        cat.setUser(adminUser);
        cat = categoryRepository.save(cat);

        mockMvc.perform(delete("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve deletar categoria inexistente")
    void naoDeveDeletarCategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/categories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Tipo não encontrado"));
    }

    @Test
    @DisplayName("Não deve listar categorias sem autenticação")
    void naoDeveListarCategoriasSemAutenticacao() throws Exception {
        mockMvc.perform(get("/categories"))
            .andExpect(status().isUnauthorized());
    }
}
