export enum CustomerStatus {
  LEAD = "LEAD",
  PROSPECT = "PROSPECT",
  CUSTOMER = "CUSTOMER",
  INACTIVE = "INACTIVE",
  FORMER = "FORMER",
}

export interface Customer {
  id: string
  firstName: string
  lastName: string
  email: string
  phoneNumber?: string
  company?: string
  position?: string
  address?: string
  city?: string
  postalCode?: string
  country?: string
  status: CustomerStatus
  source?: string
  notes?: string
  createdAt: Date
  updatedAt?: Date
  assignedUser?: {
    id: string
    fullName: string
  }
}

