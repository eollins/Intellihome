package doctorwho.ethan.purplemorocco;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.particle.android.sdk.utils.Toaster;

public class SecondActivity extends AppCompatActivity {
    List<String> timeList;
    List<String> locationList;
    List<String> identifierList;

    List<String> displayedLocations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
        String timeKey = "com.doctorwho.ethan.times";
        String locationKey = "com.doctorwho.ethan.geofences";
        String identifierKey = "com.doctorwho.ethan.retiredidentifiers";
        String times = prefs.getString(timeKey, "");
        String locations = prefs.getString(locationKey, "");
        String identifiers = prefs.getString(identifierKey, "");

        timeList = Arrays.asList(times.split("`"));
        locationList = Arrays.asList(locations.split("~"));
        identifierList = Arrays.asList(identifiers.split("-"));

        String[] options = { "Time-Based", "Location-Based "};
        final Spinner dropdown2 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        dropdown2.setAdapter(adapter2);

        String[] selections = { "Sauce", "Eggs"};
        final Spinner dropdown3 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        dropdown3.setAdapter(adapter3);

        Spinner s = (Spinner)findViewById(R.id.spinner3);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (dropdown2.getSelectedItemPosition() == 0) {

                }
                else {
                    info();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    String[] times = new String[timeList.size()];
                    for (int i = 0; i < timeList.size(); i++) {
                        times[i] = timeList.get(i);
                    }

                    Spinner spinner = (Spinner)findViewById(R.id.spinner3);
                    ArrayList<String> total = new ArrayList<>();

                    for (int i = 0; i < timeList.size(); i++) {
                        total.add(timeList.get(i));
                    }

                    final Spinner dropdown3 = (Spinner)findViewById(R.id.spinner3);
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(SecondActivity.this, android.R.layout.simple_spinner_dropdown_item, total);
                    dropdown3.setAdapter(adapter3);
                }
                else {
                    List<String> activeLocations = new ArrayList<String>();
                    List<String> inactiveLocations = new ArrayList<String>();

                    for (String location : locationList) {
                        List<String> components = Arrays.asList(location.split("`"));
                        String identifier = components.get(0);
                        Toaster.s(SecondActivity.this, identifier);

                        if (identifierList.contains(identifier)) {
                            inactiveLocations.add(location);
                        }
                        else {
                            double longitude = Double.parseDouble(components.get(3));
                            double latitude = Double.parseDouble(components.get(4));

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(SecondActivity.this, Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(longitude, latitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                                activeLocations.add(address);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            displayedLocations.add(location);
                        }
                    }

                    info();

                    String[] selections = { "" };
                    final Spinner dropdown3 = (Spinner)findViewById(R.id.spinner3);
                    ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(SecondActivity.this, android.R.layout.simple_spinner_dropdown_item, selections);
                    dropdown3.setAdapter(adapter4);

                    Spinner spinner = (Spinner)findViewById(R.id.spinner3);
                    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(SecondActivity.this, android.R.layout.simple_spinner_dropdown_item, activeLocations);
                    spinner.setAdapter(adapter3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    public void info() {
        Spinner s = (Spinner)findViewById(R.id.spinner3);
        String info = displayedLocations.get(s.getSelectedItemPosition());
        List<String> components = Arrays.asList(info.split("`"));

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(SecondActivity.this, Locale.getDefault());

        boolean active = false;
        String identifier = components.get(0);
        SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
        String identifierKey = "com.doctorwho.ethan.retiredidentifiers";
        String identifiers = prefs.getString(identifierKey, "");
        List<String> retiredIdentifiers = Arrays.asList(identifiers.split("-"));
        if (retiredIdentifiers.contains(identifier)) {
            active = false;
        }
        else {
            active = true;
        }

        String address = "";
        String city = "";
        String state = "";
        String country = "";
        String postalCode = "";

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(components.get(3)), Double.parseDouble(components.get(4)), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        } catch (Exception e) {
            e.printStackTrace();
        }

        String status = "";
        if (active) {
            status = "Currently Active";
        }
        else {
            status = "No Longer Active";
        }

        TextView t = (TextView)findViewById(R.id.textView6);
        t.setText(components.get(1).substring(components.get(1).indexOf('-') + 1) + "\n" + components.get(2) + "\n" + address + "\n" + city + ", " + state + " " + postalCode + "\n" + country + "\n" + status);
    }


    public void remove(View v) {
        Spinner two = (Spinner)findViewById(R.id.spinner3);

        Intent i = new Intent(SecondActivity.this, DataStorage.class);
        i.putExtra("type", "location");
        i.putExtra("action", "disable");
        i.putExtra("data", displayedLocations.get(two.getSelectedItemPosition()));

//        SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
//        String timeKey = "com.doctorwho.ethan.times";
//        String locationKey = "com.doctorwho.ethan.geofences";
//        String identifierKey = "com.doctorwho.ethan.retiredidentifiers";
//        String times = prefs.getString(timeKey, "");
//        String locations = prefs.getString(locationKey, "");
//        String identifiers = prefs.getString(identifierKey, "");
//
//        Spinner one = (Spinner)findViewById(R.id.spinner2);
//        Spinner two = (Spinner)findViewById(R.id.spinner3);
//
//        if (one.getSelectedItemPosition() == 0) {
//            //time removal goes here
//        }
//        else {
//            String location = displayedLocations.get(two.getSelectedItemPosition());
//            List<String> components = Arrays.asList(location.split("`"));
//            String identifier = components.get(0);
//            identifiers += identifier + "-";
//            prefs.edit().putString(identifierKey, identifiers);
//            int currentIndex = two.getSelectedItemPosition();
//            displayedLocations.remove(location);
//            one.setSelection(-1);
//            one.setSelection(1);
//            two.setSelection(currentIndex);
//        }
    }
}
