package sms;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class DeleteStudentFrame extends JFrame {
    private JTextField usnField;
    private JButton deleteBtn;

    public DeleteStudentFrame() {
        setTitle("Delete Student");
        setSize(300, 150);
        setLayout(null);

        JLabel usnLabel = new JLabel("Enter USN:");
        usnLabel.setBounds(30, 20, 100, 25);
        add(usnLabel);

        usnField = new JTextField();
        usnField.setBounds(120, 20, 130, 25);
        add(usnField);

        deleteBtn = new JButton("Delete");
        deleteBtn.setBounds(90, 60, 100, 30);
        add(deleteBtn);

        deleteBtn.addActionListener(e -> deleteStudent());
    }

    private void deleteStudent() {
        String usn = usnField.getText();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete USN: " + usn + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM tabletud WHERE usn = ?");
                stmt.setString(1, usn);
                int deleted = stmt.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
