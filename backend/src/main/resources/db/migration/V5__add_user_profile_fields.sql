-- V5__add_user_profile_fields.sql

-- Adicionar campos de telefone e data de nascimento na tabela de pacientes e médicos
ALTER TABLE patients ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE patients ADD COLUMN IF NOT EXISTS birth_date DATE;

ALTER TABLE doctors ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE doctors ADD COLUMN IF NOT EXISTS birth_date DATE;
