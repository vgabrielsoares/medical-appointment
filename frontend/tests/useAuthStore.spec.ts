import { beforeEach, describe, expect, it, vi } from "vitest";
import { setActivePinia, createPinia } from "pinia";
import { useAuthStore } from "../src/stores/auth";

// Mock the api module used by the store
vi.mock("../src/services/api", async () => {
  return {
    default: {
      post: vi.fn(async (url: string, body: any) => {
        if (body.email === "doc@example.com" && body.password === "secret") {
          return {
            data: {
              token:
                "header." +
                Buffer.from(
                  JSON.stringify({ sub: "user-1", role: "ROLE_DOCTOR" })
                ).toString("base64") +
                ".sig",
            },
          };
        }
        return { data: {} };
      }),
    },
    registerAuthHandlers: () => {},
  };
});

describe("useAuthStore", () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    localStorage.clear();
  });

  it("login success - extracts user from token when user not returned", async () => {
    const store = useAuthStore();
    const user = await store.login("doc@example.com", "secret");
    expect(store.token).toBeTruthy();
    expect(store.user).toEqual({ id: "user-1", role: "ROLE_DOCTOR" });
    expect(user).toEqual({ id: "user-1", role: "ROLE_DOCTOR" });
    expect(localStorage.getItem("ma_token")).toBe(store.token);
  });

  it("login failure - throws when token missing", async () => {
    const store = useAuthStore();
    await expect(store.login("bad@example.com", "x")).rejects.toBeTruthy();
  });
});
