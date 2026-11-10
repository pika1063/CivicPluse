
import java.util.*;
import java.io.*;

class Complaint {
  int id;
  String name, category, description, status;

  Complaint(int id, String name, String category, String description) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.description = description;
    this.status = "Pending";
  }

  public String toString() {
    return id + "|" + name + "|" + category + "|" +
        description.replace("|", "/") + "|" + status;
  }
}

public class CivicPulse {
  static Scanner sc = new Scanner(System.in);
  static ArrayList<Complaint> complaints = new ArrayList<>();
  static final String FILE = "complaints.txt";

  static void saveComplaints() {
    try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
      for (Complaint c : complaints)
        pw.println(c);
    } catch (IOException e) {
      System.out.println("Error saving complaints.");
    }
  }

  static void loadComplaints() {
    try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] p = line.split("\\|", -1);
        if (p.length == 5) {
          Complaint c = new Complaint(
              Integer.parseInt(p[0]), p[1], p[2], p[3]);
          c.status = p[4];
          complaints.add(c);
        }
      }
    } catch (IOException e) {
      // First run: file does not exist yet.
    }
  }

  static void submitComplaint() {
    System.out.print("Enter student name: ");
    String name = sc.nextLine();

    System.out.print("Enter category (Electricity/Water/Cleanliness/Other): ");
    String category = sc.nextLine();

    System.out.print("Enter complaint description: ");
    String description = sc.nextLine();

    int id = complaints.size() + 1;
    complaints.add(new Complaint(id, name, category, description));
    saveComplaints();

    System.out.println("Complaint submitted! Your ID is: " + id);
  }

  static void viewComplaints() {
    if (complaints.isEmpty()) {
      System.out.println("No complaints found.");
      return;
    }

    for (Complaint c : complaints) {
      System.out.println("\nID: " + c.id);
      System.out.println("Student: " + c.name);
      System.out.println("Category: " + c.category);
      System.out.println("Description: " + c.description);
      System.out.println("Status: " + c.status);
    }
  }

  static void updateStatus() {
    viewComplaints();

    System.out.print("\nEnter complaint ID to update: ");
    int id = Integer.parseInt(sc.nextLine());

    for (Complaint c : complaints) {
      if (c.id == id) {
        System.out.print("Enter new status (Pending/In Progress/Resolved): ");
        c.status = sc.nextLine();
        saveComplaints();
        System.out.println("Status updated successfully!");
        return;
      }
    }

    System.out.println("Complaint ID not found.");
  }

  public static void main(String[] args) {
    loadComplaints();

    while (true) {
      System.out.println("\n===== CIVICPULSE =====");
      System.out.println("1. Student - Submit Complaint");
      System.out.println("2. View All Complaints");
      System.out.println("3. Admin - Update Status");
      System.out.println("4. Exit");
      System.out.print("Enter choice: ");

      String choice = sc.nextLine();

      switch (choice) {
        case "1":
          submitComplaint();
          break;
        case "2":
          viewComplaints();
          break;
        case "3":
          updateStatus();
          break;
        case "4":
          saveComplaints();
          System.out.println("Thank you for using CivicPulse!");
          return;
        default:
          System.out.println("Invalid choice. Try again.");
      }
    }
  }
}
