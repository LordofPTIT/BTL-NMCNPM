package com.example.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
import com.example.model.Round;
import com.example.model.Standing;
import com.example.model.Tournament;
import com.example.model.User;

public class PairingSetupFrm extends JFrame implements ActionListener {
    private User loggedInUser;
    private PairingController pairingController;

    private JComboBox<Tournament> cmbTournaments;
    private JComboBox<Object> cmbPreviousRounds; // Object to hold Round or String placeholder
    private JLabel lblCurrentRoundInfo;
    private JButton btnLoadStandings;
    private JButton btnGeneratePairings;
    private JButton btnBackToHome;
    private JButton btnViewPairings;

    private JTable tblStandings;
    private DefaultTableModel standingsTableModel;

    private Tournament selectedTournament;
    private int forWhichRoundNumber; // The round number we are generating pairings FOR
    private List<Standing> currentStandingsForPairing;
    private final String ROUND_1_PLACEHOLDER = "--- Cơ sở cho Vòng 1 (Elo ban đầu) ---";


    public PairingSetupFrm(User user) {
        super("Thiết lập Xếp cặp Thi đấu");
        this.loggedInUser = user;
        this.pairingController = new PairingController();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650); // Increased size
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel lựa chọn và thông tin
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Chọn Giải đấu:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        cmbTournaments = new JComboBox<>();
        loadTournaments();
        cmbTournaments.addActionListener(this);
        topPanel.add(cmbTournaments, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        topPanel.add(new JLabel("Chọn Vòng đấu cơ sở:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        cmbPreviousRounds = new JComboBox<>();
        cmbPreviousRounds.addActionListener(this);
        topPanel.add(cmbPreviousRounds, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth=2; gbc.weightx = 1.0;
        lblCurrentRoundInfo = new JLabel("Vui lòng chọn giải đấu.", SwingConstants.CENTER);
        lblCurrentRoundInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        topPanel.add(lblCurrentRoundInfo, gbc);


        JPanel buttonControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnLoadStandings = new JButton("1. Tải BXH / Chuẩn bị");
        btnLoadStandings.addActionListener(this);
        buttonControlPanel.add(btnLoadStandings);

        btnGeneratePairings = new JButton("2. Xếp cặp");
        btnGeneratePairings.addActionListener(this);
        btnGeneratePairings.setEnabled(false);
        buttonControlPanel.add(btnGeneratePairings);

        btnViewPairings = new JButton("Xem cặp đấu");
        btnViewPairings.addActionListener(this);
        btnViewPairings.setEnabled(false);
        buttonControlPanel.add(btnViewPairings);

        btnBackToHome = new JButton("Về Trang chủ");
        btnBackToHome.addActionListener(this);
        buttonControlPanel.add(btnBackToHome);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth=2;
        topPanel.add(buttonControlPanel, gbc);

        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Hạng", "ID Cờ thủ", "Tên Cờ thủ", "Elo", "Tổng Điểm", "Tổng Điểm ĐThủ", "Đã Bye"};
        standingsTableModel = new DefaultTableModel(columnNames, 0){
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblStandings = new JTable(standingsTableModel);
        JScrollPane scrollPane = new JScrollPane(tblStandings);
        add(scrollPane, BorderLayout.CENTER);

        if (cmbTournaments.getItemCount() > 0) {
            cmbTournaments.setSelectedIndex(0);
        } else {
            // No tournaments loaded, disable controls
            disablePairingControls();
            lblCurrentRoundInfo.setText("Không có giải đấu nào.");
        }
        // Initial call after loading tournaments
        actionTournamentSelected();
    }

    private void loadTournaments() {
        List<Tournament> tournaments = pairingController.getAllTournaments();
        cmbTournaments.removeAllItems(); // Clear before loading
        for (Tournament t : tournaments) {
            cmbTournaments.addItem(t);
        }
    }

    private void actionTournamentSelected() {
        selectedTournament = (Tournament) cmbTournaments.getSelectedItem();
        standingsTableModel.setRowCount(0); // Clear table
        btnGeneratePairings.setEnabled(false);
        btnViewPairings.setEnabled(false);
        currentStandingsForPairing = null;

        if (selectedTournament == null) {
            disablePairingControls();
            lblCurrentRoundInfo.setText("Vui lòng chọn giải đấu.");
            cmbPreviousRounds.removeAllItems();
            return;
        }

        btnLoadStandings.setEnabled(true);
        List<Round> completedRounds = pairingController.getCompletedRoundsByTournament(selectedTournament.getId());
        cmbPreviousRounds.removeAllItems();

        // Lấy vòng đấu tiếp theo dựa trên các vòng đã hoàn thành
        int nextRoundNumber = completedRounds.isEmpty() ? 1 : completedRounds.get(completedRounds.size() - 1).getRoundNumber() + 1;
        forWhichRoundNumber = nextRoundNumber;

        if (completedRounds.isEmpty()) {
            cmbPreviousRounds.addItem(ROUND_1_PLACEHOLDER);
            cmbPreviousRounds.setEnabled(false); // No previous rounds to select for Round 1
            lblCurrentRoundInfo.setText("Chuẩn bị xếp cặp cho Vòng 1 của giải " + selectedTournament.getName());
        } else {
            // Thêm tất cả các vòng đã hoàn thành vào combobox
            for (Round r : completedRounds) {
                cmbPreviousRounds.addItem(r);
            }
            // Chọn vòng hoàn thành gần nhất
            cmbPreviousRounds.setSelectedItem(completedRounds.get(completedRounds.size() - 1));
            cmbPreviousRounds.setEnabled(true);
            btnViewPairings.setEnabled(true);
            lblCurrentRoundInfo.setText("Cơ sở: " + completedRounds.get(completedRounds.size() - 1).getName() + 
                " | Chuẩn bị xếp cặp cho Vòng " + nextRoundNumber + " của giải " + selectedTournament.getName());
        }
    }

    private void actionPreviousRoundSelected() {
        standingsTableModel.setRowCount(0); // Clear table on selection change
        btnGeneratePairings.setEnabled(false);
        currentStandingsForPairing = null;
        updateRoundInfoAndForWhichRound();
    }

    private void updateRoundInfoAndForWhichRound() {
        if (selectedTournament == null) return;

        Object selectedItem = cmbPreviousRounds.getSelectedItem();
        int basedOnRoundNumber = 0; // Default for Round 1

        if (selectedItem instanceof Round) {
            Round prevRound = (Round) selectedItem;
            basedOnRoundNumber = prevRound.getRoundNumber();
            forWhichRoundNumber = basedOnRoundNumber + 1;
            lblCurrentRoundInfo.setText("Cơ sở: " + prevRound.getName() + 
                " | Chuẩn bị xếp cặp cho Vòng " + forWhichRoundNumber + " của giải " + selectedTournament.getName());
        } else if (ROUND_1_PLACEHOLDER.equals(selectedItem)) {
            // This case means we are pairing for Round 1
            forWhichRoundNumber = 1;
            lblCurrentRoundInfo.setText("Cơ sở: Elo ban đầu | Chuẩn bị xếp cặp cho Vòng " + 
                forWhichRoundNumber + " của giải " + selectedTournament.getName());
        }
    }


    private void loadAndDisplayStandings() {
        if (selectedTournament == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một giải đấu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object selectedPrevRoundItem = cmbPreviousRounds.getSelectedItem();
        int basedOnRoundNumber;

        if (selectedPrevRoundItem instanceof Round) {
            basedOnRoundNumber = ((Round) selectedPrevRoundItem).getRoundNumber();
        } else { // Assumed ROUND_1_PLACEHOLDER or initial state
            basedOnRoundNumber = 0; // Signal for Round 1 logic (based on initial Elo)
        }

        // The actual round number we are generating pairings FOR
        forWhichRoundNumber = basedOnRoundNumber + 1;

        currentStandingsForPairing = pairingController.getCurrentStandings(selectedTournament.getId(), basedOnRoundNumber);
        standingsTableModel.setRowCount(0);

        if (currentStandingsForPairing.isEmpty()) {
            String msg = (basedOnRoundNumber == 0) ? "Chưa có cờ thủ nào được đăng ký cho giải đấu này hoặc không tải được danh sách."
                    : "Không có dữ liệu bảng xếp hạng sau vòng đã chọn.";
            JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            btnGeneratePairings.setEnabled(false);
            return;
        }

        for (Standing standing : currentStandingsForPairing) {
            Vector<Object> row = new Vector<>();
            row.add(standing.getRank());
            row.add(standing.getPlayerId());
            row.add(standing.getPlayerName());
            row.add(standing.getCurrentElo()); // This Elo is context-dependent (initial for R1, current otherwise)
            row.add(standing.getTotalPoints());
            row.add(standing.getSumOpponentPoints());
            row.add(standing.hasReceivedBye() ? "Rồi" : "Chưa");
            standingsTableModel.addRow(row);
        }

        updateRoundInfoAndForWhichRound(); // Re-confirm the target round info
        lblCurrentRoundInfo.setText(lblCurrentRoundInfo.getText() + " - Đã tải BXH.");


        if (currentStandingsForPairing.size() < 2) {
            JOptionPane.showMessageDialog(this, "Cần ít nhất 2 cờ thủ để có thể xếp cặp.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            btnGeneratePairings.setEnabled(false);
        } else {
            btnGeneratePairings.setEnabled(true);
        }
    }

    private void disablePairingControls() {
        btnLoadStandings.setEnabled(false);
        btnGeneratePairings.setEnabled(false);
        btnViewPairings.setEnabled(false);
        cmbPreviousRounds.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cmbTournaments) {
            actionTournamentSelected();
        } else if (e.getSource() == cmbPreviousRounds) {
            actionPreviousRoundSelected();
            // Xử lý khi chọn vòng đấu
            Object selectedItem = cmbPreviousRounds.getSelectedItem();
            if (selectedItem instanceof Round) {
                btnViewPairings.setEnabled(true);
            } else {
                btnViewPairings.setEnabled(false);
            }
        } else if (e.getSource() == btnLoadStandings) {
            loadAndDisplayStandings();
        } else if (e.getSource() == btnGeneratePairings) {
            if (selectedTournament == null || currentStandingsForPairing == null || currentStandingsForPairing.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng tải Bảng xếp hạng trước khi xếp cặp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (currentStandingsForPairing.size() < 2) {
                JOptionPane.showMessageDialog(this, "Không đủ cờ thủ để xếp cặp (cần ít nhất 2).", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Match> generatedMatches = pairingController.generatePairings(selectedTournament.getId(), forWhichRoundNumber, currentStandingsForPairing);

            if (generatedMatches.isEmpty() && currentStandingsForPairing.size() >=1 ) {
                long nonByePlayerCount = generatedMatches.stream().filter(m -> m.getPlayer2Id() != null).count() * 2;
                if (generatedMatches.stream().anyMatch(m -> m.getPlayer2Id() == null)) nonByePlayerCount++;

                if (nonByePlayerCount < currentStandingsForPairing.size() && currentStandingsForPairing.size() > 1) {
                    JOptionPane.showMessageDialog(this, "Không thể tạo đủ cặp đấu cho tất cả cờ thủ hợp lệ. Có thể một số cờ thủ không tìm được đối thủ theo luật.", "Cảnh báo Xếp Cặp", JOptionPane.WARNING_MESSAGE);
                } else if (currentStandingsForPairing.size() > 1){
                    JOptionPane.showMessageDialog(this, "Không thể tạo cặp đấu. Vui lòng kiểm tra lại dữ liệu và luật.", "Lỗi Xếp Cặp", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (!generatedMatches.isEmpty() || currentStandingsForPairing.size() == 1) {
                new GeneratedPairingsFrm(this, loggedInUser, pairingController, selectedTournament, forWhichRoundNumber, generatedMatches).setVisible(true);
            } else if (currentStandingsForPairing.size() < 2) {
                JOptionPane.showMessageDialog(this, "Không đủ cờ thủ để xếp cặp.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }

        } else if (e.getSource() == btnViewPairings) {
            Object selectedItem = cmbPreviousRounds.getSelectedItem();
            if (selectedItem instanceof Round) {
                Round selectedRound = (Round) selectedItem;
                new ViewPairingsFrm(this, loggedInUser, pairingController, selectedTournament, selectedRound).setVisible(true);
            }
        } else if (e.getSource() == btnBackToHome) {
            this.dispose();
            new RefereeHomeFrm(loggedInUser).setVisible(true);
        }
    }

    // Method to be called from GeneratedPairingsFrm after successful save
    public void refreshAfterSave() {
        // Reload everything as if a new tournament was selected
        standingsTableModel.setRowCount(0);
        btnGeneratePairings.setEnabled(false);
        currentStandingsForPairing = null;

        if (selectedTournament != null) {
            List<Round> completedRounds = pairingController.getCompletedRoundsByTournament(selectedTournament.getId());
            cmbPreviousRounds.removeAllItems();

            // Lấy vòng đấu tiếp theo dựa trên các vòng đã hoàn thành
            int nextRoundNumber = completedRounds.isEmpty() ? 1 : completedRounds.get(completedRounds.size() - 1).getRoundNumber() + 1;
            forWhichRoundNumber = nextRoundNumber;

            if (completedRounds.isEmpty()) {
                cmbPreviousRounds.addItem(ROUND_1_PLACEHOLDER);
                cmbPreviousRounds.setEnabled(false);
                lblCurrentRoundInfo.setText("Chuẩn bị xếp cặp cho Vòng 1 của giải " + selectedTournament.getName());
            } else {
                for (Round r : completedRounds) {
                    cmbPreviousRounds.addItem(r);
                }
                // Chọn vòng hoàn thành gần nhất
                cmbPreviousRounds.setSelectedItem(completedRounds.get(completedRounds.size() - 1));
                cmbPreviousRounds.setEnabled(true);
                btnViewPairings.setEnabled(true);
                lblCurrentRoundInfo.setText("Cơ sở: " + completedRounds.get(completedRounds.size() - 1).getName() + 
                    " | Chuẩn bị xếp cặp cho Vòng " + nextRoundNumber + " của giải " + selectedTournament.getName());
            }
        } else {
            actionTournamentSelected(); // Fallback to reload all tournaments if selectedTournament became null
        }
    }
}