"use client"

import { useEffect, useState } from "react"
import Link from "next/link"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import { Skeleton } from "@/components/ui/skeleton"
import { formatDate } from "@/lib/utils"
import { type Task, TaskStatus, PriorityLevel } from "@/types/task"

export function UpcomingTasks() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        // In a real app, this would fetch from the API
        // const data = await taskService.getUpcomingTasks();
        
        // Mock data for demonstration
        setTasks([
          {
            id: "1",
            title: "Follow up with Acme Corp",
            status: TaskStatus.NOT_STARTED,
            priorityLevel: PriorityLevel.HIGH,
            dueDate: new Date("2023-06-10"),
            customer: { id: "101", firstName: "Acme", lastName: "Corp" },
          },
          {
            id: "2",
            title: "Prepare proposal for Global Industries",
            status: TaskStatus.IN_PROGRESS,
            priorityLevel: PriorityLevel.MEDIUM,
            dueDate: new Date("2023-06-12"),
            customer: { id: "102", firstName: "Global", lastName: "Industries" },
          },
          {
            id: "3",
            title: "Schedule demo with Tech Solutions",
            status: TaskStatus.NOT_STARTED,
            priorityLevel: PriorityLevel.LOW,
            dueDate: new Date("2023-06-15"),
            customer: { id: "103", firstName: "Tech", lastName: "Solutions" },
          },
        ]);
      } catch (error) {
        console.error("Failed to fetch upcoming tasks:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTasks();
  }, []);

  const handleTaskComplete = async (taskId: string) => {
    try {
      // In a real app, this would update the API
      // await taskService.completeTask(taskId);
      
      // Update local state
      setTasks(tasks.map(task => 
        task.id === taskId 
          ? { ...task, status: TaskStatus.COMPLETED } 
          : task
      ));
    } catch (error) {
      console.error("Failed to complete task:", error);
    }
  };

  const getPriorityClass = (priority: PriorityLevel) => {
    switch (priority) {
      case PriorityLevel.HIGH:
        return "text-red-500";\
      case  => {
    switch (priority) {
      case PriorityLevel.HIGH:
        return "text-red-500";
      case PriorityLevel.MEDIUM:
        return "text-amber-500";
      case PriorityLevel.LOW:
        return "text-green-500";
      case PriorityLevel.URGENT:
        return "text-purple-500";
      default:
        return "";
    }
  };

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <div>
          <CardTitle>Upcoming Tasks</CardTitle>
          <CardDescription>Tasks due in the next 7 days</CardDescription>
        </div>
        <Button asChild variant="outline" size="sm">
          <Link href="/tasks">View All</Link>
        </Button>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-4">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="flex items-center space-x-4">
                <Skeleton className="h-4 w-4 rounded-sm" />
                <div className="space-y-2 flex-1">
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-[160px]" />
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="space-y-4">
            {tasks.map((task) => (
              <div key={task.id} className="flex items-start space-x-4">
                <Checkbox 
                  id={`task-${task.id}`} 
                  checked={task.status === TaskStatus.COMPLETED}
                  onCheckedChange={() => handleTaskComplete(task.id)}
                />
                <div className="space-y-1 flex-1">
                  <label
                    htmlFor={`task-${task.id}`}
                    className="font-medium peer-disabled:cursor-not-allowed peer-disabled:opacity-70"
                  >
                    <Link href={`/tasks/${task.id}`} className="hover:underline">
                      {task.title}
                    </Link>
                  </label>
                  <div className="flex items-center text-sm text-muted-foreground">
                    <span className={getPriorityClass(task.priorityLevel)}>
                      {task.priorityLevel}
                    </span>
                    <span className="mx-2">•</span>
                    <span>Due {formatDate(task.dueDate)}</span>
                    {task.customer && (
                      <>
                        <span className="mx-2">•</span>
                        <Link href={`/customers/${task.customer.id}`} className="hover:underline">
                          {task.customer.firstName} {task.customer.lastName}
                        </Link>
                      </>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}

