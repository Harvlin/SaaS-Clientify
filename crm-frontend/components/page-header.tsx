import Link from "next/link"
import { ChevronLeft } from "lucide-react"
import { Button } from "@/components/ui/button"

interface PageHeaderProps {
  title: string
  description?: string
  backHref?: string
  action?: {
    label: string
    href: string
  }
}

export function PageHeader({ title, description, backHref, action }: PageHeaderProps) {
  return (
    <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
      <div className="space-y-1">
        {backHref && (
          <Button variant="ghost" size="sm" asChild className="mb-2 -ml-2 h-8 px-2">
            <Link href={backHref}>
              <ChevronLeft className="mr-1 h-4 w-4" />
              Back
            </Link>
          </Button>
        )}
        <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
        {description && <p className="text-muted-foreground">{description}</p>}
      </div>
      {action && (
        <Button asChild>
          <Link href={action.href}>{action.label}</Link>
        </Button>
      )}
    </div>
  )
}

