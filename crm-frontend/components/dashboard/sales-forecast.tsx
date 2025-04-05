"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, type TooltipProps } from "recharts"
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { formatCurrency } from "@/lib/utils"

interface ForecastData {
  month: string
  forecast: number
  actual: number
}

export function SalesForecast() {
  const [data, setData] = useState<ForecastData[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        // In a real app, this would fetch from the API
        // const data = await dashboardService.getSalesForecast();

        // Mock data for demonstration
        setData([
          { month: "Jan", forecast: 65000, actual: 68000 },
          { month: "Feb", forecast: 59000, actual: 62500 },
          { month: "Mar", forecast: 80000, actual: 79000 },
          { month: "Apr", forecast: 81000, actual: 85000 },
          { month: "May", forecast: 56000, actual: 61000 },
          { month: "Jun", forecast: 55000, actual: 58000 },
          { month: "Jul", forecast: 40000, actual: 0 },
          { month: "Aug", forecast: 45000, actual: 0 },
          { month: "Sep", forecast: 60000, actual: 0 },
          { month: "Oct", forecast: 70000, actual: 0 },
          { month: "Nov", forecast: 80000, actual: 0 },
          { month: "Dec", forecast: 90000, actual: 0 },
        ])
      } catch (error) {
        console.error("Failed to fetch sales forecast data:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchData()
  }, [])

  const CustomTooltip = ({ active, payload }: TooltipProps<number, string>) => {
    if (active && payload && payload.length) {
      return (
        <ChartTooltip>
          <ChartTooltipContent
            content={
              <div className="space-y-1">
                <p className="text-sm font-medium">{payload[0]?.payload.month}</p>
                {payload[0]?.value !== undefined && (
                  <p className="text-sm text-muted-foreground">Forecast: {formatCurrency(payload[0].value, "USD")}</p>
                )}
                {payload[1]?.value !== undefined && payload[1].value > 0 && (
                  <p className="text-sm text-muted-foreground">Actual: {formatCurrency(payload[1].value, "USD")}</p>
                )}
              </div>
            }
          />
        </ChartTooltip>
      )
    }
    return null
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Sales Forecast</CardTitle>
        <CardDescription>Projected vs actual sales for the year</CardDescription>
      </CardHeader>
      <CardContent>
        <div className="h-[300px]">
          {isLoading ? (
            <div className="flex items-center justify-center h-full">
              <p className="text-muted-foreground">Loading chart data...</p>
            </div>
          ) : (
            <ChartContainer>
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={data} margin={{ top: 10, right: 10, left: 10, bottom: 20 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} />
                  <XAxis dataKey="month" />
                  <YAxis tickFormatter={(value) => `$${(value / 1000).toFixed(0)}k`} width={60} />
                  <Tooltip content={<CustomTooltip />} />
                  <Bar dataKey="forecast" fill="#93c5fd" radius={[4, 4, 0, 0]} />
                  <Bar dataKey="actual" fill="#3b82f6" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </ChartContainer>
          )}
        </div>
      </CardContent>
    </Card>
  )
}

