package sms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        // 🔍 Search Panel
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

            // ✅ No password column
            String[] columnNames = {
                "Full Name", "USN", "Branch", "Semester", "CGPA", "Days Present", "Total Days Marked", "Attendance %"
            };

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

                model.addRow(rowData);
            }

            table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student data.");
        }

        // 🔍 Search button action
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
}
