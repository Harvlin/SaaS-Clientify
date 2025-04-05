import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"
import { format, formatDistance } from "date-fns"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatDate(date: Date | string | undefined): string {
  if (!date) return "N/A"

  const dateObj = typeof date === "string" ? new Date(date) : date
  return format(dateObj, "MMM d, yyyy")
}

export function formatDateTime(date: Date | string | undefined): string {
  if (!date) return "N/A"

  const dateObj = typeof date === "string" ? new Date(date) : date
  return format(dateObj, "MMM d, yyyy h:mm a")
}

export function formatRelativeTime(date: Date | string | undefined): string {
  if (!date) return "N/A"

  const dateObj = typeof date === "string" ? new Date(date) : date
  return formatDistance(dateObj, new Date(), { addSuffix: true })
}

export function formatCurrency(amount: number | undefined, currency = "USD"): string {
  if (amount === undefined) return "N/A"

  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency,
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount)
}

export function truncateText(text: string, maxLength: number): string {
  if (text.length <= maxLength) return text
  return `${text.slice(0, maxLength)}...`
}

