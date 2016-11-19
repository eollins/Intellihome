package doctorwho.ethan.purplemorocco;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import io.particle.android.sdk.cloud.ParticleCloudSDK;


public class LocationCheck extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient googleApiClient = null;

    List<Geofence> geofences = new ArrayList<>();
    List<GeofencingRequest> requests = new ArrayList<>();
    List<PendingIntent> intents = new ArrayList<>();

    int id = 0;

    public LocationCheck() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        Log.d("e", "e");

        String task = "";
        try {
            task = "";
            if (intent.hasExtra("task")) {
                task = intent.getStringExtra("task");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (task.equals("")) {
        } else if (task.equals("add") || !intent.hasExtra("task")) {
            String data2 = "";
            if (task.equals("add")) {
                if (intent.hasExtra("data")) {
                    data2 = intent.getStringExtra("data");
                } else {
                    return START_STICKY;
                }
            } else {
                SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
                String dateTimeKey = "com.example.app.geofences";
                String locations = prefs.getString(dateTimeKey, "");
                data2 = locations;
            }

            data2 = data2.replace("~", "");
            List<String> components = Arrays.asList(data2.split("`"));
            Log.e("", "");

            String rId = requests.size() + "-" + components.get(1) + "-" + components.get(2) + ";";

            final Geofence geofence = new Geofence.Builder()
                    .setRequestId(requests.size() + "-" + components.get(1) + "-" + components.get(2) + ";" + data2)
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

                data2 = data2 + "~";

                try {
                    editFile(data2, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (task.contains("remove")) {
                List<String> geofencesToRemove = new ArrayList<>();
                geofencesToRemove.add(task.substring(task.indexOf('-') + 1));
                LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofencesToRemove);

                try {
                    editFile(geofencesToRemove.get(0), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//        intent = new Intent(this,GeofenceService.class);
//        this.startService(intent);
        }

        return Service.START_STICKY;
    }

    public void removeGeofence(String geofenceID) {
        if (googleApiClient == null) { return; }
        Log.e("", "");
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

    public void editFile(String data, boolean remove) throws IOException {
        Intent intent = new Intent(LocationCheck.this, DataStorage.class);
        intent.putExtra("type", "location");
        intent.putExtra("data", data);

        if (!remove) {
            intent.putExtra("action", "remove");
        }
        else {
            intent.putExtra("action", "add");
        }

        startService(intent);
    }
}
