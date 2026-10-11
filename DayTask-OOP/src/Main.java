import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Scanner;

// ABSTRACTION + ENCAPSULATION: common task data is private, accessed by getters/setters
abstract class TaskEntry {
    private String title;
    private String date;
    private boolean completed;

    public TaskEntry(String title, String date) {
        this.title = title;
        this.date = date;
        this.completed = false;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public String getStatus() {
        return completed ? "Completed" : "Pending";
    }

    // POLYMORPHISM: each child class displays itself differently
    public abstract String displayInfo();
}

// INHERITANCE: Task extends TaskEntry
class Task extends TaskEntry {
    private String priority;

    public Task(String title, String date, String priority) {
        super(title, date);
        this.priority = priority;
    }

    public String getPriority() { return priority; }

    @Override
    public String displayInfo() {
        return title() + " | Priority: " + priority + " | " + getStatus();
    }

    private String title() { return getTitle(); }
}

// INHERITANCE: DeadlineTask extends TaskEntry
class DeadlineTask extends TaskEntry {
    private String priority;
    private String deadlineTime;

    public DeadlineTask(String title, String date, String priority, String deadlineTime) {
        super(title, date);
        this.priority = priority;
        this.deadlineTime = deadlineTime;
    }

    public String getPriority() { return priority; }
    public String getDeadlineTime() { return deadlineTime; }

    @Override
    public String displayInfo() {
        return getTitle() + " | Priority: " + priority
                + " | Deadline: " + deadlineTime + " | " + getStatus();
    }
}

class TaskManager {
    private ArrayList<TaskEntry> taskList = new ArrayList<TaskEntry>();

    public void addTask(TaskEntry task) {
        taskList.add(task);
    }

    public String viewTasksByDate(String date) {
        String result = "";
        int count = 1;
        for (TaskEntry t : taskList) {
            if (t.getDate().equals(date)) {
                result += count + ". " + t.displayInfo() + "\n";
                count++;
            }
        }
        return result;
    }

    public boolean markCompleted(String title, String date) {
        for (TaskEntry t : taskList) {
            if (t.getTitle().equalsIgnoreCase(title) && t.getDate().equals(date)) {
                t.setCompleted(true);
                return true;
            }
        }
        return false;
    }

    public boolean deleteTask(String title, String date) {
        for (TaskEntry t : taskList) {
            if (t.getTitle().equalsIgnoreCase(title) && t.getDate().equals(date)) {
                taskList.remove(t);
                return true;
            }
        }
        return false;
    }

    public ArrayList<TaskEntry> getTaskList() {
        return taskList;
    }
}

class ProgressReport {
    private int totalTasks;
    private int completedTasks;
    private int pendingTasks;
    private double progressPercentage;

    public void calculateProgress(ArrayList<TaskEntry> tasks) {
        totalTasks = tasks.size();
        completedTasks = 0;
        for (TaskEntry t : tasks) {
            if (t.isCompleted()) {
                completedTasks++;
            }
        }
        pendingTasks = totalTasks - completedTasks;
        if (totalTasks == 0) {
            progressPercentage = 0;
        } else {
            progressPercentage = (completedTasks * 100.0) / totalTasks;
        }
    }

    public String generateReport() {
        return "Total Tasks: " + totalTasks
                + "\nCompleted: " + completedTasks
                + "\nPending: " + pendingTasks
                + "\nProgress: " + String.format("%.1f", progressPercentage) + "%";
    }
}

public class Main {

    // Uses Scanner to read the date text (year-month-day) and check it
    static boolean isValidDate(String date) {
        Scanner sc = new Scanner(date);
        sc.useDelimiter("-");
        if (!sc.hasNextInt()) { sc.close(); return false; }
        int year = sc.nextInt();
        if (!sc.hasNextInt()) { sc.close(); return false; }
        int month = sc.nextInt();
        if (!sc.hasNextInt()) { sc.close(); return false; }
        int day = sc.nextInt();
        sc.close();
        return year > 0 && month >= 1 && month <= 12 && day >= 1 && day <= 31;
    }

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();
        ProgressReport report = new ProgressReport();

