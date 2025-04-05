import type { Metadata } from "next"
import { CustomerDetails } from "@/components/customers/customer-details"
import { CustomerTabs } from "@/components/customers/customer-tabs"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Customer Details | UMKM CRM",
  description: "View and manage customer details",
}

export default function CustomerDetailPage({ params }: { params: { id: string } }) {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Customer Details"
        description="View and manage customer information"
        backHref="/customers"
        action={{ label: "Edit Customer", href: `/customers/${params.id}/edit` }}
      />
      <CustomerDetails id={params.id} />
      <CustomerTabs id={params.id} />
    </div>
  )
}

