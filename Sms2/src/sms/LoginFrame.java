package sms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        // Setup the login form
        setTitle("Login");
        setSize(350, 180);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Username (USN for students):"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());
        add(loginButton);

        setVisible(true);
    }

    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Please enter username and password.");
                return;
            }

            try (Connection conn = Database.getConnection()) {

                // Check for teacher login in tableuser
                String sql = "SELECT role FROM tableuser WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");

                    if ("teacher".equalsIgnoreCase(role)) {
                        new TeacherDashboard();
                        dispose();
                        return;
                    }
                }

                // Check for student login in student_logins
                sql = "SELECT * FROM student_logins WHERE usn = ? AND original_password = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    new StudentDashboard(username); // Pass USN
                    dispose();
                    return;
                }

                // If neither found
                JOptionPane.showMessageDialog(LoginFrame.this, "Invalid username or password.");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(LoginFrame.this, "Database error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
