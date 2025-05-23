package com.example.model;

import java.util.Date;

public class Round {
    private int id;
    private int tournamentId;
    private int roundNumber;
    private String name;
    private Date startTime;
    private String status; // PENDING, COMPLETED, ACTIVE

    public Round() {}

    public Round(int id, int tournamentId, int roundNumber, String name, Date startTime, String status) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.roundNumber = roundNumber;
        this.name = name;
        this.startTime = startTime;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }
    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { // Quan trọng cho JComboBox
        return name != null ? name : "Vòng " + roundNumber;
    }
}