import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public Login() {
        setTitle("Event Management System - Login");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CENTER the panel in frame
       setLayout(new GridBagLayout());
getContentPane().setBackground(new Color(230, 230, 230)); // soft background

JPanel panel = new JPanel(new GridBagLayout());
panel.setPreferredSize(new Dimension(320, 220));
panel.setBackground(Color.WHITE);
panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200,200,200)),
        BorderFactory.createEmptyBorder(20, 25, 20, 25)
));

GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(10, 10, 10, 10);
gbc.anchor = GridBagConstraints.WEST;

// Title
JLabel title = new JLabel("Event Management System");
title.setFont(new Font("Segoe UI", Font.BOLD, 16));
title.setForeground(new Color(40,40,40));
gbc.gridx = 0;
gbc.gridy = 0;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.CENTER;
panel.add(title, gbc);

// Username
gbc.gridwidth = 1;
gbc.gridy = 1;
gbc.gridx = 0;
gbc.anchor = GridBagConstraints.WEST;
panel.add(new JLabel("Username:"), gbc);

gbc.gridx = 1;
txtUsername = new JTextField(14);
panel.add(txtUsername, gbc);

// Password
gbc.gridy = 2;
gbc.gridx = 0;
panel.add(new JLabel("Password:"), gbc);

gbc.gridx = 1;
txtPassword = new JPasswordField(14);
panel.add(txtPassword, gbc);

// Button
gbc.gridy = 3;
gbc.gridx = 0;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.CENTER;
btnLogin = new JButton("Login");
btnLogin.setPreferredSize(new Dimension(100,30));
panel.add(btnLogin, gbc);

add(panel);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginAction();
            }
        });

        setVisible(true);
    }

    private void loginAction() {
        String username = this.txtUsername.getText();
        String password = String.valueOf(this.txtPassword.getPassword());

        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("user_id");

                JOptionPane.showMessageDialog(this, "Login successful as " + role);

                if(role.equalsIgnoreCase("admin")) {
                    new AdminGUI();
                } else {
                    new ParticipantGUI(userId);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        } catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to DB");
        }
    }

    public static void main(String[] args) {
        new Login();
    }
}