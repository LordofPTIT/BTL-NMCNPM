package com.example.model;

import java.util.Date;

public class Match {
    private int id;
    private int roundId;
    private int player1Id;
    private ChessPlayer player1Details; // Để hiển thị tên
    private Integer player2Id; // Integer để có thể là null (trường hợp bye)
    private ChessPlayer player2Details; // Để hiển thị tên
    private String result; // "1-0", "0-1", "0.5-0.5", "BYE"
    private int tableNumber;
    private Date matchTime;

    public Match() {}

    // Constructor cho cặp đấu mới (chưa có ID, result, time)
    public Match(int roundId, int player1Id, Integer player2Id, int tableNumber) {
        this.roundId = roundId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.tableNumber = tableNumber;
    }


    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRoundId() { return roundId; }
    public void setRoundId(int roundId) { this.roundId = roundId; }
    public int getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(int player1Id) { this.player1Id = player1Id; }
    public ChessPlayer getPlayer1Details() { return player1Details; }
    public void setPlayer1Details(ChessPlayer player1Details) { this.player1Details = player1Details; }
    public Integer getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(Integer player2Id) { this.player2Id = player2Id; }
    public ChessPlayer getPlayer2Details() { return player2Details; }
    public void setPlayer2Details(ChessPlayer player2Details) { this.player2Details = player2Details; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    public Date getMatchTime() { return matchTime; }
    public void setMatchTime(Date matchTime) { this.matchTime = matchTime; }

    public String getPlayer1Name() {
        return player1Details != null ? player1Details.getName() : String.valueOf(player1Id);
    }

    public String getPlayer2Name() {
        if (player2Id == null) return "BYE";
        return player2Details != null ? player2Details.getName() : String.valueOf(player2Id);
    }
}