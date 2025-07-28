package sms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MarkAttendanceFrame extends JFrame {
    private JTable table;
    private JButton submitBtn;
    private ArrayList<String> usnList = new ArrayList<>(); 

    public MarkAttendanceFrame() {
        setTitle("Mark Attendance");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setupUI();
        loadStudents();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
    }

    private void loadStudents() {
        try (Connection conn = Database.getConnection()) {
            String query = "SELECT full_name, usn FROM tabletud";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            ArrayList<String[]> rows = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("full_name");
                String usn = rs.getString("usn");
                rows.add(new String[]{name, usn});
                usnList.add(usn); 
            }

            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found.");
                dispose();
                return;
            }

            String[] columnNames = {"Full Name", "USN", "Status (1=Present, 0=Absent)"};
            Object[][] data = new Object[rows.size()][3];

            for (int i = 0; i < rows.size(); i++) {
                data[i][0] = rows.get(i)[0];
                data[i][1] = rows.get(i)[1];
                data[i][2] = "1"; // Default: Present
            }

            DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                public boolean isCellEditable(int row, int column) {
                    return column == 2; // Only the attendance column is editable
                }
            };

            table = new JTable(model);
            table.setRowHeight(30);

            JComboBox<String> attendanceBox = new JComboBox<>(new String[]{"1", "0"});
            table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(attendanceBox));

            submitBtn = new JButton("Submit Attendance");
            submitBtn.addActionListener(e -> saveAttendance());

            add(new JScrollPane(table), BorderLayout.CENTER);
            add(submitBtn, BorderLayout.SOUTH);

            setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student list.");
        }
    }

    private void saveAttendance() {
        try (Connection conn = Database.getConnection()) {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); // Get today's date

            for (int i = 0; i < usnList.size(); i++) {
                String usn = usnList.get(i);
                String statusStr = table.getValueAt(i, 2).toString().trim();
                int status = Integer.parseInt(statusStr);

                // Insert attendance data into the attendance_log table
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO attendance_log (usn, class_date, status) VALUES (?, ?, ?)"
                );
                stmt.setString(1, usn);
                stmt.setString(2, today); // Use today's date
                stmt.setInt(3, status);
                stmt.executeUpdate();

                // Get total attendance days for the student
                PreparedStatement totalStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM attendance_log WHERE usn = ?"
                );
                totalStmt.setString(1, usn);
                ResultSet totalRs = totalStmt.executeQuery();
                totalRs.next();
                int total = totalRs.getInt(1);

                // Get total days present for the student
                PreparedStatement presentStmt = conn.prepareStatement(
                        "SELECT COUNT(*) FROM attendance_log WHERE usn = ? AND status = 1"
                );
                presentStmt.setString(1, usn);
                ResultSet presentRs = presentStmt.executeQuery();
                presentRs.next();
                int present = presentRs.getInt(1);

                // Calculate attendance percentage
                float percent = total > 0 ? (present * 100f / total) : 0f;
                percent = Math.round(percent * 100.0f) / 100.0f; // Round to 2 decimal places

                // Update attendance information in tabletud table
                PreparedStatement updateStmt = conn.prepareStatement(
                        "UPDATE tabletud SET attendance_percent = ?, days_present = ?, days_total = ? WHERE usn = ?"
                );
                updateStmt.setFloat(1, percent);
                updateStmt.setInt(2, present);
                updateStmt.setInt(3, total);
                updateStmt.setString(4, usn);
                updateStmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Attendance saved and updated!");
            dispose(); // Close the attendance frame

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving attendance.");
        }
    }

    public static void main(String[] args) {
        new MarkAttendanceFrame();  // Show the Mark Attendance frame
    }
}
