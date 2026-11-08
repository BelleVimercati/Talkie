# Levantamento de requisitos do Talkie

> **Documento de trabalho.** 8 dos 69 RFs e 17 dos 49 RNFs descrevem o sistema como se as pendências já estivessem resolvidas, **mas elas ainda não foram implementadas**. Esses itens estão marcados com ⚠️ e um código `Pxx`, detalhado na seção 4. No LaTeX, a marca aparece como comentário `% PENDENTE Pxx`, que não sai no PDF. Remova as marcas à medida que resolver cada item.

## 1. Resumo

O Talkie é uma plataforma web de gestão de ocorrências comunitárias. Cidadãos registram problemas classificados por categoria e subcategoria, com localização e foto, e acompanham o andamento de cada registro. Administradores analisam e tratam as ocorrências, mantêm a classificação e gerenciam os usuários. Quem assina uma categoria recebe notificações por e-mail. Os atores são o **visitante** (sem conta), o **usuário** (conta comum) e o **administrador**, que também pode usar as funções de usuário.

## 2. Requisitos funcionais

| ID | Descrição | Prioridade | Evidência |
|---|---|---|---|
| | **Cadastro e acesso** | | |
| RF01 | O sistema deve permitir que um visitante crie uma conta informando nome, CPF, e-mail e senha. | Alta | `frontend/src/pages/AuthPage/RegisterPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/AuthService.java` |
| RF02 | O sistema deve impedir que um visitante crie uma conta com CPF já cadastrado. | Alta | `backend/src/main/java/com/tcc/talkie/service/AuthService.java` |
| RF03 | O sistema deve impedir que um visitante crie uma conta com e-mail já cadastrado. | Alta | `backend/src/main/java/com/tcc/talkie/service/AuthService.java` |
| RF04 | O sistema deve permitir que um usuário cadastrado acesse a plataforma com e-mail e senha. | Alta | `frontend/src/pages/AuthPage/LoginPage.tsx`; `backend/src/main/java/com/tcc/talkie/controller/AuthController.java` |
| RF05 | O sistema deve permitir que um usuário autenticado encerre sua sessão. | Média | `frontend/src/components/layout/Sidebar.tsx`; `frontend/src/stores/authStore.ts` |
| | **Conta do usuário** | | |
| RF06 | O sistema deve permitir que um usuário visualize o e-mail e o perfil de sua conta. | Baixa | `frontend/src/pages/ProfilePage.tsx` |
| RF07 | O sistema deve permitir que um usuário exclua a própria conta mediante confirmação da senha. | Média | `frontend/src/pages/ProfilePage.tsx`; `backend/src/main/java/com/tcc/talkie/service/UserService.java` |
| RF08 | O sistema deve manter as ocorrências de uma conta excluída, sem identificar o autor. | Média | `backend/src/main/java/com/tcc/talkie/service/UserService.java`; `frontend/src/utils/formatAuthor.ts` |
| RF09 | O sistema deve impedir a exclusão do único administrador. | Média | `backend/src/main/java/com/tcc/talkie/service/UserService.java` |
| | **Registro de ocorrências** | | |
| RF10 | O sistema deve permitir que um usuário registre uma ocorrência com título, descrição, localização, categoria e subcategoria. | Alta | `frontend/src/pages/OccurrencesPage/CreateOccurrencePage.tsx`; `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RF11 | O sistema deve permitir que um usuário informe a data e o horário do fato ocorrido. | Média | `frontend/src/pages/OccurrencesPage/CreateOccurrencePage.tsx`; `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RF12 | O sistema deve permitir que um usuário marque o local da ocorrência em um mapa. | Média | `frontend/src/components/occurrences/LocationPicker.tsx` |
| RF13 | O sistema deve permitir que um usuário utilize a localização do dispositivo para marcar o local da ocorrência. | Baixa | `frontend/src/hooks/useGeolocation.ts`; `frontend/src/components/occurrences/LocationPicker.tsx` |
| RF14 | O sistema deve permitir que um usuário ajuste o ponto marcado arrastando-o no mapa. | Baixa | `frontend/src/components/occurrences/LocationPicker.tsx` |
| RF15 | O sistema deve permitir que um usuário remova o ponto marcado no mapa. | Baixa | `frontend/src/components/occurrences/LocationPicker.tsx` |
| RF16 | O sistema deve sugerir ao usuário um endereço a partir do ponto marcado no mapa. | Baixa | `frontend/src/services/geocodingService.ts`; `frontend/src/pages/OccurrencesPage/CreateOccurrencePage.tsx` |
| RF17 | O sistema deve permitir que um usuário anexe uma foto à ocorrência. | Média | `frontend/src/components/occurrences/ImageUpload.tsx`; `backend/src/main/java/com/tcc/talkie/service/StorageService.java` |
| RF18 | O sistema deve impedir o registro de ocorrências em categorias ou subcategorias inativas. | Média | `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RF19 | O sistema deve impedir o registro de ocorrência com subcategoria que não pertença à categoria informada. | Média | `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| | **Consulta e acompanhamento** | | |
| RF20 | O sistema deve permitir que um usuário visualize a lista das ocorrências que registrou. | Alta | `frontend/src/pages/HomePage.tsx`; `frontend/src/hooks/useOccurrences.ts` |
| RF21 | O sistema deve permitir que um usuário acompanhe o status de suas ocorrências. | Alta | `frontend/src/components/occurrences/OccurrencesTable.tsx` |
| RF22 ⚠️ P05 | O sistema deve permitir que um usuário visualize os detalhes e a foto de uma ocorrência que registrou. | Média | **PENDENTE P05.** A corrigir em: `frontend/src/App.tsx`; `frontend/src/pages/HomePage.tsx` |
| RF23 | O sistema deve permitir que um administrador visualize todas as ocorrências registradas. | Alta | `frontend/src/pages/HomePage.tsx`; `backend/src/main/java/com/tcc/talkie/controller/OccurrenceController.java` |
| RF24 | O sistema deve permitir que um usuário visualize os totais de ocorrências por situação. | Baixa | `frontend/src/components/occurrences/StatsCards.tsx` |
| RF25 | O sistema deve permitir que um usuário pesquise ocorrências por título ou categoria. | Média | `frontend/src/components/occurrences/OccurrencesTable.tsx` |
| | **Tratamento pelo administrador** | | |
| RF26 | O sistema deve permitir que um administrador visualize os detalhes de qualquer ocorrência. | Alta | `frontend/src/pages/OccurrencesPage/OccurrenceDetailsPage.tsx` |
| RF27 | O sistema deve permitir que um administrador visualize a foto anexada à ocorrência. | Média | `frontend/src/components/occurrences/OccurrenceImage.tsx` |
| RF28 | O sistema deve permitir que um administrador visualize no mapa o local da ocorrência. | Média | `frontend/src/pages/OccurrencesPage/OccurrenceDetailsPage.tsx` |
| RF29 | O sistema deve permitir que um administrador abra o local da ocorrência em um serviço externo de mapas. | Baixa | `frontend/src/pages/OccurrencesPage/OccurrenceDetailsPage.tsx` |
| RF30 ⚠️ P06 | O sistema deve permitir que um administrador marque uma ocorrência como em análise. | Média | **PENDENTE P06.** A corrigir em: `backend/src/main/java/com/tcc/talkie/domain/occurrence/OccurrenceStatus.java`; `frontend/src/components/occurrences/StatusActionPanel.tsx` |
| RF31 | O sistema deve permitir que um administrador marque uma ocorrência como resolvida. | Alta | `frontend/src/components/occurrences/StatusActionPanel.tsx`; `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RF32 | O sistema deve permitir que um administrador encerre uma ocorrência sem resolução. | Alta | `frontend/src/components/occurrences/StatusActionPanel.tsx` |
| RF33 | O sistema deve permitir que um administrador reabra uma ocorrência resolvida ou encerrada. | Média | `frontend/src/components/occurrences/StatusActionPanel.tsx` |
| RF34 | O sistema deve permitir que um administrador informe o motivo de uma mudança de status. | Média | `frontend/src/components/occurrences/StatusActionPanel.tsx`; `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RF35 | O sistema deve registrar a data em que uma ocorrência foi resolvida. | Baixa | `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RF36 | O sistema deve permitir que um administrador consulte o histórico de status de uma ocorrência, com data e autor de cada mudança. | Média | `frontend/src/components/occurrences/StatusTimeline.tsx`; `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RF37 ⚠️ P01 | O sistema deve permitir que um administrador exclua uma ocorrência. | Média | **PENDENTE P01.** A corrigir em: `backend/src/main/java/com/tcc/talkie/controller/OccurrenceController.java`; `frontend/src/services/occurrenceService.ts` |
| | **Categorias** | | |
| RF38 | O sistema deve permitir que um administrador crie uma categoria. | Alta | `frontend/src/pages/SettingsPage/CreateCategoryPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/CategoryService.java` |
| RF39 | O sistema deve permitir que um administrador edite uma categoria. | Média | `frontend/src/pages/SettingsPage/CreateCategoryPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/CategoryService.java` |
| RF40 | O sistema deve permitir que um administrador defina a prioridade de uma categoria. | Média | `frontend/src/pages/SettingsPage/CreateCategoryPage.tsx`; `backend/src/main/java/com/tcc/talkie/domain/category/CategoryPriority.java` |
| RF41 | O sistema deve permitir que um administrador defina o ícone de uma categoria. | Baixa | `frontend/src/components/ui/IconPicker.tsx` |
| RF42 | O sistema deve permitir que um administrador defina a cor de uma categoria. | Baixa | `frontend/src/components/ui/ColorPicker.tsx` |
| RF43 | O sistema deve permitir que um administrador desative uma categoria, mantendo as ocorrências já registradas. | Média | `frontend/src/pages/SettingsPage/CategoriesListPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/CategoryService.java` |
| RF44 | O sistema deve permitir que um administrador reative uma categoria. | Média | `frontend/src/pages/SettingsPage/CategoriesListPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/CategoryService.java` |
| RF45 | O sistema deve permitir que um administrador pesquise categorias por nome. | Baixa | `frontend/src/pages/SettingsPage/CategoriesListPage.tsx` |
| RF46 | O sistema deve permitir que um administrador exiba as categorias e subcategorias inativas. | Baixa | `frontend/src/pages/SettingsPage/CategoriesListPage.tsx` |
| RF47 | O sistema deve impedir a existência de categorias com o mesmo nome. | Média | `backend/src/main/java/com/tcc/talkie/service/CategoryService.java` |
| | **Subcategorias** | | |
| RF48 | O sistema deve permitir que um administrador crie uma subcategoria dentro de uma categoria. | Alta | `frontend/src/pages/SettingsPage/CreateSubcategoryPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/SubcategoryService.java` |
| RF49 | O sistema deve permitir que um administrador edite uma subcategoria. | Média | `frontend/src/pages/SettingsPage/CreateSubcategoryPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/SubcategoryService.java` |
| RF50 | O sistema deve permitir que um administrador visualize as subcategorias de uma categoria. | Baixa | `frontend/src/pages/SettingsPage/CategoriesListPage.tsx` |
| RF51 | O sistema deve permitir que um administrador desative uma subcategoria, mantendo as ocorrências já registradas. | Média | `frontend/src/pages/SettingsPage/CategoriesListPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/SubcategoryService.java` |
| RF52 | O sistema deve permitir que um administrador reative uma subcategoria cuja categoria esteja ativa. | Média | `frontend/src/pages/SettingsPage/CategoriesListPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/SubcategoryService.java` |
| RF53 | O sistema deve impedir a existência de subcategorias com o mesmo nome em uma mesma categoria. | Média | `backend/src/main/java/com/tcc/talkie/service/SubcategoryService.java` |
| | **Assinaturas e notificações** | | |
| RF54 | O sistema deve permitir que um usuário assine uma categoria de seu interesse. | Média | `frontend/src/components/notifications/SubscribeModal.tsx`; `backend/src/main/java/com/tcc/talkie/service/SubscriptionService.java` |
| RF55 | O sistema deve permitir que um usuário cancele a assinatura de uma categoria. | Média | `frontend/src/pages/NotificationsPage.tsx`; `backend/src/main/java/com/tcc/talkie/service/SubscriptionService.java` |
| RF56 | O sistema deve permitir que um usuário troque a categoria de uma assinatura. | Baixa | `frontend/src/components/notifications/SubscribeModal.tsx` |
| RF57 | O sistema deve permitir que um usuário visualize suas assinaturas. | Média | `frontend/src/pages/NotificationsPage.tsx`; `backend/src/main/java/com/tcc/talkie/controller/SubscriptionController.java` |
| RF58 | O sistema deve impedir que um usuário assine mais de uma vez a mesma categoria. | Baixa | `backend/src/main/java/com/tcc/talkie/service/SubscriptionService.java` |
| RF59 | O sistema deve impedir a assinatura de uma categoria inativa. | Baixa | `backend/src/main/java/com/tcc/talkie/service/SubscriptionService.java` |
| RF60 | O sistema deve enviar um e-mail aos assinantes da categoria quando uma nova ocorrência for registrada. | Média | `backend/src/main/java/com/tcc/talkie/listeners/OccurrenceNotificationListener.java`; `backend/src/main/java/com/tcc/talkie/service/EmailService.java` |
| RF61 ⚠️ P08 | O sistema deve incluir no e-mail de notificação um link de acesso à plataforma. | Baixa | **PENDENTE P08.** A corrigir em: `backend/src/main/java/com/tcc/talkie/service/EmailService.java` |
| RF62 ⚠️ P09 | O sistema deve enviar um e-mail ao autor da ocorrência quando o status dela for alterado. | Média | **PENDENTE P09.** A corrigir em: `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java`; `backend/src/main/java/com/tcc/talkie/service/EmailService.java` |
| RF63 | O sistema deve permitir que um usuário visualize as ocorrências das categorias que assina. | Média | `frontend/src/pages/NotificationsPage.tsx`; `frontend/src/hooks/useNotifications.ts` |
| RF64 | O sistema deve permitir que um usuário pesquise as ocorrências das categorias que assina. | Baixa | `frontend/src/pages/NotificationsPage.tsx` |
| | **Gestão de usuários** | | |
| RF65 | O sistema deve permitir que um administrador visualize a lista de usuários. | Média | `frontend/src/pages/SettingsPage/UsersListPage.tsx`; `backend/src/main/java/com/tcc/talkie/controller/UserController.java` |
| RF66 | O sistema deve permitir que um administrador pesquise usuários por nome ou e-mail. | Baixa | `frontend/src/pages/SettingsPage/UsersListPage.tsx` |
| RF67 ⚠️ P03 | O sistema deve permitir que um administrador consulte os dados de um usuário específico. | Baixa | **PENDENTE P03.** A corrigir em: `backend/src/main/java/com/tcc/talkie/controller/UserController.java`; `frontend/src/services/userService.ts` |
| RF68 ⚠️ P02 | O sistema deve permitir que um administrador edite o nome e o e-mail de um usuário. | Média | **PENDENTE P02.** A corrigir em: `backend/src/main/java/com/tcc/talkie/controller/UserController.java`; `frontend/src/services/userService.ts` |
| RF69 ⚠️ P04 | O sistema deve permitir que um administrador exclua a conta de um usuário. | Média | **PENDENTE P04.** A corrigir em: `backend/src/main/java/com/tcc/talkie/controller/UserController.java`; `frontend/src/services/userService.ts` |

