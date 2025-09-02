# Smoke Test — Medical Appointment (Passos rápidos)

Este documento descreve um smoke test manual / semi-automatizado para validar localmente a integração básica entre banco, backend e frontend.

Resumo do que será verificado

- Banco PostgreSQL sobe (via docker-compose)
- Backend inicia e aplica migrations (Flyway)
- Frontend inicia (opcional, valida UI)
- Login com usuários seed (doctor / patient)
- Médico cria um slot
- Paciente lista slots disponíveis e cria um appointment
- Verificar que appointment existe para o paciente

Pré-requisitos

- Docker + docker-compose (ou um PostgreSQL 15+ acessível)
- Java 17+ e Maven (para rodar backend sem Docker)
- Node 18+ / npm (para rodar frontend local)

Credenciais seed (criadas por `DataSeeder`)

- Médico: email `doctor@example.com`, senha `doctorpass`
- Paciente: email `patient@example.com`, senha `patientpass`

Valores e portas usadas neste repo (padrões)

- Postgres (docker-compose): POSTGRES_DB=medical_appointment, POSTGRES_USER=ma_user, POSTGRES_PASSWORD=ma_pass, exposto em localhost:5432
- Backend: porta 8080 (default)
- Frontend: porta 5173 (Vite default)

Passos rápidos (comandos)

1 - Subir o banco com docker-compose

```bash
# na raiz do repo
docker compose up -d
```

2 - Rodar o backend (opções: script helper ou mvn)

Você pode iniciar o backend de duas maneiras locais:

- Usando o script helper `./run.sh` (recomendado):

```bash
cd backend
# se estiver usando o docker-compose acima, use as variáveis abaixo
POSTGRES_HOST=localhost POSTGRES_PORT=5432 POSTGRES_DB=medical-appointment POSTGRES_USER=postgres POSTGRES_PASSWORD=postgres ./run.sh
```

- Ou manualmente com Maven (`mvn spring-boot:run`) — útil se quiser controlar argumentos do Spring diretamente:

```bash
cd backend
# exemplo equivalente usando mvn (define profile e passa argumentos para a aplicação)
POSTGRES_HOST=localhost POSTGRES_PORT=5432 POSTGRES_DB=medical-appointment POSTGRES_USER=postgres POSTGRES_PASSWORD=postgres \
  mvn -DskipTests -Dspring-boot.run.profiles=dev \
    -Dspring-boot.run.arguments="--server.port=8080 --spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB} --spring.datasource.username=${POSTGRES_USER} --spring.datasource.password=${POSTGRES_PASSWORD}" \
    spring-boot:run
```

Observe os logs do backend. Flyway deverá aplicar migrations automaticamente.

3 - (Opcional) Rodar frontend

```bash
cd frontend
npm install
npm run dev
```

4 - Login (curl) — obter token JWT

## Doctor

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"doctor@example.com","password":"doctorpass"}' | jq
```

## Patient

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"patient@example.com","password":"patientpass"}' | jq
```

A resposta contém:

```json
{
  "token": "JWT_TOKEN",
  "user": { "id": "UUID", "role": "ROLE_DOCTOR|ROLE_PATIENT" }
}
```

Anote o token do médico e paciente para as próximas etapas.

5 - Listar médicos para obter `doctorId`

```bash
curl -s http://localhost:8080/api/doctors | jq
```

Anote o `id` do médico (deve corresponder ao seed `Dr. Example`).

6 - Criar um slot (como médico)

Exemplo: criar um slot de 30 minutos às 09:00 UTC

```bash
DOCTOR_ID=<obtido_no_passo_acima>
TOKEN_DOCTOR=<token_do_medico>
curl -s -X POST http://localhost:8080/api/doctors/${DOCTOR_ID}/slots \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN_DOCTOR}" \
  -d '{"start":"2025-09-03T09:00:00Z","end":"2025-09-03T09:30:00Z","metadata":{},"status":"available"}' | jq
```

Resposta esperada: HTTP 201 com objeto `SlotDto` contendo `id`, `start`, `end`, `status`.

7 - Listar slots públicos (ou do médico) e confirmar disponibilidade

```bash
curl -s http://localhost:8080/api/doctors/${DOCTOR_ID}/slots | jq
```

Deverá aparecer o slot criado com status `available`.

8 - Agendar o slot (como paciente)

```bash
TOKEN_PATIENT=<token_do_paciente>
SLOT_ID=<id_do_slot_criado>
DOCTOR_ID=<doctor_id_medico>
curl -s -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${TOKEN_PATIENT}" \
  -d '{"doctorId":"'${DOCTOR_ID}'","slotId":"'${SLOT_ID}'"}' -w "%{http_code}\n" -o /tmp/appointment_response.json

cat /tmp/appointment_response.json | jq
```

Resposta esperada: HTTP 201 e o corpo com `AppointmentDto` contendo `id`, `slotId`, `doctorId`, `patientId`, `status`.

9 - Verificar agendamentos do paciente

```bash
curl -s -H "Authorization: Bearer ${TOKEN_PATIENT}" http://localhost:8080/api/appointments/my | jq
```

Deve listar o appointment criado.

Checklist de verificação

- [ ] Banco subiu e aceita conexões
- [ ] Backend iniciou sem erros e aplicou migrations
- [ ] Login devolve token para doctor e patient
- [ ] Médico consegue criar slot (201)
- [ ] Paciente consegue reservar slot (201)
- [ ] Appointment aparece em /api/appointments/my

Diagnóstico rápido em falhas comuns

- Se login retorna 500 ou credenciais inválidas, verifique logs do backend (target/logs, backend.log) e se o seed foi executado.
- Se o slot create retorna 403, verifique se o token pertence ao médico certo (o controller valida proprietário do doctorId).
- Se a requisição de agendamento retorna 409, o slot já foi reservado — verifique /api/doctors/{id}/slots e /api/appointments/my.
