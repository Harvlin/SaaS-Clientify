export enum TaskType {
  CALL = "CALL",
  EMAIL = "EMAIL",
  MEETING = "MEETING",
  FOLLOW_UP = "FOLLOW_UP",
  TASK = "TASK",
  DEADLINE = "DEADLINE",
}

export enum TaskStatus {
  NOT_STARTED = "NOT_STARTED",
  IN_PROGRESS = "IN_PROGRESS",
  COMPLETED = "COMPLETED",
  CANCELLED = "CANCELLED",
  POSTPONED = "POSTPONED",
}

export enum PriorityLevel {
  LOW = "LOW",
  MEDIUM = "MEDIUM",
  HIGH = "HIGH",
  URGENT = "URGENT",
}

export interface Task {
  id: string
  title: string
  description?: string
  type?: TaskType
  dueDate?: Date
  reminderDate?: Date
  status: TaskStatus
  priorityLevel: PriorityLevel
  assignedUser?: {
    id: string
    fullName: string
  }
  customer?: {
    id: string
    firstName: string
    lastName: string
  }
  deal?: {
    id: string
    title: string
  }
  createdAt?: Date
  updatedAt?: Date
  completedAt?: Date
}

