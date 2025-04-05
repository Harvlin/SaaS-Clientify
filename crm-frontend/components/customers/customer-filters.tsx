"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Separator } from "@/components/ui/separator"
import { ChevronDown, Filter, X } from "lucide-react"
import { CustomerStatus } from "@/types/customer"

export function CustomerFilters() {
  const [statusFilters, setStatusFilters] = useState<CustomerStatus[]>([])
  const [showFilters, setShowFilters] = useState(false)

  const handleStatusToggle = (status: CustomerStatus) => {
    if (statusFilters.includes(status)) {
      setStatusFilters(statusFilters.filter((s) => s !== status))
    } else {
      setStatusFilters([...statusFilters, status])
    }
  }

  const clearFilters = () => {
    setStatusFilters([])
  }

  return (
    <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4 pb-4">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="outline" size="sm" className="h-8 gap-1">
            <Filter className="h-3.5 w-3.5" />
            <span>Status</span>
            <ChevronDown className="h-3.5 w-3.5" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="start" className="w-48">
          <DropdownMenuLabel>Filter by status</DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuCheckboxItem
            checked={statusFilters.includes(CustomerStatus.LEAD)}
            onCheckedChange={() => handleStatusToggle(CustomerStatus.LEAD)}
          >
            Lead
          </DropdownMenuCheckboxItem>
          <DropdownMenuCheckboxItem
            checked={statusFilters.includes(CustomerStatus.PROSPECT)}
            onCheckedChange={() => handleStatusToggle(CustomerStatus.PROSPECT)}
          >
            Prospect
          </DropdownMenuCheckboxItem>
          <DropdownMenuCheckboxItem
            checked={statusFilters.includes(CustomerStatus.CUSTOMER)}
            onCheckedChange={() => handleStatusToggle(CustomerStatus.CUSTOMER)}
          >
            Customer
          </DropdownMenuCheckboxItem>
          <DropdownMenuCheckboxItem
            checked={statusFilters.includes(CustomerStatus.INACTIVE)}
            onCheckedChange={() => handleStatusToggle(CustomerStatus.INACTIVE)}
          >
            Inactive
          </DropdownMenuCheckboxItem>
          <DropdownMenuCheckboxItem
            checked={statusFilters.includes(CustomerStatus.FORMER)}
            onCheckedChange={() => handleStatusToggle(CustomerStatus.FORMER)}
          >
            Former
          </DropdownMenuCheckboxItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <Popover open={showFilters} onOpenChange={setShowFilters}>
        <PopoverTrigger asChild>
          <Button variant="outline" size="sm" className="h-8 gap-1">
            <Filter className="h-3.5 w-3.5" />
            <span>More Filters</span>
            <ChevronDown className="h-3.5 w-3.5" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-80" align="start">
          <div className="grid gap-4">
            <div className="space-y-2">
              <h4 className="font-medium leading-none">Advanced Filters</h4>
              <p className="text-sm text-muted-foreground">Filter customers by additional criteria</p>
            </div>
            <Separator />
            <div className="grid gap-2">
              <div className="grid grid-cols-3 items-center gap-4">
                <Label htmlFor="source">Source</Label>
                <Input id="source" placeholder="Any source" className="col-span-2 h-8" />
              </div>
              <div className="grid grid-cols-3 items-center gap-4">
                <Label htmlFor="city">City</Label>
                <Input id="city" placeholder="Any city" className="col-span-2 h-8" />
              </div>
              <div className="grid grid-cols-3 items-center gap-4">
                <Label htmlFor="country">Country</Label>
                <Input id="country" placeholder="Any country" className="col-span-2 h-8" />
              </div>
            </div>
            <div className="flex justify-between">
              <Button variant="ghost" size="sm" onClick={clearFilters}>
                Clear filters
              </Button>
              <Button size="sm" onClick={() => setShowFilters(false)}>
                Apply filters
              </Button>
            </div>
          </div>
        </PopoverContent>
      </Popover>

      {statusFilters.length > 0 && (
        <div className="flex flex-wrap gap-2 mt-2 sm:mt-0">
          {statusFilters.map((status) => (
            <div
              key={status}
              className="flex items-center gap-1 bg-secondary text-secondary-foreground px-2 py-1 rounded-md text-xs"
            >
              <span>{status}</span>
              <Button variant="ghost" size="icon" className="h-4 w-4 p-0" onClick={() => handleStatusToggle(status)}>
                <X className="h-3 w-3" />
                <span className="sr-only">Remove {status} filter</span>
              </Button>
            </div>
          ))}

          <Button variant="ghost" size="sm" className="h-6 px-2 text-xs" onClick={clearFilters}>
            Clear all
          </Button>
        </div>
      )}
    </div>
  )
}

