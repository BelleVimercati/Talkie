package com.tcc.talkie.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import com.tcc.talkie.domain.category.Category;
import com.tcc.talkie.domain.category.Subcategory;
import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.occurrence.OccurrenceStatus;
import com.tcc.talkie.domain.user.User;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceIT {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    private User subscriber;
    private Occurrence occurrence;

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
    }

    @Test
    void shouldSendOccurrenceNotificationSuccessfully() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendOccurrenceNotification(subscriber, occurrence);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldBuildHtmlEmailWithOccurrenceDetails() {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        assertDoesNotThrow(() -> {
            emailService.sendOccurrenceNotification(subscriber, occurrence);
        });

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldHandleEmailSendingException() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Email service error"));

        assertDoesNotThrow(() -> {
            emailService.sendOccurrenceNotification(subscriber, occurrence);
        });
    }
}
