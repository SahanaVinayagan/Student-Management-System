package sms;

import sms.RandomPasswordGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class AddStudentFrame extends JFrame {
    private JTextField nameField, usnField, branchField, semesterField, cgpaField;
    private JTable studentTable;

    public AddStudentFrame(JTable studentTable) {
        this.studentTable = studentTable;

        setTitle("Add New Student");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(7, 2, 10, 10));

        // Input fields
        add(new JLabel("Full Name:"));
        nameField = new JTextField(); add(nameField);

        add(new JLabel("USN:"));
        usnField = new JTextField(); add(usnField);

        add(new JLabel("Branch:"));
        branchField = new JTextField(); add(branchField);

        add(new JLabel("Semester:"));
        semesterField = new JTextField(); add(semesterField);

        add(new JLabel("CGPA:"));
        cgpaField = new JTextField(); add(cgpaField);

        // Buttons
        JButton addButton = new JButton("Add Student");
        addButton.addActionListener(e -> addStudent()); add(addButton);

        JButton importButton = new JButton("Import from CSV");
        importButton.addActionListener(e -> importFromCSV()); add(importButton);

        setVisible(true);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String usn = usnField.getText().trim();
        String branch = branchField.getText().trim();
        String semester = semesterField.getText().trim();
        String cgpa = cgpaField.getText().trim();

        if (name.isEmpty() || usn.isEmpty() || branch.isEmpty() || semester.isEmpty() || cgpa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            int sem = Integer.parseInt(semester);
            float gpa = Float.parseFloat(cgpa);

            String generatedPassword = RandomPasswordGenerator.generate();
            String hashedPassword = PasswordUtil.hashPassword(generatedPassword);

            try (Connection conn = Database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO tabletud (full_name, usn, branch, semester, cgpa, attendance_percent, days_present, days_total) VALUES (?, ?, ?, ?, ?, 0, 0, 0)"
                );
                stmt.setString(1, name);
                stmt.setString(2, usn);
                stmt.setString(3, branch);
                stmt.setInt(4, sem);
                stmt.setFloat(5, gpa);
                stmt.executeUpdate();

                PreparedStatement loginStmt = conn.prepareStatement(
                    "INSERT INTO student_logins (usn, password_hash, original_password) VALUES (?, ?, ?)"
                );
                loginStmt.setString(1, usn);
                loginStmt.setString(2, hashedPassword);
                loginStmt.setString(3, generatedPassword);
                loginStmt.executeUpdate();

                refreshAndSortTable();

                JOptionPane.showMessageDialog(this,
                    "Student added successfully!\nGenerated Password: " + generatedPassword);
                dispose();

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding student. USN might already exist.");
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Semester and CGPA.");
        }
    }

    private void importFromCSV() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File csvFile = chooser.getSelectedFile();
        int successCount = 0, failCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip CSV header
                }

                String[] parts = line.split(",");
                if (parts.length < 5) continue;

                String name = parts[0].trim();
                String usn = parts[1].trim();
                String branch = parts[2].trim();
                int semester;
                float cgpa;

                try {
                    semester = Integer.parseInt(parts[3].trim());
                    cgpa = Float.parseFloat(parts[4].trim());
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid semester/CGPA for USN " + usn);
                    failCount++;
                    continue;
                }

                String password = RandomPasswordGenerator.generate();
                String hashed = PasswordUtil.hashPassword(password);

                try (Connection conn = Database.getConnection()) {
                    PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO tabletud (full_name, usn, branch, semester, cgpa, attendance_percent, days_present, days_total) VALUES (?, ?, ?, ?, ?, 0, 0, 0)"
                    );
                    stmt.setString(1, name);
                    stmt.setString(2, usn);
                    stmt.setString(3, branch);
                    stmt.setInt(4, semester);
                    stmt.setFloat(5, cgpa);
                    stmt.executeUpdate();

                    PreparedStatement loginStmt = conn.prepareStatement(
                        "INSERT INTO student_logins (usn, password_hash, original_password) VALUES (?, ?, ?)"
                    );
                    loginStmt.setString(1, usn);
                    loginStmt.setString(2, hashed);
                    loginStmt.setString(3, password);
                    loginStmt.executeUpdate();

                    successCount++;
                } catch (SQLException ex) {
                    System.err.println("❌ Failed for USN " + usn + ": " + ex.getMessage());
                    failCount++;
                }
            }

            refreshAndSortTable();
            JOptionPane.showMessageDialog(this,
                "✅ Successfully imported: " + successCount + "\n❌ Failed: " + failCount + "\nCheck console for details.");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to read CSV.");
            e.printStackTrace();
        }
    }

    private void refreshAndSortTable() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM tabletud ORDER BY usn";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("usn"),
                    rs.getString("full_name"),
                    rs.getString("branch"),
                    rs.getInt("semester"),
                    rs.getFloat("cgpa"),
                    rs.getFloat("attendance_percent")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
