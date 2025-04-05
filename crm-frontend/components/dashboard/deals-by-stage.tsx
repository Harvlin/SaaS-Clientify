"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip, type TooltipProps } from "recharts"
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { formatCurrency } from "@/lib/utils"

interface StageData {
  name: string
  value: number
  color: string
}

export function DealsByStage() {
  const [data, setData] = useState<StageData[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      try {
        // In a real app, this would fetch from the API
        // const data = await dashboardService.getDealValueByStage();

        // Mock data for demonstration
        setData([
          { name: "Discovery", value: 45000, color: "#94a3b8" },
          { name: "Proposal", value: 85000, color: "#60a5fa" },
          { name: "Negotiation", value: 65000, color: "#3b82f6" },
          { name: "Closing", value: 35000, color: "#2563eb" },
          { name: "Won", value: 120000, color: "#16a34a" },
        ])
      } catch (error) {
        console.error("Failed to fetch deals by stage data:", error)
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
                <p className="text-sm font-medium">{payload[0]?.name}</p>
                <p className="text-sm text-muted-foreground">{formatCurrency(payload[0]?.value || 0, "USD")}</p>
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
        <CardTitle>Deals by Stage</CardTitle>
        <CardDescription>Value distribution across pipeline stages</CardDescription>
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
                <PieChart>
                  <Pie
                    data={data}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={90}
                    paddingAngle={2}
                    dataKey="value"
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                    labelLine={false}
                  >
                    {data.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip content={<CustomTooltip />} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </ChartContainer>
          )}
        </div>
      </CardContent>
    </Card>
  )
}

