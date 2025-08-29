import api from "./api";

export interface CreateAppointmentDTO {
  doctorId: string;
  slotId: string;
}

export async function createAppointment(payload: CreateAppointmentDTO) {
  const res = await api.post(`/appointments`, payload);
  return res.data;
}

export async function listPatientAppointments(patientId: string) {
  const res = await api.get(`/patients/${patientId}/appointments`);
  return res.data;
}
