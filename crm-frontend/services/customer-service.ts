// Add this file to ensure the service is properly defined
import { CustomerStatus } from "@/types/customer"

export const customerService = {
  async getCustomers(page = 1, search = "") {
    // Mock implementation for demo
    return [
      {
        id: "1",
        firstName: "Acme",
        lastName: "Corp",
        email: "contact@acmecorp.com",
        phoneNumber: "+1 (555) 123-4567",
        company: "Acme Corporation",
        position: "Client",
        status: CustomerStatus.CUSTOMER,
        createdAt: new Date("2023-01-15"),
      },
      // ... other customers
    ]
  },

  async getRecentCustomers() {
    // Mock implementation for demo
    return [
      {
        id: "101",
        firstName: "Acme",
        lastName: "Corp",
        email: "contact@acmecorp.com",
        company: "Acme Corporation",
        status: CustomerStatus.CUSTOMER,
        createdAt: new Date("2023-05-28"),
      },
      // ... other customers
    ]
  },

  async createCustomer(customerData: any) {
    // Mock implementation for demo
    return {
      id: "new-id",
      ...customerData,
      createdAt: new Date(),
      updatedAt: new Date(),
    }
  },

  async updateCustomer(id: string, customerData: any) {
    // Mock implementation for demo
    return {
      id,
      ...customerData,
      updatedAt: new Date(),
    }
  },
}

