import { describe, it, expect } from "vitest";
import { render, fireEvent } from "@testing-library/vue";
import SlotForm from "../src/components/SlotForm.vue";

describe("SlotForm", () => {
  it("shows validation errors and emits save with ISO dates", async () => {
    const { getByLabelText, getByText, emitted } = render(SlotForm);

    const start = getByLabelText("Início") as HTMLInputElement;
    const end = getByLabelText("Fim") as HTMLInputElement;
    const saveBtn = getByText("Salvar");

    // submit empty -> error
    await fireEvent.click(saveBtn);
    getByText("Preencha início e fim.");

    // fill end earlier than start -> error
    await fireEvent.update(start, "2023-01-01T10:00");
    await fireEvent.update(end, "2023-01-01T09:00");
    await fireEvent.click(saveBtn);
    getByText("Início deve ser anterior ao fim.");

    // valid
    await fireEvent.update(end, "2023-01-01T11:00");
    await fireEvent.click(saveBtn);
    const saveEmits = emitted()["save"];
    expect(saveEmits).toBeTruthy();
    // payload should have ISO strings
    expect(typeof saveEmits[0][0].start).toBe("string");
    expect(saveEmits[0][0].start.endsWith("Z")).toBeTruthy();
  });
});
