package com.example.model;

import java.util.Set;

public class Standing {
    private int rank; // Thứ hạng
    private int playerId;
    private String playerName;
    private int currentElo;
    private float totalPoints;
    private float sumOpponentPoints; // Tổng điểm các đối thủ đã gặp
    private Set<Integer> opponentsPlayedIds; // Danh sách ID các đối thủ đã gặp
    private boolean hasReceivedBye;


    public Standing(int rank, int playerId, String playerName, int currentElo, float totalPoints, float sumOpponentPoints, Set<Integer> opponentsPlayedIds, boolean hasReceivedBye) {
        this.rank = rank;
        this.playerId = playerId;
        this.playerName = playerName;
        this.currentElo = currentElo;
        this.totalPoints = totalPoints;
        this.sumOpponentPoints = sumOpponentPoints;
        this.opponentsPlayedIds = opponentsPlayedIds;
        this.hasReceivedBye = hasReceivedBye;
    }

    // Getters
    public int getRank() { return rank; }
    public int getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public int getCurrentElo() { return currentElo; }
    public float getTotalPoints() { return totalPoints; }
    public float getSumOpponentPoints() { return sumOpponentPoints; }
    public Set<Integer> getOpponentsPlayedIds() { return opponentsPlayedIds; }
    public boolean hasReceivedBye() { return hasReceivedBye; }

    // Setters (nếu cần)
    public void setRank(int rank) { this.rank = rank; }

}