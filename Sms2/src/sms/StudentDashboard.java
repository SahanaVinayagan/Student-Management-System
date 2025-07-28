package sms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentDashboard extends JFrame {

    private JTable studentTable;

    // Constructor that receives the USN to filter the student's data
    public StudentDashboard(String usn) {
        setTitle("Student Dashboard");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadStudentDetails(usn);  // Load the student's details using their USN
    }

    private void loadStudentDetails(String usn) {
        try (Connection conn = Database.getConnection()) {
            // Query to get student details based on their USN
            String query = "SELECT full_name, usn, branch, semester, cgpa, attendance_percent FROM tabletud WHERE usn = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, usn);  // Use the student's USN to fetch their data
            ResultSet rs = stmt.executeQuery();

            String[] columnNames = {"Full Name", "USN", "Branch", "Semester", "CGPA", "Attendance %"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            if (rs.next()) {
                String name = rs.getString("full_name");
                String branch = rs.getString("branch");
                int semester = rs.getInt("semester");
                float cgpa = rs.getFloat("cgpa");
                float attendance = rs.getFloat("attendance_percent");

                model.addRow(new Object[]{name, usn, branch, semester, cgpa, attendance});
            }

            studentTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(studentTable);
            add(scrollPane, BorderLayout.CENTER);

            setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student details.");
        }
    }
}
