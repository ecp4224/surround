package com.example.levis.trails.core;

public class Song {
    private long id;
    private String name;
    private String artist;
    private long user_id;
    private double latitude;
    private double longitude;
    private long timePosted;
    private String[] genre;

    private Song() { }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public long getUser_id() {
        return user_id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTimePosted() {
        return timePosted;
    }

    public String[] getGenre() {
        return genre;
    }
}
