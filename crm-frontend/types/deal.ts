export enum DealStatus {
  OPEN = "OPEN",
  WON = "WON",
  LOST = "LOST",
  CANCELLED = "CANCELLED",
  POSTPONED = "POSTPONED",
}

export interface PipelineStage {
  id: string
  name: string
  description?: string
  displayOrder?: number
  defaultProbabilityPercentage?: number
}

export interface Deal {
  id: string
  title: string
  customer: {
    id: string
    firstName: string
    lastName: string
  }
  assignedUser?: {
    id: string
    fullName: string
  }
  pipelineStage: PipelineStage
  valueAmount?: number
  valueCurrency?: string
  expectedCloseDate?: Date
  actualCloseDate?: Date
  status: DealStatus
  probabilityPercentage?: number
  notes?: string
  createdAt?: Date
  updatedAt?: Date
}

