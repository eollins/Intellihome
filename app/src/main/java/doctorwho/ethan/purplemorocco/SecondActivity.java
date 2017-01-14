package doctorwho.ethan.purplemorocco;

import android.app.ListActivity;
import android.content.Context;
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, total);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
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
                            activeLocations.add(location);
                        }
                    }

                    Spinner spinner = (Spinner)findViewById(R.id.spinner3);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, activeLocations);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }
}
