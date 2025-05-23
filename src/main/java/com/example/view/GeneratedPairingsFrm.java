package com.example.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.example.controller.PairingController;
import com.example.model.Match;
import com.example.model.Tournament;
import com.example.model.User;

public class GeneratedPairingsFrm extends JDialog implements ActionListener { // JDialog để là cửa sổ con
    private User loggedInUser;
    private PairingController pairingController;
    private Tournament tournament;
    private int roundNumber;
    private List<Match> generatedMatches;
    private JFrame parentFrame; // Để quay lại

    private JTable tblGeneratedPairings;
    private DefaultTableModel pairingsTableModel;
    private JButton btnSavePairings;
    private JButton btnCancel;

    public GeneratedPairingsFrm(JFrame parent, User user, PairingController controller, Tournament t, int rn, List<Match> matches) {
        super(parent, "Các cặp đấu cho " + t.getName() + " - Vòng " + rn, true); // true for modal
        this.parentFrame = parent;
        this.loggedInUser = user;
        this.pairingController = controller;
        this.tournament = t;
        this.roundNumber = rn;
        this.generatedMatches = matches;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Các cặp đấu dự kiến cho Vòng " + roundNumber, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);

        // Bảng các cặp đấu
        String[] columnNames = {"Bàn số", "Cờ thủ 1 (Trắng)", "Elo 1", "Cờ thủ 2 (Đen)", "Elo 2"};
        pairingsTableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblGeneratedPairings = new JTable(pairingsTableModel);
        loadGeneratedPairings();
        JScrollPane scrollPane = new JScrollPane(tblGeneratedPairings);
        add(scrollPane, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSavePairings = new JButton("Lưu các cặp đấu");
        btnSavePairings.addActionListener(this);
        buttonPanel.add(btnSavePairings);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.addActionListener(this);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadGeneratedPairings() {
        pairingsTableModel.setRowCount(0);
        for (Match match : generatedMatches) {
            Vector<Object> row = new Vector<>();
            row.add(match.getTableNumber());
            row.add(match.getPlayer1Name());
            row.add(match.getPlayer1Details() != null ? match.getPlayer1Details().getInitialElo() : "N/A"); // Hoặc currentElo nếu có

            if (match.getPlayer2Id() != null) { // Không phải BYE
                row.add(match.getPlayer2Name());
                row.add(match.getPlayer2Details() != null ? match.getPlayer2Details().getInitialElo() : "N/A");
            } else {
                row.add("BYE");
                row.add(""); // Elo cho BYE
            }
            pairingsTableModel.addRow(row);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSavePairings) {
            try {
                boolean success = pairingController.saveNewRoundAndPairings(tournament.getId(), roundNumber, generatedMatches);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Đã lưu thành công lịch thi đấu cho Vòng " + roundNumber + "!", 
                        "Thành công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                    // Thông báo cho parentFrame (PairingSetupFrm) để cập nhật
                    if (parentFrame instanceof PairingSetupFrm) {
                        ((PairingSetupFrm) parentFrame).refreshAfterSave();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể lưu lịch thi đấu. Vui lòng kiểm tra:\n" +
                        "1. Vòng đấu đã tồn tại chưa\n" +
                        "2. Dữ liệu đầu vào có hợp lệ không\n" +
                        "3. Kết nối database có ổn định không", 
                        "Lỗi Lưu", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Đã xảy ra lỗi không mong muốn: " + ex.getMessage(),
                    "Lỗi Hệ Thống",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnCancel) {
            this.dispose();
        }
    }
}