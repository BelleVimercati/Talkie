package com.tcc.talkie.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcc.talkie.domain.user.Role;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.UpdateDTO;
import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.exceptions.UnauthorizedException;
import com.tcc.talkie.infra.security.AuthenticatedUser;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.OccurrenceRepository;
import com.tcc.talkie.repository.OccurrenceStatusHistoryRepository;
import com.tcc.talkie.repository.SubscriptionRepository;
import com.tcc.talkie.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final OccurrenceRepository occurrenceRepository;
    private final OccurrenceStatusHistoryRepository historyRepository;
    private final CategoryRepository categoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public User updateUser(UUID id, UpdateDTO data){
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
        user.setName(data.name());
        user.setEmail(data.email());
        return repository.save(user);
    }

    public User getUserById(UUID id){
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public List<User> getAllUsers(){
        return repository.findAll();
    }

    /* Exclusão feita por um administrador */
    @Transactional
    public void deleteUser(UUID id){
        deleteAccount(id);
    }

    /* Exclusão da própria conta: o ID vem do token, nunca da requisição */
    @Transactional
    public void deleteOwnAccount(String password){
        UUID userId = AuthenticatedUser.getId();
        User user = repository.findById(userId)
            .orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));

        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Senha incorreta");
        }

        deleteAccount(userId);
    }

    /*
     * Remove a conta preservando ocorrências, categorias e histórico de status.
     * As referências ao usuário viram NULL (exibidas como "Usuário excluído");
     * as assinaturas, que só servem ao próprio usuário, são apagadas.
     * Tudo na mesma transação: qualquer falha reverte a operação inteira.
     */
    @Transactional
    public void deleteAccount(UUID userId){
        User user = repository.findById(userId)
            .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (user.getRole() == Role.ADMIN && repository.countByRole(Role.ADMIN) <= 1) {
            throw new BadRequestException("Não é possível excluir o único administrador do sistema");
        }

        subscriptionRepository.deleteBySubscriberId(userId);
        occurrenceRepository.detachOwner(userId);
        historyRepository.detachActor(userId);
        categoryRepository.detachCreator(userId);

        repository.delete(user);
        repository.flush();

        log.info("Conta excluída: id={}", userId);
    }

}
