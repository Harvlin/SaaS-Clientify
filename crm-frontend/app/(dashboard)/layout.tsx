import type React from "react"
import { AppSidebar } from "@/components/app-sidebar"
import { SidebarInset } from "@/components/ui/sidebar"
import { Header } from "@/components/header"
import { LoadingScreen } from "@/components/loading-screen"
import { useAuth } from "@/components/auth-provider"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <div className="flex h-screen">
      <AppSidebar />
      <SidebarInset>
        <div className="flex flex-col h-full">
          <Header />
          <main className="flex-1 overflow-auto p-4 md:p-6">
            <DashboardContent>{children}</DashboardContent>
          </main>
        </div>
      </SidebarInset>
    </div>
  )
}

// Client component to handle loading state
function DashboardContent({ children }: { children: React.ReactNode }) {
  "use client"

  const { isLoading } = useAuth()

  if (isLoading) {
    return <LoadingScreen />
  }

  return <>{children}</>
}

