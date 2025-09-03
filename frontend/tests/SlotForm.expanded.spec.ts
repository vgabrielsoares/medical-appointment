import { describe, it, expect, vi } from "vitest";
import { render, fireEvent, waitFor } from "@testing-library/vue";
import SlotForm from "../src/components/SlotForm.vue";

describe("SlotForm - expanded tests", () => {
  describe("form validation", () => {
    it("shows validation errors and emits save with ISO dates", async () => {
      const { getByLabelText, getByText, emitted } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;
      const saveBtn = getByText("Salvar");

      await fireEvent.click(saveBtn);
      getByText("Preencha início e fim.");

      await fireEvent.update(start, "2023-01-01T10:00");
      await fireEvent.update(end, "2023-01-01T09:00");
      await fireEvent.click(saveBtn);
      getByText("Início deve ser anterior ao fim.");

      await fireEvent.update(end, "2023-01-01T11:00");
      await fireEvent.click(saveBtn);

      const saveEmits = emitted()["save"];
      expect(saveEmits).toBeTruthy();
      expect(typeof saveEmits[0][0].start).toBe("string");
      expect(saveEmits[0][0].start.endsWith("Z")).toBeTruthy();
    });

    it("validates minimum duration", async () => {
      const { getByLabelText, getByText, emitted } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;
      const saveBtn = getByText("Salvar");

      await fireEvent.update(start, "2023-01-01T10:00");
      await fireEvent.update(end, "2023-01-01T10:01");
      await fireEvent.click(saveBtn);

      const saveEmits = emitted()["save"];
      expect(saveEmits).toBeTruthy();
      expect(saveEmits[0][0]).toMatchObject({
        start: expect.any(String),
        end: expect.any(String),
      });
    });

    it("handles edge case: exactly same minute", async () => {
      const { getByLabelText, getByText } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;
      const saveBtn = getByText("Salvar");

      await fireEvent.update(start, "2023-01-01T10:30");
      await fireEvent.update(end, "2023-01-01T10:30");
      await fireEvent.click(saveBtn);

      getByText("Início deve ser anterior ao fim.");
    });
  });

  describe("edit mode", () => {
    it("loads existing slot data for editing", async () => {
      const existingSlot = {
        id: "slot-123",
        start: "2023-06-15T14:00:00Z",
        end: "2023-06-15T15:00:00Z",
        metadata: { room: "A1", notes: "Consulta de rotina" },
      };

      const { getByLabelText } = render(SlotForm, {
        props: { modelValue: existingSlot },
      });

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;

      expect(start.value).toContain("2023-06-15");
      expect(end.value).toContain("2023-06-15");
      expect(start.value.length).toBe(16);
      expect(end.value.length).toBe(16);
    });

    it("emits save with correct data in edit mode", async () => {
      const existingSlot = {
        id: "slot-123",
        start: "2023-06-15T14:00:00Z",
        end: "2023-06-15T15:00:00Z",
      };

      const { getByText, emitted } = render(SlotForm, {
        props: { modelValue: existingSlot },
      });

      const saveBtn = getByText("Salvar");
      await fireEvent.click(saveBtn);

      const saveEmits = emitted()["save"];
      expect(saveEmits).toBeTruthy();
      expect(saveEmits[0][0]).toMatchObject({
        start: expect.any(String),
        end: expect.any(String),
      });
    });
  });

  describe("timezone handling", () => {
    it("converts local time to UTC ISO string", async () => {
      const { getByLabelText, getByText, emitted } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;
      const saveBtn = getByText("Salvar");

      await fireEvent.update(start, "2023-07-20T09:30");
      await fireEvent.update(end, "2023-07-20T10:30");
      await fireEvent.click(saveBtn);

      const saveEmits = emitted()["save"];
      const payload = saveEmits[0][0];

      expect(payload.start).toMatch(
        /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d{3})?Z$/
      );
      expect(payload.end).toMatch(
        /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(\.\d{3})?Z$/
      );
    });
  });

  describe("metadata handling", () => {
    it("emits basic payload without extra metadata", async () => {
      const { getByLabelText, getByText, emitted } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;
      const saveBtn = getByText("Salvar");

      await fireEvent.update(start, "2023-08-01T11:00");
      await fireEvent.update(end, "2023-08-01T12:00");
      await fireEvent.click(saveBtn);

      const saveEmits = emitted()["save"];
      const payload = saveEmits[0][0];

      expect(payload).toHaveProperty("start");
      expect(payload).toHaveProperty("end");
      expect(typeof payload.start).toBe("string");
      expect(typeof payload.end).toBe("string");
    });
  });

  describe("user interaction", () => {
    it("clears error messages when user corrects input and submits", async () => {
      const { getByLabelText, getByText, queryByText } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;
      const saveBtn = getByText("Salvar");

      await fireEvent.click(saveBtn);
      expect(getByText("Preencha início e fim.")).toBeTruthy();

      await fireEvent.update(start, "2023-09-01T10:00");
      await fireEvent.update(end, "2023-09-01T11:00");

      await fireEvent.click(saveBtn);

      expect(queryByText("Preencha início e fim.")).toBeNull();
    });

    it("maintains form state during rapid interactions", async () => {
      const { getByLabelText, getByText } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;
      const end = getByLabelText("Fim") as HTMLInputElement;
      const saveBtn = getByText("Salvar");

      await fireEvent.update(start, "2023-09-15T08:00");
      await fireEvent.update(start, "2023-09-15T08:30");
      await fireEvent.update(end, "2023-09-15T09:00");
      await fireEvent.update(end, "2023-09-15T09:30");

      expect(start.value).toBe("2023-09-15T08:30");
      expect(end.value).toBe("2023-09-15T09:30");
    });
  });

  describe("accessibility", () => {
    it("has proper labels for form inputs", () => {
      const { getByLabelText } = render(SlotForm);

      expect(getByLabelText("Início")).toBeTruthy();
      expect(getByLabelText("Fim")).toBeTruthy();
    });

    it("submit button is accessible", () => {
      const { getByRole } = render(SlotForm);

      const submitButton = getByRole("button", { name: /salvar/i });
      expect(submitButton).toBeTruthy();
    });
  });

  describe("performance considerations", () => {
    it("does not emit on every keystroke", async () => {
      const { getByLabelText, emitted } = render(SlotForm);

      const start = getByLabelText("Início") as HTMLInputElement;

      await fireEvent.update(start, "2");
      await fireEvent.update(start, "20");
      await fireEvent.update(start, "202");
      await fireEvent.update(start, "2023");

      expect(emitted()["save"]).toBeFalsy();
    });
  });

  describe("component lifecycle", () => {
    it("initializes form with provided modelValue", async () => {
      const initialSlot = {
        id: "slot-1",
        start: "2023-10-01T10:00:00Z",
        end: "2023-10-01T11:00:00Z",
      };

      const { getByLabelText } = render(SlotForm, {
        props: { modelValue: initialSlot },
      });

      const start = getByLabelText("Início") as HTMLInputElement;
      expect(start.value).toContain("2023-10-01");
      expect(start.value.length).toBe(16);
    });
  });
});
