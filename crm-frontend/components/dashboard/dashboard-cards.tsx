"use client"

import type React from "react"

import { useEffect, useState } from "react"
import { ArrowDownIcon, ArrowUpIcon, DollarSign, Users, BarChart, CheckCircle } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

interface StatCardProps {
  title: string
  value: string
  description: string
  icon: React.ReactNode
  trend: {
    value: number
    isPositive: boolean
  }
}

function StatCard({ title, value, description, icon, trend }: StatCardProps) {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">{title}</CardTitle>
        <div className="h-4 w-4 text-muted-foreground">{icon}</div>
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{value}</div>
        <p className="text-xs text-muted-foreground flex items-center mt-1">
          {trend.isPositive ? (
            <ArrowUpIcon className="mr-1 h-4 w-4 text-green-500" />
          ) : (
            <ArrowDownIcon className="mr-1 h-4 w-4 text-red-500" />
          )}
          <span className={trend.isPositive ? "text-green-500" : "text-red-500"}>{trend.value}%</span>
          <span className="ml-1 text-muted-foreground">{description}</span>
        </p>
      </CardContent>
    </Card>
  )
}

export function DashboardCards() {
  const [stats, setStats] = useState({
    totalCustomers: { value: "0", trend: { value: 0, isPositive: true } },
    totalRevenue: { value: "$0", trend: { value: 0, isPositive: true } },
    activeDeals: { value: "0", trend: { value: 0, isPositive: true } },
    completedTasks: { value: "0", trend: { value: 0, isPositive: true } },
  })

  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        // In a real app, this would fetch from the API
        // const data = await dashboardService.getSummary();

        // Mock data for demonstration
        setStats({
          totalCustomers: { value: "1,248", trend: { value: 12.5, isPositive: true } },
          totalRevenue: { value: "$45,231.89", trend: { value: 8.2, isPositive: true } },
          activeDeals: { value: "24", trend: { value: 5.1, isPositive: false } },
          completedTasks: { value: "32", trend: { value: 18.7, isPositive: true } },
        })
      } catch (error) {
        console.error("Failed to fetch dashboard data:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [])

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <StatCard
        title="Total Customers"
        value={stats.totalCustomers.value}
        description="from last month"
        icon={<Users className="h-4 w-4" />}
        trend={stats.totalCustomers.trend}
      />
      <StatCard
        title="Total Revenue"
        value={stats.totalRevenue.value}
        description="from last month"
        icon={<DollarSign className="h-4 w-4" />}
        trend={stats.totalRevenue.trend}
      />
      <StatCard
        title="Active Deals"
        value={stats.activeDeals.value}
        description="from last month"
        icon={<BarChart className="h-4 w-4" />}
        trend={stats.activeDeals.trend}
      />
      <StatCard
        title="Completed Tasks"
        value={stats.completedTasks.value}
        description="from last week"
        icon={<CheckCircle className="h-4 w-4" />}
        trend={stats.completedTasks.trend}
      />
    </div>
  )
}

