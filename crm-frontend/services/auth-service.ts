import type { User } from "@/types/user"

const API_URL = "/api"

export const authService = {
  async login(username: string, password: string): Promise<{ token: string }> {
    // In a real app, this would make an API call
    // const response = await fetch(`${API_URL}/auth/login`, {
    //   method: "POST",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify({ username, password }),
    // });

    // if (!response.ok) {
    //   throw new Error("Login failed");
    // }

    // const data = await response.json();
    // localStorage.setItem("token", data.token);
    // return data;

    // Mock implementation for demo
    return new Promise((resolve) => {
      setTimeout(() => {
        const token = "mock-jwt-token"
        localStorage.setItem("token", token)
        resolve({ token })
      }, 500)
    })
  },

  async logout(): Promise<void> {
    // In a real app, this would make an API call
    // await fetch(`${API_URL}/auth/logout`, {
    //   method: "POST",
    //   headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    // });

    localStorage.removeItem("token")
    return Promise.resolve()
  },

  async refreshToken(): Promise<{ token: string }> {
    // In a real app, this would make an API call
    // const response = await fetch(`${API_URL}/auth/refresh-token`, {
    //   method: "POST",
    //   headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    // });

    // if (!response.ok) {
    //   throw new Error("Token refresh failed");
    // }

    // const data = await response.json();
    // localStorage.setItem("token", data.token);
    // return data;

    // Mock implementation for demo
    return Promise.resolve({ token: "refreshed-mock-token" })
  },

  async getUserInfo(): Promise<User> {
    // In a real app, this would make an API call
    // const response = await fetch(`${API_URL}/auth/user-info`, {
    //   headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    // });

    // if (!response.ok) {
    //   throw new Error("Failed to get user info");
    // }

    // return response.json();

    // Mock implementation for demo
    return Promise.resolve({
      id: "1",
      username: "admin",
      email: "admin@example.com",
      fullName: "Admin User",
      roles: [{ id: "1", name: "ADMIN" }],
      lastLogin: new Date(),
    })
  },

  async requestPasswordReset(email: string): Promise<void> {
    // In a real app, this would make an API call
    // const response = await fetch(`${API_URL}/auth/password/reset-request`, {
    //   method: "POST",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify({ email }),
    // });

    // if (!response.ok) {
    //   throw new Error("Password reset request failed");
    // }

    // Mock implementation for demo
    return Promise.resolve()
  },

  async resetPassword(token: string, password: string): Promise<void> {
    // In a real app, this would make an API call
    // const response = await fetch(`${API_URL}/auth/password/reset`, {
    //   method: "POST",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify({ token, password }),
    // });

    // if (!response.ok) {
    //   throw new Error("Password reset failed");
    // }

    // Mock implementation for demo
    return Promise.resolve()
  },

  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    // In a real app, this would make an API call
    // const response = await fetch(`${API_URL}/auth/password/change`, {
    //   method: "POST",
    //   headers: {
    //     "Content-Type": "application/json",
    //     Authorization: `Bearer ${localStorage.getItem("token")}`,
    //   },
    //   body: JSON.stringify({ currentPassword, newPassword }),
    // });

    // if (!response.ok) {
    //   throw new Error("Password change failed");
    // }

    // Mock implementation for demo
    return Promise.resolve()
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem("token")
  },
}

