package com.example.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.model.Round;

public class RoundDAO extends DAO {
    public RoundDAO() {
        super();
    }

    public List<Round> getRoundsByTournament(int tournamentId) {
        List<Round> rounds = new ArrayList<>();
        String sql = "SELECT * FROM tblRound WHERE tournamentId = ? ORDER BY roundNumber ASC";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Round round = new Round(
                        rs.getInt("id"),
                        rs.getInt("tournamentId"),
                        rs.getInt("roundNumber"),
                        rs.getString("name"),
                        rs.getTimestamp("startTime") != null ? new Date(rs.getTimestamp("startTime").getTime()) : null,
                        rs.getString("status")
                );
                rounds.add(round);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rounds;
    }

    // NEW METHOD to get only completed rounds
    public List<Round> getCompletedRoundsByTournament(int tournamentId) {
        List<Round> rounds = new ArrayList<>();
        String sql = "SELECT * FROM tblRound WHERE tournamentId = ? AND status = 'COMPLETED' ORDER BY roundNumber ASC";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Round round = new Round(
                        rs.getInt("id"),
                        rs.getInt("tournamentId"),
                        rs.getInt("roundNumber"),
                        rs.getString("name"),
                        rs.getTimestamp("startTime") != null ? new Date(rs.getTimestamp("startTime").getTime()) : null,
                        rs.getString("status")
                );
                rounds.add(round);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rounds;
    }


    public Round getLatestCompletedRound(int tournamentId) {
        Round round = null;
        String sql = "SELECT * FROM tblRound WHERE tournamentId = ? AND status = 'COMPLETED' ORDER BY roundNumber DESC LIMIT 1";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                round = new Round(
                        rs.getInt("id"),
                        rs.getInt("tournamentId"),
                        rs.getInt("roundNumber"),
                        rs.getString("name"),
                        rs.getTimestamp("startTime") != null ? new Date(rs.getTimestamp("startTime").getTime()) : null,
                        rs.getString("status")
                );
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return round;
    }

    public int getNextRoundNumberBasedOnExisting(int tournamentId) {
        int nextRoundNumber = 1;
        String sql = "SELECT MAX(roundNumber) AS maxRound FROM tblRound WHERE tournamentId = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nextRoundNumber = rs.getInt("maxRound") + 1;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nextRoundNumber;
    }

    public Round saveRound(Round round) {
        // Kiểm tra xem vòng đấu đã tồn tại chưa
        String checkSql = "SELECT id FROM tblRound WHERE tournamentId = ? AND roundNumber = ?";
        try {
            PreparedStatement checkPs = getConnection().prepareStatement(checkSql);
            checkPs.setInt(1, round.getTournamentId());
            checkPs.setInt(2, round.getRoundNumber());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                System.err.println("Vòng đấu " + round.getRoundNumber() + " đã tồn tại trong giải đấu này.");
                return null;
            }
            checkPs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String sql = "INSERT INTO tblRound (tournamentId, roundNumber, name, startTime, status) VALUES (?, ?, ?, ?, ?)";
        try {
            getConnection().setAutoCommit(false); // Bắt đầu transaction
            PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, round.getTournamentId());
            ps.setInt(2, round.getRoundNumber());
            ps.setString(3, round.getName());
            if (round.getStartTime() != null) {
                ps.setTimestamp(4, new Timestamp(round.getStartTime().getTime()));
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }
            ps.setString(5, round.getStatus());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        round.setId(generatedKeys.getInt(1));
                        getConnection().commit(); // Commit transaction
                        return round;
                    }
                }
            }
            getConnection().rollback(); // Rollback nếu không thành công
            return null;
        } catch (SQLException e) {
            try {
                getConnection().rollback(); // Rollback nếu có lỗi
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return null;
        } finally {
            try {
                getConnection().setAutoCommit(true); // Reset auto commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}