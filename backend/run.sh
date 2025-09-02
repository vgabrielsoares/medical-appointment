#!/usr/bin/env bash
set -euo pipefail

# Script helper para rodar o backend em dev com valores padrão (substitua via env vars)
# Uso:
#   ./backend/run.sh            # roda com valores padrão
#   POSTGRES_PASSWORD=secret ./backend/run.sh  # override via env
#   SERVER_PORT=8080 ./backend/run.sh

: "${POSTGRES_HOST:=localhost}"
: "${POSTGRES_PORT:=5432}"
: "${POSTGRES_DB:=medical-appointment}"
: "${POSTGRES_USER:=postgres}"
: "${POSTGRES_PASSWORD:=postgres}"
: "${SERVER_PORT:=8080}"
: "${SPRING_PROFILE:=dev}"

# Se não existir JWT_SECRET, gera um temporário para dev
# Se não existir JWT_SECRET, tenta carregar de arquivo persistente .jwt_secret
# Isso evita invalidar tokens toda vez que o backend for reiniciado em dev.
if [ -z "${JWT_SECRET:-}" ]; then
  if [ -f ".jwt_secret" ]; then
    export JWT_SECRET="$(cat .jwt_secret)"
  else
    # gerar e salvar em .jwt_secret
    JWT_SECRET_GENERATED="$(openssl rand -hex 32)"
    echo "$JWT_SECRET_GENERATED" > .jwt_secret
    chmod 600 .jwt_secret || true
    export JWT_SECRET="$JWT_SECRET_GENERATED"
  fi
fi

echo "Running backend with:" \
     "DB=$POSTGRES_HOST:$POSTGRES_PORT/$POSTGRES_DB user=$POSTGRES_USER port=$SERVER_PORT profile=$SPRING_PROFILE"

mvn -DskipTests -Dspring-boot.run.profiles="$SPRING_PROFILE" \
  -Dspring-boot.run.arguments="--server.port=${SERVER_PORT} --spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB} --spring.datasource.username=${POSTGRES_USER} --spring.datasource.password=${POSTGRES_PASSWORD}" \
  spring-boot:run
