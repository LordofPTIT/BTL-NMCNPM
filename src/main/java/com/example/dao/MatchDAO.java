package com.example.dao;

import com.example.model.Match;

import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MatchDAO extends DAO {
    public MatchDAO() {
        super();
    }

    public boolean saveMatches(List<Match> matches, int roundId) {
        // Giả sử round đã được tạo và có roundId
        String sql = "INSERT INTO tblMatch (roundId, player1Id, player2Id, tableNumber, result, matchTime) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            getConnection().setAutoCommit(false); // Bắt đầu transaction
            PreparedStatement ps = getConnection().prepareStatement(sql);

            for (Match match : matches) {
                ps.setInt(1, roundId); // roundId từ round mới tạo
                ps.setInt(2, match.getPlayer1Id());
                if (match.getPlayer2Id() != null) {
                    ps.setInt(3, match.getPlayer2Id());
                } else {
                    ps.setNull(3, Types.INTEGER); // Cho trường hợp BYE
                }
                ps.setInt(4, match.getTableNumber());
                ps.setString(5, match.getPlayer2Id() == null ? "BYE" : null); // Kết quả ban đầu là null, hoặc "BYE"
                if (match.getMatchTime() != null) {
                    ps.setTimestamp(6, new Timestamp(match.getMatchTime().getTime()));
                } else {
                    ps.setTimestamp(6, new Timestamp(new Date().getTime())); // Hoặc null nếu chưa có giờ cụ thể
                }
                ps.addBatch();
            }
            ps.executeBatch();
            getConnection().commit(); // Kết thúc transaction
            ps.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                getConnection().rollback(); // Rollback nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Set<String> getPlayedPairsInTournament(int tournamentId) {
        Set<String> playedPairs = new HashSet<>();
        // Lấy tất cả các cặp đã đấu trong các vòng của giải đấu này
        String sql = "SELECT m.player1Id, m.player2Id " +
                "FROM tblMatch m " +
                "JOIN tblRound r ON m.roundId = r.id " +
                "WHERE r.tournamentId = ? AND m.player2Id IS NOT NULL";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int p1 = rs.getInt("player1Id");
                int p2 = rs.getInt("player2Id");
                // Lưu cặp theo thứ tự nhỏ hơn -> lớn hơn để tránh trùng lặp (1-2 và 2-1)
                String pairKey = Math.min(p1, p2) + "-" + Math.max(p1, p2);
                playedPairs.add(pairKey);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playedPairs;
    }
}