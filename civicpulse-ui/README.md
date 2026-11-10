# CivicPulse – Campus Complaint Management System

## 1. Project Overview

CivicPulse is a web-based Campus Complaint Management System designed to simplify communication between students and college administration.

Students can submit complaints related to infrastructure, academics, cleanliness, facilities, and other campus issues. Administrators can view submitted complaints and update their status.

The application provides a centralized platform for complaint submission, tracking, and management.

## 2. Objectives

- Provide an easy platform for students to submit complaints.
- Maintain a centralized record of complaints.
- Allow administrators to manage complaint status.
- Improve transparency in complaint handling.
- Reduce manual complaint management.

## 3. Technologies Used

### Frontend

- React.js
- JavaScript
- HTML
- CSS
- Vite

### Backend

- Java
- Java HTTP Server
- REST API
- File-based data storage

### Development Tools

- Visual Studio Code
- Node.js
- npm
- PowerShell

## 4. Main Modules

### Student Module

- Submit a new complaint.
- Select a complaint category.
- Enter complaint title and description.
- View submitted complaints.

### Admin Module

- View submitted complaints.
- Check complaint details.
- Update complaint status.
- Track Pending, In Progress, and Resolved complaints.

### Dashboard Module

- Display total complaints.
- Display pending complaints.
- Display complaints in progress.
- Display resolved complaints.

## 5. System Architecture

The project follows a frontend-backend architecture.

1. The user interacts with the React frontend.
2. React sends HTTP requests to the Java backend.
3. The Java backend processes the requests.
4. Complaint records are stored in a local text file.
5. The backend sends JSON responses to React.
6. The frontend updates the dashboard.

**Architecture Flow:**

Student/Admin → React Frontend → Java REST API → File Storage

## 6. API Endpoints

| Method | Endpoint                 | Purpose                 |
| ------ | ------------------------ | ----------------------- |
| GET    | `/api/complaints`        | Retrieve all complaints |
| POST   | `/api/complaints`        | Submit a new complaint  |
| POST   | `/api/complaints/status` | Update complaint status |

## 7. Data Storage

CivicPulse uses file-based persistence.

Complaint records are stored in `complaints.txt` inside the backend folder. Text fields are Base64-encoded before storage.

The backend loads existing complaint records when it starts and saves changes when complaints are submitted or updated.

## 8. Installation and Execution

### Prerequisites

- Java JDK
- Node.js and npm
- Visual Studio Code

### Step 1: Start the Java Backend

Open PowerShell:

```powershell
cd E:\CivicPulse\backend
javac --add-modules jdk.httpserver CivicPulseServer.java
java --add-modules jdk.httpserver CivicPulseServer
```

Backend URL: `http://localhost:8000`

### Step 2: Start the React Frontend

Open another PowerShell terminal:

```powershell
cd E:\CivicPulse\civicpulse-ui
npm install
npm run dev
```

Frontend URL: `http://localhost:5173`

Keep both terminals running while using the application.

## 9. Expected Results

- Students can submit complaints through the interface.
- Submitted complaints appear in the complaints list.
- Administrators can update complaint statuses.
- Dashboard statistics reflect complaint records.
- Complaint data remains available after refreshing or restarting the application.

## 10. Future Scope

- Student and administrator authentication.
- Database integration using MySQL or PostgreSQL.
- Complaint assignment to specific departments.
- Email and notification functionality.
- Complaint analytics and reporting.
- Deployment on a cloud platform.

## 11. Conclusion

CivicPulse provides a simple and centralized solution for managing campus complaints. It demonstrates frontend development using React, backend development using Java, REST API communication, and persistent file-based storage.

The project can be extended with authentication, database integration, and additional administrative features in the future.
