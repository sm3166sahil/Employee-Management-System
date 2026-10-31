package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeDAO Class (Data Access Object)
 * Encapsulates all Database CRUD operations for Employees:
 * - Create: addEmployee()
 * - Read:   getAllEmployees(), searchEmployees()
 * - Update: updateEmployee()
 * - Delete: deleteEmployee() (Admin Only)
 *
 * Uses PreparedStatements for secure, SQL-injection-safe database interactions.
 */
public class EmployeeDAO {

    /**
     * Checks if an employee with the given ID already exists.
     */
    public boolean isEmployeeIdExists(String employeeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * CREATE: Adds a new employee record (including benefits) into MySQL.
     * @param emp The employee object to insert
     * @return true if insertion succeeded, false otherwise
     */
    public boolean addEmployee(Employee emp) throws SQLException {
        // ===== BENEFITS INCLUDED IN INSERT QUERY =====
        String sql = "INSERT INTO employees (employee_id, employee_name, gender, dob, " +
                     "department, designation, salary, phone, email, address, " +
                     "health_insurance, provident_fund, company_transport, meal_allowance) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emp.getEmployeeId());
            pstmt.setString(2, emp.getEmployeeName());
            pstmt.setString(3, emp.getGender());
            pstmt.setString(4, emp.getDob());
            pstmt.setString(5, emp.getDepartment());
            pstmt.setString(6, emp.getDesignation());
            pstmt.setDouble(7, emp.getSalary());
            pstmt.setString(8, emp.getPhone());
            pstmt.setString(9, emp.getEmail());
            pstmt.setString(10, emp.getAddress());

            // Set Checkbox / Boolean Benefits
            pstmt.setBoolean(11, emp.isHealthInsurance());
            pstmt.setBoolean(12, emp.isProvidentFund());
            pstmt.setBoolean(13, emp.isCompanyTransport());
            pstmt.setBoolean(14, emp.isMealAllowance());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * READ: Retrieves all employee records from MySQL.
     * @return List of Employee objects
     */
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employeeList = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY employee_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Employee emp = extractEmployeeFromResultSet(rs);
                employeeList.add(emp);
            }
        }
        return employeeList;
    }

    /**
     * SEARCH: Searches employees by exact ID or partial Name matching.
     * @param keyword The ID or Name keyword entered by the user
     * @return List of matching Employee objects
     */
    public List<Employee> searchEmployees(String keyword) throws SQLException {
        List<Employee> employeeList = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE employee_id = ? OR employee_name LIKE ? ORDER BY employee_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, keyword.trim());
            pstmt.setString(2, "%" + keyword.trim() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Employee emp = extractEmployeeFromResultSet(rs);
                    employeeList.add(emp);
                }
            }
        }
        return employeeList;
    }

    /**
     * UPDATE: Modifies an existing employee record (including benefits).
     * @param emp The employee object with updated information
     * @return true if update succeeded, false otherwise
     */
    public boolean updateEmployee(Employee emp) throws SQLException {
        // ===== BENEFITS INCLUDED IN UPDATE QUERY =====
        String sql = "UPDATE employees SET employee_name = ?, gender = ?, dob = ?, " +
                     "department = ?, designation = ?, salary = ?, phone = ?, " +
                     "email = ?, address = ?, health_insurance = ?, provident_fund = ?, " +
                     "company_transport = ?, meal_allowance = ? WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, emp.getEmployeeName());
            pstmt.setString(2, emp.getGender());
            pstmt.setString(3, emp.getDob());
            pstmt.setString(4, emp.getDepartment());
            pstmt.setString(5, emp.getDesignation());
            pstmt.setDouble(6, emp.getSalary());
            pstmt.setString(7, emp.getPhone());
            pstmt.setString(8, emp.getEmail());
            pstmt.setString(9, emp.getAddress());

            // Set Checkbox / Boolean Benefits
            pstmt.setBoolean(10, emp.isHealthInsurance());
            pstmt.setBoolean(11, emp.isProvidentFund());
            pstmt.setBoolean(12, emp.isCompanyTransport());
            pstmt.setBoolean(13, emp.isMealAllowance());

            // Primary Key for WHERE clause
            pstmt.setString(14, emp.getEmployeeId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * DELETE: Removes an employee record from MySQL.
     * Security Layer: Verifies current user has 'ADMIN' role.
     * @param employeeId ID of employee to delete
     * @param currentUserRole Role of the logged-in user
     * @return true if deletion succeeded, false otherwise
     * @throws SecurityException if non-admin user attempts deletion
     */
    public boolean deleteEmployee(String employeeId, String currentUserRole) throws SQLException, SecurityException {
        // Double-check admin permissions on the backend
        if (currentUserRole == null || !"ADMIN".equalsIgnoreCase(currentUserRole)) {
            throw new SecurityException("Permission Denied: Only users with ADMIN role can delete employee records.");
        }

        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Helper method to map a ResultSet row to an Employee object.
     */
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getString("employee_id"));
        emp.setEmployeeName(rs.getString("employee_name"));
        emp.setGender(rs.getString("gender"));
        emp.setDob(rs.getString("dob"));
        emp.setDepartment(rs.getString("department"));
        emp.setDesignation(rs.getString("designation"));
        emp.setSalary(rs.getDouble("salary"));
        emp.setPhone(rs.getString("phone"));
        emp.setEmail(rs.getString("email"));
        emp.setAddress(rs.getString("address"));

        // ===== EXTRACT BENEFITS BOOLEAN VALUES =====
        emp.setHealthInsurance(rs.getBoolean("health_insurance"));
        emp.setProvidentFund(rs.getBoolean("provident_fund"));
        emp.setCompanyTransport(rs.getBoolean("company_transport"));
        emp.setMealAllowance(rs.getBoolean("meal_allowance"));

        return emp;
    }
}
