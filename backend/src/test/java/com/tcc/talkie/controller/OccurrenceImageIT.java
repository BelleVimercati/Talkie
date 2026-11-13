package com.tcc.talkie.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
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
class OccurrenceImageIT {

    // Assinatura mínima de um PNG: basta para a validação por "magic bytes"
    private static final byte[] PNG_BYTES = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};

    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private TokenService tokenService;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SubcategoryRepository subcategoryRepository;
    @Autowired private OccurrenceRepository occurrenceRepository;
    @Autowired private OccurrenceStatusHistoryRepository historyRepository;
    @Autowired private SubscriptionRepository subscriptionRepository;

    private String ownerToken;
    private String otherToken;
    private String adminToken;
    private Occurrence occurrence;

    @BeforeEach
    void setup() {
        historyRepository.deleteAll();
        subscriptionRepository.deleteAll();
        occurrenceRepository.deleteAll();
        subcategoryRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        User owner = saveUser("Autor", "autor@talkie.test", "11111111111", Role.USER);
        User other = saveUser("Outra", "outra@talkie.test", "22222222222", Role.USER);
        User admin = saveUser("Admin", "admin@talkie.test", "33333333333", Role.ADMIN);
        ownerToken = tokenService.generateToken(owner);
        otherToken = tokenService.generateToken(other);
        adminToken = tokenService.generateToken(admin);

        Category category = new Category();
        category.setName("Infraestrutura");
        category.setUser(admin);
        category = categoryRepository.save(category);

        Subcategory subcategory = new Subcategory();
        subcategory.setName("Buraco");
        subcategory.setCategory(category);
        subcategory = subcategoryRepository.save(subcategory);

        occurrence = new Occurrence();
        occurrence.setTitle("Buraco na rua");
        occurrence.setDescription("Descrição");
        occurrence.setLocation("Rua A");
        occurrence.setOwner(owner);
        occurrence.setCategory(category);
        occurrence.setSubcategory(subcategory);
        occurrence.setCreatedAt(LocalDateTime.now());
        occurrence = occurrenceRepository.save(occurrence);
    }

    private User saveUser(String name, String email, String cpf, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setCpf(cpf);
        user.setRole(role);
        return userRepository.save(user);
    }

    private MockMultipartFile png() {
        return new MockMultipartFile("file", "foto.png", "image/png", PNG_BYTES);
    }

    @Test
    @DisplayName("Autor deve enviar imagem e admin deve conseguir visualizá-la")
    void autorDeveEnviarImagemEAdminVisualizar() throws Exception {
        mockMvc.perform(multipart("/occurrences/" + occurrence.getId() + "/image").file(png())
                .header("Authorization", "Bearer " + ownerToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Imagem enviada com sucesso"))
            .andExpect(jsonPath("$.data.imageUrl").value("/occurrences/" + occurrence.getId() + "/image"));

        byte[] body = mockMvc.perform(get("/occurrences/" + occurrence.getId() + "/image")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType("image/png"))
            .andReturn().getResponse().getContentAsByteArray();
        assertThat(body).isEqualTo(PNG_BYTES);
    }

    @Test
    @DisplayName("Não deve aceitar arquivo que não é imagem, mesmo com tipo image/png")
    void naoDeveAceitarArquivoQueNaoEImagem() throws Exception {
        MockMultipartFile fake = new MockMultipartFile("file", "foto.png", "image/png", "<script>".getBytes());

        mockMvc.perform(multipart("/occurrences/" + occurrence.getId() + "/image").file(fake)
                .header("Authorization", "Bearer " + ownerToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Formato de imagem não suportado (use JPEG, PNG ou WebP)"));

        assertThat(occurrenceRepository.findById(occurrence.getId()).orElseThrow().getImagePath()).isNull();
    }

    @Test
    @DisplayName("Outro usuário não deve enviar nem ver a imagem da ocorrência")
    void outroUsuarioNaoDeveEnviarNemVerImagem() throws Exception {
        mockMvc.perform(multipart("/occurrences/" + occurrence.getId() + "/image").file(png())
                .header("Authorization", "Bearer " + otherToken))
            .andExpect(status().isNotFound());

        mockMvc.perform(multipart("/occurrences/" + occurrence.getId() + "/image").file(png())
                .header("Authorization", "Bearer " + ownerToken))
            .andExpect(status().isOk());

        mockMvc.perform(get("/occurrences/" + occurrence.getId() + "/image")
                .header("Authorization", "Bearer " + otherToken))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve responder 404 ao pedir imagem de ocorrência sem imagem")
    void deveResponder404SemImagem() throws Exception {
        mockMvc.perform(get("/occurrences/" + occurrence.getId() + "/image")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Ocorrência sem imagem"));
    }

    @Test
    @DisplayName("Admin e autor devem obter a ocorrência por ID; outro usuário não")
    void deveObterOcorrenciaPorIdConformeAcesso() throws Exception {
        mockMvc.perform(get("/occurrences/" + occurrence.getId()).header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.title").value("Buraco na rua"));

        mockMvc.perform(get("/occurrences/" + occurrence.getId()).header("Authorization", "Bearer " + ownerToken))
            .andExpect(status().isOk());

        mockMvc.perform(get("/occurrences/" + occurrence.getId()).header("Authorization", "Bearer " + otherToken))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/occurrences/99999").header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isNotFound());
    }
}
