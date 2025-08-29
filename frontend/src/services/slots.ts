import api from "./api";

export interface SlotCreateDTO {
  start: string; // ISO8601
  end: string; // ISO8601
  metadata?: Record<string, any>;
}

export interface SlotDTO {
  id: string;
  doctorId: string;
  start: string;
  end: string;
  status: string;
  metadata?: Record<string, any>;
}

export async function listDoctorSlots(doctorId: string) {
  const res = await api.get(`/doctors/${doctorId}/slots`);
  return res.data as SlotDTO[];
}

export async function createDoctorSlot(
  doctorId: string,
  payload: SlotCreateDTO
) {
  const res = await api.post(`/doctors/${doctorId}/slots`, payload);
  return res.data as SlotDTO;
}

export async function updateDoctorSlot(
  doctorId: string,
  slotId: string,
  payload: SlotCreateDTO
) {
  const res = await api.put(`/doctors/${doctorId}/slots/${slotId}`, payload);
  return res.data as SlotDTO;
}

export async function deleteDoctorSlot(doctorId: string, slotId: string) {
  const res = await api.delete(`/doctors/${doctorId}/slots/${slotId}`);
  return res.status === 204;
}
