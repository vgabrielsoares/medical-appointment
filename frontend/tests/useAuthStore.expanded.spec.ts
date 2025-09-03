import { beforeEach, describe, expect, it, vi } from "vitest";
import { setActivePinia, createPinia } from "pinia";

// Mock mais detalhado do api
vi.mock("../src/services/api", () => ({
  default: {
    post: vi.fn(),
  },
  registerAuthHandlers: () => {},
}));

import { useAuthStore } from "../src/stores/auth";
import api from "../src/services/api";

const mockApiPost = api.post as any;

describe("useAuthStore - expanded tests", () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    localStorage.clear();
    vi.clearAllMocks();
  });

  describe("login scenarios", () => {
    it("login success with backend user object", async () => {
      const expectedUser = {
        id: "user-123",
        role: "ROLE_DOCTOR",
        name: "Dr. João Silva",
        email: "joao@example.com",
      };

      mockApiPost.mockResolvedValue({
        data: {
          token: "valid.jwt.token",
          user: expectedUser,
        },
      });

      const store = useAuthStore();
      const user = await store.login("joao@example.com", "password");

      expect(store.token).toBe("valid.jwt.token");
      expect(store.user).toEqual(expectedUser);
      expect(user).toEqual(expectedUser);
      expect(localStorage.getItem("ma_token")).toBe("valid.jwt.token");
      expect(localStorage.getItem("ma_user")).toBe(
        JSON.stringify(expectedUser)
      );
    });

    it("login success with JWT-only response (extracts from token)", async () => {
      const tokenPayload = { sub: "user-456", role: "ROLE_PATIENT" };
      const base64Payload = Buffer.from(JSON.stringify(tokenPayload)).toString(
        "base64"
      );
      const token = `header.${base64Payload}.signature`;

      mockApiPost.mockResolvedValue({
        data: { token },
      });

      const store = useAuthStore();
      const user = await store.login("patient@example.com", "password");

      expect(store.token).toBe(token);
      expect(store.user).toEqual({ id: "user-456", role: "ROLE_PATIENT" });
      expect(user).toEqual({ id: "user-456", role: "ROLE_PATIENT" });
    });

    it("login with malformed JWT falls back gracefully", async () => {
      const malformedToken = "invalid.jwt";

      mockApiPost.mockResolvedValue({
        data: { token: malformedToken },
      });

      const store = useAuthStore();
      const user = await store.login("test@example.com", "password");

      expect(store.token).toBe(malformedToken);
      expect(store.user).toBeNull();
      expect(user).toBeNull();
    });

    it("login failure without token throws error", async () => {
      mockApiPost.mockResolvedValue({
        data: { message: "Invalid credentials" },
      });

      const store = useAuthStore();

      await expect(
        store.login("wrong@example.com", "wrongpass")
      ).rejects.toThrow("invalid auth response");
    });

    it("login network error propagates", async () => {
      mockApiPost.mockRejectedValue(new Error("Network error"));

      const store = useAuthStore();

      await expect(store.login("test@example.com", "password")).rejects.toThrow(
        "Network error"
      );
    });

    it("login with 401 error", async () => {
      const error = {
        response: { status: 401, data: { message: "Invalid credentials" } },
      };
      mockApiPost.mockRejectedValue(error);

      const store = useAuthStore();

      await expect(store.login("test@example.com", "password")).rejects.toBe(
        error
      );
    });
  });

  describe("state management", () => {
    it("initializes from localStorage if available", () => {
      const savedToken = "saved.jwt.token";
      const savedUser = { id: "user-789", role: "ROLE_DOCTOR" };

      localStorage.setItem("ma_token", savedToken);
      localStorage.setItem("ma_user", JSON.stringify(savedUser));

      setActivePinia(createPinia());
      const store = useAuthStore();

      expect(store.token).toBe(savedToken);
      expect(store.user).toEqual(savedUser);
    });

    it("handles corrupted localStorage user data", () => {
      localStorage.setItem("ma_token", "valid.token");
      localStorage.setItem("ma_user", "{invalid json}");

      setActivePinia(createPinia());
      const store = useAuthStore();

      expect(store.token).toBe("valid.token");
      expect(store.user).toBeNull();
    });

    it("logout clears all state and localStorage", () => {
      const store = useAuthStore();

      store.setToken("test.token");
      store.setUser({ id: "user-1", role: "ROLE_PATIENT" });

      expect(store.isAuthenticated).toBe(true);
      expect(localStorage.getItem("ma_token")).toBe("test.token");

      // Logout
      store.logout();

      expect(store.token).toBe("");
      expect(store.user).toBeNull();
      expect(store.isAuthenticated).toBe(false);
      expect(localStorage.getItem("ma_token")).toBeNull();
      expect(localStorage.getItem("ma_user")).toBeNull();
    });

    it("setToken updates state and localStorage", () => {
      const store = useAuthStore();
      const newToken = "new.jwt.token";

      store.setToken(newToken);

      expect(store.token).toBe(newToken);
      expect(localStorage.getItem("ma_token")).toBe(newToken);
    });

    it("setToken with empty string clears localStorage", () => {
      const store = useAuthStore();

      localStorage.setItem("ma_token", "old.token");
      store.setToken("");

      expect(store.token).toBe("");
      expect(localStorage.getItem("ma_token")).toBeNull();
    });

    it("setUser updates state and localStorage", () => {
      const store = useAuthStore();
      const newUser = { id: "user-new", role: "ROLE_DOCTOR", name: "Dr. Test" };

      store.setUser(newUser);

      expect(store.user).toEqual(newUser);
      expect(localStorage.getItem("ma_user")).toBe(JSON.stringify(newUser));
    });

    it("setUser with null clears localStorage", () => {
      const store = useAuthStore();

      localStorage.setItem(
        "ma_user",
        JSON.stringify({ id: "old-user", role: "ROLE_PATIENT" })
      );
      store.setUser(null);

      expect(store.user).toBeNull();
      expect(localStorage.getItem("ma_user")).toBeNull();
    });
  });

  describe("getters", () => {
    it("isAuthenticated returns false for empty token", () => {
      const store = useAuthStore();
      store.setToken("");

      expect(store.isAuthenticated).toBe(false);
    });

    it("isAuthenticated returns true for non-empty token", () => {
      const store = useAuthStore();
      store.setToken("any.token");

      expect(store.isAuthenticated).toBe(true);
    });

    it("isDoctor returns true for ROLE_DOCTOR", () => {
      const store = useAuthStore();
      store.setUser({ id: "user-1", role: "ROLE_DOCTOR" });

      expect(store.isDoctor).toBe(true);
      expect(store.isPatient).toBe(false);
    });

    it("isPatient returns true for ROLE_PATIENT", () => {
      const store = useAuthStore();
      store.setUser({ id: "user-1", role: "ROLE_PATIENT" });

      expect(store.isPatient).toBe(true);
      expect(store.isDoctor).toBe(false);
    });

    it("displayName returns name when available", () => {
      const store = useAuthStore();
      store.setUser({ id: "user-1", role: "ROLE_DOCTOR", name: "Dr. João" });

      expect(store.displayName).toBe("Dr. João");
    });

    it("displayName falls back to id when name not available", () => {
      const store = useAuthStore();
      store.setUser({ id: "user-123", role: "ROLE_PATIENT" });

      expect(store.displayName).toBe("user-123");
    });

    it("displayName falls back to 'Usuário' when user is null", () => {
      const store = useAuthStore();
      store.setUser(null);

      expect(store.displayName).toBe("Usuário");
    });

    it("getUser returns current user", () => {
      const store = useAuthStore();
      const user = { id: "user-1", role: "ROLE_DOCTOR" };
      store.setUser(user);

      expect(store.getUser).toEqual(user);
    });
  });

  describe("getToken method", () => {
    it("returns current token from state", () => {
      const store = useAuthStore();
      store.setToken("state.token");

      expect(store.getToken()).toBe("state.token");
    });

    it("returns token from localStorage when state is empty", () => {
      localStorage.setItem("ma_token", "localStorage.token");

      const store = useAuthStore();
      store.token = "";

      expect(store.getToken()).toBe("localStorage.token");
    });

    it("returns empty string when no token anywhere", () => {
      const store = useAuthStore();
      store.token = "";
      localStorage.removeItem("ma_token");

      expect(store.getToken()).toBe("");
    });
  });

  describe("edge cases", () => {
    it("handles multiple rapid login attempts", async () => {
      mockApiPost.mockResolvedValue({
        data: {
          token: "test.token",
          user: { id: "user-1", role: "ROLE_PATIENT" },
        },
      });

      const store = useAuthStore();

      const promise1 = store.login("test@example.com", "password");
      const promise2 = store.login("test@example.com", "password");

      const [result1, result2] = await Promise.all([promise1, promise2]);

      expect(result1).toEqual({ id: "user-1", role: "ROLE_PATIENT" });
      expect(result2).toEqual({ id: "user-1", role: "ROLE_PATIENT" });
      expect(mockApiPost).toHaveBeenCalledTimes(2);
    });

    it("handles empty string responses gracefully", async () => {
      mockApiPost.mockResolvedValue({
        data: { token: "", user: null },
      });

      const store = useAuthStore();

      await expect(store.login("test@example.com", "password")).rejects.toThrow(
        "invalid auth response"
      );
    });
  });
});
