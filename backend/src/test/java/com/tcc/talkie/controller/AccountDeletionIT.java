package com.tcc.talkie.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.occurrence.OccurrenceStatus;
import com.tcc.talkie.domain.occurrence.OccurrenceStatusHistory;
import com.tcc.talkie.domain.occurrence.Subscription;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.DeleteAccountDTO;
import com.tcc.talkie.dto.request.OccurrenceDTO;
import com.tcc.talkie.dto.request.OccurrenceStatusUpdateDTO;
import com.tcc.talkie.dto.request.RegisterDTO;
import com.tcc.talkie.infra.security.TokenService;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.OccurrenceStatusHistoryRepository;
import com.tcc.talkie.repository.SubcategoryRepository;
import com.tcc.talkie.repository.SubscriptionRepository;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.service.EmailService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountDeletionIT {

    private static final String PASSWORD = "123456";

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired TokenService tokenService;
    @Autowired UserRepository userRepository;
    @Autowired OccurrenceRepository occurrenceRepository;
    @Autowired OccurrenceStatusHistoryRepository historyRepository;
    @Autowired SubscriptionRepository subscriptionRepository;
    @Autowired SubcategoryRepository subcategoryRepository;

    @SpyBean CategoryRepository categoryRepository;
    @MockBean EmailService emailService;

    private User author;
    private User otherUser;
    private User admin;
    private String authorToken;
    private String otherUserToken;
    private String adminToken;
    private Category category;
    private Subcategory subcategory;

    private Long openOccurrenceId;
    private Long resolvedOccurrenceId;
    private Long closedOccurrenceId;
    private Long otherUserOccurrenceId;

    @BeforeEach
    void setup() throws Exception {
        cleanDatabase();

        author = saveUser("Autor", "autor@talkie.test", "11111111111", Role.USER);
        otherUser = saveUser("Outra Pessoa", "outra@talkie.test", "22222222222", Role.USER);
        admin = saveUser("Admin", "admin@talkie.test", "33333333333", Role.ADMIN);
        authorToken = tokenService.generateToken(author);
        otherUserToken = tokenService.generateToken(otherUser);
        adminToken = tokenService.generateToken(admin);

        category = new Category();
        category.setName("Infraestrutura");
        category.setUser(admin);
        category = categoryRepository.save(category);

        subcategory = new Subcategory();
        subcategory.setName("Buraco");
        subcategory.setCategory(category);
        subcategory = subcategoryRepository.save(subcategory);

        // Ocorrências criadas pela API (geram o evento inicial do histórico)
        openOccurrenceId = createOccurrence(authorToken, "Pendente");
        resolvedOccurrenceId = createOccurrence(authorToken, "Resolvida");
        closedOccurrenceId = createOccurrence(authorToken, "Declinada");
        otherUserOccurrenceId = createOccurrence(otherUserToken, "De outra pessoa");

        changeStatus(adminToken, resolvedOccurrenceId, OccurrenceStatus.RESOLVIDO, "Buraco tapado");
        changeStatus(adminToken, closedOccurrenceId, OccurrenceStatus.FECHADO, "Fora da área atendida");

        subscribe(author);
        subscribe(otherUser);
    }

    @AfterEach
    void cleanup() {
        reset(categoryRepository);
        cleanDatabase();
    }

    /* Cenários 1 a 4 */
    @Test
    @DisplayName("Deve excluir a conta preservando ocorrências pendentes, resolvidas e declinadas")
    void deveExcluirContaPreservandoOcorrencias() throws Exception {
        long totalBefore = occurrenceRepository.count();

        deleteOwnAccount(authorToken, PASSWORD)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Conta excluída com sucesso"))
            .andExpect(jsonPath("$.data").doesNotExist());

        assertThat(userRepository.findById(author.getId())).isEmpty();
        assertThat(occurrenceRepository.count()).isEqualTo(totalBefore);

        assertPreserved(openOccurrenceId, OccurrenceStatus.ABERTO);
        assertPreserved(resolvedOccurrenceId, OccurrenceStatus.RESOLVIDO);
        assertPreserved(closedOccurrenceId, OccurrenceStatus.FECHADO);
        assertThat(occurrenceRepository.findById(resolvedOccurrenceId).orElseThrow().getResolvedAt()).isNotNull();
    }

    /* Cenários 5 e 8 */
    @Test
    @DisplayName("Deve preservar o histórico, anonimizando apenas os eventos do autor excluído")
    void devePreservarHistoricoAnonimizandoAutor() throws Exception {
        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isOk());

        mockMvc.perform(get("/occurrences/" + resolvedOccurrenceId + "/history")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].previousStatus").doesNotExist())
            .andExpect(jsonPath("$.data[0].newStatus").value("ABERTO"))
            .andExpect(jsonPath("$.data[0].changedById").doesNotExist())
            .andExpect(jsonPath("$.data[0].changedByName").doesNotExist())
            .andExpect(jsonPath("$.data[1].previousStatus").value("ABERTO"))
            .andExpect(jsonPath("$.data[1].newStatus").value("RESOLVIDO"))
            .andExpect(jsonPath("$.data[1].reason").value("Buraco tapado"))
            .andExpect(jsonPath("$.data[1].changedById").value(admin.getId().toString()))
            .andExpect(jsonPath("$.data[1].changedByName").value("Admin"))
            .andExpect(jsonPath("$.data[1].changedAt").exists());
    }

    /* Cenário 6 */
    @Test
    @DisplayName("Deve remover os dados pessoais do autor das respostas e do banco")
    void deveAnonimizarReferenciasDoAutor() throws Exception {
        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isOk());

        assertThat(userRepository.findByEmail("autor@talkie.test")).isEmpty();
        assertThat(userRepository.existsByCpf("11111111111")).isFalse();

        String body = mockMvc.perform(get("/occurrences")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertThat(body).doesNotContain("autor@talkie.test", "Autor\"", author.getId().toString());

        JsonNode occurrences = objectMapper.readTree(body).get("data");
        for (JsonNode occ : occurrences) {
            if (occ.get("id").asLong() == otherUserOccurrenceId) continue;
            assertThat(occ.get("ownerId").isNull()).isTrue();
            assertThat(occ.get("ownerName").isNull()).isTrue();
        }
    }

    /* Cenário 7 */
    @Test
    @DisplayName("Admin deve continuar tratando a ocorrência pendente do autor excluído")
    void adminDeveContinuarTratandoOcorrenciaPendente() throws Exception {
        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isOk());

        changeStatus(adminToken, openOccurrenceId, OccurrenceStatus.RESOLVIDO, "Resolvido após exclusão do autor")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("RESOLVIDO"))
            .andExpect(jsonPath("$.data.ownerName").doesNotExist());

        List<OccurrenceStatusHistory> history = historyRepository.findByOccurrenceIdOrderByChangedAtAscIdAsc(openOccurrenceId);
        assertThat(history).hasSize(2);
        assertThat(history.get(1).getChangedBy().getId()).isEqualTo(admin.getId());
    }

    /* Cenário 9 */
    @Test
    @DisplayName("Não deve excluir a conta sem autenticação")
    void naoDeveExcluirSemAutenticacao() throws Exception {
        mockMvc.perform(delete("/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new DeleteAccountDTO(PASSWORD))))
            .andExpect(status().isUnauthorized());

        assertThat(userRepository.findById(author.getId())).isPresent();
    }

    @Test
    @DisplayName("Não deve excluir a conta com senha incorreta ou ausente")
    void naoDeveExcluirComSenhaIncorreta() throws Exception {
        deleteOwnAccount(authorToken, "senha-errada")
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Senha incorreta"));

        mockMvc.perform(delete("/users/me").header("Authorization", "Bearer " + authorToken))
            .andExpect(status().isUnauthorized());

        assertThat(userRepository.findById(author.getId())).isPresent();
        assertThat(occurrenceRepository.findById(openOccurrenceId).orElseThrow().getOwner().getId())
            .isEqualTo(author.getId());
        assertThat(subscriptionRepository.findBySubscriberId(author.getId())).hasSize(1);
    }

    @Test
    @DisplayName("Usuário comum não deve excluir a conta de outra pessoa")
    void usuarioNaoDeveExcluirContaDeOutraPessoa() throws Exception {
        mockMvc.perform(delete("/users/" + otherUser.getId())
                .header("Authorization", "Bearer " + authorToken))
            .andExpect(status().isForbidden());

        assertThat(userRepository.findById(otherUser.getId())).isPresent();
    }

    /* Cenários 10 e 13 */
    @Test
    @DisplayName("O token da conta excluída não deve mais dar acesso, e a repetição não altera dados")
    void tokenDaContaExcluidaDeveSerRejeitado() throws Exception {
        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isOk());
        long occurrences = occurrenceRepository.count();
        long history = historyRepository.count();
        long users = userRepository.count();

        mockMvc.perform(get("/occurrences/my").header("Authorization", "Bearer " + authorToken))
            .andExpect(status().isUnauthorized());

        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isUnauthorized());

        assertThat(occurrenceRepository.count()).isEqualTo(occurrences);
        assertThat(historyRepository.count()).isEqualTo(history);
        assertThat(userRepository.count()).isEqualTo(users);
    }

    @Test
    @DisplayName("Token antigo não deve autenticar uma nova conta cadastrada com o mesmo e-mail")
    void tokenAntigoNaoDeveAutenticarNovaContaComMesmoEmail() throws Exception {
        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new RegisterDTO("Nova Conta", "autor@talkie.test", PASSWORD, "11111111111", null))))
            .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/occurrences/my").header("Authorization", "Bearer " + authorToken))
            .andExpect(status().isUnauthorized());

        User newAccount = userRepository.findByEmail("autor@talkie.test").orElseThrow();
        mockMvc.perform(get("/occurrences/my").header("Authorization", "Bearer " + tokenService.generateToken(newAccount)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    /* Cenário 11 */
    @Test
    @DisplayName("Deve reverter toda a exclusão quando uma etapa obrigatória falhar")
    void deveReverterQuandoEtapaFalhar() throws Exception {
        doThrow(new IllegalStateException("falha simulada")).when(categoryRepository).detachCreator(any(UUID.class));

        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isBadRequest());

        assertThat(userRepository.findById(author.getId())).isPresent();
        assertThat(subscriptionRepository.findBySubscriberId(author.getId())).hasSize(1);
        for (Long id : List.of(openOccurrenceId, resolvedOccurrenceId, closedOccurrenceId)) {
            assertThat(occurrenceRepository.findById(id).orElseThrow().getOwner().getId()).isEqualTo(author.getId());
        }
        assertThat(historyRepository.findByOccurrenceIdOrderByChangedAtAscIdAsc(openOccurrenceId).get(0).getChangedBy())
            .isNotNull();
    }

    /* Cenário 12 */
    @Test
    @DisplayName("Nenhuma referência deve apontar para usuário inexistente após a exclusão")
    void deveManterIntegridadeReferencial() throws Exception {
        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isOk());

        for (Occurrence occ : occurrenceRepository.findAll()) {
            if (occ.getOwner() != null) assertThat(userRepository.existsById(occ.getOwner().getId())).isTrue();
        }
        for (OccurrenceStatusHistory h : historyRepository.findAll()) {
            if (h.getChangedBy() != null) assertThat(userRepository.existsById(h.getChangedBy().getId())).isTrue();
            assertThat(occurrenceRepository.existsById(h.getOccurrence().getId())).isTrue();
        }
        for (Subscription s : subscriptionRepository.findAll()) {
            assertThat(userRepository.existsById(s.getSubscriber().getId())).isTrue();
        }
        for (Category c : categoryRepository.findAll()) {
            if (c.getUser() != null) assertThat(userRepository.existsById(c.getUser().getId())).isTrue();
        }
    }

    /* Cenário 14 */
    @Test
    @DisplayName("Não deve afetar ocorrências, assinaturas e contas de outros usuários")
    void naoDeveAfetarOutrosUsuarios() throws Exception {
        deleteOwnAccount(authorToken, PASSWORD).andExpect(status().isOk());

        assertThat(userRepository.findById(otherUser.getId())).isPresent();
        assertThat(userRepository.findById(admin.getId())).isPresent();
        Occurrence other = occurrenceRepository.findById(otherUserOccurrenceId).orElseThrow();
        assertThat(other.getOwner().getId()).isEqualTo(otherUser.getId());
        assertThat(other.getStatus()).isEqualTo(OccurrenceStatus.ABERTO);
        assertThat(subscriptionRepository.findBySubscriberId(otherUser.getId())).hasSize(1);
        assertThat(categoryRepository.findById(category.getId()).orElseThrow().getUser().getId()).isEqualTo(admin.getId());

        mockMvc.perform(get("/occurrences/my").header("Authorization", "Bearer " + otherUserToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    @DisplayName("O único administrador não deve conseguir excluir a própria conta")
    void unicoAdminNaoDeveExcluirPropriaConta() throws Exception {
        deleteOwnAccount(adminToken, PASSWORD)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Não é possível excluir o único administrador do sistema"));

        assertThat(userRepository.findById(admin.getId())).isPresent();
    }

    @Test
    @DisplayName("Admin excluído deve preservar categorias e decisões que tomou, sem identificação")
    void adminExcluidoDevePreservarCategoriasEDecisoes() throws Exception {
        User secondAdmin = saveUser("Admin 2", "admin2@talkie.test", "44444444444", Role.ADMIN);

        deleteOwnAccount(adminToken, PASSWORD).andExpect(status().isOk());

        Category preserved = categoryRepository.findById(category.getId()).orElseThrow();
        assertThat(preserved.getUser()).isNull();
        assertThat(subcategoryRepository.findById(subcategory.getId())).isPresent();

        List<OccurrenceStatusHistory> history = historyRepository.findByOccurrenceIdOrderByChangedAtAscIdAsc(closedOccurrenceId);
        assertThat(history).hasSize(2);
        assertThat(history.get(1).getChangedBy()).isNull();
        assertThat(history.get(1).getReason()).isEqualTo("Fora da área atendida");
        assertThat(history.get(0).getChangedBy().getId()).isEqualTo(author.getId());

        assertThat(userRepository.findById(secondAdmin.getId())).isPresent();
    }

    @Test
    @DisplayName("Admin deve excluir a conta de um usuário usando o mesmo fluxo de anonimização")
    void adminDeveExcluirUsuarioPreservandoOcorrencias() throws Exception {
        mockMvc.perform(delete("/users/" + author.getId())
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());

        assertThat(userRepository.findById(author.getId())).isEmpty();
        assertPreserved(openOccurrenceId, OccurrenceStatus.ABERTO);
        assertThat(subscriptionRepository.findBySubscriberId(author.getId())).isEmpty();
    }

    // ---------- helpers ----------

    private void cleanDatabase() {
        historyRepository.deleteAll();
        subscriptionRepository.deleteAll();
        occurrenceRepository.deleteAll();
        subcategoryRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User saveUser(String name, String email, String cpf, Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setCpf(cpf);
        user.setRole(role);
        return userRepository.save(user);
    }

    private Long createOccurrence(String token, String title) throws Exception {
        OccurrenceDTO dto = new OccurrenceDTO(title, "Descrição", "Rua A", category.getId(), subcategory.getId(), null, null, null);
        MvcResult result = mockMvc.perform(post("/occurrences")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).at("/data/id").asLong();
    }

    private org.springframework.test.web.servlet.ResultActions changeStatus(String token, Long id, OccurrenceStatus status, String reason) throws Exception {
        return mockMvc.perform(put("/occurrences/" + id + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(new OccurrenceStatusUpdateDTO(status, reason))))
            .andExpect(status().isOk());
    }

    private void subscribe(User user) {
        Subscription subscription = new Subscription();
        subscription.setSubscriber(user);
        subscription.setCategory(category);
        subscriptionRepository.save(subscription);
    }

    private org.springframework.test.web.servlet.ResultActions deleteOwnAccount(String token, String password) throws Exception {
        return mockMvc.perform(delete("/users/me")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .content(objectMapper.writeValueAsString(Map.of("password", password))));
    }

    private void assertPreserved(Long occurrenceId, OccurrenceStatus expectedStatus) {
        Occurrence occ = occurrenceRepository.findById(occurrenceId).orElseThrow();
        assertThat(occ.getStatus()).isEqualTo(expectedStatus);
        assertThat(occ.getOwner()).isNull();
        assertThat(occ.getTitle()).isNotBlank();
        assertThat(historyRepository.findByOccurrenceIdOrderByChangedAtAscIdAsc(occurrenceId)).isNotEmpty();
    }
}
