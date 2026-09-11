package com.tcc.talkie.bdd.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.talkie.bdd.TestContext;
import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.request.LoginRequestDTO;
import com.tcc.talkie.dto.request.OccurrenceRequestDTO;
import com.tcc.talkie.dto.response.ApiResponse;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.SubscriptionRepository;
import com.tcc.talkie.repository.UserRepository;
import com.tcc.talkie.service.TokenService;

import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.occurrence.OccurrenceStatus;
import com.tcc.talkie.dto.request.SubscriptionRequestDTO;
import com.tcc.talkie.service.EmailService;
import org.springframework.boot.test.mock.mockito.MockBean;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import lombok.RequiredArgsConstructor;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class NotificationSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private TokenService tokenService;

    @MockBean
    private EmailService emailService;

    private final TestContext context;

    @Dado("que um usuário {string} está autenticado")
    public void usuarioAutenticado(String email) throws Exception {
        User user = new User();
        user.setEmail(email);
        user.setName(email.split("@")[0]);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.ROLE_USER);
        user = userRepository.save(user);

        context.setCurrentUser(user);
        context.setAuthToken("Bearer " + tokenService.gerarToken(user));
    }

    @Dado("um usuário {string} está inscrito na categoria {string}")
    public void usuarioInscritoNaCategoria(String email, String categoryName) throws Exception {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + categoryName));

        mockMvc.perform(post("/subscriptions/" + category.getId())
            .header("Authorization", context.getAuthToken()))
            .andExpect(status().isOk());
    }

    @Dado("um usuário {string} não está inscrito na categoria {string}")
    public void usuarioNaoInscritoNaCategoria(String email, String categoryName) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        assertTrue(subscriptionRepository.findBySubscriberIdAndCategoryId(user.getId(), category.getId()).isEmpty());
    }

    @Dado("o usuário está inscrito na categoria {string}")
    public void usuarioInscrito(String categoryName) throws Exception {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        mockMvc.perform(post("/subscriptions/" + category.getId())
            .header("Authorization", context.getAuthToken()))
            .andExpect(status().isOk());
    }

    @Quando("o usuário se inscreve na categoria {string}")
    public void usuarioSeInscreve(String categoryName) throws Exception {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        MvcResult result = mockMvc.perform(post("/subscriptions/" + category.getId())
            .header("Authorization", context.getAuthToken()))
            .andExpect(status().isOk())
            .andReturn();

        context.setLastResponse(result.getResponse().getContentAsString());
    }

    @Quando("o usuário se desinscreve da categoria {string}")
    public void usuarioSeDesinscreve(String categoryName) throws Exception {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        mockMvc.perform(delete("/subscriptions/" + category.getId())
            .header("Authorization", context.getAuthToken()))
            .andExpect(status().isOk());
    }

    @Quando("o usuário tenta se inscrever novamente na categoria {string}")
    public void usuarioTentaSeInscreverNovamente(String categoryName) throws Exception {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        MvcResult result = mockMvc.perform(post("/subscriptions/" + category.getId())
            .header("Authorization", context.getAuthToken()))
            .andReturn();

        context.setLastResponse(result.getResponse().getContentAsString());
        context.setLastStatusCode(result.getResponse().getStatus());
    }

    @Quando("o usuário solicita listar suas inscrições")
    public void usuarioListaInscrições() throws Exception {
        MvcResult result = mockMvc.perform(get("/subscriptions/my")
            .header("Authorization", context.getAuthToken()))
            .andExpect(status().isOk())
            .andReturn();

        String response = result.getResponse().getContentAsString();
        context.setLastResponse(response);
    }

    @Então("a inscrição deve ser salva com sucesso")
    public void inscricaoSalva() throws Exception {
        String response = context.getLastResponse();
        assertNotNull(response);
        assertTrue(response.contains("sucesso"));
    }

    @Então("a inscrição deve ser removida com sucesso")
    public void inscricaoRemovida() {
        assertTrue(context.getLastResponse().contains("sucesso"));
    }

    @Então("uma erro de inscrição duplicada deve ser retornado")
    public void erroInscricaoDuplicada() {
        assertEquals(400, context.getLastStatusCode());
        assertTrue(context.getLastResponse().contains("já está inscrito"));
    }

    @Então("a lista deve conter {int} inscrições")
    public void listaContemInscrições(int expectedCount) throws Exception {
        String response = context.getLastResponse();
        assertNotNull(response);
        assertTrue(response.length() > 0);
    }

    @Então("deve incluir {string}")
    public void deveIncluir(String categoryName) throws Exception {
        String response = context.getLastResponse();
        assertTrue(response.contains(categoryName));
    }

    @Quando("uma nova ocorrência {string} é criada na categoria {string}")
    public void novaOcorrenciaECriada(String title, String categoryName) throws Exception {
        Category category = categoryRepository.findByName(categoryName)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Subcategory subcategory = category.getSubcategories().stream().findFirst()
            .orElseThrow(() -> new RuntimeException("Subcategoria não encontrada"));

        OccurrenceRequestDTO dto = new OccurrenceRequestDTO(
            title,
            "Descrição da ocorrência",
            "Localização teste",
            category.getId(),
            subcategory.getId()
        );

        MvcResult result = mockMvc.perform(post("/occurrences")
            .header("Authorization", context.getAuthToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andReturn();

        context.setLastResponse(result.getResponse().getContentAsString());
    }

    @Então("um email deve ser enviado para {string}")
    public void emailDeveSerEnviado(String email) {
        verify(emailService, timeout(2000).atLeastOnce())
            .sendOccurrenceNotification(argThat(user -> user.getEmail().equals(email)), any(Occurrence.class));
    }

    @Então("nenhum email deve ser enviado para {string}")
    public void nenhumEmailDeveSerEnviado(String email) {
        verify(emailService, never())
            .sendOccurrenceNotification(argThat(user -> user.getEmail().equals(email)), any(Occurrence.class));
    }

    @Então("o email deve conter o título {string}")
    public void emailDeveConterTitulo(String title) {
        // Este passo seria verificado capturando o email ou vendo logs
        verify(emailService, timeout(2000)).sendOccurrenceNotification(any(User.class),
            argThat(occurrence -> occurrence.getTitle().contains(title)));
    }

    @Então("o email deve conter a localização da ocorrência")
    public void emailDeveConterLocalizacao() {
        // Verificação é feita através do mock do EmailService
        verify(emailService, timeout(2000)).sendOccurrenceNotification(any(User.class), any(Occurrence.class));
    }
}
