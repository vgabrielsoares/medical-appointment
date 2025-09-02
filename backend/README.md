# Backend (Spring Boot)

Principais artefatos

- `pom.xml` — dependências e plugins (Spring Boot, Flyway, PostgreSQL, etc.)
- `src/main/java/com/me/medical/MedicalAppointmentApplication.java` — classe principal Spring Boot
- `src/main/resources/application.yml` — configurações (DB, Flyway, JWT, profiles)
- `src/main/resources/db/migration/` — migrations Flyway (scripts SQL ordenados por versão)

## Estrutura de pastas (visão geral)

- `domain/` — entidades, value objects e regras de domínio puras (sem dependência em Spring)
  - Ex.: `User`, `Doctor`, `Patient`, `Slot`, `Appointment`.
- `application/` (ou `usecase/`) — casos de uso / services que orquestram regras de negócio
  e definem portas (interfaces) usadas pela infra. Aqui ficam DTOs e contratos de entrada/saída.
- `infra/` — implementações das portas/portas de saída (ex.: JPA repositories, mappers,
  entidades JPA). Interações com o banco e clients externos ficam aqui.
- `api/` (ou `adapter/api/`) — controllers REST, DTOs de transporte (request/response) e
  mapeamento para os casos de uso. Controllers apenas convertem e delegam para `application`.
- `config/` — configurações do Spring (SecurityConfig, Beans, JWT filter, Cors, etc.)

Exemplo de caminho de classes

- `com.me.medical.domain` — classes de domínio
- `com.me.medical.application` — services/usecases e DTOs
- `com.me.medical.infra` — `JpaUserEntity`, `UserRepository` (implementação JPA)
- `com.me.medical.api` — `HealthController`, controllers REST
- `com.me.medical.config` — `SecurityConfig`, filtros JWT

## Convenções e práticas adotadas

- IDs: UUID v4 para todas as entidades públicas.
- Timezones: UTC no backend; o frontend converte conforme necessidade.
- Migrations: Flyway com scripts em `src/main/resources/db/migration`.
- Autenticação: JWT simplificado (seed users, sem refresh token). Roles: `ROLE_DOCTOR`, `ROLE_PATIENT`.
- Tratamento de erros: responses JSON simples { "message": "..." } e status HTTP semântico.
- Concorrência/atomicidade: regras críticas (ex.: reservar slot) devem usar transação e
  SELECT FOR UPDATE ou constraints únicas no banco (unique on `slot_id` em appointments).

## Como compilar e executar (rápido)

1. Ajuste variáveis de ambiente (ou use `application.yml`):
   - `SPRING_DATASOURCE_URL` — jdbc:postgresql://host:5432/dbname
   - `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET` — segredo para assinar tokens (dev-only ok)
2. Build:

   mvn -U clean package -DskipTests

3. Executar (após configurar DB e variáveis):

   java -jar target/medical-backend-0.0.1-SNAPSHOT.jar

## Referências rápidas

- Migrations: `src/main/resources/db/migration/`
- Aplicação principal: `src/main/java/com/me/medical/MedicalAppointmentApplication.java`
- Security: `src/main/java/com/me/medical/config/SecurityConfig.java`
- Exemplo infra: `src/main/java/com/me/medical/infra/JpaUserEntity.java`
- Repositório: `src/main/java/com/me/medical/infra/UserRepository.java`

## Como rodar o backend (modo rápido)

1. Garanta que um PostgreSQL esteja disponível. A maneira mais rápida é usar o `docker-compose.yml` na raiz:

```powershell
docker compose up -d
```

2. Na pasta `backend/`, execute o build e rode a aplicação:

```powershell
cd backend; mvn clean package; java -jar target/medical-backend-0.0.1-SNAPSHOT.jar
```

A configuração em `application.yml` usa variáveis de ambiente para a conexão com o banco (POSTGRES_HOST, POSTGRES_PORT, POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD). Flyway está habilitado e aplicará as migrations na inicialização.

### Opção rápida: script helper (dev)

Há um script helper `backend/run.sh` que simplifica o comando de execução em desenvolvimento. Ele possui valores padrão e permite sobrescrever via variáveis de ambiente, encapsulando o comando `mvn spring-boot:run`.

Exemplos:

```bash
# rodar com padrões (gera JWT_SECRET temporário para dev se não existir)
./backend/run.sh

# sobrescrever porta ou senha do banco
SERVER_PORT=8081 POSTGRES_PASSWORD=postgres ./backend/run.sh

# usar um JWT_SECRET customizado
JWT_SECRET=my-local-secret ./backend/run.sh
```

## Como rodar sem Docker (PostgreSQL local)

Se preferir usar um PostgreSQL instalado localmente (sem Docker), siga estes passos básicos:

1. Instale o PostgreSQL (versão 15+ recomendada) seguindo o instalador da sua plataforma.

2. Crie o banco e o usuário usados pelo projeto (exemplo usando `psql` como superuser):

```sql
CREATE DATABASE medical_appointment;
CREATE USER ma_user WITH PASSWORD 'ma_pass';
GRANT ALL PRIVILEGES ON DATABASE medical_appointment TO ma_user;
```

3. Exporte as variáveis de ambiente que o Spring Boot usa (exemplo PowerShell):

```powershell
$env:POSTGRES_HOST='localhost'
$env:POSTGRES_PORT='5432'
$env:POSTGRES_DB='medical_appointment'
$env:POSTGRES_USER='ma_user'
$env:POSTGRES_PASSWORD='ma_pass'
```

Ou defina as propriedades diretamente para o Spring (opcional):

```powershell
$env:SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/medical_appointment'
$env:SPRING_DATASOURCE_USERNAME='ma_user'
$env:SPRING_DATASOURCE_PASSWORD='ma_pass'
```

4. Empacote e rode a aplicação:

```powershell
mvn clean package -DskipTests
java -jar target/medical-backend-0.0.1-SNAPSHOT.jar
```

Observação: Flyway aplicará as migrations presentes em `src/main/resources/db/migration` na inicialização. Se preferir aplicar manualmente, instale o cliente Flyway e execute `flyway migrate` apontando para o banco.
