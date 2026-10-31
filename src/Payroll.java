package src;

/**
 * Payroll Model Class
 * Represents an Employee Payroll entity with salary breakdown,
 * deductions, net pay calculation, and payment status.
 */
public class Payroll {
    private int payrollId;
    private String employeeId;
    private String employeeName;

    // Earnings
    private double basicSalary;
    private double hra;
    private double da;
    private double otherAllowance;
    private double grossSalary;

    // Deductions
    private double pf;
    private double professionalTax;
    private double otherDeduction;
    private double totalDeduction;

    // Final Net Salary
    private double netSalary;

    // Pay Period & Status
    private String payMonth;
    private int payYear;
    private String paymentStatus; // "Paid" or "Pending"

    // Default Constructor
    public Payroll() {
    }

    // Parameterized Constructor
    public Payroll(int payrollId, String employeeId, String employeeName,
                   double basicSalary, double hra, double da, double otherAllowance, double grossSalary,
                   double pf, double professionalTax, double otherDeduction, double totalDeduction,
                   double netSalary, String payMonth, int payYear, String paymentStatus) {
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.otherAllowance = otherAllowance;
        this.grossSalary = grossSalary;
        this.pf = pf;
        this.professionalTax = professionalTax;
        this.otherDeduction = otherDeduction;
        this.totalDeduction = totalDeduction;
        this.netSalary = netSalary;
        this.payMonth = payMonth;
        this.payYear = payYear;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(int payrollId) {
        this.payrollId = payrollId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public double getHra() {
        return hra;
    }

    public void setHra(double hra) {
        this.hra = hra;
    }

    public double getDa() {
        return da;
    }

    public void setDa(double da) {
        this.da = da;
    }

    public double getOtherAllowance() {
        return otherAllowance;
    }

    public void setOtherAllowance(double otherAllowance) {
        this.otherAllowance = otherAllowance;
    }

    public double getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(double grossSalary) {
        this.grossSalary = grossSalary;
    }

    public double getPf() {
        return pf;
    }

    public void setPf(double pf) {
        this.pf = pf;
    }

    public double getProfessionalTax() {
        return professionalTax;
    }

    public void setProfessionalTax(double professionalTax) {
        this.professionalTax = professionalTax;
    }

    public double getOtherDeduction() {
        return otherDeduction;
    }

    public void setOtherDeduction(double otherDeduction) {
        this.otherDeduction = otherDeduction;
    }

    public double getTotalDeduction() {
        return totalDeduction;
    }

    public void setTotalDeduction(double totalDeduction) {
        this.totalDeduction = totalDeduction;
    }

    public double getNetSalary() {
        return netSalary;
    }

    public void setNetSalary(double netSalary) {
        this.netSalary = netSalary;
    }

    public String getPayMonth() {
        return payMonth;
    }

    public void setPayMonth(String payMonth) {
        this.payMonth = payMonth;
    }

    public int getPayYear() {
        return payYear;
    }

    public void setPayYear(int payYear) {
        this.payYear = payYear;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
