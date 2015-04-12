package com.example.levis.trails.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class User {

    private ArrayList<Song> usersongs;
    private List<Song> dynamicSongs;
    private String name;
    public double longitude=0, latitude=0;

    //Constructor
    public User(String username){
        name = username;
        usersongs = new ArrayList<Song>();
    }

    public List<Song> getDynamicSongs() {
        return dynamicSongs;
    }

    //Parameters: Nothing
    //Return: An Arraylist of the user's songs
    public ArrayList<Song> getUserSongs(){
        return usersongs;
    }

    //Parameters: An ArrayList of songs
    //Return: nothing
    public void addSongs(ArrayList<Song> songs){
        usersongs = songs;
    }

    //Parameters: A database of songs
    // Return: void
    public void updateDynamicSongs(List<Song> dbSongs){
        ArrayList<Song> ourlist;

        Iterator<Song> songs = dbSongs.iterator();
        while (songs.hasNext()) {
            Song s = songs.next();
            if (usersongs.contains(s))
                songs.remove();
        }

        dynamicSongs = dbSongs;

    }

    public void addToPlaylistSong(Song s) {
        usersongs.add(s);
    }
}
