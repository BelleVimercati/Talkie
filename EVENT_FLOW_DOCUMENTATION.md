# Documentação do Fluxo de Publicação de Eventos - Talkie

## 📋 Sumário Executivo

O sistema **Talkie** implementa um padrão **Pub/Sub (Publicador/Assinante)** baseado em eventos do Spring Framework para desacoplar a criação de ocorrências do envio de notificações por email. Quando uma nova ocorrência é criada, um evento é publicado e capturado por um listener que notifica automaticamente todos os usuários inscritos na categoria correspondente.

---

## 🏗️ Arquitetura do Sistema de Eventos

```mermaid
graph TB

subgraph Camada_Apresentacao
    Controller["OccurrenceController\nREST API"]
end

subgraph Camada_Servico
    Service["OccurrenceService\nLógica de Negócio"]
    EmailService["EmailService\nEnvio de Emails"]
end

subgraph Camada_Eventos
    EventPublisher["ApplicationEventPublisher\nSpring"]
    Event["OccurrenceCreatedEvent\nPayload"]
end

subgraph Camada_Listeners
    Listener["OccurrenceNotificationListener\n@EventListener"]
end

subgraph Camada_Persistencia
    SubRepo["SubscriptionRepository\nBusca Inscrições"]
    DB[(PostgreSQL)]
end

subgraph Servicos_Externos
    MailHog["MailHog\nSMTP Server"]
end

Controller -->|"1. POST /occurrences"| Service
Service -->|"2. Cria Ocorrência"| DB
Service -->|"3. publishEvent()"| EventPublisher
EventPublisher -->|"4. Emite Evento"| Event
Event -->|"5. Dispara"| Listener
Listener -->|"6. Busca Inscritos"| SubRepo
SubRepo -->|"7. Consulta"| DB
Listener -->|"8. Envia Email"| EmailService
EmailService -->|"9. SMTP"| MailHog

style Controller fill:#667eea,color:#fff
style Service fill:#667eea,color:#fff
style EventPublisher fill:#764ba2,color:#fff
style Listener fill:#764ba2,color:#fff
style EmailService fill:#667eea,color:#fff
style MailHog fill:#f5a623,color:#000
```

---

## 🔄 Diagrama de Sequência Completo

```mermaid
sequenceDiagram
    participant User as "👤 Usuário"
    participant API as "📡 REST API"
    participant Service as "⚙️ OccurrenceService"
    participant Publisher as "🔊 EventPublisher"
    participant Listener as "👂 Listener"
    participant SubRepo as "🗄️ SubscriptionRepository"
    participant EmailService as "✉️ EmailService"
    participant SMTP as "📬 MailHog (SMTP)"
    
    User->>API: 1️⃣ POST /occurrences<br/>(createOccurrence)
    
    API->>Service: 2️⃣ service.create(request)
    
    Service->>Service: 3️⃣ Validações<br/>(categoria, subcategoria, user)
    
    Service->>Service: 4️⃣ Cria Occurrence<br/>e persiste no BD
    
    Service->>Publisher: 5️⃣ publishEvent<br/>(new OccurrenceCreatedEvent)
    
    Publisher->>Listener: 6️⃣ Dispara @EventListener<br/>(onOccurrenceCreated)
    
    Listener->>SubRepo: 7️⃣ findByCategoryId<br/>(occurrence.categoryId)
    
    SubRepo-->>Listener: 8️⃣ Lista de Subscriptions
    
    loop Para cada Subscription
        Listener->>EmailService: 9️⃣ sendOccurrenceNotification<br/>(subscriber, occurrence)
        
        EmailService->>EmailService: 🔟 buildNotificationHtml<br/>(template)
        
        EmailService->>SMTP: 1️⃣1️⃣ Envia MimeMessage
        
        SMTP-->>EmailService: 1️⃣2️⃣ Email Aceito (250 OK)
        
        EmailService-->>Listener: 1️⃣3️⃣ Log de Sucesso
    end
    
    Listener-->>Publisher: 1️⃣4️⃣ Processamento Finalizado
    
    Publisher-->>Service: 1️⃣5️⃣ Retorna
    
    Service-->>API: 1️⃣6️⃣ ResponseEntity<OccurrenceDTO>
    
    API-->>User: 1️⃣7️⃣ HTTP 200 OK<br/>(Ocorrência Criada)
```

