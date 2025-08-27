-- V1__init_schema.sql
-- Cria tabelas iniciais: roles, users

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS roles (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name VARCHAR(50) NOT NULL UNIQUE,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role_id UUID REFERENCES roles(id) ON DELETE SET NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Insert roles padrão
INSERT INTO roles (id, name) VALUES (uuid_generate_v4(), 'ROLE_DOCTOR') ON CONFLICT DO NOTHING;
INSERT INTO roles (id, name) VALUES (uuid_generate_v4(), 'ROLE_PATIENT') ON CONFLICT DO NOTHING;
