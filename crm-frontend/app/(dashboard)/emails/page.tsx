import type { Metadata } from "next"
import { EmailsList } from "@/components/emails/emails-list"
import { EmailFilters } from "@/components/emails/email-filters"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Emails | UMKM CRM",
  description: "Manage your email communications",
}

export default function EmailsPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Emails"
        description="Manage your email communications"
        action={{ label: "Compose Email", href: "/emails/compose" }}
      />
      <EmailFilters />
      <EmailsList />
    </div>
  )
}

