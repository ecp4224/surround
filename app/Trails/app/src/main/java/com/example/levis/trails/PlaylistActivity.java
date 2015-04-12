package com.example.levis.trails;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.levis.trails.core.Song;
import com.example.levis.trails.core.TrailsServer;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

public class PlaylistActivity extends ActionBarActivity {

    private static final AdapterView.OnItemClickListener DYNAMIC_ITEM_CLICKED = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < 0 || position >= MainActivity.user.getUserSongs().size())
                return;
            Song s = MainActivity.user.getUserSongs().get(position);
            if (s == null)
                return;

            TrailsServer.playSong(s.getSongName(), s.getArtist(), parent.getContext());
        }
    };
    private TrackAdapter dynamicListAdapter;
    private DynamicListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_activity);

        setupListView();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff009590));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupListView() {
        listView = (DynamicListView)findViewById(R.id.list_items);
        dynamicListAdapter = new TrackAdapter(this);

        OnDismissCallback myOnDismissCallback = new OnDismissCallback() {

            @Override
            public void onDismiss(ViewGroup viewGroup, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    Song s = (Song) dynamicListAdapter.getItem(position);
                    dynamicListAdapter.remove(position);
                    MainActivity.user.removeSong(s);
                }
            }
        };

        listView.enableSwipeToDismiss(myOnDismissCallback);

        listView.setAdapter(dynamicListAdapter);
        listView.setOnItemClickListener(DYNAMIC_ITEM_CLICKED);

        dynamicListAdapter.addAll(MainActivity.user.getUserSongs());
    }
}
