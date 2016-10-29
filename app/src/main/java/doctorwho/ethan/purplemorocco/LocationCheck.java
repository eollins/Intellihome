package doctorwho.ethan.purplemorocco;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class LocationCheck extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient = null;

    List<Geofence> geofences = new ArrayList<>();
    List<GeofencingRequest> requests = new ArrayList<>();

    int id = 0;

    public LocationCheck() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Log.d("e", "e");

        File file = new File("/data/data/doctorwho.ethan.purplemorocco/files", "Locations.txt");

        int length = (int) file.length();

        byte[] bytes = new byte[length];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            in.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String contents = new String(bytes);
        List<String> data = Arrays.asList(contents.split("~"));
        Log.e("", "");

        for (String location : data) {
            if (location == "") { break; }
            List<String> components = Arrays.asList(location.split("`"));
            Log.e("", "");

            final Geofence geofence = new Geofence.Builder()
                    .setRequestId(Integer.toString(id) + "-" + components.get(1) + "-" + components.get(2))
                    .setCircularRegion(Double.parseDouble(components.get(3)), Double.parseDouble(components.get(4)), 100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();

            final GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(geofence).build();

            Intent i = new Intent(LocationCheck.this, GeofenceService.class);
            Log.e("", "");
            PendingIntent pendingIntent = PendingIntent.getService(LocationCheck.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

            //googleApiClient.connect();
            if (!googleApiClient.isConnected()) {
                Log.i("Service", "GoogleApiClient is not connected");
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { }
                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofenceRequest, pendingIntent)
                        .setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    Log.i("Service", "Added geofence");
                                    geofences.add(geofence);
                                    requests.add(geofenceRequest);
                                } else {
                                    Log.i("Service", "Geofence failed");
                                }
                            }
                        });
            }
        }

//        intent = new Intent(this,GeofenceService.class);
//        this.startService(intent);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        googleApiClient = new GoogleApiClient.Builder(LocationCheck.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.i("Service", "Connected to GoogleApiClient");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i("Service", "Suspended connection to GoogleApiClient");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i("Service", "Connection failed");
                    }
                })
                .build();

        googleApiClient.connect();

        Log.e("", "");
    }

    public void addGeofence(double longitude, double latitude, String boardName, String taskName) {
        Log.e("", "");
        Geofence geofence = new Geofence.Builder()
                .setRequestId(Integer.toString(id) + "-" + boardName + "-" + taskName)
                .setCircularRegion(longitude, latitude, 100)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();

        GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence).build();

        Intent intent = new Intent(LocationCheck.this, GeofenceService.class);
        Log.e("", "");
        PendingIntent pendingIntent = PendingIntent.getService(LocationCheck.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, "GoogleApiClient is not connected", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;

            }
            LocationServices.GeofencingApi.addGeofences(googleApiClient, geofenceRequest, pendingIntent)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Toast.makeText(LocationCheck.this, "Added geofence", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LocationCheck.this, "Geofence failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        id++;
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
