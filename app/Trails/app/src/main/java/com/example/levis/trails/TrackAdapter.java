package com.example.levis.trails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.levis.trails.core.Song;
import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends BaseAdapter {
    public List<Song> songlist; //Our global dynamic song list
    private LayoutInflater mInflater;

    public TrackAdapter(Context context) {
        songlist = new ArrayList<Song>();
        mInflater = LayoutInflater.from(context);
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.music_list_item, null);

            holder = new ViewHolder();

            holder.art = (ImageView)convertView.findViewById(R.id.album_art);
            holder.name = (TextView)convertView.findViewById(R.id.song_name);
            holder.number = (TextView)convertView.findViewById(R.id.song_number);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Song song = songlist.get(position);

        holder.name.setText(song.getArtist() + " - " + song.getSongName());
        holder.number.setText("" + (position + 1));

        return convertView;
    }

    public void setSongs(List<Song> songs) {
        songlist.addAll(songs);
    }

    public void addSong(Song song){
        songlist.add(0,song);
    }

    public void removeSongAt(int position){
        songlist.remove(position);
    }

    static class ViewHolder {
        TextView name;
        TextView number;
        ImageView art;
    }

}
