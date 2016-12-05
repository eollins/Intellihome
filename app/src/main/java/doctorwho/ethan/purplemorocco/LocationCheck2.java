package doctorwho.ethan.purplemorocco;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloudSDK;

import static java.lang.System.in;


public class LocationCheck2 extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient = null;

    /*Process:
    • Check to see which type of task is being requested
    • If service is restarting, do nothing and create geofences per stored data
    • If MainActivity is requesting "add," add new data to prefs and create geofences
    • If GeofenceService or SecondActivity are requesting "remove," remove old data and create geofences
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("", "");
        String newData = "";
        String dataToDelete = "";

        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
        String dateTimeKey = "com.doctorwho.ethan.geofences";
        String allLocations = prefs.getString(dateTimeKey, "");

        List<String> locationList = Arrays.asList(allLocations.split("~"));
        List<String> idList = new ArrayList<>();

        if (allLocations != "") {
            for (String s : locationList) {
                List<String> components = Arrays.asList(s.split("`"));
                String boardName = components.get(1);
                String taskName = components.get(2);
                String longitude = components.get(3);
                String latitude = components.get(4);

                String originalId = boardName + "~" + taskName + "~" + longitude + "~" + latitude;
                idList.add(originalId);
            }

            if (!googleApiClient.isConnected()) {
            } else {
                LocationServices.GeofencingApi.removeGeofences(googleApiClient, idList);
            }
        }

        List<String> locationsFull = new ArrayList<>();
        for (String s : locationList) {
            locationsFull.add(s);
        }

        if (!intent.hasExtra("task")) {
        }
        else if (intent.hasExtra("task") && intent.getStringExtra("task").equals("add")) {
            Intent i = new Intent(LocationCheck2.this, DataStorage.class);
            i.putExtra("data", intent.getStringExtra("data"));
            i.putExtra("type", "location");
            i.putExtra("action", "add");
            newData = intent.getStringExtra("data");

            locationsFull.add(newData);

            startService(i);
        }
        else if (intent.hasExtra("task") && intent.getStringExtra("task").equals("remove")) {
            Intent i = new Intent(LocationCheck2.this, DataStorage.class);
            i.putExtra("data", intent.getStringExtra("data"));
            i.putExtra("type", "location");
            i.putExtra("action", "remove");
            dataToDelete = intent.getStringExtra("data");

            List<String> deletion = new ArrayList<>();
            deletion.add(dataToDelete);
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, deletion);

            List<String> components = Arrays.asList(dataToDelete.split("~"));
            dataToDelete = "0`" + components.get(0) + "`" + components.get(1) + "`" + components.get(2) + "`" + components.get(3);

            try {
                locationsFull.remove(dataToDelete);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            startService(i);
        }

        String locations = "";
        for (String s : locationsFull) {
            locations += (s + "~");
        }

        if (locations.equals("") || locations.equals("")) { }
        else {
            List<String> data = Arrays.asList(locations.split("~"));
            for (String s : data) {
                String boardName;
                String taskName;
                String longitude;
                String latitude;

                try {
                    List<String> components = Arrays.asList(s.split("`"));
                    boardName = components.get(1);
                    taskName = components.get(2);
                    longitude = components.get(3);
                    latitude = components.get(4);
                }
                catch (Exception e) {
                    break;
                }

                String rId = boardName + "~" + taskName + "~" + longitude + "~" + latitude;

                final Geofence geofence = new Geofence.Builder()
                        .setRequestId(rId)
                        .setCircularRegion(Double.parseDouble(longitude), Double.parseDouble(latitude), 100)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setNotificationResponsiveness(1000).setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .build();

                final GeofencingRequest geofenceRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(geofence).build();

                Intent i = new Intent(LocationCheck2.this, GeofenceService.class);
                Log.e("", "");
                PendingIntent pendingIntent = PendingIntent.getService(LocationCheck2.this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                //googleApiClient.connect();
                if (!googleApiClient.isConnected()) {
                    Log.i("Service", "GoogleApiClient is not connected");
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    LocationServices.GeofencingApi.addGeofences(googleApiClient, geofenceRequest, pendingIntent)
                            .setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    if (status.isSuccess()) {
                                        Log.i("Service", "Added geofence");
                                    } else {
                                        Log.i("Service", "Geofence failed");
                                    }
                                }
                            });
                }
            }
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        googleApiClient = new GoogleApiClient.Builder(LocationCheck2.this)
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
    }

    @Override
    public void onDestroy() {
        googleApiClient.disconnect();
    }

    public void editData(String dataToChange, boolean removal) {

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
