# TravelApp - Smart Travel Planner

<div align="center">

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[Português](#português) | [English](#english)

</div>

---

<div id="português"></div>

## 🇧🇷 Português

### 🚀 Resumo Executivo
O **TravelApp** é uma solução completa de planejamento de viagens projetada para transformar a maneira como viajantes organizam suas aventuras. Diferente de planilhas complexas ou aplicativos fragmentados, o TravelApp centraliza itinerários, gestão financeira e descoberta de destinos em uma única plataforma intuitiva.

Nossa proposta de valor é oferecer **controle total e tranquilidade**: desde a inspiração inicial com busca de destinos e hotéis integrados, até o controle financeiro em tempo real durante a viagem. Com o TravelApp, o usuário foca na experiência, enquanto o sistema cuida da organização, garantindo que o orçamento seja respeitado e cada dia da viagem seja aproveitado ao máximo.

### ✨ Funcionalidades Principais
*   **Gestão Inteligente de Viagens**: Criação e gerenciamento de múltiplas viagens com datas e orçamentos definidos.
*   **Itinerário Dinâmico**: Planejamento dia a dia com atividades arrastáveis e organizadas por horário.
*   **Controle Financeiro em Tempo Real**:
    *   Registro de despesas por categoria.
    *   Monitoramento da saúde do orçamento (Saudável, Alerta, Crítico, Excedido).
*   **Integrações Poderosas**:
    *   **Amadeus**: Busca de hotéis, cidades e pontos de interesse.
    *   **Unsplash**: Imagens inspiradoras de destinos automaticamente.
    *   **RestCountries**: Dados geográficos precisos.
*   **Rede Social de Viajantes**:
    *   Perfil de usuário personalizável.
    *   Timeline para compartilhar experiências, fotos e dicas.
    *   Interação com curtir e comentar em posts da comunidade.
*   **Segurança Robusta**: Autenticação via JWT (JSON Web Tokens) e proteção de dados sensíveis.

### 🛠 Tecnologias Utilizadas
O projeto foi construído seguindo as melhores práticas de Engenharia de Software e Clean Architecture.

*   **Backend**: Java 17, Spring Boot 3, Spring Security.
*   **Banco de Dados**: PostgreSQL, Hibernate (JPA).
*   **Autenticação**: JWT (JJWT Library).
*   **Integrações Externas**: Amadeus API, Unsplash API.
*   **Ferramentas**: Maven, Lombok, Swagger/OpenAPI (para documentação da API).

### 📸 Screenshots do Aplicativo

| Dashboard de Viagens | Detalhes do Itinerário |
|:---:|:---:|
| ![Dashboard Placeholder](https://via.placeholder.com/400x200?text=Dashboard+Viagens) | ![Itinerario Placeholder](https://via.placeholder.com/400x200?text=Itinerario+Detalhado) |
| *Visão geral das viagens planejadas* | *Organização dia a dia com atividades* |

| Controle Financeiro | Perfil e Social |
|:---:|:---:|
| ![Financeiro Placeholder](https://via.placeholder.com/400x200?text=Controle+Financeiro) | ![Social Placeholder](https://via.placeholder.com/400x200?text=Rede+Social) |
| *Gráficos de orçamento e gastos* | *Timeline e interação com usuários* |

### ⚙️ Instalação e Configuração

#### Pré-requisitos
*   Java JDK 17+
*   Maven 3.8+
*   PostgreSQL instalado e rodando

#### Passo a Passo
1.  **Clone o repositório**
    ```bash
    git clone https://github.com/seu-usuario/travelapp-spring-jwt.git
    cd travelapp-spring-jwt
    ```

2.  **Configure o Banco de Dados**
    Crie um banco de dados chamado `travelapp` no PostgreSQL.
    ```sql
    CREATE DATABASE travelapp;
    ```

3.  **Variáveis de Ambiente**
    Configure as variáveis no arquivo `application.yml` ou via linha de comando:
    *   `DB_HOST`, `DB_USER`, `DB_PASSWORD`
    *   `JWT_SECRET` (Gere uma chave segura)
    *   Chaves de API (Opcional para funcionalidades completas): `AMADEUS_CLIENT_ID`, `AMADEUS_CLIENT_SECRET`, `UNSPLASH_ACCESS_KEY`.

4.  **Execute a Aplicação**
    ```bash
    ./mvnw spring-boot:run
    ```
    O servidor iniciará em `http://localhost:8080`.

### 🤝 Contribuição
Contribuições são muito bem-vindas! Se você deseja melhorar o TravelApp:

1.  Faça um **Fork** do projeto.
2.  Crie uma **Branch** para sua feature (`git checkout -b feature/NovaFeature`).
3.  Faça o **Commit** (`git commit -m 'Add: Nova Feature incrível'`).
4.  Faça o **Push** (`git push origin feature/NovaFeature`).
5.  Abra um **Pull Request**.

---

<div id="english"></div>

## 🇺🇸 English

### 🚀 Executive Summary
**TravelApp** is a comprehensive travel planning solution designed to revolutionize how travelers organize their adventures. Moving away from complex spreadsheets and fragmented apps, TravelApp centralizes itineraries, financial management, and destination discovery into a single, intuitive platform.

Our value proposition is **total control and peace of mind**: from initial inspiration with integrated hotel and destination search, to real-time financial tracking during the trip. With TravelApp, users focus on the experience, while the system handles the organization, ensuring budgets are met and every day of the trip is fully enjoyed.

### ✨ Key Features
*   **Smart Trip Management**: Create and manage multiple trips with defined dates and budgets.
*   **Dynamic Itinerary**: Day-by-day planning with draggable activities organized by time.
*   **Real-Time Financial Control**:
    *   Expense tracking by category.
    *   Budget health monitoring (Healthy, Warning, Critical, Exceeded).
*   **Powerful Integrations**:
    *   **Amadeus**: Search for hotels, cities, and points of interest.
    *   **Unsplash**: Automatic fetching of inspiring destination images.
    *   **RestCountries**: Accurate geographical data.
*   **Traveler Social Network**:
    *   Customizable user profiles.
    *   Timeline to share experiences, photos, and tips.
    *   Interact with likes and comments on community posts.
*   **Robust Security**: Authentication via JWT (JSON Web Tokens) and sensitive data protection.

### 🛠 Tech Stack
The project was built following Software Engineering best practices and Clean Architecture principles.

*   **Backend**: Java 17, Spring Boot 3, Spring Security.
*   **Database**: PostgreSQL, Hibernate (JPA).
*   **Authentication**: JWT (JJWT Library).
*   **External Integrations**: Amadeus API, Unsplash API.
*   **Tools**: Maven, Lombok, Swagger/OpenAPI (for API documentation).

### 📸 App Screenshots

| Trip Dashboard | Itinerary Details |
|:---:|:---:|
| ![Dashboard Placeholder](https://via.placeholder.com/400x200?text=Trip+Dashboard) | ![Itinerary Placeholder](https://via.placeholder.com/400x200?text=Itinerary+Details) |
| *Overview of planned trips* | *Day-by-day activity organization* |

| Financial Control | Profile & Social |
|:---:|:---:|
| ![Financial Placeholder](https://via.placeholder.com/400x200?text=Financial+Control) | ![Social Placeholder](https://via.placeholder.com/400x200?text=Social+Feed) |
| *Budget and expense charts* | *Timeline and user interaction* |

### ⚙️ Installation & Setup

#### Prerequisites
*   Java JDK 17+
*   Maven 3.8+
*   PostgreSQL installed and running

#### Step-by-Step
1.  **Clone the repository**
    ```bash
    git clone https://github.com/your-username/travelapp-spring-jwt.git
    cd travelapp-spring-jwt
    ```

2.  **Configure the Database**
    Create a database named `travelapp` in PostgreSQL.
    ```sql
    CREATE DATABASE travelapp;
    ```

3.  **Environment Variables**
    Configure variables in `application.yml` or via command line:
    *   `DB_HOST`, `DB_USER`, `DB_PASSWORD`
    *   `JWT_SECRET` (Generate a secure key)
    *   API Keys (Optional for full features): `AMADEUS_CLIENT_ID`, `AMADEUS_CLIENT_SECRET`, `UNSPLASH_ACCESS_KEY`.

4.  **Run the Application**
    ```bash
    ./mvnw spring-boot:run
    ```
    The server will start at `http://localhost:8080`.

### 🤝 Contribution
Contributions are welcome! If you want to improve TravelApp:

1.  **Fork** the project.
2.  Create a **Branch** for your feature (`git checkout -b feature/NewFeature`).
3.  **Commit** your changes (`git commit -m 'Add: Amazing New Feature'`).
4.  **Push** to the branch (`git push origin feature/NewFeature`).
5.  Open a **Pull Request**.

---

<div align="center">
  Developed with ❤️ by <strong>Paulo Henrique dos Anjos</strong>
</div>
