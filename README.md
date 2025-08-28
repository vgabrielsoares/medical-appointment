# Medical Appointment

Estrutura inicial:

- `backend/` — backend Spring Boot
- `frontend/` — frontend Vite + Vue 3 + TypeScript

## Como rodar (modo rápido com Docker)

Este repositório inclui um `docker-compose.yml` com um serviço PostgreSQL (`db`) e opcional `pgadmin`.

Credenciais padrão (definidas como variáveis de ambiente no `docker-compose.yml`):

- POSTGRES_USER: `ma_user`
- POSTGRES_PASSWORD: `ma_pass`
- POSTGRES_DB: `medical_appointment`

Para subir apenas o banco de dados (em background):

```powershell
docker compose up -d
```

Depois disso, a aplicação backend (em `backend/`) pode se conectar usando as mesmas variáveis de ambiente. As migrations Flyway estão configuradas no backend e serão aplicadas automaticamente na inicialização do Spring Boot.

Se quiser acessar o pgAdmin (interface web), abra `http://localhost:8080` e faça login com as credenciais padrão definidas no `docker-compose.yml` (`admin@local` / `admin` por padrão).

## Como rodar sem Docker (PostgreSQL local)

Se preferir executar o backend usando um PostgreSQL instalado localmente, siga estas etapas rápidas:

1. Instale um PostgreSQL (15+ recomendado) para sua plataforma seguindo o instalador oficial.

2. Crie o banco e o usuário usados pelo projeto. Exemplo (execute em um cliente `psql` como superuser):

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

Alternativamente defina diretamente as propriedades do Spring:

```powershell
$env:SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/medical_appointment'
$env:SPRING_DATASOURCE_USERNAME='ma_user'
$env:SPRING_DATASOURCE_PASSWORD='ma_pass'
```

4. Na pasta `backend/`, empacote e rode a aplicação:

```powershell
cd backend
mvn clean package -DskipTests
java -jar target/medical-backend-0.0.1-SNAPSHOT.jar
```

Observação: as migrations Flyway vivas em `backend/src/main/resources/db/migration` serão aplicadas automaticamente na inicialização.

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
