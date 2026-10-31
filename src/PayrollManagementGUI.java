package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

/**
 * PayrollManagementGUI Class
 * Standalone GUI module for Employee Payroll Management.
 * Features:
 * - Real-time Salary Calculation (Gross Salary, Total Deduction, Net Salary)
 * - Fetch Employee Details directly from employees table
 * - Full CRUD Operations with MySQL using PayrollDAO
 * - Admin-Only Delete Access Control
 * - JTable with dual-axis scrollbars (Vertical + Horizontal)
 */
public class PayrollManagementGUI extends JFrame {

    private User currentUser;
    private PayrollDAO payrollDAO;

    // Form Components
    private JTextField txtPayrollId;
    private JTextField txtEmpId;
    private JButton btnFetchEmp;
    private JTextField txtEmpName;

    // Earnings
    private JTextField txtBasicSalary;
    private JTextField txtHra;
    private JTextField txtDa;
    private JTextField txtOtherAllowance;
    private JTextField txtGrossSalary; // Read-only / Auto-calculated

    // Deductions
    private JTextField txtPf;
    private JTextField txtProfTax;
    private JTextField txtOtherDeduction;
    private JTextField txtTotalDeduction; // Read-only / Auto-calculated

    // Net Salary
    private JTextField txtNetSalary; // Read-only / Auto-calculated

    // Period & Status
    private JComboBox<String> cmbPayMonth;
    private JTextField txtPayYear;
    private JComboBox<String> cmbPaymentStatus;

    // Action Buttons
    private JButton btnCalculate;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnBack;

    // Search Components
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnShowAll;

    // Table Components
    private JTable payrollTable;
    private DefaultTableModel tableModel;

    public PayrollManagementGUI(User user) {
        this.currentUser = user;
        this.payrollDAO = new PayrollDAO();

        initComponents();
        applyRolePermissions();
        loadPayrollToTable();
    }

    private void initComponents() {
        setTitle("Employee Payroll Management - [Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")]");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // =========================================================================
        // 1. HEADER PANEL
        // =========================================================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 150, 136)); // Teal color for Payroll module
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("EMPLOYEE PAYROLL MANAGEMENT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUserInfo = new JLabel("User: " + currentUser.getUsername() + " | Role: " + currentUser.getRole());
        lblUserInfo.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblUserInfo.setForeground(Color.WHITE);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(lblUserInfo, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // =========================================================================
        // 2. MAIN CENTER CONTENT (Left: Scrollable Form | Right: Search + Table)
        // =========================================================================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        // -------------------------------------------------------------------------
        // Left: Form Panel
        // -------------------------------------------------------------------------
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), " Payroll Details ",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(33, 33, 33)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 6, 3, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Payroll ID (Auto-generated / Non-editable)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Payroll ID:"), gbc);
        txtPayrollId = new JTextField(12);
        txtPayrollId.setEditable(false);
        txtPayrollId.setBackground(new Color(240, 240, 240));
        txtPayrollId.setText("Auto");
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(txtPayrollId, gbc);

        // Row 1: Employee ID + Fetch Button
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Employee ID:*"), gbc);
        JPanel empIdPanel = new JPanel(new BorderLayout(4, 0));
        txtEmpId = new JTextField(8);
        btnFetchEmp = new JButton("Fetch");
        btnFetchEmp.setToolTipText("Fetch Employee Name & Salary from database");
        empIdPanel.add(txtEmpId, BorderLayout.CENTER);
        empIdPanel.add(btnFetchEmp, BorderLayout.EAST);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(empIdPanel, gbc);

        // Row 2: Employee Name (Non-editable)
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Employee Name:"), gbc);
        txtEmpName = new JTextField(12);
        txtEmpName.setEditable(false);
        txtEmpName.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(txtEmpName, gbc);

        // Row 3: Basic Salary
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Basic Salary (₹):*"), gbc);
        txtBasicSalary = new JTextField("0.00", 12);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtBasicSalary, gbc);

