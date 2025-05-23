package com.example.dao;

import com.example.model.Tournament;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TournamentDAO extends DAO {
    public TournamentDAO() {
        super();
    }

    public List<Tournament> getAllTournaments() {
        List<Tournament> tournaments = new ArrayList<>();
        String sql = "SELECT * FROM tblTournament ORDER BY year DESC, name ASC";
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Tournament tournament = new Tournament(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("year"),
                        rs.getInt("edition"),
                        rs.getString("location"),
                        rs.getString("description"),
                        rs.getString("rules")
                );
                tournaments.add(tournament);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournaments;
    }
    public Tournament getTournamentById(int tournamentId) {
        Tournament tournament = null;
        String sql = "SELECT * FROM tblTournament WHERE id = ?";
        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tournament = new Tournament(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("year"),
                        rs.getInt("edition"),
                        rs.getString("location"),
                        rs.getString("description"),
                        rs.getString("rules")
                );
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tournament;
    }
}