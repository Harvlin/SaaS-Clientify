"use client"

import type React from "react"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Input } from "@/components/ui/input"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import { Skeleton } from "@/components/ui/skeleton"
import { MoreHorizontal, Search } from "lucide-react"
import { formatDate } from "@/lib/utils"
import { type Customer, CustomerStatus } from "@/types/customer"

export function CustomersList() {
  const [customers, setCustomers] = useState<Customer[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [searchQuery, setSearchQuery] = useState("")
  const [selectedCustomers, setSelectedCustomers] = useState<string[]>([])
  const [currentPage, setCurrentPage] = useState(1)
  const [totalPages, setTotalPages] = useState(1)

  useEffect(() => {
    const fetchCustomers = async () => {
      try {
        // In a real app, this would fetch from the API
        // const data = await customerService.getCustomers(currentPage, searchQuery);

        // Mock data for demonstration
        const mockCustomers = [
          {
            id: "1",
            firstName: "Acme",
            lastName: "Corp",
            email: "contact@acmecorp.com",
            phoneNumber: "+1 (555) 123-4567",
            company: "Acme Corporation",
            position: "Client",
            status: CustomerStatus.CUSTOMER,
            createdAt: new Date("2023-01-15"),
          },
          {
            id: "2",
            firstName: "Global",
            lastName: "Industries",
            email: "info@globalindustries.com",
            phoneNumber: "+1 (555) 987-6543",
            company: "Global Industries Ltd",
            position: "Prospect",
            status: CustomerStatus.PROSPECT,
            createdAt: new Date("2023-02-28"),
          },
          {
            id: "3",
            firstName: "Tech",
            lastName: "Solutions",
            email: "sales@techsolutions.com",
            phoneNumber: "+1 (555) 456-7890",
            company: "Tech Solutions Inc",
            position: "Lead",
            status: CustomerStatus.LEAD,
            createdAt: new Date("2023-03-10"),
          },
          {
            id: "4",
            firstName: "Metro",
            lastName: "Services",
            email: "info@metroservices.com",
            phoneNumber: "+1 (555) 789-0123",
            company: "Metro Services Group",
            position: "Client",
            status: CustomerStatus.CUSTOMER,
            createdAt: new Date("2023-04-05"),
          },
          {
            id: "5",
            firstName: "Innovative",
            lastName: "Systems",
            email: "contact@innovativesystems.com",
            phoneNumber: "+1 (555) 234-5678",
            company: "Innovative Systems LLC",
            position: "Former Client",
            status: CustomerStatus.FORMER,
            createdAt: new Date("2023-01-20"),
          },
          {
            id: "6",
            firstName: "Bright",
            lastName: "Future",
            email: "info@brightfuture.org",
            phoneNumber: "+1 (555) 345-6789",
            company: "Bright Future Foundation",
            position: "Lead",
            status: CustomerStatus.LEAD,
            createdAt: new Date("2023-05-12"),
          },
          {
            id: "7",
            firstName: "Summit",
            lastName: "Enterprises",
            email: "contact@summitenterprises.com",
            phoneNumber: "+1 (555) 567-8901",
            company: "Summit Enterprises Inc",
            position: "Prospect",
            status: CustomerStatus.PROSPECT,
            createdAt: new Date("2023-04-18"),
          },
          {
            id: "8",
            firstName: "Horizon",
            lastName: "Group",
            email: "info@horizongroup.com",
            phoneNumber: "+1 (555) 678-9012",
            company: "Horizon Group International",
            position: "Inactive",
            status: CustomerStatus.INACTIVE,
            createdAt: new Date("2023-02-08"),
          },
        ]

        setCustomers(mockCustomers)
        setTotalPages(3) // Mock total pages
      } catch (error) {
        console.error("Failed to fetch customers:", error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchCustomers()
  }, [currentPage, searchQuery])

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setCurrentPage(1) // Reset to first page on new search
  }

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedCustomers(customers.map((customer) => customer.id))
    } else {
      setSelectedCustomers([])
    }
  }

  const handleSelectCustomer = (customerId: string, checked: boolean) => {
    if (checked) {
      setSelectedCustomers([...selectedCustomers, customerId])
    } else {
      setSelectedCustomers(selectedCustomers.filter((id) => id !== customerId))
    }
  }

  const getStatusBadgeVariant = (status: CustomerStatus) => {
    switch (status) {
      case CustomerStatus.CUSTOMER:
        return "success"
      case CustomerStatus.PROSPECT:
        return "default"
      case CustomerStatus.LEAD:
        return "secondary"
      case CustomerStatus.INACTIVE:
        return "outline"
      case CustomerStatus.FORMER:
        return "destructive"
      default:
        return "default"
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-col sm:flex-row gap-4 justify-between">
        <form onSubmit={handleSearch} className="relative w-full sm:max-w-sm">
          <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
          <Input
            type="search"
            placeholder="Search customers..."
            className="w-full pl-8 bg-background"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>

        {selectedCustomers.length > 0 && (
          <div className="flex items-center gap-2">
            <span className="text-sm text-muted-foreground">{selectedCustomers.length} selected</span>
            <Button variant="outline" size="sm">
              Assign
            </Button>
            <Button variant="outline" size="sm">
              Export
            </Button>
          </div>
        )}
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[40px]">
                <Checkbox
                  checked={selectedCustomers.length === customers.length && customers.length > 0}
                  onCheckedChange={handleSelectAll}
                  aria-label="Select all customers"
                />
              </TableHead>
              <TableHead>Customer</TableHead>
              <TableHead className="hidden md:table-cell">Company</TableHead>
              <TableHead className="hidden md:table-cell">Phone</TableHead>
              <TableHead className="hidden md:table-cell">Status</TableHead>
              <TableHead className="hidden md:table-cell">Added</TableHead>
              <TableHead className="w-[60px]"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              Array.from({ length: 8 }).map((_, i) => (
                <TableRow key={i}>
                  <TableCell>
                    <Skeleton className="h-4 w-4" />
                  </TableCell>
                  <TableCell>
                    <div className="space-y-2">
                      <Skeleton className="h-4 w-[200px]" />
                      <Skeleton className="h-4 w-[150px]" />
                    </div>
                  </TableCell>
                  <TableCell className="hidden md:table-cell">
                    <Skeleton className="h-4 w-[150px]" />
                  </TableCell>
                  <TableCell className="hidden md:table-cell">
                    <Skeleton className="h-4 w-[120px]" />
                  </TableCell>
                  <TableCell className="hidden md:table-cell">
                    <Skeleton className="h-4 w-[80px]" />
                  </TableCell>
                  <TableCell className="hidden md:table-cell">
                    <Skeleton className="h-4 w-[100px]" />
                  </TableCell>
                  <TableCell>
                    <Skeleton className="h-8 w-8" />
                  </TableCell>
                </TableRow>
              ))
            ) : customers.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} className="h-24 text-center">
                  No customers found.
                </TableCell>
              </TableRow>
            ) : (
              customers.map((customer) => (
                <TableRow key={customer.id}>
                  <TableCell>
                    <Checkbox
                      checked={selectedCustomers.includes(customer.id)}
                      onCheckedChange={(checked) => handleSelectCustomer(customer.id, !!checked)}
                      aria-label={`Select ${customer.firstName} ${customer.lastName}`}
                    />
                  </TableCell>
                  <TableCell>
                    <div className="font-medium">
                      <Link href={`/customers/${customer.id}`} className="hover:underline">
                        {customer.firstName} {customer.lastName}
                      </Link>
                    </div>
                    <div className="text-sm text-muted-foreground">{customer.email}</div>
                  </TableCell>
                  <TableCell className="hidden md:table-cell">{customer.company}</TableCell>
                  <TableCell className="hidden md:table-cell">{customer.phoneNumber}</TableCell>
                  <TableCell className="hidden md:table-cell">
                    <Badge variant={getStatusBadgeVariant(customer.status)}>{customer.status}</Badge>
                  </TableCell>
                  <TableCell className="hidden md:table-cell">{formatDate(customer.createdAt)}</TableCell>
                  <TableCell>
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="icon">
                          <MoreHorizontal className="h-4 w-4" />
                          <span className="sr-only">Open menu</span>
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuLabel>Actions</DropdownMenuLabel>
                        <DropdownMenuItem asChild>
                          <Link href={`/customers/${customer.id}`}>View details</Link>
                        </DropdownMenuItem>
                        <DropdownMenuItem asChild>
                          <Link href={`/customers/${customer.id}/edit`}>Edit customer</Link>
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem asChild>
                          <Link href={`/deals/new?customerId=${customer.id}`}>Add deal</Link>
                        </DropdownMenuItem>
                        <DropdownMenuItem asChild>
                          <Link href={`/tasks/new?customerId=${customer.id}`}>Add task</Link>
                        </DropdownMenuItem>
                        <DropdownMenuItem asChild>
                          <Link href={`/emails/compose?customerId=${customer.id}`}>Send email</Link>
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem className="text-destructive">Delete customer</DropdownMenuItem>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      <Pagination>
        <PaginationContent>
          <PaginationItem>
            <PaginationPrevious
              href="#"
              onClick={(e) => {
                e.preventDefault()
                if (currentPage > 1) setCurrentPage(currentPage - 1)
              }}
              className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
            />
          </PaginationItem>
          {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
            <PaginationItem key={page}>
              <PaginationLink
                href="#"
                onClick={(e) => {
                  e.preventDefault()
                  setCurrentPage(page)
                }}
                isActive={currentPage === page}
              >
                {page}
              </PaginationLink>
            </PaginationItem>
          ))}
          <PaginationItem>
            <PaginationNext
              href="#"
              onClick={(e) => {
                e.preventDefault()
                if (currentPage < totalPages) setCurrentPage(currentPage + 1)
              }}
              className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
            />
          </PaginationItem>
        </PaginationContent>
      </Pagination>
    </div>
  )
}

