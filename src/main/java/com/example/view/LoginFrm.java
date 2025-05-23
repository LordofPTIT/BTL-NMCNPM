package com.example.view;

import com.example.controller.PairingController; // Sẽ không dùng trực tiếp ở đây
import com.example.dao.UserDAO;
import com.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrm extends JFrame implements ActionListener {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UserDAO userDAO;

    public LoginFrm() {
        super("Đăng nhập Trọng tài"); // [cite: 93]
        userDAO = new UserDAO();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200); // [cite: 93]
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridBagLayout()); // [cite: 93]
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title "Login" (hoặc "Đăng nhập")
        JLabel lblTitle = new JLabel("Đăng nhập", SwingConstants.CENTER); // [cite: 93]
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        add(lblTitle, gbc);


        gbc.gridwidth = 1; // Reset gridwidth

        JLabel lblUsername = new JLabel("Tên đăng nhập:"); // [cite: 93]
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0; // Label không co giãn
        add(lblUsername, gbc);

        txtUsername = new JTextField(15); // [cite: 93]
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // TextField co giãn
        add(txtUsername, gbc);
        // Sample data for quick testing
        txtUsername.setText("refereeA");


        JLabel lblPassword = new JLabel("Mật khẩu:"); // [cite: 93]
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        add(lblPassword, gbc);

        txtPassword = new JPasswordField(15); // [cite: 93]
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        add(txtPassword, gbc);
        // Sample data for quick testing
        txtPassword.setText("Lct@28062004");


        btnLogin = new JButton("Đăng nhập"); // [cite: 93]
        btnLogin.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Button chiếm 2 cột
        gbc.anchor = GridBagConstraints.CENTER; // Căn giữa button
        gbc.fill = GridBagConstraints.NONE; // Button không co giãn
        gbc.weightx = 0;
        add(btnLogin, gbc);

        // For quick testing, prefill (remove for production)
        // txtUsername.setText("refereeA");
        // txtPassword.setText("Lct@28062004");

        pack(); // Adjust window size to components
        setMinimumSize(new Dimension(300, 180)); // Đặt kích thước tối thiểu
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập và mật khẩu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = userDAO.checkLogin(username, password);

            if (user != null && "REFEREE".equalsIgnoreCase(user.getRole())) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công với vai trò Trọng tài: " + user.getFullName(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                this.dispose(); // Đóng cửa sổ login
                new RefereeHomeFrm(user).setVisible(true); // Mở giao diện chính của trọng tài
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không chính xác, hoặc bạn không có quyền Trọng tài.", "Lỗi Đăng Nhập", JOptionPane.ERROR_MESSAGE); // [cite: 171]
            }
        }
    }

    public static void main(String[] args) {
        // Optional: Set Look and Feel (e.g., Nimbus for a more modern look)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to default look and feel.
        }
        SwingUtilities.invokeLater(() -> new LoginFrm().setVisible(true));
    }
}