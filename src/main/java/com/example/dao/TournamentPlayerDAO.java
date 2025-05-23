package com.example.dao;

import com.example.model.ChessPlayer;
import com.example.model.TournamentPlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentPlayerDAO extends DAO {

    public TournamentPlayerDAO() {
        super();
    }

    public List<TournamentPlayer> getTournamentPlayersByTournamentId(int tournamentId) {
        List<TournamentPlayer> tournamentPlayers = new ArrayList<>();
        // Sắp xếp theo điểm giảm dần, tổng điểm đối thủ giảm dần, elo hiện tại giảm dần
        // Đây là thứ tự cho việc xếp cặp từ vòng 2 trở đi
        String sql = "SELECT tp.*, cp.name as playerName, cp.birthYear, cp.nationality, cp.initialElo, cp.notes " +
                "FROM tblTournamentPlayer tp " +
                "JOIN tblChessPlayer cp ON tp.playerId = cp.id " +
                "WHERE tp.tournamentId = ? " +
                "ORDER BY tp.totalPoints DESC, tp.sumOpponentPoints DESC, tp.currentElo DESC";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TournamentPlayer tp = new TournamentPlayer();
                tp.setTournamentId(rs.getInt("tournamentId"));
                tp.setPlayerId(rs.getInt("playerId"));
                tp.setCurrentElo(rs.getInt("currentElo"));
                tp.setTotalPoints(rs.getFloat("totalPoints"));
                tp.setSumOpponentPoints(rs.getFloat("sumOpponentPoints"));
                tp.setOpponentsPlayedRaw(rs.getString("opponentsPlayed"));
                tp.setHasReceivedBye(rs.getBoolean("hasReceivedBye"));

                ChessPlayer cp = new ChessPlayer(
                        rs.getInt("playerId"), // id của cờ thủ
                        rs.getString("playerName"),
                        rs.getInt("birthYear"),
                        rs.getString("nationality"),
                        rs.getInt("initialElo"),
                        rs.getString("notes")
                );
                tp.setPlayerDetails(cp);
                tournamentPlayers.add(tp);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournamentPlayers;
    }

    // Lấy danh sách cờ thủ cho việc xếp cặp Vòng 1 (sắp xếp theo Elo ban đầu)
    public List<TournamentPlayer> getTournamentPlayersForRound1Pairing(int tournamentId) {
        List<TournamentPlayer> tournamentPlayers = new ArrayList<>();
        String sql = "SELECT tp.*, cp.name as playerName, cp.birthYear, cp.nationality, cp.initialElo, cp.notes " +
                "FROM tblTournamentPlayer tp " +
                "JOIN tblChessPlayer cp ON tp.playerId = cp.id " +
                "WHERE tp.tournamentId = ? " +
                "ORDER BY cp.initialElo DESC"; // Sắp xếp theo Elo ban đầu giảm dần
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TournamentPlayer tp = new TournamentPlayer();
                tp.setTournamentId(rs.getInt("tournamentId"));
                tp.setPlayerId(rs.getInt("playerId"));
                tp.setCurrentElo(rs.getInt("currentElo")); // Sẽ bằng initialElo
                tp.setTotalPoints(rs.getFloat("totalPoints")); // Sẽ là 0
                tp.setSumOpponentPoints(rs.getFloat("sumOpponentPoints")); // Sẽ là 0
                tp.setOpponentsPlayedRaw(rs.getString("opponentsPlayed")); // Sẽ là null/empty
                tp.setHasReceivedBye(rs.getBoolean("hasReceivedBye")); // Sẽ là false

                ChessPlayer cp = new ChessPlayer(
                        rs.getInt("playerId"),
                        rs.getString("playerName"),
                        rs.getInt("birthYear"),
                        rs.getString("nationality"),
                        rs.getInt("initialElo"),
                        rs.getString("notes")
                );
                tp.setPlayerDetails(cp);
                tournamentPlayers.add(tp);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournamentPlayers;
    }

    // Hàm này có thể cần thiết khi đăng ký cờ thủ vào giải đấu
    public boolean addPlayerToTournament(int tournamentId, int playerId, int initialElo) {
        String sql = "INSERT INTO tblTournamentPlayer (tournamentId, playerId, currentElo, totalPoints, sumOpponentPoints, opponentsPlayed, hasReceivedBye) " +
                "VALUES (?, ?, ?, 0, 0, '', FALSE) ON DUPLICATE KEY UPDATE currentElo = VALUES(currentElo)";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ps.setInt(2, playerId);
            ps.setInt(3, initialElo);
            int affectedRows = ps.executeUpdate();
            ps.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật sau khi một cờ thủ nhận bye
    public void updatePlayerByeStatus(int tournamentId, int playerId) {
        String sql = "UPDATE tblTournamentPlayer SET hasReceivedBye = TRUE WHERE tournamentId = ? AND playerId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật danh sách đối thủ đã gặp
    public void updateOpponentsPlayed(int tournamentId, int playerId, Set<Integer> opponentIds) {
        String opponentsPlayedStr = opponentIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "UPDATE tblTournamentPlayer SET opponentsPlayed = ? WHERE tournamentId = ? AND playerId = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, opponentsPlayedStr);
            ps.setInt(2, tournamentId);
            ps.setInt(3, playerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}