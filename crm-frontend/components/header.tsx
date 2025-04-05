"use client"

import type React from "react"

import { useState } from "react"
import { usePathname } from "next/navigation"
import { Bell, Search } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { ModeToggle } from "@/components/mode-toggle"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import { Badge } from "@/components/ui/badge"
import { useAuth } from "@/hooks/use-auth"

export function Header() {
  const [searchQuery, setSearchQuery] = useState("")
  const pathname = usePathname()
  const { user } = useAuth()

  // Mock notifications for demo
  const notifications = [
    { id: 1, title: "New customer added", time: "5 minutes ago" },
    { id: 2, title: "Task due today", time: "1 hour ago" },
    { id: 3, title: "Deal moved to Won", time: "3 hours ago" },
  ]

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    // Implement search functionality
    console.log("Searching for:", searchQuery)
  }

  return (
    <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="flex h-14 items-center px-4 md:px-6">
        <form onSubmit={handleSearch} className="relative w-full max-w-sm mr-auto">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            type="search"
            placeholder="Search..."
            className="w-full pl-8 bg-background"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>

        <div className="flex items-center gap-2 md:gap-4">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" size="icon" className="relative">
                <Bell className="h-4 w-4" />
                <Badge className="absolute -top-1 -right-1 h-4 w-4 p-0 flex items-center justify-center text-[10px]">
                  {notifications.length}
                </Badge>
                <span className="sr-only">Notifications</span>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-80">
              <div className="flex items-center justify-between px-4 py-2 font-medium">
                <span>Notifications</span>
                <Button variant="ghost" size="sm" className="h-auto p-0 text-xs">
                  Mark all as read
                </Button>
              </div>
              {notifications.map((notification) => (
                <DropdownMenuItem key={notification.id} className="flex flex-col items-start p-4">
                  <div className="font-medium">{notification.title}</div>
                  <div className="text-xs text-muted-foreground">{notification.time}</div>
                </DropdownMenuItem>
              ))}
              <DropdownMenuItem className="justify-center font-medium">View all notifications</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <ModeToggle />
        </div>
      </div>
    </header>
  )
}

