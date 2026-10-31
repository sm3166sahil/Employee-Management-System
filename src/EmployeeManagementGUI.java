package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

/**
 * EmployeeManagementGUI Class
 * Main Graphical User Interface for Employee Management System.
 * Built using Java Swing, JDBC, and DefaultTableModel.
 * 
 * Features:
 * - CRUD Operations (Create, Read, Update, Delete)
 * - JCheckBox for multiple Employee Benefits selection (No ButtonGroup)
 * - JScrollPane for both the Employee Form and the Employee JTable
 * - Role-Based Access Control (Admin-only Delete Access)
 */
public class EmployeeManagementGUI extends JFrame {

    // Current Logged-in User Session
    private User currentUser;

    // DAO Layer
    private EmployeeDAO employeeDAO;

    // --- GUI Components ---
    // Form Input Fields
    private JTextField txtEmpId;
    private JTextField txtEmpName;
    private JRadioButton rbMale;
    private JRadioButton rbFemale;
    private JRadioButton rbOther;
    private ButtonGroup genderGroup;
    private JTextField txtDob;
    private JComboBox<String> cmbDepartment;
    private JComboBox<String> cmbDesignation;

    // ===== BENEFITS SECTION (FEATURE 1: JCheckBox) =====
    private JCheckBox healthInsurance;
    private JCheckBox providentFund;
    private JCheckBox companyTransport;
    private JCheckBox mealAllowance;

    private JTextField txtSalary;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextArea txtAddress;

    // Search Components
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnShowAll;

    // Action Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnPayroll;
    private JButton btnClear;
    private JButton btnLogout;

    // Table Components
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    /**
     * Constructor
     * @param user The logged-in user object passed from LoginGUI
     */
    public EmployeeManagementGUI(User user) {
        this.currentUser = user;
        this.employeeDAO = new EmployeeDAO();

        initComponents();
        applyRolePermissions();
        loadEmployeesToTable();
    }

    /**
     * Initializes and arranges all GUI components.
     */
    private void initComponents() {
        setTitle("Employee Management System - [Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")]");
        setSize(1150, 780);
        setMinimumSize(new Dimension(1050, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout(10, 10));

        // =========================================================================
        // 1. HEADER PANEL
        // =========================================================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 119, 242));
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("EMPLOYEE MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUserInfo = new JLabel("User: " + currentUser.getUsername() + " | Role: " + currentUser.getRole());
        lblUserInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblUserInfo.setForeground(Color.WHITE);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUserInfo, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // =========================================================================
        // 2. MAIN CENTER CONTENT (Split into Left: Form & Right: Search + Table)
        // =========================================================================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        // -------------------------------------------------------------------------
        // Left: Form Panel (Employee Details Input)
        // -------------------------------------------------------------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Employee Details ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(33, 33, 33)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Employee ID
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee ID:*"), gbc);
        txtEmpId = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtEmpId, gbc);

        // Row 1: Employee Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Employee Name:*"), gbc);
        txtEmpName = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtEmpName, gbc);

