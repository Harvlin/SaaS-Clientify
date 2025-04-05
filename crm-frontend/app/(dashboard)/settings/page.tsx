import type { Metadata } from "next"
import { SettingsTabs } from "@/components/settings/settings-tabs"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Settings | UMKM CRM",
  description: "Manage your CRM settings",
}

export default function SettingsPage() {
  return (
    <div className="space-y-6">
      <PageHeader title="Settings" description="Manage your CRM settings and preferences" />
      <SettingsTabs />
    </div>
  )
}