---

## 📊 Fluxo de Processamento Passo a Passo

```mermaid
flowchart LR
    A["📥 Recebe Request<br/>POST /occurrences"] -->|DTO| B["🔍 Valida Dados"]
    
    B -->|Válido| C["💾 Cria Occurrence<br/>no BD"]
    B -->|Inválido| Z["❌ Erro 400<br/>BadRequest"]
    
    C -->|Sucesso| D["🔊 Publica Evento<br/>OccurrenceCreatedEvent"]
    C -->|Falha| Z
    
    D -->|Assíncrono| E["👂 Listener Captura<br/>@EventListener"]
    
    E -->|Processa| F["🔎 Busca Subscriptions<br/>da Categoria"]
    
    F -->|Lista Vazia| G["✅ Sem Notificações<br/>(Ninguém Inscrito)"]
    F -->|Com Dados| H["🔁 Itera cada<br/>Subscriber"]
    
    H -->|Para cada um| I["✉️ Envia Email<br/>via SMTP"]
    
    I -->|Sucesso| J["✅ Log Success<br/>Email Enviado"]
    I -->|Erro| K["⚠️ Log Error<br/>Falha no SMTP"]
    
    J -->|Continua loop| H
    K -->|Continua loop| H
    
    H -->|Fim do Loop| L["✅ Evento Processado<br/>Retorna"]
    G -->|Volta| L
    
    L -->|Responde ao Client| M["📤 HTTP 200<br/>+ Dados Ocorrência"]
    
    Z -->|Erro| M
    
    style A fill:#667eea,color:#fff
    style D fill:#764ba2,color:#fff
    style E fill:#764ba2,color:#fff
    style I fill:#f5a623,color:#000
    style M fill:#52c41a,color:#fff
    style Z fill:#ff4d4f,color:#fff
```

---

## 🎯 Componentes Principais

### 1️⃣ OccurrenceService (Publicador do Evento)

**Localização:** `src/main/java/com/tcc/talkie/service/OccurrenceService.java`

**Responsabilidade:** Publicar o evento após a criação bem-sucedida de uma ocorrência.

```java
@Service
@RequiredArgsConstructor
public class OccurrenceService {
    
    private final OccurrenceRepository occurrenceRepository;
    private final ApplicationEventPublisher eventPublisher;  // ← Publica eventos
    
    public OccurrenceResponseDTO create(OccurrenceRequestDTO request) {
        // 1. Validações
        // 2. Criar Occurrence
        
        Occurrence occurrence = new Occurrence();
        occurrence.setTitle(request.title());
        occurrence.setDescription(request.description());
        occurrence.setLocation(request.location());
        occurrence.setOwner(AuthenticatedUser.get());
        occurrence.setCategory(category);
        occurrence.setSubcategory(subcategory);
        
        // 3. Persistir
        Occurrence saved = occurrenceRepository.save(occurrence);
        
        // 4. 🔊 PUBLICAR EVENTO
        eventPublisher.publishEvent(new OccurrenceCreatedEvent(saved));
        
        return mapToResponse(saved);
    }
}
```

---

### 2️⃣ OccurrenceCreatedEvent (Payload do Evento)

**Localização:** `src/main/java/com/tcc/talkie/events/OccurrenceCreatedEvent.java`

**Responsabilidade:** Encapsular os dados da ocorrência criada para serem passados ao listener.