        // Row 2: Gender (Radio Buttons with ButtonGroup)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Gender:*"), gbc);
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbOther = new JRadioButton("Other");
        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);
        rbMale.setSelected(true);

        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(genderPanel, gbc);

        // Row 3: Date of Birth
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("DOB (YYYY-MM-DD):*"), gbc);
        txtDob = new JTextField(15);
        txtDob.setToolTipText("Enter Date of Birth in YYYY-MM-DD format");
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtDob, gbc);

        // Row 4: Department (JComboBox)
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Department:*"), gbc);
        String[] departments = {"HR", "IT", "Finance", "Marketing", "Sales", "Operations"};
        cmbDepartment = new JComboBox<>(departments);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(cmbDepartment, gbc);

        // Row 5: Designation (JComboBox)
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Designation:*"), gbc);
        String[] designations = {"Intern", "Junior Developer", "Software Developer", "Senior Developer", "Manager", "Team Lead"};
        cmbDesignation = new JComboBox<>(designations);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(cmbDesignation, gbc);

        // ===== BENEFITS SECTION (FEATURE 1: 4 JCheckBoxes, No ButtonGroup) =====
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Benefits:"), gbc);

        JPanel benefitsPanel = new JPanel(new GridLayout(4, 1, 2, 2));
        healthInsurance = new JCheckBox("Health Insurance");
        providentFund = new JCheckBox("Provident Fund");
        companyTransport = new JCheckBox("Company Transport");
        mealAllowance = new JCheckBox("Meal Allowance");

        benefitsPanel.add(healthInsurance);
        benefitsPanel.add(providentFund);
        benefitsPanel.add(companyTransport);
        benefitsPanel.add(mealAllowance);

        gbc.gridx = 1; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(benefitsPanel, gbc);

        // Row 7: Salary
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Salary (₹/$):*"), gbc);
        txtSalary = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 7;
        formPanel.add(txtSalary, gbc);

        // Row 8: Phone
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Phone Number:*"), gbc);
        txtPhone = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 8;
        formPanel.add(txtPhone, gbc);

        // Row 9: Email
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Email Address:*"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 9;
        formPanel.add(txtEmail, gbc);

        // Row 10: Address (JTextArea in JScrollPane)
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Address:*"), gbc);
        txtAddress = new JTextArea(3, 15);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(txtAddress);
        gbc.gridx = 1; gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(addressScroll, gbc);

        // ===== SCROLLABLE FORM (FEATURE 2: JScrollPane for Form Panel) =====
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.setPreferredSize(new Dimension(380, 500));
        formScrollPane.getVerticalScrollBar().setUnitIncrement(14); // Smooth scrolling

        centerPanel.add(formScrollPane, BorderLayout.WEST);

        // -------------------------------------------------------------------------
        // Right: Search Bar & Table Panel
        // -------------------------------------------------------------------------
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Employee"));
        searchPanel.add(new JLabel("Search by ID / Name:"));
        txtSearch = new JTextField(16);
        searchPanel.add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);

        btnShowAll = new JButton("Show All");
        searchPanel.add(btnShowAll);

        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // Table Panel with Benefits Column
        String[] columnNames = {
                "ID", "Name", "Gender", "DOB", "Department",
                "Designation", "Benefits", "Salary", "Phone", "Email", "Address"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Cells non-editable directly
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(24);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Enable horizontal scrolling when needed
        employeeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        employeeTable.getTableHeader().setBackground(new Color(230, 230, 230));

        // Set column widths for clean readability
        TableColumnModel colModel = employeeTable.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(80);   // ID
        colModel.getColumn(1).setPreferredWidth(120);  // Name
        colModel.getColumn(2).setPreferredWidth(65);   // Gender
        colModel.getColumn(3).setPreferredWidth(85);   // DOB
        colModel.getColumn(4).setPreferredWidth(90);   // Department
        colModel.getColumn(5).setPreferredWidth(130);  // Designation
        colModel.getColumn(6).setPreferredWidth(220);  // Benefits
        colModel.getColumn(7).setPreferredWidth(80);   // Salary
        colModel.getColumn(8).setPreferredWidth(95);   // Phone
        colModel.getColumn(9).setPreferredWidth(150);  // Email
        colModel.getColumn(10).setPreferredWidth(200); // Address

        // ===== SCROLLABLE TABLE (FEATURE 2: JScrollPane for Table) =====
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(" Employee Records "));

        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // =========================================================================
        // 3. BOTTOM BUTTON PANEL
        // =========================================================================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        btnAdd = new JButton("Add Employee");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(new Color(76, 175, 80)); // Green
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        btnUpdate = new JButton("Update Employee");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdate.setBackground(new Color(255, 152, 0)); // Orange
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);

        btnDelete = new JButton("Delete Employee");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(new Color(244, 67, 54)); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        btnPayroll = new JButton("Payroll Management");
        btnPayroll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPayroll.setBackground(new Color(0, 150, 136)); // Teal color
        btnPayroll.setForeground(Color.WHITE);
        btnPayroll.setFocusPainted(false);

        btnClear = new JButton("Clear Fields");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClear.setFocusPainted(false);

        btnLogout = new JButton("Logout / Exit");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setFocusPainted(false);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnPayroll);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnLogout);

        add(buttonPanel, BorderLayout.SOUTH);

        // =========================================================================
        // 4. ATTACH EVENT LISTENERS
        // =========================================================================

        // Table Row Selection -> Fill Input Form
        employeeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateFormFromSelectedRow();
            }
        });

        // Add Button
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployeeAction();
            }
        });

        // Update Button
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeAction();
            }
        });

        // Delete Button (Admin Only)
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployeeAction();
            }
        });

        // Payroll Management Button -> Opens Payroll Management Window
        btnPayroll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PayrollManagementGUI(currentUser).setVisible(true);
            }
        });

        // Search Button
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchEmployeeAction();
            }
        });

        // Show All Button
        btnShowAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                loadEmployeesToTable();
            }
        });

        // Clear Button
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        // Logout / Exit Button
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(EmployeeManagementGUI.this,
                        "Are you sure you want to logout?",
                        "Logout Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginGUI().setVisible(true);
                }
            }
        });
    }

    /**
     * Applies Role-Based Access Control.
     * If user is not ADMIN, disables Delete button.
     */
    private void applyRolePermissions() {
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Permission Denied: Only Admin can delete employees.");
            btnDelete.setText("Delete (Admin Only)");
        }
    }

    // =========================================================================
    // HELPER METHOD: COMBINE BENEFITS FOR JTABLE (FEATURE 1)
    // =========================================================================
    private String getBenefits(boolean health, boolean provident, boolean transport, boolean meal) {
        StringBuilder benefits = new StringBuilder();

        if (health)
            benefits.append("Health Insurance, ");

        if (provident)
            benefits.append("Provident Fund, ");

        if (transport)
            benefits.append("Company Transport, ");

        if (meal)
            benefits.append("Meal Allowance, ");

        if (benefits.length() == 0)
            return "None";

        return benefits.substring(0, benefits.length() - 2);
    }

    // =========================================================================
    // CRUD EVENT HANDLERS
    // =========================================================================

    /**
     * Handles adding a new employee.
     */
    private void addEmployeeAction() {
        if (!validateInputFields()) {
            return;
        }

        String empId = txtEmpId.getText().trim();

        try {
            if (employeeDAO.isEmployeeIdExists(empId)) {
                JOptionPane.showMessageDialog(this,
                        "Employee ID '" + empId + "' already exists! Please use a unique ID.",
                        "Duplicate ID",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Employee emp = createEmployeeFromFields();
            boolean success = employeeDAO.addEmployee(emp);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Employee added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadEmployeesToTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add employee.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "SQL Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles updating an existing employee record.
     */
    private void updateEmployeeAction() {
        if (!validateInputFields()) {
            return;
        }

        String empId = txtEmpId.getText().trim();

        try {
            if (!employeeDAO.isEmployeeIdExists(empId)) {
                JOptionPane.showMessageDialog(this,
                        "Employee ID '" + empId + "' not found. Cannot update non-existent record.",
                        "Record Not Found",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Employee emp = createEmployeeFromFields();
            boolean success = employeeDAO.updateEmployee(emp);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Employee updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadEmployeesToTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update employee.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + ex.getMessage(),
                    "SQL Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles deleting an employee record.
     * Enforces Role Check (Only ADMIN allowed) and Confirmation Dialog.
     */
    private void deleteEmployeeAction() {
        // First Security Check: Role-Based Authorization
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "Only Admin can delete employees.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String empId = txtEmpId.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee from the table or enter an Employee ID to delete.",
                    "No Employee Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete employee with ID: " + empId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = employeeDAO.deleteEmployee(empId, currentUser.getRole());
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Employee deleted successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadEmployeesToTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Employee ID '" + empId + "' not found in database.",
                            "Delete Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Database Error: " + ex.getMessage(),
                        "SQL Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage(),
                        "Security Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles searching employees by ID or Name.
     */
    private void searchEmployeeAction() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter an Employee ID or Name to search.",
                    "Search Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Employee> list = employeeDAO.searchEmployees(keyword);
            tableModel.setRowCount(0);

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No employees found matching: " + keyword,
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Employee emp : list) {
                    tableModel.addRow(new Object[]{
                            emp.getEmployeeId(),
                            emp.getEmployeeName(),
                            emp.getGender(),
                            emp.getDob(),
                            emp.getDepartment(),
                            emp.getDesignation(),
                            getBenefits(emp.isHealthInsurance(), emp.isProvidentFund(), emp.isCompanyTransport(), emp.isMealAllowance()),
                            emp.getSalary(),
                            emp.getPhone(),
                            emp.getEmail(),
                            emp.getAddress()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while searching: " + ex.getMessage(),
                    "SQL Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads all employee records from MySQL into the JTable.
     */
    private void loadEmployeesToTable() {
        try {
            List<Employee> list = employeeDAO.getAllEmployees();
            tableModel.setRowCount(0);

            for (Employee emp : list) {
                tableModel.addRow(new Object[]{
                        emp.getEmployeeId(),
                        emp.getEmployeeName(),
                        emp.getGender(),
                        emp.getDob(),
                        emp.getDepartment(),
                        emp.getDesignation(),
                        getBenefits(emp.isHealthInsurance(), emp.isProvidentFund(), emp.isCompanyTransport(), emp.isMealAllowance()),
                        emp.getSalary(),
                        emp.getPhone(),
                        emp.getEmail(),
                        emp.getAddress()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while loading records: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads details of selected table row into the input form fields (including Benefits checkboxes).
     */
    private void populateFormFromSelectedRow() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtEmpId.setText(tableModel.getValueAt(selectedRow, 0).toString());
            txtEmpName.setText(tableModel.getValueAt(selectedRow, 1).toString());

            String gender = tableModel.getValueAt(selectedRow, 2).toString();
            if ("Male".equalsIgnoreCase(gender)) {
                rbMale.setSelected(true);
            } else if ("Female".equalsIgnoreCase(gender)) {
                rbFemale.setSelected(true);
            } else {
                rbOther.setSelected(true);
            }

            txtDob.setText(tableModel.getValueAt(selectedRow, 3).toString());
            cmbDepartment.setSelectedItem(tableModel.getValueAt(selectedRow, 4).toString());
            cmbDesignation.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());

            // ===== POPULATE BENEFITS CHECKBOXES FROM JTABLE =====
            String benefitsStr = tableModel.getValueAt(selectedRow, 6).toString();
            healthInsurance.setSelected(benefitsStr.contains("Health Insurance"));
            providentFund.setSelected(benefitsStr.contains("Provident Fund"));
            companyTransport.setSelected(benefitsStr.contains("Company Transport"));
            mealAllowance.setSelected(benefitsStr.contains("Meal Allowance"));

            txtSalary.setText(tableModel.getValueAt(selectedRow, 7).toString());
            txtPhone.setText(tableModel.getValueAt(selectedRow, 8).toString());
            txtEmail.setText(tableModel.getValueAt(selectedRow, 9).toString());
            txtAddress.setText(tableModel.getValueAt(selectedRow, 10).toString());
        }
    }

    /**
     * Constructs an Employee object from GUI input fields (including JCheckBoxes).
     */
    private Employee createEmployeeFromFields() {
        String gender = rbMale.isSelected() ? "Male" : (rbFemale.isSelected() ? "Female" : "Other");
        double salary = Double.parseDouble(txtSalary.getText().trim());

        // Read checkbox selections
        boolean health = healthInsurance.isSelected();
        boolean provident = providentFund.isSelected();
        boolean transport = companyTransport.isSelected();
        boolean meal = mealAllowance.isSelected();

        return new Employee(
                txtEmpId.getText().trim(),
                txtEmpName.getText().trim(),
                gender,
                txtDob.getText().trim(),
                (String) cmbDepartment.getSelectedItem(),
                (String) cmbDesignation.getSelectedItem(),
                salary,
                txtPhone.getText().trim(),
                txtEmail.getText().trim(),
                txtAddress.getText().trim(),
                health,
                provident,
                transport,
                meal
        );
    }

    /**
     * Validates all form inputs.
     * @return true if all validations pass, false otherwise
     */
    private boolean validateInputFields() {
        if (txtEmpId.getText().trim().isEmpty()) {
            showValidationError("Employee ID cannot be empty!", txtEmpId);
            return false;
        }

        if (txtEmpName.getText().trim().isEmpty()) {
            showValidationError("Employee Name cannot be empty!", txtEmpName);
            return false;
        }

        String dob = txtDob.getText().trim();
        if (dob.isEmpty() || !dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
            showValidationError("Date of Birth must be in format YYYY-MM-DD (e.g., 1998-05-20)!", txtDob);
            return false;
        }

        try {
            double sal = Double.parseDouble(txtSalary.getText().trim());
            if (sal < 0) {
                showValidationError("Salary must be a positive number!", txtSalary);
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Salary must be a valid number (e.g., 50000.00)!", txtSalary);
            return false;
        }

        String phone = txtPhone.getText().trim();
        if (phone.isEmpty() || !phone.matches("\\d{10,15}")) {
            showValidationError("Phone Number must contain between 10 to 15 digits!", txtPhone);
            return false;
        }

        String email = txtEmail.getText().trim();
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            showValidationError("Please enter a valid Email Address (e.g., user@example.com)!", txtEmail);
            return false;
        }

        if (txtAddress.getText().trim().isEmpty()) {
            showValidationError("Address cannot be empty!", txtAddress);
            return false;
        }

        return true;
    }

    private void showValidationError(String message, JComponent componentToFocus) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
        componentToFocus.requestFocus();
    }

    /**
     * Clears all form fields and unchecks all benefit checkboxes.
     */
    private void clearFields() {
        txtEmpId.setText("");
        txtEmpName.setText("");
        rbMale.setSelected(true);
        txtDob.setText("");
        cmbDepartment.setSelectedIndex(0);
        cmbDesignation.setSelectedIndex(0);

        // ===== CLEAR BENEFITS CHECKBOXES =====
        healthInsurance.setSelected(false);
        providentFund.setSelected(false);
        companyTransport.setSelected(false);
        mealAllowance.setSelected(false);

        txtSalary.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        employeeTable.clearSelection();
        txtEmpId.requestFocus();
    }
}
