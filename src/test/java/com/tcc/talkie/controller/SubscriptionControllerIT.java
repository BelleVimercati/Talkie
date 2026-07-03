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
import com.tcc.talkie.domain.occurrence.Subscription;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.SubscriptionRepository;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.infra.security.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SubscriptionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

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
        subscriptionRepository.deleteAll();
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
    @DisplayName("Deve se inscrever em uma categoria com sucesso")
    void deveInscreverEmUmaCategoria() throws Exception {
        mockMvc.perform(post("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Inscrito com sucesso"))
            .andExpect(jsonPath("$.data.categoryId").value(category.getId()))
            .andExpect(jsonPath("$.data.categoryName").value("Infraestrutura"))
            .andExpect(jsonPath("$.data.subscribedAt").exists());
    }

    @Test
    @DisplayName("Não deve se inscrever em categoria inexistente")
    void naoDeveInscreverEmCategoriaInexistente() throws Exception {
        mockMvc.perform(post("/subscriptions/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve se inscrever na mesma categoria duas vezes")
    void naoDeveInscreverDuasVezesNaMesmaCategoria() throws Exception {
        // Primeira inscrição
        mockMvc.perform(post("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk());

        // Segunda inscrição no mesmo usuário e categoria
        mockMvc.perform(post("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Você já está inscrito nesta categoria"));
    }

    @Test
    @DisplayName("Não deve se inscrever sem autenticação")
    void naoDeveInscreverSemAutenticacao() throws Exception {
        mockMvc.perform(post("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve listar minhas inscrições")
    void deveListarMinhasInscricoes() throws Exception {
        // Create some categories and subscribe to them
        for (int i = 0; i < 3; i++) {
            Category cat = new Category();
            cat.setName("Categoria " + i);
            cat.setIcon("icon.png");
            cat.setUser(adminUser);
            cat = categoryRepository.save(cat);

            Subscription subscription = new Subscription();
            subscription.setSubscriber(commonUser);
            subscription.setCategory(cat);
            subscriptionRepository.save(subscription);
        }

        mockMvc.perform(get("/subscriptions/my")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Suas inscrições recuperadas com sucesso"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(3))
            .andExpect(jsonPath("$.data[0].categoryName").exists())
            .andExpect(jsonPath("$.data[0].subscribedAt").exists());
    }

    @Test
    @DisplayName("Deve listar inscrições vazias quando usuário não tem nenhuma")
    void deveListarInscricoesVazias() throws Exception {
        mockMvc.perform(get("/subscriptions/my")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Suas inscrições recuperadas com sucesso"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @DisplayName("Não deve listar inscrições sem autenticação")
    void naoDeveListarInscricoesSemAutenticacao() throws Exception {
        mockMvc.perform(get("/subscriptions/my")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve cancelar inscrição com sucesso")
    void deveCancelarInscrição() throws Exception {
        // Criar inscrição
        Subscription subscription = new Subscription();
        subscription.setSubscriber(commonUser);
        subscription.setCategory(category);
        subscriptionRepository.save(subscription);

        mockMvc.perform(delete("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Inscrição removida com sucesso"));

        assert subscriptionRepository.findBySubscriberIdAndCategoryId(commonUser.getId(), category.getId()).isEmpty();
    }

    @Test
    @DisplayName("Não deve cancelar inscrição inexistente")
    void naoDeveCancelarInscricaoInexistente() throws Exception {
        mockMvc.perform(delete("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Inscrição não encontrada"));
    }

    @Test
    @DisplayName("Não deve cancelar inscrição sem autenticação")
    void naoDeveCancelarInscricaoSemAutenticacao() throws Exception {
        mockMvc.perform(delete("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Usuários diferentes devem ter inscrições independentes")
    void usuariosDiferentesTemInscricoesIndependentes() throws Exception {
        // User 1 se inscreve
        mockMvc.perform(post("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk());

        // User 2 se inscreve na mesma categoria (deve funcionar)
        mockMvc.perform(post("/subscriptions/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk());

        // Verificar que existem 2 inscrições na categoria
        var subscriptions = subscriptionRepository.findByCategoryId(category.getId());
        assert subscriptions.size() == 2;
    }
}