```java
public class OccurrenceCreatedEvent {
    
    private final Occurrence occurrence;
    
    public OccurrenceCreatedEvent(Occurrence occurrence) {
        this.occurrence = occurrence;
    }
    
    public Occurrence getOccurrence() {
        return occurrence;
    }
}
```

---

### 3️⃣ OccurrenceNotificationListener (Assinante do Evento)

**Localização:** `src/main/java/com/tcc/talkie/listeners/OccurrenceNotificationListener.java`

**Responsabilidade:** Escutar eventos de ocorrência criada e coordenar o envio de emails.

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class OccurrenceNotificationListener {
    
    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;
    
    @EventListener
    public void onOccurrenceCreated(OccurrenceCreatedEvent event) {
        var occurrence = event.getOccurrence();
        
        // 1. Buscar todas as inscrições para essa categoria
        var subscribers = subscriptionRepository.findByCategoryId(
            occurrence.getCategory().getId()
        );
        
        // 2. Para cada inscrito, enviar notificação
        subscribers.forEach(sub -> {
            log.info("Enviando notificação para {} ({}) sobre ocorrência '{}'",
                sub.getSubscriber().getName(),
                sub.getSubscriber().getEmail(),
                occurrence.getTitle()
            );
            
            emailService.sendOccurrenceNotification(
                sub.getSubscriber(),
                occurrence
            );
        });
    }
}
```

---

### 4️⃣ EmailService (Serviço de Notificação)

**Localização:** `src/main/java/com/tcc/talkie/service/EmailService.java`

**Responsabilidade:** Construir e enviar emails formatados via SMTP.

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    public void sendOccurrenceNotification(User subscriber, Occurrence occurrence) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 1. Construir conteúdo HTML
            String subject = String.format("Nova ocorrência em %s", 
                occurrence.getCategory().getName());
            String htmlContent = buildNotificationHtml(subscriber, occurrence);
            
            // 2. Configurar email
            helper.setFrom(mailFrom, mailFromName);
            helper.setTo(subscriber.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            // 3. Enviar via SMTP
            mailSender.send(message);
            
            log.info("Email enviado com sucesso para {} ({})",
                subscriber.getName(),
                subscriber.getEmail()
            );
        } catch (MessagingException e) {
            log.error("Erro ao enviar email para {}: {}",
                subscriber.getEmail(),
                e.getMessage(),
                e
            );
        }
    }
    
    private String buildNotificationHtml(User subscriber, Occurrence occurrence) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            ...
            Olá <strong>%s</strong>,
            Uma nova ocorrência foi reportada em %s!
            ...
            """,
            subscriber.getName(),
            occurrence.getCategory().getName()
        );
    }
}
```

---

### 5️⃣ SubscriptionRepository (Acesso aos Dados)

**Localização:** `src/main/java/com/tcc/talkie/repository/SubscriptionRepository.java`

**Responsabilidade:** Buscar todas as inscrições para uma categoria específica.

```java
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    // Busca inscrições por categoria (usada no listener)
    List<Subscription> findByCategoryId(Long categoryId);
    
    // Outras operações
    List<Subscription> findBySubscriberId(UUID subscriberId);
    Optional<Subscription> findBySubscriberIdAndCategoryId(UUID subscriberId, Long categoryId);
    boolean existsBySubscriberIdAndCategoryId(UUID subscriberId, Long categoryId);
}
```

---

## 📧 Fluxo de Email

```mermaid
graph LR
    A["📧 Construct<br/>MimeMessage"] -->|UTF-8| B["🎨 Build HTML<br/>Template"]
    
    B -->|Subject| C["📝 Configure Email<br/>(From, To, Subject)"]
    
    C -->|HTML Content| D["🔐 Set Body<br/>(Text Mode)"]
    
    D -->|Complete| E["📤 Send via<br/>JavaMailSender"]
    
    E -->|TCP 1025| F["📬 MailHog SMTP<br/>Server"]
    
    F -->|Accept (250)| G["✅ Success Log"]
    F -->|Error| H["❌ Error Log"]
    
    G -->|Salva| I["💾 MailHog<br/>Database"]
    I -->|Visualizar em| J["🌐 http://localhost:8025"]
    
    style A fill:#667eea,color:#fff
    style B fill:#667eea,color:#fff
    style E fill:#764ba2,color:#fff
    style F fill:#f5a623,color:#000
    style J fill:#52c41a,color:#fff
```