**Justificativas de prioridade**

- **Assinaturas e notificações (Média):** ampliam o alcance da plataforma, mas registrar e tratar ocorrências funciona sem elas.
- **Bloqueios de CPF e e-mail repetidos (Alta):** garantem que cada conta identifique uma única pessoa.
- **Resolver e encerrar ocorrências (Alta):** sem eles, o ciclo de tratamento não se completa.
- **Em análise e reabertura (Média):** detalham o ciclo de tratamento, que se completa sem elas.
- **Ícone e cor de categoria (Baixa):** afetam apenas a apresentação.

## 3. Requisitos não funcionais

| ID | Categoria | Descrição | Prioridade | Evidência |
|---|---|---|---|---|
| RNF01 | Segurança | O sistema deve armazenar as senhas de forma irreversível, por meio de função de hash. | Alta | `backend/src/main/java/com/tcc/talkie/infra/security/SecurityConfig.java`; `backend/src/main/java/com/tcc/talkie/service/AuthService.java` |
| RNF02 | Segurança | O sistema deve exigir autenticação em todas as funcionalidades, exceto cadastro, login e documentação da API. | Alta | `backend/src/main/java/com/tcc/talkie/infra/security/SecurityConfig.java` |
| RNF03 | Segurança | O sistema deve encerrar a validade de cada sessão após 2 horas. | Média | `backend/src/main/java/com/tcc/talkie/infra/security/TokenService.java` |
| RNF04 | Segurança | O sistema deve restringir as funcionalidades administrativas ao perfil de administrador. | Alta | `backend/src/main/java/com/tcc/talkie/infra/security/SecurityConfig.java`; `frontend/src/components/common/ProtectedRoute.tsx` |
| RNF05 | Segurança | O sistema deve restringir o acesso ao detalhe e à foto de uma ocorrência ao seu autor e aos administradores. | Alta | `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RNF06 | Segurança | O sistema deve invalidar as sessões de uma conta excluída. | Média | `backend/src/main/java/com/tcc/talkie/infra/security/SecurityFilter.java` |
| RNF07 ⚠️ P18 | Segurança | O sistema deve impedir que o cadastro público crie contas de administrador. | Alta | **PENDENTE P18.** A corrigir em: `backend/src/main/java/com/tcc/talkie/service/AuthService.java`; `backend/src/main/java/com/tcc/talkie/dto/request/RegisterDTO.java` |
| RNF08 ⚠️ P19 | Segurança | O sistema deve restringir a consulta de ocorrências por categoria às categorias assinadas pelo usuário. | Alta | **PENDENTE P19.** A corrigir em: `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java` |
| RNF09 ⚠️ P20 | Segurança | O sistema deve manter as chaves de segurança fora do código-fonte. | Alta | **PENDENTE P20.** A corrigir em: `backend/src/main/resources/application.properties` |
| RNF10 ⚠️ P21 | Segurança | O sistema não deve manter credenciais administrativas padrão em ambiente de produção. | Alta | **PENDENTE P21.** A corrigir em: `backend/src/main/resources/db/migration/V7__seed_admin_user.sql` |
| RNF11 ⚠️ P22 | Segurança | O sistema deve responder da mesma forma a uma tentativa de login com e-mail inexistente ou senha incorreta. | Média | **PENDENTE P22.** A corrigir em: `backend/src/main/java/com/tcc/talkie/controller/AuthController.java` |
| RNF12 ⚠️ P15 | Segurança | O sistema deve utilizar comunicação criptografada por HTTPS em produção. | Alta | **PENDENTE P15.** A corrigir em: `backend/docker-compose.yml`; `frontend/src/hooks/useGeolocation.ts` |
| RNF13 ⚠️ P07 | Segurança | O sistema deve validar no servidor os dados recebidos nas requisições. | Alta | **PENDENTE P07.** A corrigir em: `backend/pom.xml`; `backend/src/main/java/com/tcc/talkie/infra/exceptions/GlobalExceptionHandler.java` |
| RNF14 | Segurança | O sistema deve verificar o tipo real das imagens enviadas, aceitando apenas JPEG, PNG e WebP. | Média | `backend/src/main/java/com/tcc/talkie/service/StorageService.java` |
| RNF15 | Segurança | O sistema deve limitar o tamanho das imagens enviadas a 5 MB. | Média | `backend/src/main/java/com/tcc/talkie/service/StorageService.java`; `backend/src/main/resources/application.properties` |
| RNF16 | Segurança | O sistema deve armazenar as imagens com nomes gerados internamente, sem usar o nome informado pelo usuário. | Média | `backend/src/main/java/com/tcc/talkie/service/StorageService.java` |
| RNF17 ⚠️ P24 | Usabilidade | O sistema deve apresentar toda a interface em português. | Média | **PENDENTE P24.** A corrigir em: `frontend/src/pages/AuthPage/LoginPage.tsx`; `frontend/src/pages/AuthPage/RegisterPage.tsx` |
| RNF18 | Usabilidade | O sistema deve exibir um indicador visual durante o carregamento de dados. | Baixa | `frontend/src/components/occurrences/OccurrencesTable.tsx`; `frontend/src/components/ui/Button.tsx` |
| RNF19 | Usabilidade | O sistema deve informar ao usuário o motivo das falhas em suas operações. | Média | `frontend/src/components/ui/Alert.tsx`; `frontend/src/utils/errorHandler.ts` |
| RNF20 | Usabilidade | O sistema deve indicar, junto a cada campo do formulário, os erros de preenchimento. | Média | `frontend/src/schemas/createOccurrenceSchema.ts`; `frontend/src/schemas/registerSchema.ts` |
| RNF21 ⚠️ P25 | Usabilidade | O sistema deve informar ao usuário quando uma imagem for recusada ou não puder ser enviada. | Baixa | **PENDENTE P25.** A corrigir em: `frontend/src/components/occurrences/ImageUpload.tsx`; `frontend/src/pages/OccurrencesPage/CreateOccurrencePage.tsx` |
| RNF22 | Usabilidade | O sistema deve solicitar confirmação antes de ações críticas, como excluir a conta ou desativar categorias. | Média | `frontend/src/components/ui/ConfirmDialog.tsx`; `frontend/src/pages/ProfilePage.tsx` |
| RNF23 | Usabilidade | O sistema deve permitir que o usuário exiba ou oculte a senha digitada. | Baixa | `frontend/src/components/ui/PasswordInput.tsx` |
| RNF24 | Usabilidade | O sistema deve formatar automaticamente o CPF durante a digitação. | Baixa | `frontend/src/utils/cpf.ts`; `frontend/src/pages/AuthPage/RegisterPage.tsx` |
| RNF25 ⚠️ P11 | Usabilidade | O sistema deve adaptar suas telas a computadores, tablets e celulares. | Média | **PENDENTE P11.** A corrigir em: `frontend/src/components/layout/AppLayout.tsx`; `frontend/src/components/layout/Sidebar.tsx`; `frontend/src/pages/OccurrencesPage/CreateOccurrencePage.tsx` |
| RNF26 ⚠️ P12 | Usabilidade | O sistema deve oferecer recursos de acessibilidade para navegação por teclado e leitores de tela. | Média | **PENDENTE P12.** A corrigir em: `frontend/src/components/ui/Input.tsx`; `frontend/src/components/notifications/SubscribeModal.tsx` |
| RNF27 | Confiabilidade | O sistema deve tratar os erros de forma centralizada, retornando respostas em formato padronizado. | Média | `backend/src/main/java/com/tcc/talkie/infra/exceptions/GlobalExceptionHandler.java` |
| RNF28 | Confiabilidade | O sistema deve reverter integralmente a exclusão de uma conta caso alguma etapa falhe. | Média | `backend/src/main/java/com/tcc/talkie/service/UserService.java` |
| RNF29 | Confiabilidade | O sistema deve preservar os dados históricos ao desativar categorias e subcategorias, utilizando exclusão lógica. | Média | `backend/src/main/resources/db/migration/V10__category_subcategory_active.sql`; `backend/src/main/java/com/tcc/talkie/service/CategoryService.java` |
| RNF30 | Confiabilidade | O sistema deve registrar em log os eventos relevantes de envio de notificações e de exclusão de contas. | Baixa | `backend/src/main/java/com/tcc/talkie/service/EmailService.java`; `backend/src/main/java/com/tcc/talkie/service/UserService.java` |
| RNF31 ⚠️ P10 | Confiabilidade | O sistema deve concluir o registro da ocorrência mesmo que o envio de e-mail falhe. | Alta | **PENDENTE P10.** A corrigir em: `backend/src/main/java/com/tcc/talkie/service/EmailService.java`; `backend/src/main/java/com/tcc/talkie/listeners/OccurrenceNotificationListener.java` |
| RNF32 ⚠️ P14 | Confiabilidade | O sistema deve preservar as imagens enviadas quando o contêiner da aplicação for recriado. | Média | **PENDENTE P14.** A corrigir em: `backend/docker-compose.yml` |
| RNF33 ⚠️ P17 | Confiabilidade | O sistema deve dispor de rotina de cópia de segurança do banco de dados e das imagens. | Média | **PENDENTE P17.** A corrigir em: `backend/docker-compose.yml` |
| RNF34 | Desempenho | O sistema deve utilizar índices no banco de dados para as consultas de ocorrências por autor e de histórico por ocorrência. | Baixa | `backend/src/main/resources/db/migration/V9__account_deletion_and_status_history.sql` |
| RNF35 | Desempenho | O sistema deve reaproveitar no navegador os endereços já consultados, evitando chamadas repetidas ao serviço externo. | Baixa | `frontend/src/services/geocodingService.ts` |
| RNF36 | Portabilidade | O sistema deve ser acessado por meio de navegador web. | Alta | `frontend/package.json`; `frontend/src/App.tsx` |
| RNF37 | Portabilidade | O sistema deve permitir a execução do servidor e de seus serviços de apoio em contêineres. | Média | `backend/Dockerfile`; `backend/docker-compose.yml` |
| RNF38 ⚠️ P13 | Portabilidade | O sistema deve permitir a execução da interface web em contêiner. | Média | **PENDENTE P13.** A corrigir em: `frontend/ (sem Dockerfile)` |
| RNF39 | Portabilidade | O sistema deve permitir configurar banco de dados, servidor de e-mail e diretório de imagens por variáveis de ambiente. | Média | `backend/src/main/resources/application.properties` |
| RNF40 ⚠️ P16 | Portabilidade | O sistema deve permitir configurar a origem autorizada da interface web sem alteração de código. | Média | **PENDENTE P16.** A corrigir em: `backend/src/main/java/com/tcc/talkie/infra/security/SecurityConfig.java` |
| RNF41 | Manutenibilidade | O sistema deve ser organizado em camadas de apresentação, negócio e persistência. | Média | `backend/src/main/java/com/tcc/talkie/controller/`; `backend/src/main/java/com/tcc/talkie/service/`; `backend/src/main/java/com/tcc/talkie/repository/` |
| RNF42 | Manutenibilidade | O sistema deve versionar as alterações da estrutura do banco de dados por meio de migrações. | Média | `backend/src/main/resources/db/migration/`; `backend/src/main/resources/application.properties` |
| RNF43 | Manutenibilidade | O sistema deve disponibilizar documentação interativa da API. | Baixa | `backend/src/main/java/com/tcc/talkie/config/OpenApiConfig.java`; `backend/pom.xml` |
| RNF44 | Manutenibilidade | O sistema deve adotar um formato padronizado para as respostas da API. | Baixa | `backend/src/main/java/com/tcc/talkie/dto/response/ApiResponse.java`; `frontend/src/types/api.ts` |
| RNF45 ⚠️ P23 | Manutenibilidade | O sistema deve possuir testes automatizados de integração e de comportamento para o servidor. | Média | **PENDENTE P23.** A corrigir em: `backend/src/test/`; `backend/pom.xml` |
| RNF46 | Tecnologia | O servidor deve ser desenvolvido em Java 17 com o framework Spring Boot. | Média | `backend/pom.xml` |
| RNF47 | Tecnologia | A interface web deve ser desenvolvida em TypeScript com a biblioteca React. | Média | `frontend/package.json` |
| RNF48 | Tecnologia | O sistema deve utilizar o banco de dados PostgreSQL. | Média | `backend/docker-compose.yml`; `backend/pom.xml` |
| RNF49 | Tecnologia | O sistema deve utilizar dados do OpenStreetMap para mapas e sugestão de endereços. | Baixa | `frontend/src/components/occurrences/LocationPicker.tsx`; `frontend/src/services/geocodingService.ts` |

**Justificativas de prioridade**

- **Segurança (Alta):** os itens assim marcados protegem dados pessoais e impedem acesso indevido a funções administrativas.
- **Concluir o registro mesmo com falha de e-mail (Alta):** a função principal não pode depender de um serviço acessório.
- **Tecnologia (Média):** são restrições do projeto; outra pilha poderia entregar as mesmas funcionalidades.
- **Desempenho (Baixa):** são otimizações pontuais, sem meta definida.

## 4. Parcialmente implementado / não confirmado

Esta seção é um controle de trabalho e não deve ir para o TCC.

### 4.1 Pendências tratadas como resolvidas

Comece por P18 e pelas demais pendências de segurança: P19, P20, P21 e P22.

| Código | Requisitos | Situação atual | O que fazer | Onde |
|---|---|---|---|---|
| P01 | RF37 | A API exclui a ocorrência, mas não há tela nem função no front-end. | Criar a ação de excluir, com confirmação, na página de detalhes. | `frontend/src/pages/OccurrencesPage/OccurrenceDetailsPage.tsx`; `frontend/src/services/occurrenceService.ts` |
| P02 | RF68 | A API edita usuários, mas não há tela. | Criar o formulário de edição. | `frontend/src/pages/SettingsPage/UsersListPage.tsx`; `frontend/src/services/userService.ts` |
| P03 | RF67 | A API consulta um usuário por ID, mas não há tela. | Criar a visualização de um usuário. | `frontend/src/pages/SettingsPage/UsersListPage.tsx`; `frontend/src/services/userService.ts` |
| P04 | RF69 | A API exclui usuários, mas a tela só oferece a exclusão da própria conta. | Criar a ação de excluir, com confirmação, na lista de usuários. | `frontend/src/pages/SettingsPage/UsersListPage.tsx`; `frontend/src/services/userService.ts` |
| P05 | RF22 | A API já permite o acesso do autor, mas a rota `/ocorrencias/:id` é exclusiva de administrador e a linha da tabela só é clicável para ele. | Liberar a rota e o clique, e ocultar do usuário comum o painel de tratamento e o histórico (que a API restringe a administradores). | `frontend/src/App.tsx`; `frontend/src/pages/HomePage.tsx`; `frontend/src/pages/OccurrencesPage/OccurrenceDetailsPage.tsx` |
| P06 | RF30 | `EM_ANALISE` existe só no front-end; o back-end aceita apenas `ABERTO`, `RESOLVIDO` e `FECHADO`. | Incluir o valor no enum e a ação no painel de status. | `backend/src/main/java/com/tcc/talkie/domain/occurrence/OccurrenceStatus.java`; `frontend/src/components/occurrences/StatusActionPanel.tsx` |
| P07 | RNF13 | `spring-boot-starter-validation` não está no `pom.xml`, então as anotações dos DTOs não rodam. CPF válido, formato de e-mail, tamanho da senha e data não futura são verificados só no formulário. | Adicionar a dependência, tratar `MethodArgumentNotValidException` e validar o CPF no servidor. | `backend/pom.xml`; `backend/src/main/java/com/tcc/talkie/infra/exceptions/GlobalExceptionHandler.java`; `backend/src/main/java/com/tcc/talkie/service/AuthService.java` |
| P08 | RF61 | Os links apontam para `http://localhost:3000/occurrences` e `/settings/subscriptions`, porta e rotas que não existem. | Usar a URL do front-end por configuração e as rotas reais. | `backend/src/main/java/com/tcc/talkie/service/EmailService.java (linhas 205 e 210)` |
| P09 | RF62 | Só há e-mail de nova ocorrência. **Decisão assumida:** o destinatário é o autor; altere o texto do requisito se preferir outro. | Publicar um evento na mudança de status e enviar o e-mail, tratando autor nulo (conta excluída). | `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java`; `backend/src/main/java/com/tcc/talkie/service/EmailService.java`; `backend/src/main/java/com/tcc/talkie/events/` |
| P10 | RNF31 | O envio ocorre dentro da transação e só `MessagingException` é tratada. Uma falha em tempo de execução desfaz o registro; o teste `shouldHandleEmailSendingException` falha por isso. | Tratar também `MailException`, de preferência enviando após a confirmação da transação. | `backend/src/main/java/com/tcc/talkie/service/EmailService.java`; `backend/src/main/java/com/tcc/talkie/listeners/OccurrenceNotificationListener.java` |
| P11 | RNF25 | A barra lateral é fixa em 256 px, sem menu recolhível, e o formulário de ocorrência tem sempre 2 colunas. | Recolher a barra lateral em telas pequenas e empilhar os campos. | `frontend/src/components/layout/AppLayout.tsx`; `frontend/src/components/layout/Sidebar.tsx`; `frontend/src/pages/OccurrencesPage/CreateOccurrencePage.tsx` |
| P12 | RNF26 | Há atributos pontuais em alguns diálogos e botões. Os rótulos dos campos não estão associados a eles, as linhas clicáveis não funcionam por teclado e alguns botões só têm ícone. | Associar rótulos, tornar as ações acessíveis por teclado e nomear os botões de ícone. Só cite um nível WCAG se fizer a avaliação. | `frontend/src/components/ui/Input.tsx`; `frontend/src/components/occurrences/OccurrencesTable.tsx`; `frontend/src/components/notifications/` |
| P13 | RNF38 | O front-end não tem Dockerfile nem serviço no `docker-compose.yml`. | Criar o Dockerfile e o serviço. | `frontend/`; `backend/docker-compose.yml` |
| P14 | RNF32 | As imagens ficam no sistema de arquivos do contêiner, sem volume. | Montar um volume em `app.upload-dir`. | `backend/docker-compose.yml` |
| P15 | RNF12 | Não há HTTPS configurado. A localização do dispositivo também depende dele fora de `localhost`. | Configurar HTTPS no ambiente de implantação. | `backend/docker-compose.yml` |
| P16 | RNF40 | A origem está fixa em `http://localhost:5173`. | Ler a origem de uma variável de ambiente. | `backend/src/main/java/com/tcc/talkie/infra/security/SecurityConfig.java` |
| P17 | RNF33 | Não encontrei rotina de cópia de segurança. | Criar e documentar a rotina para o banco e para as imagens. | `backend/docker-compose.yml`; `backend/docs/` |
| P18 | RNF07 | **Crítico.** `POST /auth/register` é público e aceita o campo `role`, então qualquer pessoa pode criar uma conta de administrador pela API. | Remover `role` do DTO e fixar `USER`; ajustar os testes que passam esse campo. | `backend/src/main/java/com/tcc/talkie/dto/request/RegisterDTO.java`; `backend/src/main/java/com/tcc/talkie/service/AuthService.java (linha 42)`; `backend/src/test/java/com/tcc/talkie/controller/AuthControllerIT.java` |
| P19 | RNF08 | Qualquer usuário autenticado lê as ocorrências de qualquer categoria, com o nome do autor. **Decisão assumida:** restringir às categorias assinadas; avalie também se o nome do autor deve continuar visível. | Verificar a assinatura no serviço, liberando administradores. | `backend/src/main/java/com/tcc/talkie/service/OccurrenceService.java`; `backend/src/main/java/com/tcc/talkie/controller/OccurrenceController.java` |
| P20 | RNF09 | `api.security.token.secret=my-secret` e `security.crypto.secret` estão versionados. | Ler de variáveis de ambiente, sem valor padrão em produção. | `backend/src/main/resources/application.properties` |
| P21 | RNF10 | A migração V7 cria `admin@talkie.local` com a senha `123456`. | Não editar a V7 (já aplicada): criar uma nova migração ou definir a senha na implantação. | `backend/src/main/resources/db/migration/` |
| P22 | RNF11 | E-mail inexistente responde 404 e senha incorreta responde 401, o que revela quais e-mails estão cadastrados. | Responder 401 com a mesma mensagem nos dois casos. | `backend/src/main/java/com/tcc/talkie/controller/AuthController.java (linhas 45 e 46)` |
| P23 | RNF45 | `mvnw test` não compila por causa de `NotificationSteps.java` e não executa os `*IT`. Em uma cópia sem esse arquivo, 76 de 117 `*IT` passaram: 20 erros vêm do Mockito no JDK 26, 20 testes estão desatualizados ou com dados incompletos, e 1 aponta o problema real de P10. `contextLoads` falha por falta de servidor de e-mail na configuração de teste, e o Cucumber passou 5 de 12 cenários (6 erros vêm da remoção do arquivo). | Corrigir o arquivo, incluir os `*IT` na execução, atualizar os testes e rodar com JDK 17. | `backend/src/test/`; `backend/pom.xml` |
| P24 | RNF17 | As telas de login e cadastro têm textos em inglês, como "Sign in", "Sign up", "Password" e "Email or phone number". | Traduzir os textos. | `frontend/src/pages/AuthPage/LoginPage.tsx`; `frontend/src/pages/AuthPage/RegisterPage.tsx` |
| P25 | RNF21 | Arquivo inválido é recusado sem aviso. Se o envio falhar, a mensagem some porque a página navega logo em seguida. | Exibir o erro no componente e avisar o usuário após a navegação. | `frontend/src/components/occurrences/ImageUpload.tsx`; `frontend/src/pages/OccurrencesPage/CreateOccurrencePage.tsx` |

