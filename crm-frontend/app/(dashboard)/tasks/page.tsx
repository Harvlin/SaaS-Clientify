import type { Metadata } from "next"
import { TasksList } from "@/components/tasks/tasks-list"
import { TaskFilters } from "@/components/tasks/task-filters"
import { PageHeader } from "@/components/page-header"

export const metadata: Metadata = {
  title: "Tasks | UMKM CRM",
  description: "Manage your tasks and follow-ups",
}

export default function TasksPage() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Tasks"
        description="Manage your tasks and follow-ups"
        action={{ label: "Add Task", href: "/tasks/new" }}
      />
      <TaskFilters />
      <TasksList />
    </div>
  )
}

