-- V3__add_slots.sql

CREATE TABLE IF NOT EXISTS slots (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  doctor_id UUID NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
  start_time TIMESTAMP WITH TIME ZONE NOT NULL,
  end_time TIMESTAMP WITH TIME ZONE NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'available',
  metadata JSONB,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Garantir que um médico não tenha dois slots começando no mesmo instante
CREATE UNIQUE INDEX IF NOT EXISTS idx_slots_doctor_start ON slots(doctor_id, start_time);
