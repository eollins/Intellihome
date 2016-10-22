package doctorwho.ethan.purplemorocco;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationCheckOLD extends Service {
    final Context context = this;

    boolean checking = false;
    int delay = 10;
    int stayed = 0;

    private LocationListener listener;
    private LocationManager locationManager;

    String currentLatitude = "";
    String currentLongitude = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
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
                Log.e("a", location.getLatitude() + " " + location.getLatitude());
                float PLACEHOLDER = 0f;

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

                    try {
                        for (String loc : locations) {
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

                            ranges.add(Double.toString(range));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    double smallest = Double.parseDouble(ranges.get(0));
                    double largest = Double.parseDouble(ranges.get(0));
                    double match = 0;
                    boolean matched = false;

                    for (int i = 0; i < ranges.size(); i++) {
                        if (Double.parseDouble(ranges.get(i)) < 150) {
                            match = i;
                            matched = true;
                        }

                        if (Double.parseDouble(ranges.get(i)) > largest)
                            largest = Double.parseDouble(ranges.get(i));
                        else if (Double.parseDouble(ranges.get(i)) < smallest)
                            smallest = Double.parseDouble(ranges.get(i));
                    }

                    int index = ranges.indexOf(smallest);

                    if (matched == true) {
                        Toast.makeText(context, "Location reached", Toast.LENGTH_LONG).show();
                    }

                    TuneManager(Float.parseFloat(ranges.get(index)) / 2);
                }
                catch (Exception e) {
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f, listener);
    }

    private void TuneManager(float distance) {
        Toast.makeText(context, Float.toString(distance), Toast.LENGTH_LONG).show();
        locationManager.removeUpdates(listener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, distance, listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
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
            locationManager.removeUpdates(listener);
        }
    }

    public void ReadLocations() {

    }
}


//try {
//        File file = new File("/data/data/doctorwho.ethan.purplemorocco/files", "Locations.txt");
//
//        int length = (int) file.length();
//        byte[] bytes = new byte[length];
//
//        FileInputStream in = null;
//        try {
//        in = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//        e.printStackTrace();
//        }
//        try {
//        try {
//        in.read(bytes);
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//        } finally {
//        try {
//        in.close();
//        } catch (IOException e) {
//        e.printStackTrace();
//        }
//        }
//
//        String contents = new String(bytes);
//
//        try {
//        List<String> locations = Arrays.asList(contents.split(";"));
//        for (String loc : locations) {
//        String lat = loc.substring(0, loc.indexOf(" "));
//        String lon = loc.substring(loc.indexOf(" ") + 1, loc.length() - 1);
//
//        String latDeg = lat.substring(0, lat.indexOf('°'));
//        String latMin = lat.substring(lat.indexOf('°') + 1, lat.indexOf('\''));
//        String latSec = lat.substring(lat.indexOf('\'') + 1, lat.indexOf('"'));
//
//        String lonDeg = lon.substring(0, lon.indexOf('°'));
//        String lonMin = lon.substring(lon.indexOf('°') + 1, lon.indexOf('\''));
//        String lonSec = lon.substring(lon.indexOf('\'') + 1, lon.indexOf('"'));
//
//        double finalLatitude = Double.parseDouble(latDeg) + (Double.parseDouble(latMin) / 60) + (Double.parseDouble(latSec) / 3600);
//        double finalLongitude = Double.parseDouble(lonDeg) + (Double.parseDouble(lonMin) / 60) + (Double.parseDouble(lonSec) / 3600);
//
//        float tuneDistance = (float) (distFrom(Double.parseDouble(currentLatitude), Double.parseDouble(currentLongitude), finalLatitude, finalLongitude) / 2);
//        TuneManager(tuneDistance);
//        Log.e("a", "a");
//        }
//        }
//        catch (Exception e) {
//        e.printStackTrace();
//        }
//        }
//        catch (Exception e) {
//        e.printStackTrace();
//        }