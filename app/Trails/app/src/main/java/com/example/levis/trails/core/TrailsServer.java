package com.example.levis.trails.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TrailsServer {
    private static final String URL = "http://45.55.186.104:8080/";
    private static final String songsite = "http://developer.echonest.com/";
    private static Gson GSON = new Gson();
    private static RequestQueue queue;
    public static String apiKey = "TD3ZI08H9ARMYGGUZ";
    public static String consumerKey = "3837c1b220f7bc2939aeaa96d60a190a";
    public static String sharedSecret = "ZgEV5LUVQdyvsxc7opcG0A";

    public static void initQueue(Context context) {
        queue = Volley.newRequestQueue(context);
    }
    public static void fetchSongs(double longitude, double latitude, Context context, final PRunnable<List<Song>> onComplete) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + "api/fetch?lat=" + latitude + "&long=" + longitude,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Song[] songs = GSON.fromJson(s, Song[].class);
                        onComplete.run(new ArrayList<>(Arrays.asList(songs)));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("trails", "ERROR: " + volleyError.getMessage());
            }
        });

        queue.add(stringRequest);
    }

    public static void pushToServer(final double longitude, final double latitude, final String songName, final String artist, final Runnable completed) {
        StringRequest sr = new StringRequest(Request.Method.POST, URL + "api/post", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                completed.run();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("trails", "ERROR: " + error.getMessage());
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("lat", String.valueOf(latitude));
                params.put("long", String.valueOf(longitude));
                params.put("songName", songName);
                params.put("artist", artist);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(sr);
    }

    public static void createListener(Context context, final PRunnable<String[]> data) {
        IntentFilter iF = new IntentFilter();


        iF.addCategory("ComponentInfo");
        iF.addCategory("com.spotify.mobile.android.service.SpotifyIntentService");
        iF.addCategory("com.spotify.mobile.android.service.SpotifyService");


        iF.addAction("com.spotify.mobile.android.ui.widget.SpotifyWidget");
        iF.addAction("ComponentInfo");
        iF.addAction("com.spotify");
        iF.addAction("com.spotify.mobile.android.service.SpotifyIntentService");
        iF.addAction("com.spotify.mobile.android.service.SpotifyService");


        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");
        iF.addAction("com.spotify.mobile.android.ui");

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String artist = intent.getStringExtra("artist");
                String track = intent.getStringExtra("track");
                data.run(new String[] { artist, track });
            }
        }, iF);
    }

    public static void playSong(String songName,String artistName,Context someContext){
        String QUERY = artistName+" - "+songName;

//        songName=songName.replaceAll(" ","%20");
//        songName+="&bucket=id:7digital-US&bucket=audio_summary&bucket=tracks";

        String url = "https://www.youtube.com/embed?listType=search&list="+QUERY+"&autoplay=true";


        Intent videoClient = new Intent(Intent.ACTION_VIEW);
        videoClient.setData(Uri.parse(url));
        videoClient.setClassName("com.google.android.youtube", "com.google.android.youtube.WatchActivity");
        someContext.startActivity(videoClient);

/*
        StringRequest stringRequest = new StringRequest(Request.Method.GET, songsite + "api/v4/song/search?api_key=" + apiKey + "&format=json&results="+
                "=1&artist="+artistName+"&title="+songName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d("trails", "ERROR: " + volleyError.getMessage());
            }
        });

        queue.add(stringRequest);
*/    }


}