---

## 🧪 Guia de Teste Manual

### Pré-requisitos
- PostgreSQL rodando (localhost:5432)
- MailHog rodando (localhost:1025 SMTP, 8025 Web)
- Aplicação rodando (localhost:8080)

```bash
# Iniciar tudo
docker-compose up &
mvn spring-boot:run
```

### Cenário de Teste Completo

#### 1. Registrar Usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@test.com",
    "password": "senha123"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "joao@test.com"
}
```

Guarde o `token`.

---

#### 2. Listar Categorias
```bash
curl -X GET http://localhost:8080/categories \
  -H "Authorization: Bearer {seu_token}"
```

Guarde o ID de uma categoria (ex: `1`).

---

#### 3. Inscrever-se em uma Categoria
```bash
curl -X POST http://localhost:8080/subscriptions/1 \
  -H "Authorization: Bearer {seu_token}"
```

**Resposta esperada:**
```json
{
  "message": "Inscrito com sucesso",
  "data": {
    "id": 1,
    "categoryId": 1,
    "categoryName": "Infraestrutura",
    "subscribedAt": "2026-07-06T12:00:00"
  }
}
```

---

#### 4. Criar uma Ocorrência (Dispara o Evento!)
```bash
curl -X POST http://localhost:8080/occurrences \
  -H "Authorization: Bearer {seu_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Buraco na avenida principal",
    "description": "Grande buraco prejudicando o trânsito",
    "categoryId": 1,
    "subcategoryId": 1,
    "location": "Avenida Paulista, nº 1000"
  }'
```

**O que acontece:**
1. ✅ Ocorrência é criada e salva no BD
2. 🔊 Evento `OccurrenceCreatedEvent` é publicado
3. 👂 `OccurrenceNotificationListener` captura o evento
4. 🔍 Busca todas as inscrições da categoria 1
5. 📧 Para cada inscrito, envia um email
6. 📬 Email é capturado pelo MailHog

**Logs esperados:**
```
2026-07-06T12:05:30.123-03:00  INFO [...] Enviando notificação para João Silva (joao@test.com) sobre ocorrência 'Buraco na avenida principal'
2026-07-06T12:05:30.456-03:00  INFO [...] Email enviado com sucesso para joao@test.com (João Silva)
```

---

#### 5. Verificar Email no MailHog
Acesse: **http://localhost:8025**

Você verá:
- Subject: "Nova ocorrência em Infraestrutura"
- From: "noreply@talkie.com"
- To: "joao@test.com"
- Body: HTML formatado com os detalhes da ocorrência

---

## 🔍 Monitoramento e Logs

### Logs Importantes

| Log | Significado | Nível |
|-----|-------------|-------|
| `Enviando notificação para...` | Listener disparado com sucesso | INFO |
| `Email enviado com sucesso para...` | Email aceito pelo SMTP | INFO |
| `Erro ao enviar email para...` | Falha na conexão SMTP | ERROR |
| `FormatFlagsConversionMismatchException` | Erro na string de formato HTML | ERROR |

### Consultas SQL Úteis

```sql
-- Ver todas as inscrições
SELECT s.id, s.subscriber_id, c.name, s.subscribed_at
FROM subscriptions s
JOIN categories c ON s.category_id = c.id
ORDER BY s.subscribed_at DESC;

