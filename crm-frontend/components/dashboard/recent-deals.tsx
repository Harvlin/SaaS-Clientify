"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { formatCurrency, formatDate } from "@/lib/utils"
import { type Deal, DealStatus } from "@/types/deal"

export function RecentDeals() {
  const [deals, setDeals] = useState<Deal[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchDeals = async () => {
      try {
        // In a real app, this would fetch from the API
        // const data = await dealService.getRecentDeals();

        // Mock data for demonstration
        setDeals([
          {
            id: "1",
            title: "Enterprise Software License",
            customer: { id: "101", firstName: "Acme", lastName: "Corp" },
            valueAmount: 24500,
            valueCurrency: "USD",
            status: DealStatus.OPEN,
            pipelineStage: { id: "1", name: "Proposal" },
            expectedCloseDate: new Date("2023-06-15"),
            probabilityPercentage: 70,
          },
          {
            id: "2",
            title: "Consulting Services Package",
            customer: { id: "102", firstName: "Global", lastName: "Industries" },
            valueAmount: 12800,
            valueCurrency: "USD",
            status: DealStatus.WON,
            pipelineStage: { id: "5", name: "Closed Won" },
            expectedCloseDate: new Date("2023-05-28"),
            probabilityPercentage: 100,
          },
          {
            id: "3",
            title: "Hardware Upgrade",
            customer: { id: "103", firstName: "Tech", lastName: "Solutions" },
            valueAmount: 8750,
            valueCurrency: "USD",
            status: DealStatus.OPEN,
            pipelineStage: { id: "3", name: "Negotiation" },
            expectedCloseDate: new Date("2023-06-22"),
            probabilityPercentage: 60,
          },
          {
            id: "4",
            title: "Annual Support Contract",
            customer: { id: "104", firstName: "Metro", lastName: "Services" },
            valueAmount: 18200,
            valueCurrency: "USD",
            status: DealStatus.OPEN,
            pipelineStage: { id: "2", name: "Discovery" },
            expectedCloseDate: new Date("2023-07-10"),
            probabilityPercentage: 40,
          },
          {
            id: "5",
            title: "Cloud Migration Project",
            customer: { id: "105", firstName: "Innovative", lastName: "Systems" },
            valueAmount: 32000,
            valueCurrency: "USD",
            status: DealStatus.LOST,
            pipelineStage: { id: "6", name: "Closed Lost" },
            expectedCloseDate: new Date("2023-05-15"),
            probabilityPercentage: 0,
          },
        ])
      } catch (error) {
        console.error("Failed to fetch recent deals:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchDeals()
  }, [])

  const getStatusBadgeVariant = (status: DealStatus) => {
    switch (status) {
      case DealStatus.WON:
        return "success"
      case DealStatus.LOST:
        return "destructive"
      case DealStatus.CANCELLED:
        return "outline"
      case DealStatus.POSTPONED:
        return "secondary"
      default:
        return "default"
    }
  }

  return (
    <Card className="col-span-2">
      <CardHeader className="flex flex-row items-center justify-between">
        <div>
          <CardTitle>Recent Deals</CardTitle>
          <CardDescription>Overview of your latest deals</CardDescription>
        </div>
        <Button asChild variant="outline" size="sm">
          <Link href="/deals">View All</Link>
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-2">
            {Array.from({ length: 5 }).map((_, i) => (
              <div key={i} className="flex items-center space-x-4">
                <Skeleton className="h-12 w-full" />
              </div>
            ))}
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Deal</TableHead>
                <TableHead>Customer</TableHead>
                <TableHead>Stage</TableHead>
                <TableHead>Close Date</TableHead>
                <TableHead>Value</TableHead>
                <TableHead>Status</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {deals.map((deal) => (
                <TableRow key={deal.id}>
                  <TableCell className="font-medium">
                    <Link href={`/deals/${deal.id}`} className="hover:underline">
                      {deal.title}
                    </Link>
                  </TableCell>
                  <TableCell>
                    <Link href={`/customers/${deal.customer.id}`} className="hover:underline">
                      {`${deal.customer.firstName} ${deal.customer.lastName}`}
                    </Link>
                  </TableCell>
                  <TableCell>{deal.pipelineStage.name}</TableCell>
                  <TableCell>{formatDate(deal.expectedCloseDate)}</TableCell>
                  <TableCell>{formatCurrency(deal.valueAmount, deal.valueCurrency)}</TableCell>
                  <TableCell>
                    <Badge variant={getStatusBadgeVariant(deal.status)}>{deal.status}</Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>
  )
}

