# Medical Appointment (MedBook)

Sistema de agendamento médico que permite médicos gerenciarem horários disponíveis e pacientes realizarem agendamentos.

## Visão Geral

Este projeto implementa um sistema de agendamento médico onde:

- **Médicos** cadastram e gerenciam horários disponíveis
- **Pacientes** visualizam horários e realizam agendamentos
- **Sistema** previne conflitos de agendamento e garante integridade dos dados

## Tecnologias

- **Backend**: Java 17 + Spring Boot + Spring Security + JPA/Hibernate + Flyway
- **Frontend**: Vue 3 + TypeScript + Vite + Pinia + Vue Router + Tailwind CSS
- **Banco**: PostgreSQL 15+ com migrations automatizadas
- **Autenticação**: JWT simples (sem refresh token)

## Estrutura do Projeto

```
medical-appointment/
├── backend/           # API REST (Spring Boot)
├── frontend/          # Interface web (Vue 3 + TypeScript)
└── docker-compose.yml # PostgreSQL + pgAdmin
```

## Requisitos

- **Docker** (recomendado para o banco de dados)
- **Java 17+** e **Maven** (para o backend)
- **Node.js 18+** e **npm** (para o frontend)

## Como Executar

### 1. Banco de Dados (PostgreSQL)

Subir PostgreSQL via Docker (recomendado):

```bash
docker compose up -d
```

**Credenciais padrão:**

- Host: `localhost:5432`
- Database: `medical-appointment`
- User: `postgres`
- Password: `postgres`

### 2. Backend (Spring Boot)

```bash
cd backend
./run.sh
```

Ou manualmente:

```bash
cd backend
mvn clean package -DskipTests
java -jar target/medical-backend-0.0.1-SNAPSHOT.jar
```

**O backend estará disponível em:** `http://localhost:8080`

### 3. Frontend (Vue 3)

```bash
cd frontend
npm install
npm run dev
```

**O frontend estará disponível em:** `http://localhost:5173`

## Configuração Alternativa (PostgreSQL Local)

Se preferir usar PostgreSQL instalado localmente:

1. Criar banco e usuário:

```sql
CREATE DATABASE "medical-appointment";
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE "medical-appointment" TO postgres;
```

2. Definir variáveis de ambiente:

```bash
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=medical-appointment
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=postgres
```

## Credenciais de Desenvolvimento

O backend inclui um `DataSeeder` que cria automaticamente usuários para desenvolvimento:

### Médico

- **Email**: `doctor@example.com`
- **Senha**: `doctorpass`
- **Role**: `ROLE_DOCTOR`

### Paciente

- **Email**: `patient@example.com`
- **Senha**: `patientpass`
- **Role**: `ROLE_PATIENT`

> ⚠️ **Importante**: Estas credenciais são apenas para desenvolvimento. Nunca usar em produção.

## API Endpoints

### Autenticação

**POST** `/api/auth/login`

- Body: `{ "email": "string", "password": "string" }`
- Response: `{ "token": "jwt_token", "user": { "id": "uuid", "role": "ROLE_DOCTOR|ROLE_PATIENT" } }`

**POST** `/api/auth/register`

- Body: `{ "name": "string", "email": "string", "password": "string", "role": "string", "specialty": "string", "phone": "string" }`
- Response: `{ "token": "jwt_token", "user": { ... } }`

### Médicos e Slots

**GET** `/api/doctors` - Listar médicos (público)

**GET** `/api/doctors/{doctorId}/slots` - Listar slots do médico

**POST** `/api/doctors/{doctorId}/slots` - Criar slot (ROLE_DOCTOR)

- Body: `{ "start": "2025-09-10T09:00:00Z", "end": "2025-09-10T09:30:00Z", "metadata": {} }`

**PUT** `/api/doctors/{doctorId}/slots/{slotId}` - Atualizar slot (ROLE_DOCTOR)

**DELETE** `/api/doctors/{doctorId}/slots/{slotId}` - Deletar slot (ROLE_DOCTOR)

### Agendamentos

**POST** `/api/appointments` - Criar agendamento (ROLE_PATIENT)

- Body: `{ "doctorId": "uuid", "slotId": "uuid" }`

**GET** `/api/appointments/my` - Listar agendamentos do paciente (ROLE_PATIENT)

**GET** `/api/patients/{patientId}/appointments` - Listar agendamentos específicos (ROLE_PATIENT)

> 📝 **Nota**: Todas as rotas protegidas requerem header `Authorization: Bearer <token>`

## Arquitetura e Decisões Técnicas

### Backend (Clean Architecture)

O backend segue princípios de Clean Architecture com separação clara de responsabilidades:

```
backend/src/main/java/com/me/medical/
├── domain/ # Entidades de negócio (User, Doctor, Patient, Slot, Appointment)
├── application/ # Casos de uso e interfaces (AuthService, SlotService, AppointmentService)
├── infra/ # Implementações JPA (repositories, entidades, mappers)
├── api/ # Controllers REST (AuthController, DoctorController, etc.)
└── config/ # Configurações Spring (SecurityConfig, JwtTokenProvider)
```

**Vantagens desta abordagem:**

- Facilita testes unitários (domain isolado de frameworks)
- Permite mudanças de infraestrutura sem afetar regras de negócio
- Melhora legibilidade e manutenção do código

