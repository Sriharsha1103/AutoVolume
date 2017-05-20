package com.vvit.ammu.autovolume;

import android.Manifest;
import android.content.Context;



import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    LocationRequest request;
    GoogleApiClient googleApiClient;
    SeekBar media = null;
    Switch enable;
    AudioManager mgr = null;
    private Location lastLocation;
    private Location currentLocation;
    private float speed;
    TextView textView;
    boolean flag = false;
    int initialVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildApiClient();
        googleApiClient.connect();
        mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        enable = (Switch) findViewById(R.id.id_switch_enable);
        media = (SeekBar) findViewById(R.id.id_volume_bar);
        textView = (TextView) findViewById(R.id.id_speed_view);
        media.setEnabled(false);
        initialVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enable.isChecked()) {
                    media.setEnabled(true);
                    flag =true;
                    initBar(media, AudioManager.STREAM_MUSIC);
                    Toast.makeText(getApplicationContext(),""+flag,Toast.LENGTH_SHORT).show();
                } else {
                    flag = false;
                    media.setEnabled(false);
                    mgr.setStreamVolume(AudioManager.STREAM_MUSIC,initialVolume,0);
                    Toast.makeText(getApplicationContext(),""+flag,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void buildApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    private void initBar(SeekBar bar, final int stream) {
        bar.setMax(mgr.getStreamMaxVolume(stream));
        bar.setProgress(mgr.getStreamVolume(stream));

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mgr.setStreamVolume(stream, progress, 0);
               /* Toast.makeText(getApplicationContext(),""+progress,Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        speed = currentLocation.getSpeed();
        if(flag) {
            changeVolume((speed * 18) / 5);
            Log.d("Flag:",""+flag);
        }
        else
        {

        }
        textView.setText("Speed is "+((speed * 18)/5)+" Km/h");
    }

    private void changeVolume(float speed) {
        final int stream = AudioManager.STREAM_MUSIC;
        //float peed = (speed * 5) / 18;
        if(speed <=10.0){
            mgr.setStreamVolume(stream, 2, 0);
            media.setProgress(1);

        }
        else if(speed <=20.0) {
            mgr.setStreamVolume(stream, 4, 0);
            media.setProgress(3);
        }
        else if(speed <=40.0){
            mgr.setStreamVolume(stream, 6, 0);

        }
        else if(speed <= 60.0){
            mgr.setStreamVolume(stream, 9, 0);

        }
        else if(speed <=70.0){
            mgr.setStreamVolume(stream, 11, 0);

        }
        else if(speed <= 80.0){
            mgr.setStreamVolume(stream, 13, 0);

        }
        else {
            mgr.setStreamVolume(stream, 15, 0);

        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        request = new LocationRequest();
        request.setFastestInterval(3000);
        request.setInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        speed = lastLocation.getSpeed();
        textView.setText("Speed is "+((speed * 18)/5)+" Km/h");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
