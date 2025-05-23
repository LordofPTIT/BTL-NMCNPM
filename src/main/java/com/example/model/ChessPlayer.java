package com.example.model;

public class ChessPlayer {
    private int id;
    private String name;
    private int birthYear;
    private String nationality;
    private int initialElo;
    private String notes;

    public ChessPlayer() {}

    public ChessPlayer(int id, String name, int birthYear, String nationality, int initialElo, String notes) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
        this.nationality = nationality;
        this.initialElo = initialElo;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getBirthYear() { return birthYear; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public int getInitialElo() { return initialElo; }
    public void setInitialElo(int initialElo) { this.initialElo = initialElo; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return name + " (Elo: " + initialElo + ")";
    }
}