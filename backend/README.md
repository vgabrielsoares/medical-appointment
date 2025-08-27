# Backend (Spring Boot)

- `pom.xml` — dependências Spring Boot + Flyway
- `src/main/java/com/me/medical/MedicalAppointmentApplication.java` — classe principal
- `src/main/resources/application.yml` — configuração usando variáveis de ambiente
- `src/main/resources/db/migration/V1__init_schema.sql` — migration inicial (roles, users)

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
