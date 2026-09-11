package com.tcc.talkie.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
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
import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.occurrence.OccurrenceStatus;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.request.OccurrenceStatusUpdateDTO;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.SubcategoryRepository;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.infra.security.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OccurrenceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OccurrenceRepository occurrenceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    private User commonUser;
    private User adminUser;
    private Category category;
    private Subcategory subcategory;
    private String commonUserToken;
    private String adminUserToken;

    @BeforeEach
    void setup() {
        occurrenceRepository.deleteAll();
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

        // Create subcategory
        subcategory = new Subcategory();
        subcategory.setName("Potholes");
        subcategory.setCategory(category);
        subcategory = subcategoryRepository.save(subcategory);
    }

    @Test
    @DisplayName("Deve criar uma ocorrência com sucesso")
    void deveCriarOcorrenciaComSucesso() throws Exception {
        OccurrenceDTO dto = new OccurrenceDTO(
            "Buraco na rua",
            "Há um grande buraco na Rua A",
            "Rua A, número 123",
            category.getId(),
            subcategory.getId()
        );

        mockMvc.perform(post("/occurrences")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Buraco na rua"))
            .andExpect(jsonPath("$.description").value("Há um grande buraco na Rua A"))
            .andExpect(jsonPath("$.location").value("Rua A, número 123"));
    }

    @Test
    @DisplayName("Não deve criar ocorrência sem autenticação")
    void naoDeveCriarOcorrenciaSemAutenticacao() throws Exception {
        OccurrenceDTO dto = new OccurrenceDTO(
            "Buraco na rua",
            "Há um grande buraco na Rua A",
            "Rua A, número 123",
            category.getId(),
            subcategory.getId()
        );

        mockMvc.perform(post("/occurrences")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve criar ocorrência com categoria inexistente")
    void naoDeveCriarOcorrenciaComCategoriaInexistente() throws Exception {
        OccurrenceDTO dto = new OccurrenceDTO(
            "Buraco na rua",
            "Há um grande buraco na Rua A",
            "Rua A, número 123",
            99999L,
            subcategory.getId()
        );

        mockMvc.perform(post("/occurrences")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Categoria não encontrada"));
    }

    @Test
    @DisplayName("Não deve criar ocorrência com subcategoria inexistente")
    void naoDeveCriarOcorrenciaComSubcategoriaInexistente() throws Exception {
        OccurrenceDTO dto = new OccurrenceDTO(
            "Buraco na rua",
            "Há um grande buraco na Rua A",
            "Rua A, número 123",
            category.getId(),
            99999L
        );

        mockMvc.perform(post("/occurrences")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Subcategoria não encontrada"));
    }

    @Test
    @DisplayName("Deve listar todas as ocorrências")
    void deveListarTodasAsOcorrencias() throws Exception {
        // Create some occurrences
        for (int i = 0; i < 3; i++) {
            Occurrence occ = new Occurrence();
            occ.setTitle("Ocorrência " + i);
            occ.setDescription("Descrição " + i);
            occ.setLocation("Local " + i);
            occ.setOwner(commonUser);
            occ.setCategory(category);
            occ.setSubcategory(subcategory);
            occ.setCreatedAt(LocalDateTime.now());
            occurrenceRepository.save(occ);
        }

        mockMvc.perform(get("/occurrences")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @DisplayName("Deve listar ocorrências do usuário logado")
    void deveListarOcorrenciasDoUsuarioLogado() throws Exception {
        // Create occurrences for common user
        for (int i = 0; i < 2; i++) {
            Occurrence occ = new Occurrence();
            occ.setTitle("Ocorrência do comum " + i);
            occ.setDescription("Descrição " + i);
            occ.setLocation("Local " + i);
            occ.setOwner(commonUser);
            occ.setCategory(category);
            occ.setSubcategory(subcategory);
            occ.setCreatedAt(LocalDateTime.now());
            occurrenceRepository.save(occ);
        }

        // Create occurrence for admin user
        Occurrence adminOcc = new Occurrence();
        adminOcc.setTitle("Ocorrência do admin");
        adminOcc.setDescription("Descrição admin");
        adminOcc.setLocation("Local admin");
        adminOcc.setOwner(adminUser);
        adminOcc.setCategory(category);
        adminOcc.setSubcategory(subcategory);
        adminOcc.setCreatedAt(LocalDateTime.now());
        occurrenceRepository.save(adminOcc);

        mockMvc.perform(get("/occurrences/my")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Deve deletar ocorrência como admin")
    void deveDeletarOcorrenciaComoAdmin() throws Exception {
        Occurrence occ = new Occurrence();
        occ.setTitle("Buraco");
        occ.setDescription("Descrição");
        occ.setLocation("Local");
        occ.setOwner(commonUser);
        occ.setCategory(category);
        occ.setSubcategory(subcategory);
        occ.setCreatedAt(LocalDateTime.now());
        occ = occurrenceRepository.save(occ);

        mockMvc.perform(delete("/occurrences/" + occ.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Ocorrência deletada com sucesso"));

        assert occurrenceRepository.findById(occ.getId()).isEmpty();
    }

    @Test
    @DisplayName("Não deve deletar ocorrência como usuário comum")
    void naoDeveDeletarOcorrenciaComoUsuarioComum() throws Exception {
        Occurrence occ = new Occurrence();
        occ.setTitle("Buraco");
        occ.setDescription("Descrição");
        occ.setLocation("Local");
        occ.setOwner(commonUser);
        occ.setCategory(category);
        occ.setSubcategory(subcategory);
        occ.setCreatedAt(LocalDateTime.now());
        occ = occurrenceRepository.save(occ);

        mockMvc.perform(delete("/occurrences/" + occ.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve deletar ocorrência inexistente")
    void naoDeveDeletarOcorrenciaInexistente() throws Exception {
        mockMvc.perform(delete("/occurrences/99999")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Ocorrência não encontrada"));
    }

    @Test
    @DisplayName("Não deve listar ocorrências sem autenticação")
    void naoDeveListarOcorrenciasSemAutenticacao() throws Exception {
        mockMvc.perform(get("/occurrences"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve listar ocorrências por categoria")
    void deveListarOcorrenciasPorCategoria() throws Exception {
        // Create occurrences for the category
        for (int i = 0; i < 2; i++) {
            Occurrence occ = new Occurrence();
            occ.setTitle("Ocorrência da categoria " + i);
            occ.setDescription("Descrição " + i);
            occ.setLocation("Local " + i);
            occ.setOwner(commonUser);
            occ.setCategory(category);
            occ.setSubcategory(subcategory);
            occurrenceRepository.save(occ);
        }

        mockMvc.perform(get("/occurrences/category/" + category.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Ocorrências da categoria recuperadas com sucesso"))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].status").value("PENDENTE"));
    }

    @Test
    @DisplayName("Deve atualizar status da ocorrência como admin")
    void deveAtualizarStatusDaOcorrenciaComoAdmin() throws Exception {
        Occurrence occ = new Occurrence();
        occ.setTitle("Buraco");
        occ.setDescription("Descrição");
        occ.setLocation("Local");
        occ.setOwner(commonUser);
        occ.setCategory(category);
        occ.setSubcategory(subcategory);
        occ.setStatus(OccurrenceStatus.PENDENTE);
        occ = occurrenceRepository.save(occ);

        OccurrenceStatusUpdateDTO dto = new OccurrenceStatusUpdateDTO(OccurrenceStatus.RESOLVIDO);

        mockMvc.perform(put("/occurrences/" + occ.getId() + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Status atualizado com sucesso"))
            .andExpect(jsonPath("$.data.status").value("RESOLVIDO"));
    }

    @Test
    @DisplayName("Não deve atualizar status como usuário comum")
    void naoDeveAtualizarStatusComoUsuarioComum() throws Exception {
        Occurrence occ = new Occurrence();
        occ.setTitle("Buraco");
        occ.setDescription("Descrição");
        occ.setLocation("Local");
        occ.setOwner(commonUser);
        occ.setCategory(category);
        occ.setSubcategory(subcategory);
        occ = occurrenceRepository.save(occ);

        OccurrenceStatusUpdateDTO dto = new OccurrenceStatusUpdateDTO(OccurrenceStatus.RESOLVIDO);

        mockMvc.perform(put("/occurrences/" + occ.getId() + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + commonUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Não deve atualizar status sem autenticação")
    void naoDeveAtualizarStatusSemAutenticacao() throws Exception {
        Occurrence occ = new Occurrence();
        occ.setTitle("Buraco");
        occ.setDescription("Descrição");
        occ.setLocation("Local");
        occ.setOwner(commonUser);
        occ.setCategory(category);
        occ.setSubcategory(subcategory);
        occ = occurrenceRepository.save(occ);

        OccurrenceStatusUpdateDTO dto = new OccurrenceStatusUpdateDTO(OccurrenceStatus.RESOLVIDO);

        mockMvc.perform(put("/occurrences/" + occ.getId() + "/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Não deve atualizar status de ocorrência inexistente")
    void naoDeveAtualizarStatusDeOcorrenciaInexistente() throws Exception {
        OccurrenceStatusUpdateDTO dto = new OccurrenceStatusUpdateDTO(OccurrenceStatus.RESOLVIDO);

        mockMvc.perform(put("/occurrences/99999/status")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + adminUserToken)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Ocorrência não encontrada"));
    }
}
