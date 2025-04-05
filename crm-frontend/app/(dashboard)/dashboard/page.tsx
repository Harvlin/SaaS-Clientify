import type { Metadata } from "next"
import { DashboardHeader } from "@/components/dashboard/dashboard-header"
import { DashboardCards } from "@/components/dashboard/dashboard-cards"
import { RecentDeals } from "@/components/dashboard/recent-deals"
import { RecentCustomers } from "@/components/dashboard/recent-customers"
import { UpcomingTasks } from "@/components/dashboard/upcoming-tasks"
import { SalesForecast } from "@/components/dashboard/sales-forecast"
import { DealsByStage } from "@/components/dashboard/deals-by-stage"

export const metadata: Metadata = {
  title: "Dashboard | UMKM CRM",
  description: "CRM Dashboard for Small and Medium Enterprises",
}

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <DashboardHeader />
      <DashboardCards />

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <SalesForecast />
        <DealsByStage />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <RecentDeals />
        </div>
        <div className="space-y-6">
          <UpcomingTasks />
          <RecentCustomers />
        </div>
      </div>
    </div>
  )
}

