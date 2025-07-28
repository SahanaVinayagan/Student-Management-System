package sms;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class EditStudentFrame extends JFrame {
    private JTextField usnField, nameField, semField, deptField, cgpaField;
    private JButton fetchBtn, updateBtn;

    public EditStudentFrame() {
        setTitle("Edit Student");
        setSize(400, 300);
        setLayout(null);

        JLabel usnLabel = new JLabel("Enter USN:");
        usnLabel.setBounds(30, 20, 100, 25);
        add(usnLabel);

        usnField = new JTextField();
        usnField.setBounds(150, 20, 200, 25);
        add(usnField);

        fetchBtn = new JButton("Fetch");
        fetchBtn.setBounds(150, 50, 80, 25);
        add(fetchBtn);

        nameField = new JTextField(); nameField.setBounds(150, 90, 200, 25); add(nameField);
        semField = new JTextField(); semField.setBounds(150, 120, 200, 25); add(semField);
        deptField = new JTextField(); deptField.setBounds(150, 150, 200, 25); add(deptField);
        cgpaField = new JTextField(); cgpaField.setBounds(150, 180, 200, 25); add(cgpaField);

        add(new JLabel("Name:")).setBounds(30, 90, 100, 25);
        add(new JLabel("Semester:")).setBounds(30, 120, 100, 25);
        add(new JLabel("Department:")).setBounds(30, 150, 100, 25);
        add(new JLabel("CGPA:")).setBounds(30, 180, 100, 25);

        updateBtn = new JButton("Update");
        updateBtn.setBounds(150, 220, 100, 30);
        add(updateBtn);

        fetchBtn.addActionListener(e -> fetchStudent());
        updateBtn.addActionListener(e -> updateStudent());
    }

    private void fetchStudent() {
        String usn = usnField.getText();
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tabletud WHERE usn = ?");
            stmt.setString(1, usn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("full_name"));
                semField.setText(rs.getString("semester"));
                deptField.setText(rs.getString("branch"));
                cgpaField.setText(rs.getString("cgpa"));
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateStudent() {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE tabletud SET full_name=?, semester=?, branch=?, cgpa=? WHERE usn=?");
            stmt.setString(1, nameField.getText());
            stmt.setString(2, semField.getText());
            stmt.setString(3, deptField.getText());
            stmt.setString(4, cgpaField.getText());
            stmt.setString(5, usnField.getText());

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
