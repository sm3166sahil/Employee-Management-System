# Employee Management System (Java Swing + JDBC + MySQL)

A desktop-based CRUD application with Role-Based Access Control (Admin vs Employee).

## Project Structure
```
Project Employee/
├── database.sql                 # MySQL DB & Tables creation script
├── bin/                         # Compiled .class files
├── lib/                         # Place mysql-connector-j-8.x.jar here
└── src/
    ├── Employee.java            # Employee entity model
    ├── User.java                # User entity model (Role-Based Access)
    ├── DBConnection.java        # JDBC connection manager
    ├── EmployeeDAO.java         # Employee CRUD Database Access Object
    ├── UserDAO.java             # User Authentication DAO
    ├── LoginGUI.java            # Login Screen
    ├── EmployeeManagementGUI.java # Main GUI with Table and Form
    └── Main.java                # Application Entry Point
```

## Quick Start
1. Run `database.sql` in MySQL Workbench / Command Line.
2. Update MySQL username/password in `src/DBConnection.java`.
3. Add `mysql-connector-j.jar` to your classpath.
4. Run `Main.java`.

### Default Credentials:
- **Admin**: Username: `admin` | Password: `admin123` (Full Access - Can Add, View, Update, Delete)
- **Employee**: Username: `employee1` | Password: `emp123` (View, Add, Update - Delete is DISABLED)
