import type { Metadata } from "next"
import { CustomersList } from "@/components/customers/customers-list"
import { CustomerFilters } from "@/components/customers/customer-filters"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Customers | UMKM CRM",
  description: "Manage your customers and leads",
}

export default function CustomersPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Customers"
        description="Manage your customers and leads"
        action={{ label: "Add Customer", href: "/customers/new" }}
      />
      <CustomerFilters />
      <CustomersList />
    </div>
  )
}

