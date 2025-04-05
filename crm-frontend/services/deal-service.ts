// Add this file to ensure the service is properly defined
import { DealStatus } from "@/types/deal"

export const dealService = {
  async getDeals(page = 1, search = "") {
    // Mock implementation for demo
    return [
      // ... deals
    ]
  },

  async getRecentDeals() {
    // Mock implementation for demo
    return [
      {
        id: "1",
        title: "Enterprise Software License",
        customer: { id: "101", firstName: "Acme", lastName: "Corp" },
        valueAmount: 24500,
        valueCurrency: "USD",
        status: DealStatus.OPEN,
        pipelineStage: { id: "1", name: "Proposal" },
        expectedCloseDate: new Date("2023-06-15"),
        probabilityPercentage: 70,
      },
      // ... other deals
    ]
  },
}

