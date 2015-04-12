package com.example.levis.trails;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.levis.trails.core.Song;
import java.util.ArrayList;

public class TrackAdapter extends BaseAdapter {
    public ArrayList<Song> songlist; //Our global dynamic song list
    public ArrayList<Song> usersongs;

    public TrackAdapter(){
        songlist = new ArrayList<Song>();
    }

    @Override
    public int getCount() {
        return songlist.size();
    }

    @Override
    public Object getItem(int position) {

        return songlist.get(position);
    }

    @Override
    public long getItemId(int position) {

        return songlist.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void addSong(Song song){
        songlist.add(0,song);
    }

    public void removeSongAt(int position){
        songlist.remove(position);
    }

    public ArrayList<Song> listOfSongs(){
        return songlist;
    }

}
