"use client"

import { useState } from "react"
import { CalendarIcon } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { cn } from "@/lib/utils"
import { format } from "date-fns"
import { useAuth } from "@/components/auth-provider"

export function DashboardHeader() {
  const { user } = useAuth()
  const [date, setDate] = useState<Date | undefined>(new Date())

  // Get time of day for greeting
  const hours = new Date().getHours()
  let greeting = "Good evening"
  if (hours < 12) greeting = "Good morning"
  else if (hours < 18) greeting = "Good afternoon"

  // Safely access user properties
  const firstName = user?.fullName?.split(" ")[0] || "User"

  return (
    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div className="space-y-1">
        <h1 className="text-2xl font-bold tracking-tight">
          {greeting}, {firstName}
        </h1>
        <p className="text-muted-foreground">Here's what's happening with your business today</p>
      </div>
      <div className="flex items-center gap-2">
        <Popover>
          <PopoverTrigger asChild>
            <Button
              variant="outline"
              className={cn("justify-start text-left font-normal w-[240px]", !date && "text-muted-foreground")}
            >
              <CalendarIcon className="mr-2 h-4 w-4" />
              {date ? format(date, "PPP") : <span>Pick a date</span>}
            </Button>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0" align="end">
            <Calendar mode="single" selected={date} onSelect={setDate} initialFocus />
          </PopoverContent>
        </Popover>
      </div>
    </div>
  )
}

