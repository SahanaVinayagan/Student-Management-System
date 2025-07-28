package sms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UpdateCGPAFrame extends JFrame {
    private JTextField usnField, cgpaField;

    public UpdateCGPAFrame() {
        setTitle("Update Student CGPA");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("USN:"));
        usnField = new JTextField();
        add(usnField);

        add(new JLabel("New CGPA:"));
        cgpaField = new JTextField();
        add(cgpaField);

        JButton updateButton = new JButton("Update CGPA");
        add(updateButton);

        updateButton.addActionListener(e -> updateCGPA());

        setVisible(true);
    }

    private void updateCGPA() {
        String usn = usnField.getText();
        String cgpa = cgpaField.getText();

        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE tabletud SET cgpa = ? WHERE usn = ?"
            );
            stmt.setFloat(1, Float.parseFloat(cgpa));
            stmt.setString(2, usn);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "CGPA updated successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No student found with that USN.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating CGPA.");
        }
    }
}
