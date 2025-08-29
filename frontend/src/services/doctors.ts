import api from "./api";

export interface DoctorDTO {
  id: string;
  name: string;
  specialty?: string;
}

export async function listDoctors() {
  const res = await api.get(`/doctors`);
  return res.data as DoctorDTO[];
}

export async function getMyDoctor() {
  const res = await api.get(`/doctors/me`);
  return res.data as DoctorDTO;
}
