package com.example.levis.trails;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.levis.trails.core.PRunnable;
import com.example.levis.trails.core.Song;
import com.example.levis.trails.core.TrailsServer;
import com.example.levis.trails.core.User;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {
    final Context context = this;
    private Button button;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private Thread timer;
    private boolean timerActive = true;
    private GoogleMap map;
    private TrackAdapter dynamicListAdapter;
    private User user = new User("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.list_items);
        dynamicListAdapter = new TrackAdapter(this);
        listView.setAdapter(dynamicListAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xff009590));
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SmartLocation.with(this).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        double mLatitude = 0.0, mLongitude = 0.0;
                        mLastLocation = location;

                        mLatitude = mLastLocation.getLatitude();
                        mLongitude = mLastLocation.getLongitude();

                        map.clear();
                        map.addCircle(new CircleOptions()
                                        .center(new LatLng(mLatitude, mLongitude))
                        );
                        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude, mLongitude)));
                        map.animateCamera(CameraUpdateFactory.zoomTo(15));
                    }
                });

        TrailsServer.initQueue(this);

        timer = new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean[] completed = {true};
                while(timerActive) {
                    if (completed[0] && mLastLocation != null) {
                        completed[0] = false;
                        TrailsServer.fetchSongs(mLastLocation.getLatitude(), mLastLocation.getLongitude(), MainActivity.this, new PRunnable<List<Song>>() {
                            @Override
                            public void run(List<Song> p) {
                                user.updateDynamicSongs(p);

                                dynamicListAdapter.songlist = user.getUserSongs();
                                dynamicListAdapter.notifyDataSetChanged();

                                completed[0] = true;
                            }
                        });
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timer.start();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }).start();*/


        TrailsServer.createListener(this, new PRunnable<String[]>() {
            @Override
            public void run(String[] p) {
                if (mLastLocation == null || p[0] == null || p[1] == null)
                    return;
                if (posting)
                    return;

                posting = true;
                TrailsServer.pushToServer(mLastLocation.getLatitude(), mLastLocation.getLongitude(), p[0], p[1], new Runnable() {
                    @Override
                    public void run() {
                        posting = false;
                        Toast.makeText(MainActivity.this, "Posted!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private boolean posting = false;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                //set title
                alertDialogBuilder.setTitle("Connection Lost");

                //set dialog message
                alertDialogBuilder
                        .setMessage("Try again.")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //if this button is clicked close
                                //current activity
                                MainActivity.this.finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if this button is clicked, just do noting
                        //the dialog box and do nothing
                        dialog.cancel();
                    }
                });
                //create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                //show it
                alertDialog.show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}