### 4.2 Não confirmado

- **Interface no navegador:** analisei as telas pelo código, sem abri-las em um navegador. Comportamentos visuais e de geolocalização não foram testados em execução.
- **Pendências de segurança P18, P19 e P22:** identificadas pela leitura do código, sem execução de um ataque ou requisição real.
- **Pendência P10:** o comportamento foi deduzido do código e do teste com serviço simulado, sem derrubar um servidor de e-mail real.
- **Testes afetados pelo JDK 26:** os 20 erros do Mockito precisam ser confirmados com JDK 17, que não está instalado nesta máquina. `AccountDeletionIT` passou 15 de 15 no relatório de 16/10/2026.
- **Contêineres:** li `Dockerfile` e `docker-compose.yml`, mas não subi os contêineres.
- **Texto "Clique ou arraste uma imagem":** não incluí arrastar como requisito, porque o código não trata esse evento e não testei o comportamento.
- **Desempenho:** não há medição, paginação nem cache no servidor; por isso nenhum requisito cita tempo de resposta ou capacidade.

## 5. Tabelas em LaTeX

Requer `\usepackage{longtable}` no preâmbulo. As larguras somam 16 cm, a área útil das margens ABNT em A4.

```latex
\begin{longtable}{|p{1.3cm}|p{12.4cm}|p{1.8cm}|}
\caption{Requisitos funcionais do sistema Talkie}\label{tab:requisitos-funcionais}\\
\hline
\textbf{ID} & \textbf{Descrição} & \textbf{Prioridade} \\
\hline
\endfirsthead
\caption[]{Requisitos funcionais do sistema Talkie (continuação)}\\
\hline
\textbf{ID} & \textbf{Descrição} & \textbf{Prioridade} \\
\hline
\endhead
\hline
\endfoot
\multicolumn{3}{|l|}{\textbf{Cadastro e acesso}} \\ \hline
RF01 & O sistema deve permitir que um visitante crie uma conta informando nome, CPF, e-mail e senha. & Alta \\ \hline
RF02 & O sistema deve impedir que um visitante crie uma conta com CPF já cadastrado. & Alta \\ \hline
RF03 & O sistema deve impedir que um visitante crie uma conta com e-mail já cadastrado. & Alta \\ \hline
RF04 & O sistema deve permitir que um usuário cadastrado acesse a plataforma com e-mail e senha. & Alta \\ \hline
RF05 & O sistema deve permitir que um usuário autenticado encerre sua sessão. & Média \\ \hline
\multicolumn{3}{|l|}{\textbf{Conta do usuário}} \\ \hline
RF06 & O sistema deve permitir que um usuário visualize o e-mail e o perfil de sua conta. & Baixa \\ \hline
RF07 & O sistema deve permitir que um usuário exclua a própria conta mediante confirmação da senha. & Média \\ \hline
RF08 & O sistema deve manter as ocorrências de uma conta excluída, sem identificar o autor. & Média \\ \hline
RF09 & O sistema deve impedir a exclusão do único administrador. & Média \\ \hline
\multicolumn{3}{|l|}{\textbf{Registro de ocorrências}} \\ \hline
RF10 & O sistema deve permitir que um usuário registre uma ocorrência com título, descrição, localização, categoria e subcategoria. & Alta \\ \hline
RF11 & O sistema deve permitir que um usuário informe a data e o horário do fato ocorrido. & Média \\ \hline
RF12 & O sistema deve permitir que um usuário marque o local da ocorrência em um mapa. & Média \\ \hline
RF13 & O sistema deve permitir que um usuário utilize a localização do dispositivo para marcar o local da ocorrência. & Baixa \\ \hline
RF14 & O sistema deve permitir que um usuário ajuste o ponto marcado arrastando-o no mapa. & Baixa \\ \hline
RF15 & O sistema deve permitir que um usuário remova o ponto marcado no mapa. & Baixa \\ \hline
RF16 & O sistema deve sugerir ao usuário um endereço a partir do ponto marcado no mapa. & Baixa \\ \hline
RF17 & O sistema deve permitir que um usuário anexe uma foto à ocorrência. & Média \\ \hline
RF18 & O sistema deve impedir o registro de ocorrências em categorias ou subcategorias inativas. & Média \\ \hline
RF19 & O sistema deve impedir o registro de ocorrência com subcategoria que não pertença à categoria informada. & Média \\ \hline
\multicolumn{3}{|l|}{\textbf{Consulta e acompanhamento}} \\ \hline
RF20 & O sistema deve permitir que um usuário visualize a lista das ocorrências que registrou. & Alta \\ \hline
RF21 & O sistema deve permitir que um usuário acompanhe o status de suas ocorrências. & Alta \\ \hline
RF22 & O sistema deve permitir que um usuário visualize os detalhes e a foto de uma ocorrência que registrou. & Média \\ \hline % PENDENTE P05
RF23 & O sistema deve permitir que um administrador visualize todas as ocorrências registradas. & Alta \\ \hline
RF24 & O sistema deve permitir que um usuário visualize os totais de ocorrências por situação. & Baixa \\ \hline
RF25 & O sistema deve permitir que um usuário pesquise ocorrências por título ou categoria. & Média \\ \hline
\multicolumn{3}{|l|}{\textbf{Tratamento pelo administrador}} \\ \hline
RF26 & O sistema deve permitir que um administrador visualize os detalhes de qualquer ocorrência. & Alta \\ \hline
RF27 & O sistema deve permitir que um administrador visualize a foto anexada à ocorrência. & Média \\ \hline
RF28 & O sistema deve permitir que um administrador visualize no mapa o local da ocorrência. & Média \\ \hline
RF29 & O sistema deve permitir que um administrador abra o local da ocorrência em um serviço externo de mapas. & Baixa \\ \hline
RF30 & O sistema deve permitir que um administrador marque uma ocorrência como em análise. & Média \\ \hline % PENDENTE P06
RF31 & O sistema deve permitir que um administrador marque uma ocorrência como resolvida. & Alta \\ \hline
RF32 & O sistema deve permitir que um administrador encerre uma ocorrência sem resolução. & Alta \\ \hline
RF33 & O sistema deve permitir que um administrador reabra uma ocorrência resolvida ou encerrada. & Média \\ \hline
RF34 & O sistema deve permitir que um administrador informe o motivo de uma mudança de status. & Média \\ \hline
RF35 & O sistema deve registrar a data em que uma ocorrência foi resolvida. & Baixa \\ \hline
RF36 & O sistema deve permitir que um administrador consulte o histórico de status de uma ocorrência, com data e autor de cada mudança. & Média \\ \hline
RF37 & O sistema deve permitir que um administrador exclua uma ocorrência. & Média \\ \hline % PENDENTE P01
\multicolumn{3}{|l|}{\textbf{Categorias}} \\ \hline
RF38 & O sistema deve permitir que um administrador crie uma categoria. & Alta \\ \hline
RF39 & O sistema deve permitir que um administrador edite uma categoria. & Média \\ \hline
RF40 & O sistema deve permitir que um administrador defina a prioridade de uma categoria. & Média \\ \hline
RF41 & O sistema deve permitir que um administrador defina o ícone de uma categoria. & Baixa \\ \hline
RF42 & O sistema deve permitir que um administrador defina a cor de uma categoria. & Baixa \\ \hline
RF43 & O sistema deve permitir que um administrador desative uma categoria, mantendo as ocorrências já registradas. & Média \\ \hline
RF44 & O sistema deve permitir que um administrador reative uma categoria. & Média \\ \hline
RF45 & O sistema deve permitir que um administrador pesquise categorias por nome. & Baixa \\ \hline
RF46 & O sistema deve permitir que um administrador exiba as categorias e subcategorias inativas. & Baixa \\ \hline
RF47 & O sistema deve impedir a existência de categorias com o mesmo nome. & Média \\ \hline
\multicolumn{3}{|l|}{\textbf{Subcategorias}} \\ \hline
RF48 & O sistema deve permitir que um administrador crie uma subcategoria dentro de uma categoria. & Alta \\ \hline
RF49 & O sistema deve permitir que um administrador edite uma subcategoria. & Média \\ \hline
RF50 & O sistema deve permitir que um administrador visualize as subcategorias de uma categoria. & Baixa \\ \hline
RF51 & O sistema deve permitir que um administrador desative uma subcategoria, mantendo as ocorrências já registradas. & Média \\ \hline
RF52 & O sistema deve permitir que um administrador reative uma subcategoria cuja categoria esteja ativa. & Média \\ \hline
RF53 & O sistema deve impedir a existência de subcategorias com o mesmo nome em uma mesma categoria. & Média \\ \hline
\multicolumn{3}{|l|}{\textbf{Assinaturas e notificações}} \\ \hline
RF54 & O sistema deve permitir que um usuário assine uma categoria de seu interesse. & Média \\ \hline
RF55 & O sistema deve permitir que um usuário cancele a assinatura de uma categoria. & Média \\ \hline
RF56 & O sistema deve permitir que um usuário troque a categoria de uma assinatura. & Baixa \\ \hline
RF57 & O sistema deve permitir que um usuário visualize suas assinaturas. & Média \\ \hline
RF58 & O sistema deve impedir que um usuário assine mais de uma vez a mesma categoria. & Baixa \\ \hline
RF59 & O sistema deve impedir a assinatura de uma categoria inativa. & Baixa \\ \hline
RF60 & O sistema deve enviar um e-mail aos assinantes da categoria quando uma nova ocorrência for registrada. & Média \\ \hline
RF61 & O sistema deve incluir no e-mail de notificação um link de acesso à plataforma. & Baixa \\ \hline % PENDENTE P08
RF62 & O sistema deve enviar um e-mail ao autor da ocorrência quando o status dela for alterado. & Média \\ \hline % PENDENTE P09
RF63 & O sistema deve permitir que um usuário visualize as ocorrências das categorias que assina. & Média \\ \hline
RF64 & O sistema deve permitir que um usuário pesquise as ocorrências das categorias que assina. & Baixa \\ \hline
\multicolumn{3}{|l|}{\textbf{Gestão de usuários}} \\ \hline
RF65 & O sistema deve permitir que um administrador visualize a lista de usuários. & Média \\ \hline
RF66 & O sistema deve permitir que um administrador pesquise usuários por nome ou e-mail. & Baixa \\ \hline
RF67 & O sistema deve permitir que um administrador consulte os dados de um usuário específico. & Baixa \\ \hline % PENDENTE P03
RF68 & O sistema deve permitir que um administrador edite o nome e o e-mail de um usuário. & Média \\ \hline % PENDENTE P02
RF69 & O sistema deve permitir que um administrador exclua a conta de um usuário. & Média \\ \hline % PENDENTE P04
\end{longtable}

\begin{longtable}{|p{1.4cm}|p{3cm}|p{9.2cm}|p{1.8cm}|}
\caption{Requisitos não funcionais do sistema Talkie}\label{tab:requisitos-nao-funcionais}\\
\hline
\textbf{ID} & \textbf{Categoria} & \textbf{Descrição} & \textbf{Prioridade} \\
\hline
\endfirsthead
\caption[]{Requisitos não funcionais do sistema Talkie (continuação)}\\
\hline
\textbf{ID} & \textbf{Categoria} & \textbf{Descrição} & \textbf{Prioridade} \\
\hline
\endhead
\hline
\endfoot
RNF01 & Segurança & O sistema deve armazenar as senhas de forma irreversível, por meio de função de hash. & Alta \\ \hline
RNF02 & Segurança & O sistema deve exigir autenticação em todas as funcionalidades, exceto cadastro, login e documentação da API. & Alta \\ \hline
RNF03 & Segurança & O sistema deve encerrar a validade de cada sessão após 2 horas. & Média \\ \hline
RNF04 & Segurança & O sistema deve restringir as funcionalidades administrativas ao perfil de administrador. & Alta \\ \hline
RNF05 & Segurança & O sistema deve restringir o acesso ao detalhe e à foto de uma ocorrência ao seu autor e aos administradores. & Alta \\ \hline
RNF06 & Segurança & O sistema deve invalidar as sessões de uma conta excluída. & Média \\ \hline
RNF07 & Segurança & O sistema deve impedir que o cadastro público crie contas de administrador. & Alta \\ \hline % PENDENTE P18
RNF08 & Segurança & O sistema deve restringir a consulta de ocorrências por categoria às categorias assinadas pelo usuário. & Alta \\ \hline % PENDENTE P19
RNF09 & Segurança & O sistema deve manter as chaves de segurança fora do código-fonte. & Alta \\ \hline % PENDENTE P20
RNF10 & Segurança & O sistema não deve manter credenciais administrativas padrão em ambiente de produção. & Alta \\ \hline % PENDENTE P21
RNF11 & Segurança & O sistema deve responder da mesma forma a uma tentativa de login com e-mail inexistente ou senha incorreta. & Média \\ \hline % PENDENTE P22
RNF12 & Segurança & O sistema deve utilizar comunicação criptografada por HTTPS em produção. & Alta \\ \hline % PENDENTE P15
RNF13 & Segurança & O sistema deve validar no servidor os dados recebidos nas requisições. & Alta \\ \hline % PENDENTE P07
RNF14 & Segurança & O sistema deve verificar o tipo real das imagens enviadas, aceitando apenas JPEG, PNG e WebP. & Média \\ \hline
RNF15 & Segurança & O sistema deve limitar o tamanho das imagens enviadas a 5 MB. & Média \\ \hline
RNF16 & Segurança & O sistema deve armazenar as imagens com nomes gerados internamente, sem usar o nome informado pelo usuário. & Média \\ \hline
RNF17 & Usabilidade & O sistema deve apresentar toda a interface em português. & Média \\ \hline % PENDENTE P24
RNF18 & Usabilidade & O sistema deve exibir um indicador visual durante o carregamento de dados. & Baixa \\ \hline
RNF19 & Usabilidade & O sistema deve informar ao usuário o motivo das falhas em suas operações. & Média \\ \hline
RNF20 & Usabilidade & O sistema deve indicar, junto a cada campo do formulário, os erros de preenchimento. & Média \\ \hline
RNF21 & Usabilidade & O sistema deve informar ao usuário quando uma imagem for recusada ou não puder ser enviada. & Baixa \\ \hline % PENDENTE P25
RNF22 & Usabilidade & O sistema deve solicitar confirmação antes de ações críticas, como excluir a conta ou desativar categorias. & Média \\ \hline
RNF23 & Usabilidade & O sistema deve permitir que o usuário exiba ou oculte a senha digitada. & Baixa \\ \hline
RNF24 & Usabilidade & O sistema deve formatar automaticamente o CPF durante a digitação. & Baixa \\ \hline
RNF25 & Usabilidade & O sistema deve adaptar suas telas a computadores, tablets e celulares. & Média \\ \hline % PENDENTE P11
RNF26 & Usabilidade & O sistema deve oferecer recursos de acessibilidade para navegação por teclado e leitores de tela. & Média \\ \hline % PENDENTE P12
RNF27 & Confiabilidade & O sistema deve tratar os erros de forma centralizada, retornando respostas em formato padronizado. & Média \\ \hline
RNF28 & Confiabilidade & O sistema deve reverter integralmente a exclusão de uma conta caso alguma etapa falhe. & Média \\ \hline
RNF29 & Confiabilidade & O sistema deve preservar os dados históricos ao desativar categorias e subcategorias, utilizando exclusão lógica. & Média \\ \hline
RNF30 & Confiabilidade & O sistema deve registrar em log os eventos relevantes de envio de notificações e de exclusão de contas. & Baixa \\ \hline
RNF31 & Confiabilidade & O sistema deve concluir o registro da ocorrência mesmo que o envio de e-mail falhe. & Alta \\ \hline % PENDENTE P10
RNF32 & Confiabilidade & O sistema deve preservar as imagens enviadas quando o contêiner da aplicação for recriado. & Média \\ \hline % PENDENTE P14
RNF33 & Confiabilidade & O sistema deve dispor de rotina de cópia de segurança do banco de dados e das imagens. & Média \\ \hline % PENDENTE P17
RNF34 & Desempenho & O sistema deve utilizar índices no banco de dados para as consultas de ocorrências por autor e de histórico por ocorrência. & Baixa \\ \hline
RNF35 & Desempenho & O sistema deve reaproveitar no navegador os endereços já consultados, evitando chamadas repetidas ao serviço externo. & Baixa \\ \hline
RNF36 & Portabilidade & O sistema deve ser acessado por meio de navegador web. & Alta \\ \hline
RNF37 & Portabilidade & O sistema deve permitir a execução do servidor e de seus serviços de apoio em contêineres. & Média \\ \hline
RNF38 & Portabilidade & O sistema deve permitir a execução da interface web em contêiner. & Média \\ \hline % PENDENTE P13
RNF39 & Portabilidade & O sistema deve permitir configurar banco de dados, servidor de e-mail e diretório de imagens por variáveis de ambiente. & Média \\ \hline
RNF40 & Portabilidade & O sistema deve permitir configurar a origem autorizada da interface web sem alteração de código. & Média \\ \hline % PENDENTE P16
RNF41 & Manutenibilidade & O sistema deve ser organizado em camadas de apresentação, negócio e persistência. & Média \\ \hline
RNF42 & Manutenibilidade & O sistema deve versionar as alterações da estrutura do banco de dados por meio de migrações. & Média \\ \hline
RNF43 & Manutenibilidade & O sistema deve disponibilizar documentação interativa da API. & Baixa \\ \hline
RNF44 & Manutenibilidade & O sistema deve adotar um formato padronizado para as respostas da API. & Baixa \\ \hline
RNF45 & Manutenibilidade & O sistema deve possuir testes automatizados de integração e de comportamento para o servidor. & Média \\ \hline % PENDENTE P23
RNF46 & Tecnologia & O servidor deve ser desenvolvido em Java 17 com o framework Spring Boot. & Média \\ \hline
RNF47 & Tecnologia & A interface web deve ser desenvolvida em TypeScript com a biblioteca React. & Média \\ \hline
RNF48 & Tecnologia & O sistema deve utilizar o banco de dados PostgreSQL. & Média \\ \hline
RNF49 & Tecnologia & O sistema deve utilizar dados do OpenStreetMap para mapas e sugestão de endereços. & Baixa \\ \hline
\end{longtable}
```
