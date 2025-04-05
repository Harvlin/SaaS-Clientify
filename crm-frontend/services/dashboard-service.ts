// Add this file to ensure the service is properly defined
export const dashboardService = {
  async getSummary() {
    // Mock implementation for demo
    return {
      totalCustomers: { value: "1,248", trend: { value: 12.5, isPositive: true } },
      totalRevenue: { value: "$45,231.89", trend: { value: 8.2, isPositive: true } },
      activeDeals: { value: "24", trend: { value: 5.1, isPositive: false } },
      completedTasks: { value: "32", trend: { value: 18.7, isPositive: true } },
    }
  },

  async getSalesForecast() {
    // Mock implementation for demo
    return [
      { month: "Jan", forecast: 65000, actual: 68000 },
      { month: "Feb", forecast: 59000, actual: 62500 },
      { month: "Mar", forecast: 80000, actual: 79000 },
      { month: "Apr", forecast: 81000, actual: 85000 },
      { month: "May", forecast: 56000, actual: 61000 },
      { month: "Jun", forecast: 55000, actual: 58000 },
      { month: "Jul", forecast: 40000, actual: 0 },
      { month: "Aug", forecast: 45000, actual: 0 },
      { month: "Sep", forecast: 60000, actual: 0 },
      { month: "Oct", forecast: 70000, actual: 0 },
      { month: "Nov", forecast: 80000, actual: 0 },
      { month: "Dec", forecast: 90000, actual: 0 },
    ]
  },

  async getDealValueByStage() {
    // Mock implementation for demo
    return [
      { name: "Discovery", value: 45000, color: "#94a3b8" },
      { name: "Proposal", value: 85000, color: "#60a5fa" },
      { name: "Negotiation", value: 65000, color: "#3b82f6" },
      { name: "Closing", value: 35000, color: "#2563eb" },
      { name: "Won", value: 120000, color: "#16a34a" },
    ]
  },
}

