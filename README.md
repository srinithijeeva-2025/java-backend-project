# User Management System - Full Stack Java Application

A beginner-friendly full-stack web application built with Core Java, HTML/CSS, and MySQL. This project demonstrates authentication, authorization, session management, and CRUD operations.

## Features

- **User Authentication**: Login system with username and password validation
- **Role-Based Authorization**: ADMIN and USER roles with different permissions
- **Session Management**: Simple session-based authentication using UUID tokens
- **CRUD Operations**: Create, Read, Update, and Delete users (Admin only)
- **Responsive Design**: Mobile-friendly HTML and CSS
- **MySQL Database**: JDBC-based database connectivity

## Project Structure

\`\`\`
user-management-system/
├── public/
│   ├── login.html
│   ├── dashboard.html
│   ├── addUser.html
│   ├── editUser.html
│   ├── deleteUser.html
│   └── css/
│       └── style.css
├── src/
│   ├── Main.java
│   ├── DatabaseConnection.java
│   ├── SessionManager.java
│   ├── UserService.java
│   ├── User.java
│   ├── LoginHandler.java
│   ├── AddUserHandler.java
│   ├── EditUserHandler.java
│   ├── DeleteUserHandler.java
│   └── StaticFileHandler.java
├── database-setup.sql
└── README.md
\`\`\`

## Prerequisites

- Java 8 or higher
- MySQL 5.7 or higher
- MySQL JDBC Driver (mysql-connector-java)

## Setup Instructions

### 1. Create MySQL Database

\`\`\`bash
mysql -u root -p
\`\`\`

Then run the SQL commands from `database-setup.sql`:

\`\`\`sql
CREATE DATABASE user_management;
USE user_management;

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(10) NOT NULL DEFAULT 'USER'
);

INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('user', 'user123', 'USER');
\`\`\`

### 2. Configure Database Connection

Edit `src/DatabaseConnection.java` and update:

\`\`\`java
private static final String DB_URL = "jdbc:mysql://localhost:3306/user_management";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = ""; // Your MySQL password
\`\`\`

### 3. Download MySQL JDBC Driver

Download `mysql-connector-java-x.x.x.jar` from:
https://dev.mysql.com/downloads/connector/j/

Add the JAR file to your classpath.

### 4. Compile Java Files

\`\`\`bash
cd src
javac -cp ".:path/to/mysql-connector.jar" *.java
\`\`\`

### 5. Run the Application

\`\`\`bash
java -cp ".:path/to/mysql-connector.jar" Main
\`\`\`

The application will start on `http://localhost:8080`

## Demo Credentials

**Admin Account:**
- Username: admin
- Password: admin123

**Regular User:**
- Username: user
- Password: user123

## Access the Application

1. Open your browser and go to: `http://localhost:8080/public/login.html`
2. Log in with either admin or user credentials
3. Access different features based on your role

## User Roles and Permissions

| Role | Permissions |
|------|-------------|
| ADMIN | Login, View Dashboard, Add Users, Edit Users, Delete Users |
| USER | Login, View Dashboard Only |

## Key Features Explained

### Authentication
- Username and password validation against MySQL database
- Session tokens generated using UUID
- Session data stored in HashMap (in-memory)

### Authorization
- Admin users see additional buttons for user management
- Regular users only see the dashboard view
- Direct access attempts to admin pages are blocked

### CRUD Operations
- **Add User**: Admin can create new users with custom roles
- **Edit User**: Admin can update username, password, and role
- **Delete User**: Admin can delete users from the system
- **View Users**: All users can view the dashboard

### Session Management
- Sessions stored in memory using HashMap
- Session ID sent as cookie to browser
- Session validation on each request to admin pages

## Troubleshooting

### Database Connection Error
- Ensure MySQL service is running
- Check database credentials in `DatabaseConnection.java`
- Verify MySQL JDBC driver is in classpath

### Server Already Running
- Change PORT in `Main.java` if port 8080 is in use
- Or kill the process using port 8080

### Static Files Not Loading
- Ensure `public/` folder is in the same directory where you run the application
- Check file paths in `StaticFileHandler.java`

## Future Enhancements

- Password encryption/hashing (currently stored in plain text for demo)
- Email verification for new accounts
- Remember me functionality
- User activity logging
- Database session storage instead of in-memory
- OAuth integration
- Input validation and sanitization
- SQL injection protection

## Notes for Beginners

- This project uses Core Java without any frameworks
- HTTP server is implemented using Java's built-in `com.sun.net.httpserver`
- Database connections use JDBC directly without ORM
- Session management is simplified for learning purposes
- For production, use proper frameworks like Spring Boot

---

**Author**: Java Learning Project  
**Last Updated**: 2024
