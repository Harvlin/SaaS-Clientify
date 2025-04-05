// Add this file to ensure the service is properly defined
import { TaskStatus, PriorityLevel } from "@/types/task"

export const taskService = {
  async getTasks(page = 1, search = "") {
    // Mock implementation for demo
    return [
      // ... tasks
    ]
  },

  async getUpcomingTasks() {
    // Mock implementation for demo
    return [
      {
        id: "1",
        title: "Follow up with Acme Corp",
        status: TaskStatus.NOT_STARTED,
        priorityLevel: PriorityLevel.HIGH,
        dueDate: new Date("2023-06-10"),
        customer: { id: "101", firstName: "Acme", lastName: "Corp" },
      },
      // ... other tasks
    ]
  },

  async completeTask(taskId: string) {
    // Mock implementation for demo
    return {
      id: taskId,
      status: TaskStatus.COMPLETED,
      completedAt: new Date(),
    }
  },
}

