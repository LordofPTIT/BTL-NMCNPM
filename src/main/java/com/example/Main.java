package com.example;

import com.example.view.LoginFrm;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Nên đặt Look and Feel ở đây để áp dụng cho toàn bộ ứng dụng
        try {
            // Cố gắng sử dụng Nimbus Look and Feel cho giao diện hiện đại hơn
            boolean nimbusFound = false;
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    nimbusFound = true;
                    break;
                }
            }
            // Nếu không có Nimbus, sử dụng Look and Feel mặc định của hệ thống
            if (!nimbusFound) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            // Nếu có lỗi, sử dụng Look and Feel mặc định của Java (Metal)
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace(); // In lỗi nếu không thể đặt cả L&F mặc định
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrm().setVisible(true);
            }
        });
    }
}