# Usando .env para variáveis locais (desenvolvimento)

Este projeto suporta carregar variáveis de ambiente a partir de um arquivo `.env` local para facilitar o desenvolvimento.

Passos:

1. Copie o arquivo de exemplo:

   cp .env.example .env

   No Windows PowerShell:

   Copy-Item .env.example .env

2. Abra `.env` e insira suas credenciais (por exemplo Mailtrap ou Gmail App Password). Nunca comite `.env`.

3. Carregue as variáveis na sessão atual do PowerShell:

   .\load-env.ps1

   Observação: execute com `.` (dot-sourcing) se quiser que as variáveis persistam no mesmo contexto: `. .\load-env.ps1`.

4. Rode a aplicação normalmente (variáveis estarão disponíveis via `System.getenv` do Java/Spring):

   mvn spring-boot:run

5. Teste o envio de e-mail com o endpoint de teste:

   Invoke-RestMethod -Method Get -Uri 'http://localhost:8081/email/test?to=seu-email@exemplo.com'

Dicas:
- Em vez de `mvn spring-boot:run`, você pode usar `java -jar target/mdvBackend-0.0.1-SNAPSHOT.jar` após o build.
- Para depuração SMTP, habilite no `application.properties`:
  - `spring.mail.properties.mail.debug=true`
  - `logging.level.org.springframework.mail=DEBUG`
  - `logging.level.com.sun.mail=DEBUG`

Segurança:
- `.env` está listado em `.gitignore` para evitar commits acidentais.
- Para produção, use um secrets manager ou variáveis de ambiente no servidor/CI.

