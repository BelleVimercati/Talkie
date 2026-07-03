package com.tcc.talkie.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.tcc.talkie.events.OccurrenceCreatedEvent;
import com.tcc.talkie.repository.SubscriptionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OccurrenceNotificationListener {

    private final SubscriptionRepository subscriptionRepository;

    @EventListener
    public void onOccurrenceCreated(OccurrenceCreatedEvent event) {
        var occurrence = event.getOccurrence();
        var subscribers = subscriptionRepository.findByCategoryId(occurrence.getCategory().getId());
        subscribers.forEach(sub ->
            log.info("Notificando usuário {} ({}) sobre nova ocorrência: '{}'",
                sub.getSubscriber().getName(),
                sub.getSubscriber().getEmail(),
                occurrence.getTitle())
        );
    }
}
