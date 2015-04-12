package com.example.levis.trails.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Levis on 4/12/2015.
 */
public class User {

    private ArrayList<Song> usersongs;
    private ArrayList<Song> dynamicSongs;
    private String name;
    public double longitude=0, latitude=0;

    //Constructor
    public User(String username){
        name = username;
        usersongs = new ArrayList<Song>();
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
    public void updateDynamicSongs(ArrayList<Song>dbSongs){
        ArrayList<Song> ourlist;

        Map<Integer,Song> map = new HashMap<Integer,Song>();

        for(Integer i=0;i<usersongs.size();i++){
            map.put(i,usersongs.get(i));
        }
        for(Integer j=0;j<dbSongs.size();j++){
            map.put(j,dbSongs.get(j));
        }

        dynamicSongs = (ArrayList<Song>) map.values();

    }
}
