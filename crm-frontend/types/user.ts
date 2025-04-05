export interface User {
  id: string
  username: string
  email: string
  fullName?: string
  phoneNumber?: string
  roles: Role[]
  lastLogin?: Date
}

export interface Role {
  id: string
  name: string
  description?: string
}

