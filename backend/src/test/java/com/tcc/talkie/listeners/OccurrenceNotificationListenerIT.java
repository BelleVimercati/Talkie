package com.tcc.talkie.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.occurrence.OccurrenceStatus;
import com.tcc.talkie.domain.occurrence.Subscription;
import com.tcc.talkie.domain.user.User;
import com.tcc.talkie.events.OccurrenceCreatedEvent;
import com.tcc.talkie.repository.SubscriptionRepository;
import com.tcc.talkie.service.EmailService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class OccurrenceNotificationListenerIT {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private EmailService emailService;

    private User subscriber;
    private Occurrence occurrence;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        subscriber = new User();
        subscriber.setId(UUID.randomUUID());
        subscriber.setName("João Silva");
        subscriber.setEmail("joao@example.com");

        Category category = new Category();
        category.setId(1L);
        category.setName("Infraestrutura");

        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);
        subcategory.setName("Rua Esburacada");

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setName("Maria Santos");
        owner.setEmail("maria@example.com");

        occurrence = new Occurrence();
        occurrence.setId(1L);
        occurrence.setTitle("Buraco na Rua Principal");
        occurrence.setDescription("Há um grande buraco na rua que oferece risco aos pedestres");
        occurrence.setLocation("Rua Principal, 123");
        occurrence.setCategory(category);
        occurrence.setSubcategory(subcategory);
        occurrence.setOwner(owner);
        occurrence.setStatus(OccurrenceStatus.ABERTO);
        occurrence.setCreatedAt(LocalDateTime.now());

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setSubscriber(subscriber);
        subscription.setCategory(category);
        subscription.setSubscribedAt(LocalDateTime.now());
    }

    @Test
    void shouldSendEmailsToSubscribersWhenOccurrenceIsCreated() {
        when(subscriptionRepository.findByCategoryId(occurrence.getCategory().getId()))
            .thenReturn(List.of(subscription));

        OccurrenceCreatedEvent event = new OccurrenceCreatedEvent(occurrence);
        eventPublisher.publishEvent(event);

        verify(emailService, timeout(5000)).sendOccurrenceNotification(eq(subscriber), eq(occurrence));
    }

    @Test
    void shouldNotSendEmailsWhenNoneAreSubscribed() {
        when(subscriptionRepository.findByCategoryId(occurrence.getCategory().getId()))
            .thenReturn(List.of());

        OccurrenceCreatedEvent event = new OccurrenceCreatedEvent(occurrence);
        eventPublisher.publishEvent(event);

        verify(emailService, never()).sendOccurrenceNotification(any(), any());
    }

    @Test
    void shouldSendEmailsToMultipleSubscribers() {
        User subscriber2 = new User();
        subscriber2.setId(UUID.randomUUID());
        subscriber2.setName("Jane Doe");
        subscriber2.setEmail("jane@example.com");

        Subscription subscription2 = new Subscription();
        subscription2.setId(2L);
        subscription2.setSubscriber(subscriber2);
        subscription2.setCategory(occurrence.getCategory());
        subscription2.setSubscribedAt(LocalDateTime.now());

        when(subscriptionRepository.findByCategoryId(occurrence.getCategory().getId()))
            .thenReturn(List.of(subscription, subscription2));

        OccurrenceCreatedEvent event = new OccurrenceCreatedEvent(occurrence);
        eventPublisher.publishEvent(event);

        verify(emailService, timeout(5000)).sendOccurrenceNotification(eq(subscriber), eq(occurrence));
        verify(emailService, timeout(5000)).sendOccurrenceNotification(eq(subscriber2), eq(occurrence));
    }
}
