import type { Metadata } from "next"
import { TemplatesList } from "@/components/templates/templates-list"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Email Templates | UMKM CRM",
  description: "Manage your email templates",
}

export default function TemplatesPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Email Templates"
        description="Manage your email templates for consistent communication"
        action={{ label: "Create Template", href: "/templates/new" }}
      />
      <TemplatesList />
    </div>
  )
}

