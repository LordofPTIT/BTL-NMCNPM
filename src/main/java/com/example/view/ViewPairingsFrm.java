package com.example.view;

import com.example.controller.PairingController;
import com.example.model.Match;
import com.example.model.Round;
import com.example.model.Tournament;
import com.example.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewPairingsFrm extends JDialog implements ActionListener {
    private final User loggedInUser;
    private final PairingController pairingController;
    private final Tournament tournament;
    private final Round selectedRound;
    private JTable pairingsTable;
    private DefaultTableModel pairingsTableModel;
    private JButton btnClose;

    public ViewPairingsFrm(JFrame parent, User user, PairingController controller, Tournament t, Round r) {
        super(parent, "Danh sách cặp đấu - " + t.getName() + " - " + r.getName(), true);
        this.loggedInUser = user;
        this.pairingController = controller;
        this.tournament = t;
        this.selectedRound = r;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("Danh sách cặp đấu - " + r.getName(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle, BorderLayout.NORTH);

        // Bảng các cặp đấu
        String[] columnNames = {"Bàn số", "Cờ thủ 1 (Trắng)", "Elo 1", "Cờ thủ 2 (Đen)", "Elo 2", "Kết quả"};
        pairingsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pairingsTable = new JTable(pairingsTableModel);
        loadPairings();
        JScrollPane scrollPane = new JScrollPane(pairingsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnClose = new JButton("Đóng");
        btnClose.addActionListener(this);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPairings() {
        List<Match> matches = pairingController.getMatchesByRound(selectedRound.getId());
        pairingsTableModel.setRowCount(0);

        for (Match match : matches) {
            String player1Name = match.getPlayer1Details() != null ? match.getPlayer1Details().getName() : "Unknown";
            String player2Name = match.getPlayer2Details() != null ? match.getPlayer2Details().getName() : "BYE";
            int player1Elo = match.getPlayer1Details() != null ? match.getPlayer1Details().getInitialElo() : 0;
            int player2Elo = match.getPlayer2Details() != null ? match.getPlayer2Details().getInitialElo() : 0;
            String result = match.getResult() != null ? match.getResult() : "";

            pairingsTableModel.addRow(new Object[]{
                    match.getTableNumber(),
                    player1Name,
                    player1Elo,
                    player2Name,
                    player2Elo,
                    result
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose) {
            this.dispose();
        }
    }
} 