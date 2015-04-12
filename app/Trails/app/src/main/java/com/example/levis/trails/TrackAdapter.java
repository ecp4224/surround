package com.example.levis.trails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.levis.trails.core.Song;
import com.nhaarman.listviewanimations.ArrayAdapter;
import com.nhaarman.listviewanimations.util.Insertable;

public class TrackAdapter extends ArrayAdapter<Song> implements Insertable<Song> {
    private LayoutInflater mInflater;
    private boolean isPlaylist;

    public TrackAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public TrackAdapter(Context context, boolean isPlaylist) {
        mInflater = LayoutInflater.from(context);
        this.isPlaylist = isPlaylist;
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

            if (isPlaylist) {
                //TODO Do something
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        Song song = super.getItem(position);

        holder.name.setText(song.getArtist() + " - " + song.getSongName());
        holder.number.setText("" + (position + 1));

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView number;
        ImageView art;
    }

}
