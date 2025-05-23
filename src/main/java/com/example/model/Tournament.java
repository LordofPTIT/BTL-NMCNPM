package com.example.model;

public class Tournament {
    private int id;
    private String name;
    private int year;
    private int edition;
    private String location;
    private String description;
    private String rules;

    public Tournament() {}

    public Tournament(int id, String name, int year, int edition, String location, String description, String rules) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.edition = edition;
        this.location = location;
        this.description = description;
        this.rules = rules;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getEdition() { return edition; }
    public void setEdition(int edition) { this.edition = edition; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    @Override
    public String toString() { // Quan trọng cho JComboBox
        return name + " (" + year + ")";
    }
}