package com.tcc.talkie.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.tcc.talkie.domain.occurrence.Occurrence;
import com.tcc.talkie.domain.user.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@talkie.com}")
    private String mailFrom;

    @Value("${app.mail.from-name:Talkie Plataforma}")
    private String mailFromName;

    public void sendOccurrenceNotification(User subscriber, Occurrence occurrence) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String subject = String.format("Nova ocorrência em %s", occurrence.getCategory().getName());
            String htmlContent = buildNotificationHtml(subscriber, occurrence);

            try {
                helper.setFrom(mailFrom, mailFromName);
            } catch (UnsupportedEncodingException e) {
                helper.setFrom(mailFrom);
            }
            helper.setTo(subscriber.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email enviado com sucesso para {} ({}) sobre ocorrência '{}'",
                subscriber.getName(),
                subscriber.getEmail(),
                occurrence.getTitle());
        } catch (MessagingException e) {
            log.error("Erro ao enviar email para {}: {}", subscriber.getEmail(), e.getMessage(), e);
        }
    }

    private String buildNotificationHtml(User subscriber, Occurrence occurrence) {
        return String.format(
            """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Nova Ocorrência - Talkie</title>
                <style>
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        background-color: #f5f5f5;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background-color: white;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                        padding: 30px;
                        color: white;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                    }
                    .content {
                        padding: 30px;
                    }
                    .greeting {
                        margin-bottom: 20px;
                    }
                    .occurrence-card {
                        background-color: #f9f9f9;
                        border-left: 4px solid #667eea;
                        padding: 20px;
                        margin: 20px 0;
                        border-radius: 4px;
                    }
                    .occurrence-card h2 {
                        margin-top: 0;
                        color: #333;
                        font-size: 20px;
                    }
                    .field {
                        margin: 12px 0;
                    }
                    .field-label {
                        font-weight: bold;
                        color: #555;
                        font-size: 13px;
                        text-transform: uppercase;
                    }
                    .field-value {
                        color: #333;
                        margin-top: 4px;
                        word-wrap: break-word;
                    }
                    .badge {
                        display: inline-block;
                        background-color: #667eea;
                        color: white;
                        padding: 6px 12px;
                        border-radius: 20px;
                        font-size: 12px;
                        margin-top: 8px;
                    }
                    .cta-button {
                        display: inline-block;
                        background-color: #667eea;
                        color: white;
                        padding: 12px 30px;
                        text-decoration: none;
                        border-radius: 4px;
                        margin-top: 20px;
                        font-weight: bold;
                    }
                    .cta-button:hover {
                        background-color: #5568d3;
                    }
                    .footer {
                        background-color: #f5f5f5;
                        padding: 20px;
                        text-align: center;
                        font-size: 12px;
                        color: #999;
                        border-top: 1px solid #ddd;
                    }
                    .unsubscribe {
                        color: #667eea;
                        text-decoration: none;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔔 Nova Ocorrência Reportada</h1>
                    </div>
                    <div class="content">
                        <div class="greeting">
                            <p>Olá <strong>%s</strong>,</p>
                            <p>Uma nova ocorrência foi reportada em uma categoria que você segue!</p>
                        </div>
                        <div class="occurrence-card">
                            <h2>%s</h2>
                            <div class="field">
                                <div class="field-label">Categoria</div>
                                <div class="field-value">%s</div>
                            </div>
                            <div class="field">
                                <div class="field-label">Subcategoria</div>
                                <div class="field-value">%s</div>
                            </div>
                            <div class="field">
                                <div class="field-label">Localização</div>
                                <div class="field-value">%s</div>
                            </div>
                            <div class="field">
                                <div class="field-label">Descrição</div>
                                <div class="field-value">%s</div>
                            </div>
                            <div class="field">
                                <div class="field-label">Status</div>
                                <div><span class="badge">%s</span></div>
                            </div>
                        </div>
                        <p style="text-align: center;">
                            <a href="http://localhost:3000/occurrences" class="cta-button">Ver Detalhes no Talkie</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>Você recebeu este email porque está inscrito em <strong>%s</strong>.</p>
                        <p><a href="http://localhost:3000/settings/subscriptions" class="unsubscribe">Gerenciar inscrições</a></p>
                    </div>
                </div>
            </body>
            </html>
            """,
            subscriber.getName(),
            occurrence.getTitle(),
            occurrence.getCategory().getName(),
            occurrence.getSubcategory().getName(),
            occurrence.getLocation(),
            occurrence.getDescription(),
            occurrence.getStatus().name(),
            occurrence.getCategory().getName()
        );
    }
}
