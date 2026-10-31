package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * PayrollDAO Class (Data Access Object)
 * Encapsulates all Database CRUD operations for Payroll Management:
 * - Create: addPayroll()
 * - Read:   getAllPayroll(), searchPayroll()
 * - Update: updatePayroll()
 * - Delete: deletePayroll() (Admin Only)
 * 
 * Uses PreparedStatements to prevent SQL injection.
 */
public class PayrollDAO {

    /**
     * Checks if payroll already exists for a specific employee in a given month and year.
     * Used to prevent duplicate payroll entries.
     */
    public boolean isPayrollExists(String empId, String month, int year, int excludePayrollId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM payroll WHERE employee_id = ? AND pay_month = ? AND pay_year = ? AND payroll_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, empId.trim());
            pstmt.setString(2, month.trim());
            pstmt.setInt(3, year);
            pstmt.setInt(4, excludePayrollId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves Employee Name and Basic Salary from 'employees' table for a given Employee ID.
     * @param empId The employee ID
     * @return Employee object or null if not found
     */
    public Employee getEmployeeDetailsForPayroll(String empId) throws SQLException {
        String sql = "SELECT employee_id, employee_name, salary FROM employees WHERE employee_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, empId.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee emp = new Employee();
                    emp.setEmployeeId(rs.getString("employee_id"));
                    emp.setEmployeeName(rs.getString("employee_name"));
                    emp.setSalary(rs.getDouble("salary"));
                    return emp;
                }
            }
        }
        return null;
    }

    /**
     * CREATE: Adds a new payroll record into MySQL.
     */
    public boolean addPayroll(Payroll p) throws SQLException {
        String sql = "INSERT INTO payroll (employee_id, employee_name, basic_salary, hra, da, " +
                     "other_allowance, gross_salary, pf, professional_tax, other_deduction, " +
                     "total_deduction, net_salary, pay_month, pay_year, payment_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getEmployeeId());
            pstmt.setString(2, p.getEmployeeName());
            pstmt.setDouble(3, p.getBasicSalary());
            pstmt.setDouble(4, p.getHra());
            pstmt.setDouble(5, p.getDa());
            pstmt.setDouble(6, p.getOtherAllowance());
            pstmt.setDouble(7, p.getGrossSalary());
            pstmt.setDouble(8, p.getPf());
            pstmt.setDouble(9, p.getProfessionalTax());
            pstmt.setDouble(10, p.getOtherDeduction());
            pstmt.setDouble(11, p.getTotalDeduction());
            pstmt.setDouble(12, p.getNetSalary());
            pstmt.setString(13, p.getPayMonth());
            pstmt.setInt(14, p.getPayYear());
            pstmt.setString(15, p.getPaymentStatus());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * READ: Retrieves all payroll records.
     */
    public List<Payroll> getAllPayroll() throws SQLException {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT * FROM payroll ORDER BY payroll_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(extractPayrollFromResultSet(rs));
            }
        }
        return list;
    }

    /**
     * SEARCH: Searches payroll records by Employee ID or Employee Name.
     */
    public List<Payroll> searchPayroll(String keyword) throws SQLException {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT * FROM payroll WHERE employee_id = ? OR employee_name LIKE ? ORDER BY payroll_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, keyword.trim());
            pstmt.setString(2, "%" + keyword.trim() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractPayrollFromResultSet(rs));
                }
            }
        }
        return list;
    }

    /**
     * UPDATE: Updates an existing payroll record.
     */
    public boolean updatePayroll(Payroll p) throws SQLException {
        String sql = "UPDATE payroll SET basic_salary = ?, hra = ?, da = ?, other_allowance = ?, " +
                     "gross_salary = ?, pf = ?, professional_tax = ?, other_deduction = ?, " +
                     "total_deduction = ?, net_salary = ?, pay_month = ?, pay_year = ?, payment_status = ? " +
                     "WHERE payroll_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, p.getBasicSalary());
            pstmt.setDouble(2, p.getHra());
            pstmt.setDouble(3, p.getDa());
            pstmt.setDouble(4, p.getOtherAllowance());
            pstmt.setDouble(5, p.getGrossSalary());
            pstmt.setDouble(6, p.getPf());
            pstmt.setDouble(7, p.getProfessionalTax());
            pstmt.setDouble(8, p.getOtherDeduction());
            pstmt.setDouble(9, p.getTotalDeduction());
            pstmt.setDouble(10, p.getNetSalary());
            pstmt.setString(11, p.getPayMonth());
            pstmt.setInt(12, p.getPayYear());
            pstmt.setString(13, p.getPaymentStatus());
            pstmt.setInt(14, p.getPayrollId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    /**
     * DELETE: Admin-Only payroll record deletion.
     */
    public boolean deletePayroll(int payrollId, String currentUserRole) throws SQLException, SecurityException {
        if (currentUserRole == null || !"ADMIN".equalsIgnoreCase(currentUserRole)) {
            throw new SecurityException("Permission Denied: Only Admin can delete payroll records.");
        }

        String sql = "DELETE FROM payroll WHERE payroll_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, payrollId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }

    private Payroll extractPayrollFromResultSet(ResultSet rs) throws SQLException {
        Payroll p = new Payroll();
        p.setPayrollId(rs.getInt("payroll_id"));
        p.setEmployeeId(rs.getString("employee_id"));
        p.setEmployeeName(rs.getString("employee_name"));
        p.setBasicSalary(rs.getDouble("basic_salary"));
        p.setHra(rs.getDouble("hra"));
        p.setDa(rs.getDouble("da"));
        p.setOtherAllowance(rs.getDouble("other_allowance"));
        p.setGrossSalary(rs.getDouble("gross_salary"));
        p.setPf(rs.getDouble("pf"));
        p.setProfessionalTax(rs.getDouble("professional_tax"));
        p.setOtherDeduction(rs.getDouble("other_deduction"));
        p.setTotalDeduction(rs.getDouble("total_deduction"));
        p.setNetSalary(rs.getDouble("net_salary"));
        p.setPayMonth(rs.getString("pay_month"));
        p.setPayYear(rs.getInt("pay_year"));
        p.setPaymentStatus(rs.getString("payment_status"));
        return p;
    }
}
