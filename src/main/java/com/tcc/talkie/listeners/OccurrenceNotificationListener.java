package com.tcc.talkie.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.tcc.talkie.events.OccurrenceCreatedEvent;
import com.tcc.talkie.repository.SubscriptionRepository;
import com.tcc.talkie.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OccurrenceNotificationListener {

    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    @EventListener
    public void onOccurrenceCreated(OccurrenceCreatedEvent event) {
        var occurrence = event.getOccurrence();
        var subscribers = subscriptionRepository.findByCategoryId(occurrence.getCategory().getId());
        subscribers.forEach(sub -> {
            log.info("Enviando notificação para {} ({}) sobre nova ocorrência: '{}'",
                sub.getSubscriber().getName(),
                sub.getSubscriber().getEmail(),
                occurrence.getTitle());
            emailService.sendOccurrenceNotification(sub.getSubscriber(), occurrence);
        });
    }
}
