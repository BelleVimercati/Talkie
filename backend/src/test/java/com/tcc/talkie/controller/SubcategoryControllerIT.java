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
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.SubcategoryCreateDTO;
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
class SubcategoryControllerIT {

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
    private Category category;
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

        category = saveCategory("Infraestrutura");
    }

    private Category saveCategory(String name) {
        Category c = new Category();
        c.setName(name);
        c.setIcon("icon.png");
        c.setPriority(CategoryPriority.MEDIA);
        c.setColor("#000000");
        c.setUser(adminUser);
        return categoryRepository.save(c);
    }

    private Subcategory saveSubcategory(String name, Category parent) {
        Subcategory sub = new Subcategory();
        sub.setName(name);
        sub.setCategory(parent);
        return subcategoryRepository.save(sub);
    }

    /* Criar */
    @Test
    @DisplayName("Deve criar uma subcategoria como admin")
    void deveCriarSubcategoriaComoAdmin() throws Exception {
        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Potholes", category.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Subcategoria criada com sucesso"))
            .andExpect(jsonPath("$.data.name").value("Potholes"))
            .andExpect(jsonPath("$.data.categoryName").value("Infraestrutura"))
            .andExpect(jsonPath("$.data.categoryId").value(category.getId()))
            .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("Não deve criar subcategoria como usuário comum")
    void naoDeveCriarSubcategoriaComoUsuarioComum() throws Exception {
        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Potholes", category.getId()))))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve criar subcategoria sem autenticação")
    void naoDeveCriarSubcategoriaSemAutenticacao() throws Exception {
        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Potholes", category.getId()))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve criar subcategoria com categoria inexistente")
    void naoDeveCriarSubcategoriaComCategoriaInexistente() throws Exception {
        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Potholes", 99999L))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve criar subcategoria em categoria inativa")
    void naoDeveCriarSubcategoriaEmCategoriaInativa() throws Exception {
        category.setActive(false);
        categoryRepository.save(category);

        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Potholes", category.getId()))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Categoria inativa"));
    }

    @Test
    @DisplayName("Não deve criar subcategoria duplicada na mesma categoria, mas deve permitir em outra")
    void naoDeveCriarSubcategoriaDuplicadaNaMesmaCategoria() throws Exception {
        saveSubcategory("Potholes", category);
        Category other = saveCategory("Trânsito");

        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("potholes", category.getId()))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Já existe uma subcategoria com esse nome nesta categoria"));

        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Potholes", other.getId()))))
            .andExpect(status().isOk());
    }

    /* Listar / obter */
    @Test
    @DisplayName("Deve listar apenas subcategorias ativas de categorias ativas por padrão")
    void deveListarApenasSubcategoriasAtivas() throws Exception {
        saveSubcategory("Potholes", category);
        Subcategory inactive = saveSubcategory("Iluminação", category);
        inactive.setActive(false);
        subcategoryRepository.save(inactive);

        Category inactiveCategory = saveCategory("Saúde");
        inactiveCategory.setActive(false);
        categoryRepository.save(inactiveCategory);
        saveSubcategory("Postos", inactiveCategory);

        mockMvc.perform(get("/subcategories").header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].name").value("Potholes"));

        mockMvc.perform(get("/subcategories?includeInactive=true").header("Authorization", "Bearer " + adminUserToken))
            .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @DisplayName("Deve obter subcategoria por ID")
    void deveObterSubcategoriaPorId() throws Exception {
        Subcategory sub = saveSubcategory("Potholes", category);

        mockMvc.perform(get("/subcategories/" + sub.getId()).header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.subcategoryId").value(sub.getId()))
            .andExpect(jsonPath("$.data.name").value("Potholes"))
            .andExpect(jsonPath("$.data.categoryName").value("Infraestrutura"))
            .andExpect(jsonPath("$.data.categoryId").value(category.getId()));
    }

    @Test
    @DisplayName("Não deve obter subcategoria inexistente")
    void naoDeveObterSubcategoriaInexistente() throws Exception {
        mockMvc.perform(get("/subcategories/99999").header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Subcategoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve listar subcategorias sem autenticação")
    void naoDeveListarSubcategoriasSemAutenticacao() throws Exception {
        mockMvc.perform(get("/subcategories"))
            .andExpect(status().isUnauthorized());
    }

    /* Atualizar */
    @Test
    @DisplayName("Deve atualizar subcategoria como admin, inclusive mudando de categoria")
    void deveAtualizarSubcategoriaComoAdmin() throws Exception {
        Subcategory sub = saveSubcategory("Potholes", category);
        Category other = saveCategory("Trânsito");

        mockMvc.perform(put("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Buracos na estrada", other.getId()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Subcategoria atualizada com sucesso"))
            .andExpect(jsonPath("$.data.name").value("Buracos na estrada"))
            .andExpect(jsonPath("$.data.categoryId").value(other.getId()));
    }

    @Test
    @DisplayName("Não deve atualizar subcategoria como usuário comum")
    void naoDeveAtualizarSubcategoriaComoUsuarioComum() throws Exception {
        Subcategory sub = saveSubcategory("Potholes", category);

        mockMvc.perform(put("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Buracos", category.getId()))))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve atualizar subcategoria inexistente")
    void naoDeveAtualizarSubcategoriaInexistente() throws Exception {
        mockMvc.perform(put("/subcategories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Buracos", category.getId()))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Subcategoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve atualizar subcategoria com categoria inexistente")
    void naoDeveAtualizarSubcategoriaComCategoriaInexistente() throws Exception {
        Subcategory sub = saveSubcategory("Potholes", category);

        mockMvc.perform(put("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(new SubcategoryCreateDTO("Buracos", 99999L))))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    /* Desativar / reativar */
    @Test
    @DisplayName("Deve desativar subcategoria como admin sem apagá-la")
    void deveDesativarSubcategoriaComoAdmin() throws Exception {
        Subcategory sub = saveSubcategory("Potholes", category);

        mockMvc.perform(delete("/subcategories/" + sub.getId()).header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Subcategoria desativada com sucesso"))
            .andExpect(jsonPath("$.data.active").value(false));

        assert subcategoryRepository.findById(sub.getId()).isPresent();
    }

    @Test
    @DisplayName("Deve reativar subcategoria, exceto se a categoria estiver inativa")
    void deveReativarSubcategoria() throws Exception {
        Subcategory sub = saveSubcategory("Potholes", category);
        sub.setActive(false);
        subcategoryRepository.save(sub);

        mockMvc.perform(put("/subcategories/" + sub.getId() + "/reactivate").header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.active").value(true));

        category.setActive(false);
        categoryRepository.save(category);
        sub = subcategoryRepository.findById(sub.getId()).orElseThrow();
        sub.setActive(false);
        subcategoryRepository.save(sub);

        mockMvc.perform(put("/subcategories/" + sub.getId() + "/reactivate").header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Reative a categoria antes de reativar a subcategoria"));
    }

    @Test
    @DisplayName("Não deve desativar subcategoria como usuário comum")
    void naoDeveDesativarSubcategoriaComoUsuarioComum() throws Exception {
        Subcategory sub = saveSubcategory("Potholes", category);

        mockMvc.perform(delete("/subcategories/" + sub.getId()).header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve desativar subcategoria inexistente")
    void naoDeveDesativarSubcategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/subcategories/99999").header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Subcategoria não encontrada"));
    }
}
