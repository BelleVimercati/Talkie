package com.tcc.talkie.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.occurrence.Subscription;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.dto.response.SubscriptionResponseDTO;
import com.tcc.talkie.infra.exceptions.BadRequestException;
import com.tcc.talkie.infra.exceptions.NotFoundException;
import com.tcc.talkie.infra.security.AuthenticatedUser;
import com.tcc.talkie.repository.CategoryRepository;
import com.tcc.talkie.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CategoryRepository categoryRepository;

    public SubscriptionResponseDTO subscribe(Long categoryId) {
        UUID subscriberId = AuthenticatedUser.getId();

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));

        boolean alreadySubscribed = subscriptionRepository.existsBySubscriberIdAndCategoryId(subscriberId, categoryId);
        if (alreadySubscribed) {
            throw new BadRequestException("Você já está inscrito nesta categoria");
        }

        User subscriber = AuthenticatedUser.get();
        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setCategory(category);

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    public void unsubscribe(Long categoryId) {
        UUID subscriberId = AuthenticatedUser.getId();

        Subscription subscription = subscriptionRepository
            .findBySubscriberIdAndCategoryId(subscriberId, categoryId)
            .orElseThrow(() -> new NotFoundException("Inscrição não encontrada"));

        subscriptionRepository.delete(subscription);
    }

    public List<SubscriptionResponseDTO> listMySubscriptions() {
        UUID subscriberId = AuthenticatedUser.getId();
        return subscriptionRepository.findBySubscriberId(subscriberId)
            .stream()
            .map(this::mapToResponse)
            .toList();
    }

    private SubscriptionResponseDTO mapToResponse(Subscription subscription) {
        return new SubscriptionResponseDTO(
            subscription.getId(),
            subscription.getCategory().getId(),
            subscription.getCategory().getName(),
            subscription.getSubscribedAt()
        );
    }
}
