import type { Metadata } from "next"
import { ReportsList } from "@/components/reports/reports-list"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Reports | UMKM CRM",
  description: "Generate and view reports",
}

export default function ReportsPage() {
  return (
    <div className="space-y-6">
      <PageHeader title="Reports" description="Generate and view reports for your business" />
      <ReportsList />
    </div>
  )
}

