import type { Metadata } from "next"
import { DealsPipeline } from "@/components/deals/deals-pipeline"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Pipeline | UMKM CRM",
  description: "Visual sales pipeline view",
}

export default function PipelinePage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Pipeline View"
        description="Visual overview of your sales pipeline"
        action={{ label: "Add Deal", href: "/deals/new" }}
      />
      <DealsPipeline />
    </div>
  )
}

