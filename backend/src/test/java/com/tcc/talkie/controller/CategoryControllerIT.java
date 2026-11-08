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
import com.tcc.talkie.domain.category.CategoryPriority;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.CategoryCreateDTO;
import com.tcc.talkie.infra.security.TokenService;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.OccurrenceStatusHistoryRepository;
import com.tcc.talkie.repository.SubcategoryRepository;
import com.tcc.talkie.repository.SubscriptionRepository;
import com.tcc.talkie.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SubcategoryRepository subcategoryRepository;
    @Autowired private OccurrenceRepository occurrenceRepository;
    @Autowired private OccurrenceStatusHistoryRepository historyRepository;
    @Autowired private SubscriptionRepository subscriptionRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TokenService tokenService;

    private User adminUser;
    private String commonUserToken;
    private String adminUserToken;

    @BeforeEach
    void setup() {
        // O contexto é compartilhado entre as classes de teste: limpar dependentes antes dos pais
        historyRepository.deleteAll();
        subscriptionRepository.deleteAll();
        occurrenceRepository.deleteAll();
        subcategoryRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User commonUser = new User();
        commonUser.setName("João Common");
        commonUser.setEmail("joao@gmail.com");
        commonUser.setPassword(passwordEncoder.encode("123456"));
        commonUser.setCpf("11111111111");
        commonUser.setRole(Role.USER);
        commonUser = userRepository.save(commonUser);
        commonUserToken = tokenService.generateToken(commonUser);

        adminUser = new User();
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@gmail.com");
        adminUser.setPassword(passwordEncoder.encode("123456"));
        adminUser.setCpf("22222222222");
        adminUser.setRole(Role.ADMIN);
        adminUser = userRepository.save(adminUser);
        adminUserToken = tokenService.generateToken(adminUser);
    }

    private Category saveCategory(String name, User creator) {
        Category category = new Category();
        category.setName(name);
        category.setIcon("icon.png");
        category.setPriority(CategoryPriority.MEDIA);
        category.setColor("#000000");
        category.setUser(creator);
        return categoryRepository.save(category);
    }

    private CategoryCreateDTO dto(String name, String icon) {
        return new CategoryCreateDTO(name, icon, CategoryPriority.MEDIA, "#000000");
    }

    /* Criar */
    @Test
    @DisplayName("Deve criar uma categoria como admin")
    void deveCriarCategoriaComoAdmin() throws Exception {
        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto("Violência", "violence-icon.png"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Categoria criada com sucesso"))
            .andExpect(jsonPath("$.data.id").isNumber())
            .andExpect(jsonPath("$.data.name").value("Violência"))
            .andExpect(jsonPath("$.data.icon").value("violence-icon.png"))
            .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("Não deve criar categoria como usuário comum")
    void naoDeveCriarCategoriaComoUsuarioComum() throws Exception {
        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto("Violência", "violence-icon.png"))))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve criar categoria sem autenticação")
    void naoDeveCriarCategoriaSemAutenticacao() throws Exception {
        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto("Violência", "violence-icon.png"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve criar categoria com nome duplicado (case insensitive)")
    void naoDeveCriarCategoriaComNomeDuplicado() throws Exception {
        saveCategory("Violência", adminUser);

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto("violência", "another-icon.png"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Já existe uma categoria com esse nome"));
    }

    @Test
    @DisplayName("Deve sugerir reativar ao criar categoria com nome de uma inativa")
    void deveSugerirReativarAoCriarComNomeDeInativa() throws Exception {
        Category inactive = saveCategory("Violência", adminUser);
        inactive.setActive(false);
        categoryRepository.save(inactive);

        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto("Violência", "icon.png"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Já existe uma categoria inativa com esse nome; reative-a"));
    }

    @Test
    @DisplayName("Não deve criar categoria com nome em branco")
    void naoDeveCriarCategoriaComNomeEmBranco() throws Exception {
        mockMvc.perform(post("/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto("   ", "icon.png"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Nome da categoria é obrigatório"));
    }

    /* Listar / obter */
    @Test
    @DisplayName("Deve listar apenas categorias ativas por padrão")
    void deveListarApenasCategoriasAtivas() throws Exception {
        saveCategory("Infraestrutura", adminUser);
        saveCategory("Segurança", adminUser);
        Category inactive = saveCategory("Saúde", adminUser);
        inactive.setActive(false);
        categoryRepository.save(inactive);

        mockMvc.perform(get("/categories").header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2));

        mockMvc.perform(get("/categories?includeInactive=true").header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @DisplayName("Deve listar categoria cujo criador excluiu a conta")
    void deveListarCategoriaSemCriador() throws Exception {
        saveCategory("Infraestrutura", null);

        mockMvc.perform(get("/categories").header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].name").value("Infraestrutura"))
            .andExpect(jsonPath("$.data[0].userId").doesNotExist());
    }

    @Test
    @DisplayName("Deve obter categoria por ID")
    void deveObterCategoriaPorId() throws Exception {
        Category cat = saveCategory("Infraestrutura", adminUser);

        mockMvc.perform(get("/categories/" + cat.getId()).header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.id").value(cat.getId()))
            .andExpect(jsonPath("$.data.name").value("Infraestrutura"))
            .andExpect(jsonPath("$.data.userId").value(adminUser.getId().toString()));
    }

    @Test
    @DisplayName("Não deve obter categoria inexistente")
    void naoDeveObterCategoriaInexistente() throws Exception {
        mockMvc.perform(get("/categories/99999").header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve listar categorias sem autenticação")
    void naoDeveListarCategoriasSemAutenticacao() throws Exception {
        mockMvc.perform(get("/categories"))
            .andExpect(status().isUnauthorized());
    }

    /* Atualizar */
    @Test
    @DisplayName("Deve atualizar categoria como admin")
    void deveAtualizarCategoriaComoAdmin() throws Exception {
        Category cat = saveCategory("Infraestrutura", adminUser);

        mockMvc.perform(put("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new CategoryCreateDTO("Segurança", "new-icon.png", CategoryPriority.ALTA, "#ff0000"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Categoria atualizada com sucesso"))
            .andExpect(jsonPath("$.data.name").value("Segurança"))
            .andExpect(jsonPath("$.data.icon").value("new-icon.png"))
            .andExpect(jsonPath("$.data.priority").value("ALTA"))
            .andExpect(jsonPath("$.data.color").value("#ff0000"));
    }

    @Test
    @DisplayName("Deve manter o próprio nome ao atualizar categoria")
    void deveManterProprioNomeAoAtualizar() throws Exception {
        Category cat = saveCategory("Infraestrutura", adminUser);

        mockMvc.perform(put("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto("Infraestrutura", "new-icon.png"))))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Não deve atualizar categoria para nome de outra existente")
    void naoDeveAtualizarParaNomeDuplicado() throws Exception {
        saveCategory("Segurança", adminUser);
        Category cat = saveCategory("Infraestrutura", adminUser);

        mockMvc.perform(put("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto("segurança", "icon.png"))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Já existe uma categoria com esse nome"));
    }

    @Test
    @DisplayName("Não deve atualizar categoria como usuário comum")
    void naoDeveAtualizarCategoriaComoUsuarioComum() throws Exception {
        Category cat = saveCategory("Infraestrutura", adminUser);

        mockMvc.perform(put("/categories/" + cat.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto("Segurança", "new-icon.png"))))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve atualizar categoria inexistente")
    void naoDeveAtualizarCategoriaInexistente() throws Exception {
        mockMvc.perform(put("/categories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto("Segurança", "icon.png"))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    /* Desativar / reativar */
    @Test
    @DisplayName("Deve desativar categoria como admin sem apagá-la")
    void deveDesativarCategoriaComoAdmin() throws Exception {
        Category cat = saveCategory("Infraestrutura", adminUser);

        mockMvc.perform(delete("/categories/" + cat.getId()).header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Categoria desativada com sucesso"))
            .andExpect(jsonPath("$.data.active").value(false));

        assert categoryRepository.findById(cat.getId()).isPresent();
        assert !categoryRepository.findById(cat.getId()).get().isActive();

        mockMvc.perform(get("/categories").header("Authorization", "Bearer " + commonUserToken))
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("Deve reativar categoria como admin")
    void deveReativarCategoriaComoAdmin() throws Exception {
        Category cat = saveCategory("Infraestrutura", adminUser);
        cat.setActive(false);
        categoryRepository.save(cat);

        mockMvc.perform(put("/categories/" + cat.getId() + "/reactivate").header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Categoria reativada com sucesso"))
            .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("Não deve desativar nem reativar categoria como usuário comum")
    void naoDeveDesativarCategoriaComoUsuarioComum() throws Exception {
        Category cat = saveCategory("Infraestrutura", adminUser);

        mockMvc.perform(delete("/categories/" + cat.getId()).header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isForbidden());
        mockMvc.perform(put("/categories/" + cat.getId() + "/reactivate").header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve desativar categoria inexistente")
    void naoDeveDesativarCategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/categories/99999").header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }
}