-- Ver inscrições de um usuário
SELECT c.name
FROM subscriptions s
JOIN categories c ON s.category_id = c.id
WHERE s.subscriber_id = '...'
ORDER BY c.name;

-- Ver inscritos de uma categoria
SELECT u.name, u.email
FROM subscriptions s
JOIN users u ON s.subscriber_id = u.id
WHERE s.category_id = 1;
```

---

## 🎨 Diagrama de Estados da Ocorrência

```mermaid
stateDiagram-v2
    [*] --> CRIADA: Usuário cria ocorrência
    CRIADA --> EVENTO_PUBLICADO: OccurrenceService.publishEvent()
    EVENTO_PUBLICADO --> LISTENER_ATIVO: Spring detecta evento
    LISTENER_ATIVO --> BUSCA_INSCRITOS: findByCategoryId()
    
    state "Processamento de Notificações" as PROC {
        BUSCA_INSCRITOS --> VAZIO: Lista vazia?
        BUSCA_INSCRITOS --> ITERACAO: Há inscritos
        
        ITERACAO --> ENVIO_EMAIL: Para cada inscrito
        ENVIO_EMAIL --> SUCESSO: Email enviado
        ENVIO_EMAIL --> ERRO: Falha SMTP
        
        SUCESSO --> ITERACAO
        ERRO --> ITERACAO
        ITERACAO --> FIM: Todos processados
    }
    
    VAZIO --> CONCLUIDO: Sem notificações
    FIM --> CONCLUIDO: Todas enviadas
    CONCLUIDO --> [*]
    
    style CRIADA fill:#667eea,color:#fff
    style EVENTO_PUBLICADO fill:#764ba2,color:#fff
    style LISTENER_ATIVO fill:#764ba2,color:#fff
    style ENVIO_EMAIL fill:#f5a623,color:#000
    style CONCLUIDO fill:#52c41a,color:#fff
```

---

## 📚 Referências

### Padrão Pub/Sub no Spring
- [Spring Framework Events Documentation](https://spring.io/blog/2015/02/11/better-application-events-in-spring-framework-4-2)
- `ApplicationEventPublisher` - interface para publicação
- `@EventListener` - anotação para escuta de eventos

### Configuração de Email
- **SMTP Server:** MailHog (localhost:1025)
- **Configuração:** `src/main/resources/application.properties`
- **Serviço:** `JavaMailSender` do Spring Mail

### Banco de Dados
- **Tabela subscriptions:**
  - `id` (Long, PK)
  - `subscriber_id` (UUID, FK users)
  - `category_id` (Long, FK categories)
  - `subscribed_at` (LocalDateTime)

---

## ✅ Checklist de Funcionalidade

- [x] Usuário pode se inscrever em categorias via POST `/subscriptions/{categoryId}`
- [x] Usuário pode listar suas inscrições via GET `/subscriptions/my`
- [x] Usuário pode desinscrever-se via DELETE `/subscriptions/{categoryId}`
- [x] Evento é publicado após criação de ocorrência
- [x] Listener captura evento e busca inscritos
- [x] Email é enviado via SMTP para cada inscrito
- [x] Erros são logados mas não interrompem o fluxo
- [x] MailHog captura e exibe emails (localhost:8025)

---

## 🚀 Próximas Etapas (Roadmap)

1. **Testes Automatizados:** Adicionar testes BDD com Cucumber para o fluxo completo
2. **Processamento Assíncrono:** Mover o envio de emails para fila/scheduler (ThreadPool)
3. **Templates Dinâmicos:** Sistema de templates customizáveis por categoria
4. **Rastreamento:** Armazenar histórico de emails enviados (audit log)
5. **Preferências de Notificação:** Permitir usuário escolher frequência (imediato/resumido/nenhum)
6. **Múltiplos Canais:** Suportar Slack, Push Notification, SMS

---

**Documento criado em:** 2026-07-06  
**Versão:** 1.0  
**Autores:** Talkie Development Team
