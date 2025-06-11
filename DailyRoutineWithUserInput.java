import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class DailyRoutineWithUserInput {

    // Task class defined inside the main class
    static class Task {
        String name;
        String time;

        Task(String name, String time) {
            this.name = name;
            this.time = time;
        }

        void show() {
            System.out.println("Task: " + name + " at " + time);
        }

        @Override
        public String toString() {
            return name + " at " + time;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Queue<Task> taskQueue = new LinkedList<>();

        System.out.print("Enter how many tasks you want to schedule: ");
        int numberOfTasks = scanner.nextInt();
        scanner.nextLine(); // Consume leftover newline

        // Input tasks
        for (int i = 1; i <= numberOfTasks; i++) {
            System.out.print("Enter name of task " + i + ": ");
            String taskName = scanner.nextLine();
            System.out.print("Enter time for task " + i + ": ");
            String taskTime = scanner.nextLine();

            taskQueue.add(new Task(taskName, taskTime));
        }

        System.out.println("\n---- Starting Task Scheduler (FIFO) ----\n");

        // Process tasks one by one
        while (!taskQueue.isEmpty()) {
            Task currentTask = taskQueue.peek();
            currentTask.show();

            System.out.print("Is this task completed? (yes/no): ");
            String answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("yes")) {
                System.out.println("✅ Task completed: " + currentTask);
            } else {
                System.out.println("❌ Task not completed, moving to next...");
            }

            taskQueue.poll(); // Remove the task from queue
            System.out.println(); // Add space
        }

        System.out.println("All tasks processed ✅");
        scanner.close();
    }
}
