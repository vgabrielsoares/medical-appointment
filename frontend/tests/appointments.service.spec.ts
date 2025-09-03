import { describe, it, expect, vi, beforeEach } from "vitest";

// Mock da implementação do api
vi.mock("../src/services/api", () => ({
  default: {
    post: vi.fn(),
    get: vi.fn(),
  },
}));

import {
  createAppointment,
  listMyAppointments,
} from "../src/services/appointments";
import api from "../src/services/api";

const mockApi = api as any;

describe("appointments service", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("createAppointment", () => {
    it("calls POST with correct payload", async () => {
      const payload = {
        doctorId: "doctor-123",
        slotId: "slot-456",
      };

      const expectedResponse = {
        id: "appointment-789",
        doctorId: "doctor-123",
        slotId: "slot-456",
        patientId: "patient-999",
        status: "confirmed",
        createdAt: "2025-09-03T10:00:00Z",
      };

      mockApi.post.mockResolvedValue({ data: expectedResponse });

      const result = await createAppointment(payload);

      expect(mockApi.post).toHaveBeenCalledWith("/appointments", payload);
      expect(result).toEqual(expectedResponse);
    });

    it("propagates errors from API", async () => {
      const payload = { doctorId: "doctor-123", slotId: "slot-456" };
      const apiError = new Error("Slot not available");

      mockApi.post.mockRejectedValue(apiError);

      await expect(createAppointment(payload)).rejects.toThrow(
        "Slot not available"
      );
      expect(mockApi.post).toHaveBeenCalledWith("/appointments", payload);
    });

    it("handles network errors", async () => {
      const payload = { doctorId: "doctor-123", slotId: "slot-456" };
      mockApi.post.mockRejectedValue(new Error("Network Error"));

      await expect(createAppointment(payload)).rejects.toThrow("Network Error");
    });
  });

  describe("listMyAppointments", () => {
    it("calls GET and returns appointment list", async () => {
      const expectedAppointments = [
        {
          id: "app-1",
          doctorId: "doc-1",
          slotId: "slot-1",
          patientId: "patient-1",
          status: "confirmed",
          doctorName: "Dr. João Silva",
          doctorSpecialty: "Cardiologia",
          start: "2025-09-03T10:00:00Z",
          end: "2025-09-03T11:00:00Z",
        },
        {
          id: "app-2",
          doctorId: "doc-2",
          slotId: "slot-2",
          patientId: "patient-1",
          status: "confirmed",
          doctorName: "Dr. Maria Santos",
          doctorSpecialty: "Neurologia",
          start: "2025-09-04T14:00:00Z",
          end: "2025-09-04T15:00:00Z",
        },
      ];

      mockApi.get.mockResolvedValue({ data: expectedAppointments });

      const result = await listMyAppointments();

      expect(mockApi.get).toHaveBeenCalledWith("/appointments/my");
      expect(result).toEqual(expectedAppointments);
      expect(result).toHaveLength(2);
    });

    it("returns empty array when no appointments", async () => {
      mockApi.get.mockResolvedValue({ data: [] });

      const result = await listMyAppointments();

      expect(result).toEqual([]);
      expect(mockApi.get).toHaveBeenCalledWith("/appointments/my");
    });

    it("handles API errors", async () => {
      mockApi.get.mockRejectedValue(new Error("Unauthorized"));

      await expect(listMyAppointments()).rejects.toThrow("Unauthorized");
    });
  });
});
