package doctorwho.ethan.purplemorocco;

import android.app.Activity;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.particle.android.sdk.utils.Toaster;

public class SecondActivity extends AppCompatActivity {
    List<String> timeList;
    List<String> locationList;
    List<String> identifierList;

    List<String> displayedLocations = new ArrayList<>();

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(34, -118), new LatLng(34, -118));

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

        List<String> retired = new ArrayList<>();

        String[] options = { "Time-Based", "Location-Based "};
        final Spinner dropdown2 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        dropdown2.setAdapter(adapter2);

        String[] selections = { "Sauce", "Eggs"};
        final Spinner dropdown3 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        dropdown3.setAdapter(adapter3);

        Button b = (Button)findViewById(R.id.button6);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner s = (Spinner)findViewById(R.id.spinner2);

                if (s.getSelectedItemPosition() == 0) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    final int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(SecondActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            Toaster.s(SecondActivity.this, "Task rescheduled for " + selectedHour + ":" + selectedMinute);
                            sHour = selectedHour;
                            sMinute = selectedMinute;
                            edit();
                        }
                    }, hour, minute, false);
                    mTimePicker.setTitle("Edit Time");
                    mTimePicker.show();
                }
                else {
                    try {
                        PlacePicker.IntentBuilder intentBuilder =
                                new PlacePicker.IntentBuilder();
                        intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                        Intent intent = intentBuilder.build(SecondActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);

                    } catch (GooglePlayServicesRepairableException
                            | GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Spinner s = (Spinner)findViewById(R.id.spinner3);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (dropdown2.getSelectedItemPosition() == 0) {
                    if (timeList.size() != 0) {
                        timeInfo();
                    }
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

                    try {
                        for (int i = 0; i < timeList.size(); i++) {
                            String time = timeList.get(i);
                            List<String> components = Arrays.asList(time.split("~"));
                            String full = components.get(1) + " at " + components.get(2);
                            total.add(full);
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
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

                        if (identifierList.contains(identifier)) {
                            inactiveLocations.add(location);
                        }
                        else {
                            try {
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
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (displayedLocations.size() != 0) {
                        info();
                    }

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

    String coordinates = "";

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        try {
            if (requestCode == PLACE_PICKER_REQUEST
                    && resultCode == Activity.RESULT_OK) {

                final Place place = PlacePicker.getPlace(this, data);
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                String attributions = (String) place.getAttributions();
                if (attributions == null) {
                    attributions = "";
                }

                final LatLng coords = place.getLatLng();
                coordinates = String.valueOf(coords.latitude) + " " + String.valueOf(coords.longitude);
                Toaster.s(SecondActivity.this, coordinates);

                edit();

                //t.setText(address);
                //mAddress.setText(address);
                //mAttributions.setText(Html.fromHtml(attributions));

            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void info() {
        try {
            Spinner s = (Spinner) findViewById(R.id.spinner3);
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
            } else {
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
            } else {
                status = "No Longer Active";
            }

            TextView t = (TextView) findViewById(R.id.textView6);
            t.setTextSize(20f);
            t.setText(components.get(1).substring(components.get(1).indexOf('-') + 1) + "\n" + components.get(2) + "\n" + address + "\n" + city + ", " + state + " " + postalCode + "\n" + country + "\n" + status);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void timeInfo() {
        Spinner s = (Spinner)findViewById(R.id.spinner3);
        String info = timeList.get(s.getSelectedItemPosition());
        List<String> components = Arrays.asList(info.split("~"));

        TextView t = (TextView)findViewById(R.id.textView6);
        t.setText(components.get(0) + "\n" + components.get(1) + "\n" + components.get(2));
        t.setTextSize(35f);
    }

    public void remove(View v) {
        Spinner s = (Spinner)findViewById(R.id.spinner2);
        Spinner d = (Spinner)findViewById(R.id.spinner3);

        if (s.getSelectedItemPosition() == 0) {
            Intent i = new Intent(SecondActivity.this, DataStorage.class);
            i.putExtra("type", "time");
            i.putExtra("action", "remove");
            i.putExtra("data", timeList.get(d.getSelectedItemPosition()));
            startService(i);

            s.setSelection(1);
            s.setSelection(0);
        }
        else {
            Intent i = new Intent(SecondActivity.this, DataStorage.class);
            i.putExtra("type", "location");
            i.putExtra("action", "remove");
            i.putExtra("data", displayedLocations.get(d.getSelectedItemPosition()));
            startService(i);

            Intent i2 = new Intent(SecondActivity.this, DataStorage.class);
            i2.putExtra("type", "location");
            i2.putExtra("action", "disable");
            i2.putExtra("data", displayedLocations.get(d.getSelectedItemPosition()));
            startService(i2);

            s.setSelection(0);
            s.setSelection(1);
        }

        finish();
        startActivity(getIntent());
    }

    int sMinute = 0;
    int sHour = 0;
    public void edit() {
        Spinner one = (Spinner)findViewById(R.id.spinner2);
        Spinner two = (Spinner)findViewById(R.id.spinner3);

        if (one.getSelectedItemPosition() == 0) {
            String s = timeList.get(two.getSelectedItemPosition());
            List<String> components = Arrays.asList(s.split("~"));
            List<String> finalComponents = new ArrayList<>();

            for (int i = 0; i < components.size(); i++) {
                finalComponents.add(components.get(i));
            }

            finalComponents.set(2, sHour + ":" + sMinute);

            String full = finalComponents.get(0) + "•" + finalComponents.get(1) + "•" + finalComponents.get(2);

            Intent i = new Intent(SecondActivity.this, DataStorage.class);
            i.putExtra("type", "time");
            i.putExtra("action", "remove");
            i.putExtra("data", s);
            startService(i);

            Intent e = new Intent(SecondActivity.this, DataStorage.class);
            e.putExtra("type", "time");
            e.putExtra("action", "add");
            e.putExtra("data", full);
            startService(e);

            finish();
            startActivity(getIntent());
        }
        else {
            String s = displayedLocations.get(two.getSelectedItemPosition());
            List<String> components = Arrays.asList(s.split("`"));
            List<String> finalComponents = new ArrayList<>();

            for (int i = 0; i < components.size(); i++) {
                finalComponents.add(components.get(i));
            }

            List<String> coords = Arrays.asList(coordinates.split(" "));

            finalComponents.set(3, coords.get(0));
            finalComponents.set(4, coords.get(1));

            String identifier = generateIdentifier();

            String full = generateIdentifier() + "`" + finalComponents.get(1) + "`" + finalComponents.get(2) + "`" + finalComponents.get(3) + "`" + finalComponents.get(4) + "~";

            Intent i = new Intent(SecondActivity.this, LocationCheck2.class);
            i.putExtra("task", "remove");
            i.putExtra("data", s);
            startService(i);

            Intent o = new Intent(SecondActivity.this, LocationCheck2.class);
            o.putExtra("task", "add");
            o.putExtra("data", full);
            startService(o);

            Intent e = new Intent(SecondActivity.this, DataStorage.class);
            e.putExtra("type", "location");
            e.putExtra("action", "disable");
            e.putExtra("data", s);
            startService(e);

            SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
            String identifierKey = "com.doctorwho.ethan.identifiers";
            String identifiers = prefs.getString(identifierKey, "");
            prefs.edit().putString(identifierKey, identifiers + (identifier + "-")).apply();

            finish();
            startActivity(getIntent());
        }
    }

    char[] characters = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P' , 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    public String generateIdentifier() {
        boolean confirmed = false;
        String full = "";

        while (!confirmed) {
            for (int i = 0; i < 8; i++) {
                full += characters[(int )(Math.random() * 62 + 0)];
            }

            SharedPreferences prefs = this.getSharedPreferences("com.doctorwho.ethan", Context.MODE_PRIVATE);
            String dateTimeKey = "com.doctorwho.ethan.identifiers";
            String identifiers = prefs.getString(dateTimeKey, "");

            List identifierList = Arrays.asList(identifiers.split("-"));
            if (identifierList.contains(full)) {
                full = "";
            }
            else {
                confirmed = true;
            }
        }

        return full;
    }
}
