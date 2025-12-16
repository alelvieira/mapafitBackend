# mdvBackend
Backend do projeto Mapafit — API REST construída em Java com Spring Boot para gerenciar usuários, rotas e dados de treino.

## Tecnologias
- Java 17
- Spring Boot
- Maven
- SQL (ex.: PostgreSQL)
- Flyway (migrações)
- JUnit / Mockito (testes)

## Funcionalidades
- Autenticação e autorização (JWT)
- CRUD de usuários
- CRUD de rotas / treinos
- Armazenamento de posições GPS
- Endpoints REST documentados

## Pré-requisitos
- JDK 17+
- Maven 3.6+
- PostgreSQL (ou outro SGBD compatível)
- Opcional: Docker e Docker Compose

## Instalação e execução local
1. Clonar repositório:
   ```bash
   git clone <repo-url>
   cd <repo-folder>
2. Configurar variáveis de ambiente ou application.properties / application.yml (ver seção abaixo).
3. Build e execução:
mvn clean package
mvn spring-boot:run
 ou
java -jar target/*.jar

## Configuração (exemplo)
Arquivo: src/main/resources/application.yml ou application.properties
Exemplo minimal (application.yml):
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mapafit
    username: mapafit_user
    password: senha_segura
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

jwt:
  secret: sua_chave_secreta
  expiration-ms: 3600000

Variáveis de ambiente suportadas:
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET

## Banco de dados e migrações
Utiliza Flyway para migrações em src/main/resources/db/migration.
Criar database e usuário conforme configuração antes de rodar a aplicação:
CREATE DATABASE mapafit;
CREATE USER mapafit_user WITH PASSWORD 'senha_segura';
GRANT ALL PRIVILEGES ON DATABASE mapafit TO mapafit_user;


## Executar testes
mvn test
Exemplos de endpoints
POST /api/auth/login — autenticação (recebe credenciais, retorna token JWT)
POST /api/auth/register — cadastro de usuário
GET /api/users — listar usuários (autenticado)
GET /api/routes — listar rotas do usuário
POST /api/routes — criar nova rota
(Adaptar os endpoints conforme implementação real no controller)
## Docker (opcional)
Exemplo mínimo de docker-compose.yml:
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: mapafit
      POSTGRES_USER: mapafit_user
      POSTGRES_PASSWORD: senha_segura
    ports:
      - "5432:5432"
  app:
    build: .
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/mapafit
      SPRING_DATASOURCE_USERNAME: mapafit_user
      SPRING_DATASOURCE_PASSWORD: senha_segura
    depends_on:
      - db
    ports:
      - "8080:8080"