        // Row 4: HRA
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("HRA (₹):"), gbc);
        txtHra = new JTextField("0.00", 12);
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(txtHra, gbc);

        // Row 5: DA
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("DA (₹):"), gbc);
        txtDa = new JTextField("0.00", 12);
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(txtDa, gbc);

        // Row 6: Other Allowance
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Other Allowance (₹):"), gbc);
        txtOtherAllowance = new JTextField("0.00", 12);
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(txtOtherAllowance, gbc);

        // Row 7: Gross Salary (Auto-calculated)
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Gross Salary (₹):"), gbc);
        txtGrossSalary = new JTextField("0.00", 12);
        txtGrossSalary.setEditable(false);
        txtGrossSalary.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtGrossSalary.setBackground(new Color(232, 245, 233));
        gbc.gridx = 1; gbc.gridy = 7;
        formPanel.add(txtGrossSalary, gbc);

        // Row 8: PF (Provident Fund)
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Provident Fund (₹):"), gbc);
        txtPf = new JTextField("0.00", 12);
        gbc.gridx = 1; gbc.gridy = 8;
        formPanel.add(txtPf, gbc);

        // Row 9: Professional Tax
        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Prof. Tax (₹):"), gbc);
        txtProfTax = new JTextField("200.00", 12);
        gbc.gridx = 1; gbc.gridy = 9;
        formPanel.add(txtProfTax, gbc);

        // Row 10: Other Deduction
        gbc.gridx = 0; gbc.gridy = 10;
        formPanel.add(new JLabel("Other Deduction (₹):"), gbc);
        txtOtherDeduction = new JTextField("0.00", 12);
        gbc.gridx = 1; gbc.gridy = 10;
        formPanel.add(txtOtherDeduction, gbc);

        // Row 11: Total Deduction (Auto-calculated)
        gbc.gridx = 0; gbc.gridy = 11;
        formPanel.add(new JLabel("Total Deduction (₹):"), gbc);
        txtTotalDeduction = new JTextField("0.00", 12);
        txtTotalDeduction.setEditable(false);
        txtTotalDeduction.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtTotalDeduction.setBackground(new Color(255, 235, 238));
        gbc.gridx = 1; gbc.gridy = 11;
        formPanel.add(txtTotalDeduction, gbc);

        // Row 12: Net Salary (Auto-calculated)
        gbc.gridx = 0; gbc.gridy = 12;
        formPanel.add(new JLabel("Net Salary (₹):"), gbc);
        txtNetSalary = new JTextField("0.00", 12);
        txtNetSalary.setEditable(false);
        txtNetSalary.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtNetSalary.setBackground(new Color(255, 249, 196));
        gbc.gridx = 1; gbc.gridy = 12;
        formPanel.add(txtNetSalary, gbc);

        // Row 13: Pay Month
        gbc.gridx = 0; gbc.gridy = 13;
        formPanel.add(new JLabel("Pay Month:*"), gbc);
        String[] months = {"January", "February", "March", "April", "May", "June",
                           "July", "August", "September", "October", "November", "December"};
        cmbPayMonth = new JComboBox<>(months);
        cmbPayMonth.setSelectedItem("September");
        gbc.gridx = 1; gbc.gridy = 13;
        formPanel.add(cmbPayMonth, gbc);

        // Row 14: Pay Year
        gbc.gridx = 0; gbc.gridy = 14;
        formPanel.add(new JLabel("Pay Year:*"), gbc);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        txtPayYear = new JTextField(String.valueOf(currentYear), 12);
        gbc.gridx = 1; gbc.gridy = 14;
        formPanel.add(txtPayYear, gbc);

        // Row 15: Payment Status
        gbc.gridx = 0; gbc.gridy = 15;
        formPanel.add(new JLabel("Payment Status:*"), gbc);
        String[] statuses = {"Paid", "Pending"};
        cmbPaymentStatus = new JComboBox<>(statuses);
        gbc.gridx = 1; gbc.gridy = 15;
        formPanel.add(cmbPaymentStatus, gbc);

        // Row 16: Calculate Button inside Form
        btnCalculate = new JButton("Calculate Salary");
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCalculate.setBackground(new Color(63, 81, 181));
        btnCalculate.setForeground(Color.WHITE);
        btnCalculate.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 6, 6, 6);
        formPanel.add(btnCalculate, gbc);

        // Scrollable Form Container
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.setPreferredSize(new Dimension(380, 500));
        formScrollPane.getVerticalScrollBar().setUnitIncrement(14);

        centerPanel.add(formScrollPane, BorderLayout.WEST);

        // -------------------------------------------------------------------------
        // Right: Search Bar & Table Panel
        // -------------------------------------------------------------------------
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Payroll"));
        searchPanel.add(new JLabel("Search by Emp ID / Name:"));
        txtSearch = new JTextField(16);
        searchPanel.add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(33, 150, 243));
        btnSearch.setForeground(Color.WHITE);
        searchPanel.add(btnSearch);

        btnShowAll = new JButton("Show All");
        searchPanel.add(btnShowAll);

        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // Table Panel (16 columns)
        String[] columnNames = {
                "Payroll ID", "Emp ID", "Emp Name", "Basic (₹)", "HRA (₹)", "DA (₹)",
                "Other All. (₹)", "Gross (₹)", "PF (₹)", "Prof. Tax (₹)", "Other Ded. (₹)",
                "Total Ded. (₹)", "Net Pay (₹)", "Month", "Year", "Status"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        payrollTable = new JTable(tableModel);
        payrollTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payrollTable.setRowHeight(24);
        payrollTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Enable dual-axis scrollbars
        payrollTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        payrollTable.getTableHeader().setBackground(new Color(230, 230, 230));

        // Column widths
        TableColumnModel colModel = payrollTable.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(75);   // Payroll ID
        colModel.getColumn(1).setPreferredWidth(80);   // Emp ID
        colModel.getColumn(2).setPreferredWidth(130);  // Name
        colModel.getColumn(3).setPreferredWidth(85);   // Basic
        colModel.getColumn(4).setPreferredWidth(75);   // HRA
        colModel.getColumn(5).setPreferredWidth(75);   // DA
        colModel.getColumn(6).setPreferredWidth(90);   // Other Allow.
        colModel.getColumn(7).setPreferredWidth(90);   // Gross
        colModel.getColumn(8).setPreferredWidth(75);   // PF
        colModel.getColumn(9).setPreferredWidth(85);   // Prof. Tax
        colModel.getColumn(10).setPreferredWidth(90);  // Other Ded.
        colModel.getColumn(11).setPreferredWidth(90);  // Total Ded.
        colModel.getColumn(12).setPreferredWidth(95);  // Net Pay
        colModel.getColumn(13).setPreferredWidth(85);  // Month
        colModel.getColumn(14).setPreferredWidth(60);  // Year
        colModel.getColumn(15).setPreferredWidth(75);  // Status

        // Dual Scrollbars for Table
        JScrollPane tableScrollPane = new JScrollPane(payrollTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(" Payroll Records "));

        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // =========================================================================
        // 3. BOTTOM BUTTON PANEL
        // =========================================================================
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        btnAdd = new JButton("Add Payroll");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(new Color(76, 175, 80)); // Green
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        btnUpdate = new JButton("Update Payroll");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdate.setBackground(new Color(255, 152, 0)); // Orange
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);

        btnDelete = new JButton("Delete Payroll");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDelete.setBackground(new Color(244, 67, 54)); // Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        btnClear = new JButton("Clear Fields");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClear.setFocusPainted(false);

        btnBack = new JButton("Back to Main");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setFocusPainted(false);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // =========================================================================
        // 4. ATTACH EVENT LISTENERS
        // =========================================================================

        // Fetch Employee button click
        btnFetchEmp.addActionListener(e -> fetchEmployeeDetails());

        // Focus lost on Emp ID auto-fetches details
        txtEmpId.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!txtEmpId.getText().trim().isEmpty() && txtEmpName.getText().trim().isEmpty()) {
                    fetchEmployeeDetails();
                }
            }
        });

        // Calculate Salary Button
        btnCalculate.addActionListener(e -> calculateSalary());

        // Table Selection -> Auto-populate Form
        payrollTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateFormFromSelectedRow();
            }
        });

        // CRUD Actions
        btnAdd.addActionListener(e -> addPayrollAction());
        btnUpdate.addActionListener(e -> updatePayrollAction());
        btnDelete.addActionListener(e -> deletePayrollAction());

        // Search Actions
        btnSearch.addActionListener(e -> searchPayrollAction());
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            loadPayrollToTable();
        });

        // Clear & Back
        btnClear.addActionListener(e -> clearFields());
        btnBack.addActionListener(e -> dispose());
    }

    private void applyRolePermissions() {
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Permission Denied: Only Admin can delete payroll records.");
            btnDelete.setText("Delete (Admin Only)");
        }
    }

    /**
     * Fetches Employee Name & Basic Salary from database for the entered Employee ID.
     */
    private void fetchEmployeeDetails() {
        String empId = txtEmpId.getText().trim();
        if (empId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID first!", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Employee emp = payrollDAO.getEmployeeDetailsForPayroll(empId);
            if (emp != null) {
                txtEmpName.setText(emp.getEmployeeName());
                txtBasicSalary.setText(String.format("%.2f", emp.getSalary()));

                // Auto-fill standard allowances based on formula if currently 0
                double basic = emp.getSalary();
                txtHra.setText(String.format("%.2f", basic * 0.20));
                txtDa.setText(String.format("%.2f", basic * 0.10));
                txtOtherAllowance.setText(String.format("%.2f", basic * 0.05));
                txtPf.setText(String.format("%.2f", basic * 0.12));
                txtProfTax.setText("200.00");
                txtOtherDeduction.setText("0.00");

                calculateSalary();
            } else {
                JOptionPane.showMessageDialog(this, "No employee found with ID: " + empId, "Not Found", JOptionPane.WARNING_MESSAGE);
                txtEmpName.setText("");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calculates Gross Salary, Total Deduction, and Net Salary.
     */
    private boolean calculateSalary() {
        try {
            double basic = Double.parseDouble(txtBasicSalary.getText().trim());
            double hra = Double.parseDouble(txtHra.getText().trim());
            double da = Double.parseDouble(txtDa.getText().trim());
            double otherAll = Double.parseDouble(txtOtherAllowance.getText().trim());

            double pf = Double.parseDouble(txtPf.getText().trim());
            double profTax = Double.parseDouble(txtProfTax.getText().trim());
            double otherDed = Double.parseDouble(txtOtherDeduction.getText().trim());

            if (basic < 0 || hra < 0 || da < 0 || otherAll < 0 || pf < 0 || profTax < 0 || otherDed < 0) {
                JOptionPane.showMessageDialog(this, "Salary and deduction amounts cannot be negative!", "Calculation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Calculations
            double gross = basic + hra + da + otherAll;
            double totalDed = pf + profTax + otherDed;
            double net = gross - totalDed;

            txtGrossSalary.setText(String.format("%.2f", gross));
            txtTotalDeduction.setText(String.format("%.2f", totalDed));
            txtNetSalary.setText(String.format("%.2f", net));

            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for all salary and deduction fields!", "Number Format Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void addPayrollAction() {
        if (!validateInputFields() || !calculateSalary()) return;

        String empId = txtEmpId.getText().trim();
        String month = (String) cmbPayMonth.getSelectedItem();
        int year = Integer.parseInt(txtPayYear.getText().trim());

        try {
            // Check for duplicate payroll for same employee, month, year
            if (payrollDAO.isPayrollExists(empId, month, year, 0)) {
                JOptionPane.showMessageDialog(this,
                        "Payroll record already exists for " + empId + " for " + month + " " + year + "!",
                        "Duplicate Record",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Payroll p = createPayrollFromFields();
            if (payrollDAO.addPayroll(p)) {
                JOptionPane.showMessageDialog(this, "Payroll added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPayrollToTable();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePayrollAction() {
        if (txtPayrollId.getText().equals("Auto") || txtPayrollId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a payroll record from the table to update!", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateInputFields() || !calculateSalary()) return;

        int payrollId = Integer.parseInt(txtPayrollId.getText().trim());
        String empId = txtEmpId.getText().trim();
        String month = (String) cmbPayMonth.getSelectedItem();
        int year = Integer.parseInt(txtPayYear.getText().trim());

        try {
            if (payrollDAO.isPayrollExists(empId, month, year, payrollId)) {
                JOptionPane.showMessageDialog(this,
                        "Another payroll record already exists for " + empId + " for " + month + " " + year + "!",
                        "Duplicate Record",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Payroll p = createPayrollFromFields();
            p.setPayrollId(payrollId);

            if (payrollDAO.updatePayroll(p)) {
                JOptionPane.showMessageDialog(this, "Payroll updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPayrollToTable();
                clearFields();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePayrollAction() {
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this, "Only Admin can delete payroll records.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtPayrollId.getText().equals("Auto") || txtPayrollId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a payroll record from the table to delete!", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int payrollId = Integer.parseInt(txtPayrollId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Payroll ID: " + payrollId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (payrollDAO.deletePayroll(payrollId, currentUser.getRole())) {
                    JOptionPane.showMessageDialog(this, "Payroll deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPayrollToTable();
                    clearFields();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchPayrollAction() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID or Name to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Payroll> list = payrollDAO.searchPayroll(keyword);
            tableModel.setRowCount(0);

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No payroll records found matching: " + keyword, "Search Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Payroll p : list) {
                    tableModel.addRow(new Object[]{
                            p.getPayrollId(), p.getEmployeeId(), p.getEmployeeName(),
                            p.getBasicSalary(), p.getHra(), p.getDa(), p.getOtherAllowance(),
                            p.getGrossSalary(), p.getPf(), p.getProfessionalTax(), p.getOtherDeduction(),
                            p.getTotalDeduction(), p.getNetSalary(), p.getPayMonth(), p.getPayYear(),
                            p.getPaymentStatus()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPayrollToTable() {
        try {
            List<Payroll> list = payrollDAO.getAllPayroll();
            tableModel.setRowCount(0);

            for (Payroll p : list) {
                tableModel.addRow(new Object[]{
                        p.getPayrollId(), p.getEmployeeId(), p.getEmployeeName(),
                        p.getBasicSalary(), p.getHra(), p.getDa(), p.getOtherAllowance(),
                        p.getGrossSalary(), p.getPf(), p.getProfessionalTax(), p.getOtherDeduction(),
                        p.getTotalDeduction(), p.getNetSalary(), p.getPayMonth(), p.getPayYear(),
                        p.getPaymentStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error while loading payroll: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelectedRow() {
        int row = payrollTable.getSelectedRow();
        if (row >= 0) {
            txtPayrollId.setText(tableModel.getValueAt(row, 0).toString());
            txtEmpId.setText(tableModel.getValueAt(row, 1).toString());
            txtEmpName.setText(tableModel.getValueAt(row, 2).toString());
            txtBasicSalary.setText(tableModel.getValueAt(row, 3).toString());
            txtHra.setText(tableModel.getValueAt(row, 4).toString());
            txtDa.setText(tableModel.getValueAt(row, 5).toString());
            txtOtherAllowance.setText(tableModel.getValueAt(row, 6).toString());
            txtGrossSalary.setText(tableModel.getValueAt(row, 7).toString());
            txtPf.setText(tableModel.getValueAt(row, 8).toString());
            txtProfTax.setText(tableModel.getValueAt(row, 9).toString());
            txtOtherDeduction.setText(tableModel.getValueAt(row, 10).toString());
            txtTotalDeduction.setText(tableModel.getValueAt(row, 11).toString());
            txtNetSalary.setText(tableModel.getValueAt(row, 12).toString());
            cmbPayMonth.setSelectedItem(tableModel.getValueAt(row, 13).toString());
            txtPayYear.setText(tableModel.getValueAt(row, 14).toString());
            cmbPaymentStatus.setSelectedItem(tableModel.getValueAt(row, 15).toString());
        }
    }

    private Payroll createPayrollFromFields() {
        return new Payroll(
                txtPayrollId.getText().equals("Auto") ? 0 : Integer.parseInt(txtPayrollId.getText().trim()),
                txtEmpId.getText().trim(),
                txtEmpName.getText().trim(),
                Double.parseDouble(txtBasicSalary.getText().trim()),
                Double.parseDouble(txtHra.getText().trim()),
                Double.parseDouble(txtDa.getText().trim()),
                Double.parseDouble(txtOtherAllowance.getText().trim()),
                Double.parseDouble(txtGrossSalary.getText().trim()),
                Double.parseDouble(txtPf.getText().trim()),
                Double.parseDouble(txtProfTax.getText().trim()),
                Double.parseDouble(txtOtherDeduction.getText().trim()),
                Double.parseDouble(txtTotalDeduction.getText().trim()),
                Double.parseDouble(txtNetSalary.getText().trim()),
                (String) cmbPayMonth.getSelectedItem(),
                Integer.parseInt(txtPayYear.getText().trim()),
                (String) cmbPaymentStatus.getSelectedItem()
        );
    }

    private boolean validateInputFields() {
        if (txtEmpId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee ID cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtEmpName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fetch a valid employee first!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            int year = Integer.parseInt(txtPayYear.getText().trim());
            if (year < 1990 || year > 2100) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 4-digit Pay Year (1990 - 2100)!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Pay Year must be a valid integer year!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearFields() {
        txtPayrollId.setText("Auto");
        txtEmpId.setText("");
        txtEmpName.setText("");
        txtBasicSalary.setText("0.00");
        txtHra.setText("0.00");
        txtDa.setText("0.00");
        txtOtherAllowance.setText("0.00");
        txtGrossSalary.setText("0.00");
        txtPf.setText("0.00");
        txtProfTax.setText("200.00");
        txtOtherDeduction.setText("0.00");
        txtTotalDeduction.setText("0.00");
        txtNetSalary.setText("0.00");
        cmbPayMonth.setSelectedIndex(0);
        txtPayYear.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        cmbPaymentStatus.setSelectedIndex(0);
        payrollTable.clearSelection();
        txtEmpId.requestFocus();
    }
}
