"use client"

import type React from "react"

import { createContext, useContext, useEffect, useState } from "react"
import { useRouter, usePathname } from "next/navigation"
import { authService } from "@/services/auth-service"
import type { User } from "@/types/user"

interface AuthContextType {
  user: User | null
  isLoading: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => Promise<void>
  refreshToken: () => Promise<void>
}

// Initialize with default values to prevent null/undefined errors
export const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: true,
  login: async () => {},
  logout: async () => {},
  refreshToken: async () => {},
})

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()
  const pathname = usePathname()

  useEffect(() => {
    const checkAuth = async () => {
      try {
        // Only check auth if we're not on auth pages
        if (!pathname.includes("/login") && !pathname.includes("/forgot-password")) {
          const userData = await authService.getUserInfo()
          setUser(userData)
        }
      } catch (error) {
        setUser(null)
        if (!pathname.includes("/login") && !pathname.includes("/forgot-password")) {
          router.push("/login")
        }
      } finally {
        setIsLoading(false)
      }
    }

    checkAuth()
  }, [pathname, router])

  const login = async (username: string, password: string) => {
    setIsLoading(true)
    try {
      await authService.login(username, password)
      const userData = await authService.getUserInfo()
      setUser(userData)
    } finally {
      setIsLoading(false)
    }
  }

  const logout = async () => {
    setIsLoading(true)
    try {
      await authService.logout()
      setUser(null)
      router.push("/login")
    } finally {
      setIsLoading(false)
    }
  }

  const refreshToken = async () => {
    try {
      await authService.refreshToken()
    } catch (error) {
      setUser(null)
      router.push("/login")
    }
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, logout, refreshToken }}>{children}</AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}

