package sms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ViewStudentsFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public ViewStudentsFrame(boolean isTeacher) {
        setTitle("View Students");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setupUI(isTeacher);
        setVisible(true);
    }

    private void setupUI(boolean isTeacher) {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by USN:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        try (Connection conn = Database.getConnection()) {
            String query = "SELECT full_name, usn, branch, semester, cgpa, days_present, days_total, attendance_percent FROM tabletud ORDER BY usn";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            String[] columnNames = {
                "Full Name", "USN", "Branch", "Semester", "CGPA", "Days Present", "Total Days Marked", "Attendance %"
            };

            if (isTeacher) {
                columnNames = addPasswordColumn(columnNames);
            }

            model = new DefaultTableModel(columnNames, 0);
            while (rs.next()) {
                Object[] rowData = new Object[]{
                    rs.getString("full_name"),
                    rs.getString("usn"),
                    rs.getString("branch"),
                    rs.getInt("semester"),
                    rs.getFloat("cgpa"),
                    rs.getInt("days_present"),
                    rs.getInt("days_total"),
                    rs.getFloat("attendance_percent")
                };

                if (isTeacher) {
                    String password = getStudentPassword(rs.getString("usn"));
                    rowData = addPasswordToRow(rowData, password);
                }

                model.addRow(rowData);
            }

            table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student data.");
        }

        // Search button action
        searchButton.addActionListener(e -> highlightUSN(searchField.getText().trim()));

        add(mainPanel);
    }

    private void highlightUSN(String usn) {
        if (usn.isEmpty()) return;

        for (int i = 0; i < table.getRowCount(); i++) {
            String tableUSN = table.getValueAt(i, 1).toString(); // column index 1 is USN
            if (tableUSN.equalsIgnoreCase(usn)) {
                table.setRowSelectionInterval(i, i);         // select the matching row
                table.scrollRectToVisible(table.getCellRect(i, 0, true));  // scroll to row
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "USN not found.");
    }

    private String[] addPasswordColumn(String[] columnNames) {
        String[] extended = new String[columnNames.length + 1];
        System.arraycopy(columnNames, 0, extended, 0, columnNames.length);
        extended[columnNames.length] = "Password";
        return extended;
    }

    private Object[] addPasswordToRow(Object[] rowData, String password) {
        Object[] extended = new Object[rowData.length + 1];
        System.arraycopy(rowData, 0, extended, 0, rowData.length);
        extended[rowData.length] = password;
        return extended;
    }

    private String getStudentPassword(String usn) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT original_password FROM student_logins WHERE usn = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("original_password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }
}