        JFrame frame = new JFrame("DAYTASK");

        JLabel titleLbl = new JLabel("Title:");
        JLabel dateLbl = new JLabel("Date (YYYY-MM-DD):");
        JLabel priorityLbl = new JLabel("Priority (High/Moderate/Low):");
        JLabel deadlineLbl = new JLabel("Deadline Time (optional):");

        JTextField titleIn = new JTextField();
        JTextField dateIn = new JTextField();
        JTextField priorityIn = new JTextField();
        JTextField deadlineIn = new JTextField();

        JButton addBtn = new JButton("ADD TASK");
        JButton viewBtn = new JButton("VIEW TASKS BY DATE");
        JButton doneBtn = new JButton("MARK COMPLETED");
        JButton deleteBtn = new JButton("DELETE TASK");
        JButton progressBtn = new JButton("PROGRESS REPORT");

        // ADD TASK
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String title = titleIn.getText().trim();
                String date = dateIn.getText().trim();
                String priority = priorityIn.getText().trim();
                String deadline = deadlineIn.getText().trim();

                if (title.isEmpty() || !isValidDate(date)) {
                    JOptionPane.showMessageDialog(frame, "Enter a title and a valid date (YYYY-MM-DD).");
                    return;
                }
                if (!priority.equalsIgnoreCase("High") && !priority.equalsIgnoreCase("Moderate")
                        && !priority.equalsIgnoreCase("Low")) {
                    JOptionPane.showMessageDialog(frame, "Priority must be High, Moderate, or Low.");
                    return;
                }

                if (deadline.isEmpty()) {
                    manager.addTask(new Task(title, date, priority));
                } else {
                    manager.addTask(new DeadlineTask(title, date, priority, deadline));
                }
                JOptionPane.showMessageDialog(frame, "Task Added Successfully!");
            }
        });

        // VIEW TASKS BY DATE
        viewBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String date = dateIn.getText().trim();
                if (!isValidDate(date)) {
                    JOptionPane.showMessageDialog(frame, "Enter a valid date (YYYY-MM-DD).");
                    return;
                }
                String tasks = manager.viewTasksByDate(date);
                if (tasks.isEmpty()) {
                    tasks = "No tasks for this date.";
                }
                JOptionPane.showMessageDialog(frame, "Tasks on " + date + ":\n" + tasks);
            }
        });

        // MARK COMPLETED (uses Title + Date)
        doneBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean found = manager.markCompleted(titleIn.getText().trim(), dateIn.getText().trim());
                if (found) {
                    JOptionPane.showMessageDialog(frame, "Task Marked as Completed!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Task not found. Check Title and Date.");
                }
            }
        });

        // DELETE TASK (uses Title + Date)
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Delete this task?");
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean found = manager.deleteTask(titleIn.getText().trim(), dateIn.getText().trim());
                    if (found) {
                        JOptionPane.showMessageDialog(frame, "Task Deleted Successfully!");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Task not found. Check Title and Date.");
                    }
                }
            }
        });

        // PROGRESS REPORT
        progressBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                report.calculateProgress(manager.getTaskList());
                JOptionPane.showMessageDialog(frame, report.generateReport());
            }
        });

        // LAYOUT rows, column, spacing rows, spacing column
        frame.setLayout(new GridLayout(9, 2, 6, 6));
        frame.add(titleLbl);
        frame.add(titleIn);

        frame.add(dateLbl);
        frame.add(dateIn);

        frame.add(priorityLbl);
        frame.add(priorityIn);

        frame.add(deadlineLbl);
        frame.add(deadlineIn);

        frame.add(addBtn);
        frame.add(viewBtn);

        frame.add(doneBtn);
        frame.add(deleteBtn);

        frame.add(progressBtn);

        frame.setSize(500, 400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}