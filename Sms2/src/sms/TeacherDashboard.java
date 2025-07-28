package sms;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class TeacherDashboard extends JFrame {
    private JButton addStudentBtn;
    private JButton markAttendanceBtn;
    private JButton viewStudentsBtn;
    private JButton updateCGPABtn;
    private JButton exportCSVBtn;
    private JButton modifyStudentBtn;
    private JButton deleteStudentBtn;
    private JButton logoutBtn;

    private JTable studentTable;  // Add the JTable reference here

    public TeacherDashboard() {
        setTitle("Teacher Dashboard");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 1, 10, 10));  // 8 rows for 8 buttons

        // Initialize buttons
        addStudentBtn = new JButton("Add Student");
        markAttendanceBtn = new JButton("Mark Attendance");
        viewStudentsBtn = new JButton("View Students");
        updateCGPABtn = new JButton("Update CGPA");
        exportCSVBtn = new JButton("Export Students to CSV");
        modifyStudentBtn = new JButton("Modify Student");
        deleteStudentBtn = new JButton("Delete Student");
        logoutBtn = new JButton("Logout");

        // Initialize JTable
        studentTable = new JTable(new DefaultTableModel(
            new Object[]{"USN", "Full Name", "Branch", "Semester", "CGPA", "Attendance %"}, 0));

        // Add buttons to the frame
        add(addStudentBtn);
        add(markAttendanceBtn);
        add(viewStudentsBtn);
        add(updateCGPABtn);
        add(exportCSVBtn);
        add(modifyStudentBtn);
        add(deleteStudentBtn);
        add(logoutBtn);

        // Set button actions
        addStudentBtn.addActionListener(e -> openAddStudentFrame());
        markAttendanceBtn.addActionListener(e -> openMarkAttendanceFrame());
        viewStudentsBtn.addActionListener(e -> openViewStudentsFrame());  // Pass true for teacher
        updateCGPABtn.addActionListener(e -> openUpdateCGPAFrame());
        exportCSVBtn.addActionListener(e -> exportStudentsData());
        modifyStudentBtn.addActionListener(e -> openModifyStudentFrame());
        deleteStudentBtn.addActionListener(e -> openDeleteStudentFrame());
        logoutBtn.addActionListener(e -> logout());

        setVisible(true);
    }

    // Method to open AddStudentFrame and pass the JTable reference
    private void openAddStudentFrame() {
        new AddStudentFrame(studentTable);  // Pass the JTable reference to the AddStudentFrame constructor
    }

    private void openMarkAttendanceFrame() {
        new MarkAttendanceFrame();
    }

    private void openViewStudentsFrame() {
        new ViewStudentsFrame(true);  // Pass true for teacher to view passwords
    }

    private void openUpdateCGPAFrame() {
        new UpdateCGPAFrame();
    }

    private void exportStudentsData() {
        ExportStudentsToCSV.exportData(); // Assumes you have this class/method implemented
    }

    private void openModifyStudentFrame() {
        new EditStudentFrame().setVisible(true);
    }

    private void openDeleteStudentFrame() {
        new DeleteStudentFrame().setVisible(true);
    }

    private void logout() {
        dispose(); // Close dashboard
        new LoginFrame(); // Show login again
    }
}
