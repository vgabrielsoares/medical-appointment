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
