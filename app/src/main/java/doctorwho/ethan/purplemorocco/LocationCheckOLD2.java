package doctorwho.ethan.purplemorocco;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationCheckOLD2 extends Service {
    private final Context context = this;

    private LocationManager locationManager;
    private LocationListener listener;

    public LocationCheckOLD2() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        return Service.START_STICKY;
    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double feet = dist * 5280.0;

        return feet;
    }

    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(context, location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                try {
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
                        try {
                            in.read(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    String contents = new String(bytes);

                    List<String> locations = Arrays.asList(contents.split(";"));
                    List<String> ranges = new ArrayList<>();
                    String cLoc = "";

                    try {
                        for (String loc : locations) {
                            cLoc = loc;

                            String lat = loc.substring(0, loc.indexOf(" "));
                            String lon = loc.substring(loc.indexOf(" ") + 1, loc.length() - 1);

                            String latDeg = lat.substring(0, lat.indexOf('°'));
                            String latMin = lat.substring(lat.indexOf('°') + 1, lat.indexOf('\''));
                            String latSec = lat.substring(lat.indexOf('\'') + 1, lat.indexOf('"'));

                            String lonDeg = lon.substring(0, lon.indexOf('°'));
                            String lonMin = lon.substring(lon.indexOf('°') + 1, lon.indexOf('\''));
                            String lonSec = lon.substring(lon.indexOf('\'') + 1, lon.indexOf('"'));

                            double finalLatitude = Double.parseDouble(latDeg) + (Double.parseDouble(latMin) / 60) + (Double.parseDouble(latSec) / 3600);
                            double finalLongitude = Double.parseDouble(lonDeg) + (Double.parseDouble(lonMin) / 60) + (Double.parseDouble(lonSec) / 3600);

                            double range = distFrom(location.getLatitude(), location.getLongitude(), finalLatitude, finalLongitude);
                            double final2 = (int)Math.floor(range);
                            String final3 = Double.toString(final2).substring(0, Double.toString(final2).indexOf('.'));
                            int finalRange = Integer.parseInt(final3);

                            Toast.makeText(context, Double.toString(finalRange), Toast.LENGTH_LONG).show();

                            if (finalRange < 20) {
                                Toast.makeText(context, "Location reached", Toast.LENGTH_LONG).show();

                                locations.remove(locations.indexOf(cLoc));

                                NotificationCompat.Builder builder =
                                        new NotificationCompat.Builder(context)
                                                .setSmallIcon(R.drawable.cast_ic_notification_0)
                                                .setContentTitle("Location Reached")
                                                .setContentText("You have reached your destination.");
                                int NOTIFICATION_ID = 12345;

                                try {
                                    try {
                                        FileOutputStream fos = openFileOutput("Locations.txt", Context.MODE_APPEND);
                                        OutputStreamWriter osw = new OutputStreamWriter(fos);

                                        for (String loc2 : locations) {
                                            osw.append(loc2);
                                        }

                                        osw.flush();
                                        osw.close();

                                    } catch (FileNotFoundException e) {
                                        //catch errors opening file
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent targetIntent = new Intent(context, MainActivity.class);
                                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                builder.setContentIntent(contentIntent);
                                NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                nManager.notify(NOTIFICATION_ID, builder.build());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.e("a", "a");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void TuneManager(long time, float distance) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, time, distance, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
    }
}
