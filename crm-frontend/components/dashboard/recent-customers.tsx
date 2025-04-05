"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { Skeleton } from "@/components/ui/skeleton"
import { formatDate } from "@/lib/utils"
import { type Customer, CustomerStatus } from "@/types/customer"

export function RecentCustomers() {
  const [customers, setCustomers] = useState<Customer[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        // In a real app, this would fetch from the API
        // const data = await customerService.getRecentCustomers();

        // Mock data for demonstration
        setCustomers([
          {
            id: "101",
            firstName: "Acme",
            lastName: "Corp",
            email: "contact@acmecorp.com",
            company: "Acme Corporation",
            status: CustomerStatus.CUSTOMER,
            createdAt: new Date("2023-05-28"),
          },
          {
            id: "102",
            firstName: "Global",
            lastName: "Industries",
            email: "info@globalindustries.com",
            company: "Global Industries Ltd",
            status: CustomerStatus.LEAD,
            createdAt: new Date("2023-06-02"),
          },
          {
            id: "103",
            firstName: "Tech",
            lastName: "Solutions",
            email: "sales@techsolutions.com",
            company: "Tech Solutions Inc",
            status: CustomerStatus.PROSPECT,
            createdAt: new Date("2023-06-05"),
          },
        ])
      } catch (error) {
        console.error("Failed to fetch recent customers:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchCustomers()
  }, [])

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <div>
          <CardTitle>Recent Customers</CardTitle>
          <CardDescription>Latest additions to your CRM</CardDescription>
        </div>
        <Button asChild variant="outline" size="sm">
          <Link href="/customers">View All</Link>
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-4">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="flex items-center space-x-4">
                <Skeleton className="h-10 w-10 rounded-full" />
                <div className="space-y-2">
                  <Skeleton className="h-4 w-[200px]" />
                  <Skeleton className="h-4 w-[160px]" />
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="space-y-4">
            {customers.map((customer) => (
              <div key={customer.id} className="flex items-center space-x-4">
                <Avatar>
                  <AvatarFallback>
                    {customer.firstName.charAt(0)}
                    {customer.lastName.charAt(0)}
                  </AvatarFallback>
                </Avatar>
                <div className="space-y-1">
                  <Link href={`/customers/${customer.id}`} className="font-medium hover:underline">
                    {customer.firstName} {customer.lastName}
                  </Link>
                  <p className="text-sm text-muted-foreground">
                    {customer.company} · Added {formatDate(customer.createdAt)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  )
}

