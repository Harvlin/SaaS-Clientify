import type { Metadata } from "next"
import { DealsList } from "@/components/deals/deals-list"
import { DealFilters } from "@/components/deals/deal-filters"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Deals | UMKM CRM",
  description: "Manage your sales pipeline and deals",
}

export default function DealsPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Deals"
        description="Manage your sales pipeline and deals"
        action={{ label: "Add Deal", href: "/deals/new" }}
      />
      <DealFilters />
      <DealsList />
    </div>
  )
}

