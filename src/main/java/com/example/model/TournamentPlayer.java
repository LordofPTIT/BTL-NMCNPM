package com.example.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentPlayer {
    private int tournamentId;
    private int playerId;
    private ChessPlayer playerDetails; // Để tiện lấy tên, elo gốc
    private int currentElo;
    private float totalPoints;
    private float sumOpponentPoints;
    private String opponentsPlayedRaw; // Chuỗi ID các đối thủ đã đấu
    private Set<Integer> opponentsPlayedIds; // Set các ID đối thủ đã đấu
    private boolean hasReceivedBye;

    public TournamentPlayer() {
        this.opponentsPlayedIds = new HashSet<>();
    }

    // Getters and Setters
    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public ChessPlayer getPlayerDetails() { return playerDetails; }
    public void setPlayerDetails(ChessPlayer playerDetails) { this.playerDetails = playerDetails; }
    public int getCurrentElo() { return currentElo; }
    public void setCurrentElo(int currentElo) { this.currentElo = currentElo; }
    public float getTotalPoints() { return totalPoints; }
    public void setTotalPoints(float totalPoints) { this.totalPoints = totalPoints; }
    public float getSumOpponentPoints() { return sumOpponentPoints; }
    public void setSumOpponentPoints(float sumOpponentPoints) { this.sumOpponentPoints = sumOpponentPoints; }
    public boolean hasReceivedBye() { return hasReceivedBye; }
    public void setHasReceivedBye(boolean hasReceivedBye) { this.hasReceivedBye = hasReceivedBye; }

    public String getOpponentsPlayedRaw() { return opponentsPlayedRaw; }
    public void setOpponentsPlayedRaw(String opponentsPlayedRaw) {
        this.opponentsPlayedRaw = opponentsPlayedRaw;
        if (opponentsPlayedRaw != null && !opponentsPlayedRaw.trim().isEmpty()) {
            this.opponentsPlayedIds = Arrays.stream(opponentsPlayedRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        } else {
            this.opponentsPlayedIds = new HashSet<>();
        }
    }

    public Set<Integer> getOpponentsPlayedIds() {
        return opponentsPlayedIds;
    }

    public void addOpponentPlayed(int opponentId) {
        this.opponentsPlayedIds.add(opponentId);
        this.opponentsPlayedRaw = this.opponentsPlayedIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    // Thuộc tính dùng cho hiển thị và sắp xếp
    public String getPlayerName() {
        return playerDetails != null ? playerDetails.getName() : "N/A";
    }
}