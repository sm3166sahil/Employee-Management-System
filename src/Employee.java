package src;

/**
 * Employee Model Class
 * Represents an Employee entity with all required attributes,
 * including Employee Benefits (Health Insurance, Provident Fund,
 * Company Transport, Meal Allowance).
 */
public class Employee {
    private String employeeId;
    private String employeeName;
    private String gender;
    private String dob;          // Format: YYYY-MM-DD
    private String department;
    private String designation;
    private double salary;
    private String phone;
    private String email;
    private String address;

    // ===== BENEFITS SECTION =====
    private boolean healthInsurance;
    private boolean providentFund;
    private boolean companyTransport;
    private boolean mealAllowance;

    // Default Constructor
    public Employee() {
    }

    // Parameterized Constructor
    public Employee(String employeeId, String employeeName, String gender, String dob,
                    String department, String designation, double salary,
                    String phone, String email, String address,
                    boolean healthInsurance, boolean providentFund,
                    boolean companyTransport, boolean mealAllowance) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.gender = gender;
        this.dob = dob;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.phone = phone;
        this.email = email;
        this.address = address;
        // Benefits
        this.healthInsurance = healthInsurance;
        this.providentFund = providentFund;
        this.companyTransport = companyTransport;
        this.mealAllowance = mealAllowance;
    }

    // Getters and Setters
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // ===== BENEFITS GETTERS AND SETTERS =====
    public boolean isHealthInsurance() {
        return healthInsurance;
    }

    public void setHealthInsurance(boolean healthInsurance) {
        this.healthInsurance = healthInsurance;
    }

    public boolean isProvidentFund() {
        return providentFund;
    }

    public void setProvidentFund(boolean providentFund) {
        this.providentFund = providentFund;
    }

    public boolean isCompanyTransport() {
        return companyTransport;
    }

    public void setCompanyTransport(boolean companyTransport) {
        this.companyTransport = companyTransport;
    }

    public boolean isMealAllowance() {
        return mealAllowance;
    }

    public void setMealAllowance(boolean mealAllowance) {
        this.mealAllowance = mealAllowance;
    }
}