### Frontend (Vue 3 + Composition API)

```

frontend/src/
├── components/ # Componentes reutilizáveis (UI components, forms)
├── pages/ # Páginas principais (Login, Doctor, Patient, etc.)
├── services/ # Integração com API (axios, interceptors)
├── stores/ # Estado global (Pinia - auth, theme)
├── router/ # Configuração de rotas (Vue Router)
├── styles/ # Design tokens e CSS global (Tailwind)
└── utils/ # Utilitários e formatters

```

### Decisões Técnicas e Trade-offs Aceitos

#### 1. **Autenticação JWT Simples**

- **Decisão**: JWT sem refresh token
- **Justificativa**: Simplifica implementação para demonstração
- **Trade-off**: Menor segurança (token não pode ser revogado facilmente)
- **Mitigação**: Token de curta duração (1 hora)

#### 2. **Prevenção de Conflitos Básica**

- **Decisão**: SELECT FOR UPDATE + unique constraints
- **Justificativa**: Resolve 99% dos casos de concorrência
- **Trade-off**: Sem retry logic ou estratégias avançadas
- **Mitigação**: Tratamento de erro adequado para o usuário

#### 3. **Validação Simplificada**

- **Decisão**: Validação básica (start < end, campos obrigatórios)
- **Justificativa**: Foco nas regras de negócio principais
- **Trade-off**: Sem sanitização avançada de inputs
- **Mitigação**: Uso de JPA/PreparedStatements contra SQL injection

#### 4. **Tratamento de Erro Básico**

- **Decisão**: Responses JSON simples `{ "message": "..." }`
- **Justificativa**: API clara e consistente
- **Trade-off**: Sem códigos de erro estruturados ou logging avançado
- **Mitigação**: Status HTTP semânticos e mensagens descritivas

#### 5. **UI Minimalista**

- **Decisão**: Foco na funcionalidade, design limpo com Tailwind
- **Justificativa**: Demonstra competência técnica vs. design visual
- **Trade-off**: UX/acessibilidade limitadas
- **Mitigação**: Responsividade implementada, componentes bem estruturados

### Tecnologias e Ferramentas

- **Java 17**: Versão LTS com recursos modernos
- **Spring Boot 3.1.4**: Framework maduro e bem documentado
- **PostgreSQL 15**: Banco robusto com suporte a JSON e timezone
- **Flyway**: Migrations versionadas e automáticas
- **Vue 3 + Composition API**: Framework reativo moderno
- **TypeScript**: Type safety no frontend
- **Tailwind CSS**: Utility-first CSS framework
- **Pinia**: Estado simples e performático
- **Vite**: Build tool rápido

## Validação e Testes

### Executar Smoke Test

Para validar a integração completa do sistema, execute os passos detalhados no arquivo [`SMOKE_TEST.md`](./SMOKE_TEST.md).

O smoke test verifica:

- Conexão com banco de dados
- Aplicação de migrations
- Autenticação JWT
- Criação de slots por médicos
- Reserva de appointments por pacientes
- Prevenção de conflitos

### Testes Automatizados

**Backend** (JUnit + Mockito):

```bash
cd backend
mvn test
```

**Frontend** (Vitest + Testing Library):

```bash
cd frontend
npm run test
```

## Estrutura de Banco de Dados

### Principais Tabelas

- **users**: Dados básicos de autenticação
- **roles**: Definição de papéis (ROLE_DOCTOR, ROLE_PATIENT)
- **doctors**: Informações específicas de médicos
- **patients**: Informações específicas de pacientes
- **slots**: Horários disponíveis dos médicos
- **appointments**: Agendamentos realizados

### Constraints Importantes

- `unique(doctor_id, start_time)` em slots - previne sobreposição
- `unique(slot_id)` em appointments - previne double-booking
- Foreign keys com cascade apropriado

## Desenvolvimento

### Executar em Modo de Desenvolvimento

**Backend** (com hot reload):

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Frontend** (com hot reload):

```bash
cd frontend
npm run dev
```

### Build para Produção

**Backend**:

```bash
cd backend
mvn clean package
```

**Frontend**:

```bash
cd frontend
npm run build
```

## Considerações para Produção

### Melhorias Necessárias

1. **Segurança**:

   - Implementar refresh tokens
   - Rate limiting
   - Validação e sanitização completa de inputs
   - HTTPS obrigatório

2. **Observabilidade**:

   - Logging estruturado
   - Métricas de performance
   - Health checks detalhados
   - Monitoramento de erros

3. **Escalabilidade**:

   - Cache Redis para sessões
   - Connection pooling
   - Índices de banco otimizados
   - CDN para assets estáticos

4. **UX/Acessibilidade**:
   - Suporte a leitores de tela
   - Testes de contraste
   - Progressive Web App (PWA)
   - Offline support básico

## Suporte e Documentação

<!-- - **API Documentation**: Disponível via Swagger UI em `/swagger-ui.html` -->

- **Smoke Test**: Instruções detalhadas em [`SMOKE_TEST.md`](./SMOKE_TEST.md)
- **Architecture Decision Records**: Documentados neste README

---

**Desenvolvido como demonstração técnica de sistema de agendamento médico usando tecnologias modernas e boas práticas de arquitetura.**
