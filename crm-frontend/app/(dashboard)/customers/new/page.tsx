import type { Metadata } from "next"
import { CustomerForm } from "@/components/customers/customer-form"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Add Customer | UMKM CRM",
  description: "Add a new customer to your CRM",
}

export default function NewCustomerPage() {
  return (
    <div className="space-y-6">
      <PageHeader title="Add Customer" description="Add a new customer or lead to your CRM" backHref="/customers" />
      <CustomerForm />
    </div>
  )
}

