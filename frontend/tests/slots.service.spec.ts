import { describe, it, expect, vi, beforeEach } from "vitest";

// Mock da implementação do api
vi.mock("../src/services/api", () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}));

import {
  listDoctorSlots,
  createDoctorSlot,
  updateDoctorSlot,
  deleteDoctorSlot,
} from "../src/services/slots";
import api from "../src/services/api";

const mockApi = api as any;

describe("slots service", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("listDoctorSlots", () => {
    it("calls GET with correct URL and returns slots", async () => {
      const doctorId = "doctor-123";
      const expectedSlots = [
        {
          id: "slot-1",
          doctorId: "doctor-123",
          start: "2025-09-03T09:00:00Z",
          end: "2025-09-03T10:00:00Z",
          status: "available",
          metadata: { room: "A1" },
        },
        {
          id: "slot-2",
          doctorId: "doctor-123",
          start: "2025-09-03T10:00:00Z",
          end: "2025-09-03T11:00:00Z",
          status: "booked",
          metadata: { room: "A2" },
        },
      ];

      mockApi.get.mockResolvedValue({ data: expectedSlots });

      const result = await listDoctorSlots(doctorId);

      expect(mockApi.get).toHaveBeenCalledWith(`/doctors/${doctorId}/slots`);
      expect(result).toEqual(expectedSlots);
      expect(result).toHaveLength(2);
    });

    it("returns empty array when doctor has no slots", async () => {
      const doctorId = "doctor-no-slots";
      mockApi.get.mockResolvedValue({ data: [] });

      const result = await listDoctorSlots(doctorId);

      expect(result).toEqual([]);
    });

    it("handles API errors", async () => {
      const doctorId = "doctor-123";
      mockApi.get.mockRejectedValue(new Error("Doctor not found"));

      await expect(listDoctorSlots(doctorId)).rejects.toThrow(
        "Doctor not found"
      );
    });
  });

  describe("createDoctorSlot", () => {
    it("calls POST with correct URL and payload", async () => {
      const doctorId = "doctor-123";
      const payload = {
        start: "2025-09-03T14:00:00Z",
        end: "2025-09-03T15:00:00Z",
        metadata: { room: "B1", type: "consultation" },
      };

      const expectedResponse = {
        id: "slot-new",
        doctorId: "doctor-123",
        start: "2025-09-03T14:00:00Z",
        end: "2025-09-03T15:00:00Z",
        status: "available",
        metadata: { room: "B1", type: "consultation" },
      };

      mockApi.post.mockResolvedValue({ data: expectedResponse });

      const result = await createDoctorSlot(doctorId, payload);

      expect(mockApi.post).toHaveBeenCalledWith(
        `/doctors/${doctorId}/slots`,
        payload
      );
      expect(result).toEqual(expectedResponse);
    });

    it("handles validation errors", async () => {
      const doctorId = "doctor-123";
      const invalidPayload = {
        start: "2025-09-03T15:00:00Z",
        end: "2025-09-03T14:00:00Z",
      };

      mockApi.post.mockRejectedValue(new Error("start must be before end"));

      await expect(createDoctorSlot(doctorId, invalidPayload)).rejects.toThrow(
        "start must be before end"
      );
    });

    it("handles overlap errors", async () => {
      const doctorId = "doctor-123";
      const payload = {
        start: "2025-09-03T09:30:00Z",
        end: "2025-09-03T10:30:00Z",
      };

      mockApi.post.mockRejectedValue(
        new Error("slot overlaps with existing slot")
      );

      await expect(createDoctorSlot(doctorId, payload)).rejects.toThrow(
        "slot overlaps with existing slot"
      );
    });
  });

  describe("updateDoctorSlot", () => {
    it("calls PUT with correct URL and payload", async () => {
      const doctorId = "doctor-123";
      const slotId = "slot-456";
      const payload = {
        start: "2025-09-03T16:00:00Z",
        end: "2025-09-03T17:00:00Z",
        metadata: { room: "C1", notes: "Updated time" },
      };

      const expectedResponse = {
        id: "slot-456",
        doctorId: "doctor-123",
        start: "2025-09-03T16:00:00Z",
        end: "2025-09-03T17:00:00Z",
        status: "available",
        metadata: { room: "C1", notes: "Updated time" },
      };

      mockApi.put.mockResolvedValue({ data: expectedResponse });

      const result = await updateDoctorSlot(doctorId, slotId, payload);

      expect(mockApi.put).toHaveBeenCalledWith(
        `/doctors/${doctorId}/slots/${slotId}`,
        payload
      );
      expect(result).toEqual(expectedResponse);
    });

    it("handles not found errors", async () => {
      const doctorId = "doctor-123";
      const slotId = "nonexistent-slot";
      const payload = {
        start: "2025-09-03T16:00:00Z",
        end: "2025-09-03T17:00:00Z",
      };

      mockApi.put.mockRejectedValue(new Error("slot not found"));

      await expect(updateDoctorSlot(doctorId, slotId, payload)).rejects.toThrow(
        "slot not found"
      );
    });

    it("handles unauthorized access", async () => {
      const doctorId = "doctor-123";
      const slotId = "slot-456";
      const payload = {
        start: "2025-09-03T16:00:00Z",
        end: "2025-09-03T17:00:00Z",
      };

      mockApi.put.mockRejectedValue(new Error("not owner"));

      await expect(updateDoctorSlot(doctorId, slotId, payload)).rejects.toThrow(
        "not owner"
      );
    });
  });

  describe("deleteDoctorSlot", () => {
    it("calls DELETE with correct URL and returns success", async () => {
      const doctorId = "doctor-123";
      const slotId = "slot-456";

      mockApi.delete.mockResolvedValue({ status: 204 });

      const result = await deleteDoctorSlot(doctorId, slotId);

      expect(mockApi.delete).toHaveBeenCalledWith(
        `/doctors/${doctorId}/slots/${slotId}`
      );
      expect(result).toBe(true);
    });

    it("handles deletion errors", async () => {
      const doctorId = "doctor-123";
      const slotId = "slot-456";

      mockApi.delete.mockRejectedValue(new Error("slot not found"));

      await expect(deleteDoctorSlot(doctorId, slotId)).rejects.toThrow(
        "slot not found"
      );
    });

    it("returns false for non-204 status", async () => {
      const doctorId = "doctor-123";
      const slotId = "slot-456";

      mockApi.delete.mockResolvedValue({ status: 500 });

      const result = await deleteDoctorSlot(doctorId, slotId);

      expect(result).toBe(false);
    });
  });

  describe("error handling patterns", () => {
    it("preserves error details from API responses", async () => {
      const doctorId = "doctor-123";
      const error = {
        response: {
          status: 409,
          data: {
            message: "slot overlaps with existing slot",
            code: "OVERLAP",
          },
        },
      };

      mockApi.get.mockRejectedValue(error);

      try {
        await listDoctorSlots(doctorId);
      } catch (e) {
        expect(e).toBe(error);
      }
    });
  });
});
