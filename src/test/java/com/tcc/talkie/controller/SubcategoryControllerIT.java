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
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.SubcategoryCreateDTO;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.SubcategoryRepository;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.infra.security.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubcategoryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

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
    private Category category;
    private String commonUserToken;
    private String adminUserToken;

    @BeforeEach
    void setup() {
        subcategoryRepository.deleteAll();
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

        // Create category
        category = new Category();
        category.setName("Infraestrutura");
        category.setIcon("icon.png");
        category.setUser(adminUser);
        category = categoryRepository.save(category);
    }

    @Test
    @DisplayName("Deve criar uma subcategoria como admin")
    void deveCriarSubcategoriaComoAdmin() throws Exception {
        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Potholes", category.getId());

        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Subcategoria criada com sucesso"))
            .andExpect(jsonPath("$.data.name").value("Potholes"))
            .andExpect(jsonPath("$.data.categoryName").value("Infraestrutura"));
    }

    @Test
    @DisplayName("Não deve criar subcategoria como usuário comum")
    void naoDeveCriarSubcategoriaComoUsuarioComum() throws Exception {
        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Potholes", category.getId());

        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve criar subcategoria sem autenticação")
    void naoDeveCriarSubcategoriaSemAutenticacao() throws Exception {
        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Potholes", category.getId());

        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve criar subcategoria com categoria inexistente")
    void naoDeveCriarSubcategoriaComCategoriaInexistente() throws Exception {
        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Potholes", 99999L);

        mockMvc.perform(post("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve listar todas as subcategorias")
    void deveListarTodasAsSubcategorias() throws Exception {
        // Create some subcategories
        for (int i = 0; i < 3; i++) {
            Subcategory sub = new Subcategory();
            sub.setName("Subcategoria " + i);
            sub.setCategory(category);
            subcategoryRepository.save(sub);
        }

        mockMvc.perform(get("/subcategories")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("Deve obter subcategoria por ID")
    void deveObterSubcategoriaPorId() throws Exception {
        Subcategory sub = new Subcategory();
        sub.setName("Potholes");
        sub.setCategory(category);
        sub = subcategoryRepository.save(sub);

        mockMvc.perform(get("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(sub.getId()))
            .andExpect(jsonPath("$.name").value("Potholes"))
            .andExpect(jsonPath("$.categoryName").value("Infraestrutura"));
    }

    @Test
    @DisplayName("Não deve obter subcategoria inexistente")
    void naoDeveObterSubcategoriaInexistente() throws Exception {
        mockMvc.perform(get("/subcategories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Subcategoria não encontrada"));
    }

    @Test
    @DisplayName("Deve atualizar subcategoria como admin")
    void deveAtualizarSubcategoriaComoAdmin() throws Exception {
        Subcategory sub = new Subcategory();
        sub.setName("Potholes");
        sub.setCategory(category);
        sub = subcategoryRepository.save(sub);

        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Buracos na estrada", category.getId());

        mockMvc.perform(put("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Subcategoria atualizada com sucesso"))
            .andExpect(jsonPath("$.data.name").value("Buracos na estrada"));
    }

    @Test
    @DisplayName("Não deve atualizar subcategoria como usuário comum")
    void naoDeveAtualizarSubcategoriaComoUsuarioComum() throws Exception {
        Subcategory sub = new Subcategory();
        sub.setName("Potholes");
        sub.setCategory(category);
        sub = subcategoryRepository.save(sub);

        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Buracos na estrada", category.getId());

        mockMvc.perform(put("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve atualizar subcategoria inexistente")
    void naoDeveAtualizarSubcategoriaInexistente() throws Exception {
        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Buracos", category.getId());

        mockMvc.perform(put("/subcategories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Subcategoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve atualizar subcategoria com categoria inexistente")
    void naoDeveAtualizarSubcategoriaComCategoriaInexistente() throws Exception {
        Subcategory sub = new Subcategory();
        sub.setName("Potholes");
        sub.setCategory(category);
        sub = subcategoryRepository.save(sub);

        SubcategoryCreateDTO dto = new SubcategoryCreateDTO("Buracos", 99999L);

        mockMvc.perform(put("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Deve deletar subcategoria como admin")
    void deveDeletarSubcategoriaComoAdmin() throws Exception {
        Subcategory sub = new Subcategory();
        sub.setName("Subcategoria Teste");
        sub.setCategory(category);
        sub = subcategoryRepository.save(sub);

        mockMvc.perform(delete("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Subcategoria deletada com sucesso"));

        assert subcategoryRepository.findById(sub.getId()).isEmpty();
    }

    @Test
    @DisplayName("Não deve deletar subcategoria como usuário comum")
    void naoDeveDeletarSubcategoriaComoUsuarioComum() throws Exception {
        Subcategory sub = new Subcategory();
        sub.setName("Subcategoria Teste");
        sub.setCategory(category);
        sub = subcategoryRepository.save(sub);

        mockMvc.perform(delete("/subcategories/" + sub.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve deletar subcategoria inexistente")
    void naoDeveDeletarSubcategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/subcategories/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Subcategoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve listar subcategorias sem autenticação")
    void naoDeveListarSubcategoriasSemAutenticacao() throws Exception {
        mockMvc.perform(get("/subcategories"))
            .andExpect(status().isUnauthorized());
    }
}
