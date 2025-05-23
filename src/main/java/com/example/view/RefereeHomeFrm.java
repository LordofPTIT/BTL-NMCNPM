package com.example.view;

import com.example.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefereeHomeFrm extends JFrame implements ActionListener {
    private User loggedInUser;
    private JButton btnPairingSetup;
    private JButton btnViewRankTable; // Tương lai
    private JButton btnViewMatchResult; // Tương lai
    private JButton btnLogout;

    public RefereeHomeFrm(User user) {
        super("Trang chủ Trọng tài: " + user.getFullName()); //
        this.loggedInUser = user;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350); //
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); //

        // Panel Chào mừng
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblWelcome = new JLabel("Chào mừng, " + loggedInUser.getFullName() + "!", SwingConstants.CENTER); //
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 16));
        welcomePanel.add(lblWelcome);
        add(welcomePanel, BorderLayout.NORTH);

        // Panel các nút chức năng
        JPanel buttonPanel = new JPanel();
        // Sử dụng GridBagLayout để các nút có kích thước bằng nhau và căn giữa
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 80, 10, 80); // Top, Left, Bottom, Right padding for buttons

        btnPairingSetup = new JButton("Xếp cặp Thi đấu"); //
        btnPairingSetup.setPreferredSize(new Dimension(200, 40)); // Kích thước nút
        btnPairingSetup.addActionListener(this);
        buttonPanel.add(btnPairingSetup, gbc);

        btnViewRankTable = new JButton("Xem Bảng Xếp Hạng"); // [cite: 94]
        btnViewRankTable.setPreferredSize(new Dimension(200, 40));
        btnViewRankTable.setEnabled(false); // Tạm thời vô hiệu hóa
        // btnViewRankTable.addActionListener(this); // Sẽ thêm action sau
        buttonPanel.add(btnViewRankTable, gbc);

        btnViewMatchResult = new JButton("Xem/Cập nhật Kết quả Trận đấu"); // [cite: 94]
        btnViewMatchResult.setPreferredSize(new Dimension(200, 40));
        btnViewMatchResult.setEnabled(false); // Tạm thời vô hiệu hóa
        // btnViewMatchResult.addActionListener(this); // Sẽ thêm action sau
        buttonPanel.add(btnViewMatchResult, gbc);

        // Panel trung tâm để chứa buttonPanel, giúp buttonPanel không bị kéo giãn toàn bộ
        JPanel centerPanel = new JPanel(new GridBagLayout()); // GridBagLayout để căn giữa buttonPanel
        centerPanel.add(buttonPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Nút Đăng xuất
        btnLogout = new JButton("Đăng xuất");
        btnLogout.addActionListener(this);
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(btnLogout);
        add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPairingSetup) {
            // Mở giao diện Xếp cặp Thi đấu
            new PairingSetupFrm(loggedInUser).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnViewRankTable) {
            // Mở giao diện Xem Bảng Xếp Hạng (chưa implement)
            JOptionPane.showMessageDialog(this, "Chức năng Xem Bảng Xếp Hạng sẽ được phát triển sau.");
        } else if (e.getSource() == btnViewMatchResult) {
            // Mở giao diện Xem Kết quả Trận đấu (chưa implement)
            JOptionPane.showMessageDialog(this, "Chức năng Xem Kết quả Trận đấu sẽ được phát triển sau.");
        } else if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginFrm().setVisible(true);
            }
        }
    }
}