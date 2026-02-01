# TravelApp API

API REST desenvolvida em **Spring Boot** com foco em **boas práticas de arquitetura** e **segurança com JWT**.  
Este projeto serve como base para um sistema de viagens, podendo evoluir para reservas, itinerários e gestão de usuários.

👨‍💻 Autor

Paulo Henrique dos Anjos

Projeto desenvolvido para estudo, prática profissional e portfólio.

---

## 🎯 Objetivo do Projeto

- Implementar uma API REST moderna e segura
- Utilizar autenticação e autorização com **JWT**
- Aplicar separação de responsabilidades (Controller, Service, Repository)
- Servir como **projeto de estudo avançado e portfólio profissional**

---

## 🚀 Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot**
- **Spring Security**
- **JWT (JSON Web Token)**
- **Spring Data JPA / Hibernate**
- **Maven**
- **Banco de dados relacional** (H2 / PostgreSQL / MySQL – configurável)

---

## 🔐 Autenticação e Segurança

A aplicação utiliza autenticação baseada em **JWT**.

### Fluxo de autenticação:

1. O usuário realiza login via endpoint:


**POST /auth/login**
2. A API valida as credenciais
3. Um token JWT é gerado
4. O token deve ser enviado no header das requisições protegidas:

Authorization: Bearer <token>


📌 Endpoints Principais
Autenticação

POST /auth/login → login e geração de token JWT

Usuários

Endpoints protegidos por autenticação JWT

Utilização de DTOs para entrada e saída de dados

🛠️ Como Executar o Projeto
Pré-requisitos

Java 17+

Maven

Passos:
# Clonar o repositório
git clone https://github.com/SEU_USUARIO/travelapp-spring-jwt.git

# Entrar no diretório
cd travelapp-spring-jwt

# Rodar a aplicação
mvn spring-boot:run

A aplicação estará disponível em: http://localhost:8080

🧪 Status do Projeto

🚧 Em desenvolvimento

Próximos passos planejados:

Implementação de roles (USER / ADMIN)

Documentação com Swagger/OpenAPI

Testes unitários

Deploy em ambiente cloud (AWS)


Authorization: Bearer <token>
5. O token é validado pelo `JwtAuthenticationFilter`

---

## 📦 Estrutura do Projeto

```text
src/main/java/com/travelapp
│
├── controller
│   ├── AuthController
│   ├── UserController
│   └── TestController
│
├── dto
│   ├── request
│   └── response
│
├── entity
│   └── User
│
├── repository
│   └── UserRepository
│
├── security
│   ├── SecurityConfig
│   ├── JwtAuthenticationFilter
│   ├── JwtService
│   └── CustomUserDetailsService
│
├── service
│   └── UserService
│
└── TravelAppApplication
